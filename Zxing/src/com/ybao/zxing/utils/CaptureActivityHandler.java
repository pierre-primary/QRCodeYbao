/*
 * Copyright (C) 2008 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ybao.zxing.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.ybao.zxing.CaptureManager;
import com.ybao.zxing.camera.CameraManager;
import com.ybao.zxing.decode.DecodeHandler;
import com.ybao.zxing.decode.DecodeThread;
import com.ybao.zxing.decode.RGBLuminanceSource;

import java.io.ByteArrayOutputStream;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class CaptureActivityHandler extends Handler {

    private final CaptureManager captureManager;
    private final Activity activity;
    private final DecodeThread decodeThread;
    private final CameraManager cameraManager;
    private State state;
    public static int DECODE_FAILED = 0;
    public static int DECODE_SUCCEEDED = 1;
    public static int RESTART_PREVIEW = 2;
    public static int RETURN_SCAN_RESULT = 3;
    public static int DECODE_PC_SUCCEEDED = 4;
    public static int DECODE_PC_FAILED = 5;
    MultiFormatReader multiFormatReader;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureActivityHandler(CaptureManager captureManager, Activity activity, CameraManager cameraManager, int decodeMode) {
        this.captureManager = captureManager;
        this.activity = activity;
        decodeThread = new DecodeThread(captureManager, decodeMode);
        decodeThread.start();

        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(decodeThread.getHints());
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == DECODE_FAILED) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), DecodeHandler.DECODE);
        } else if (message.what == DECODE_SUCCEEDED) {
            state = State.SUCCESS;
            Bundle bundle = message.getData();
            bundle.putBoolean("success", true);
            captureManager.handleDecode((Result) message.obj, bundle);
        } else if (message.what == RESTART_PREVIEW) {
            restartPreviewAndDecode();
        } else if (message.what == RETURN_SCAN_RESULT) {
            activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
            activity.finish();
        }
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), DecodeHandler.QUIT);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause()
            // will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(DECODE_SUCCEEDED);
        removeMessages(DECODE_FAILED);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), DecodeHandler.DECODE);
        }
    }


    public void scanningImage(String path) {

        Log.e("scanningImage", "scanningImage-start");
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Log.e("scanningImage", "path:" + path);
        RGBLuminanceSource source = null;
        Result rawResult = null;
        try {
            source = new RGBLuminanceSource(path);
        } catch (Exception e) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("success", false);
            bundle.putString("msg", "未找到图片");
            captureManager.handleDecode(null, bundle);
            return;
        }

        if (source != null) {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(binaryBitmap);
                Log.e("scanningImage", "rawResult:" + rawResult.getText());
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }

        if (rawResult != null) {
            captureManager.handleDecode(rawResult, null);
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("success", false);
            bundle.putString("msg", "未发现图形码");
            captureManager.handleDecode(null, bundle);
            return;
        }
    }
}

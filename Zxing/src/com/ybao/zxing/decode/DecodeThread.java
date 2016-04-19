/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ybao.zxing.decode;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.ybao.zxing.CaptureManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class DecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";

    public static final int PRODUCT_MODE = 1;
    public static final int ONE_CODE_MODE = 2;
    public static final int QR_CODE_MODE = 4;
    public static final int TWO_CODE_MODE = 8;
    public static final int ALL_MODE = ONE_CODE_MODE | TWO_CODE_MODE;

    private final CaptureManager captureManager;
    private final Map<DecodeHintType, Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    public DecodeThread(CaptureManager captureManager, int decodeMode) {

        this.captureManager = captureManager;
        handlerInitLatch = new CountDownLatch(1);

        hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);

        Collection<BarcodeFormat> decodeFormats = new ArrayList<BarcodeFormat>();
        int[] nn = new int[4];
        if ((decodeMode & ONE_CODE_MODE) != 0) {
            decodeFormats.addAll(DecodeFormatManager.getOneDFormats());
            nn[1] = 1;
        } else if ((decodeMode & PRODUCT_MODE) != 0) {
            decodeFormats.addAll(DecodeFormatManager.getProductFormats());
            nn[0] = 1;
        }
        if ((decodeMode & TWO_CODE_MODE) != 0) {
            decodeFormats.addAll(DecodeFormatManager.getTwoDFormats());
            nn[3] = 1;
        } else if ((decodeMode & QR_CODE_MODE) != 0) {
            decodeFormats.addAll(DecodeFormatManager.getQrCodeFormats());
            nn[2] = 1;
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
    }

    public Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    public Map<DecodeHintType, Object> getHints() {
        return hints;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(captureManager, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}

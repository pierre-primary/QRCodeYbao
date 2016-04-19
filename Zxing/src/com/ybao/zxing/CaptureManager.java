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
package com.ybao.zxing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.Result;
import com.ybao.zxing.camera.CameraManager;
import com.ybao.zxing.decode.DecodeThread;
import com.ybao.zxing.utils.BeepManager;
import com.ybao.zxing.utils.CaptureActivityHandler;

import java.math.BigDecimal;

public class CaptureManager implements SurfaceHolder.Callback {

    private static final String TAG = CaptureManager.class.getSimpleName();

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private BeepManager beepManager;

    private Rect mCropRect = null;

    private boolean isFlashlightOpen = false;

    private int decodeMode;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    Activity activity;
    Context context;

    public CaptureManager(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();

    }

    public void init() {
        init(DecodeThread.ONE_CODE_MODE | DecodeThread.QR_CODE_MODE);
    }

    public void init(int decodeMode) {

        if (cameraManager != null) {
            return;
        }
        this.decodeMode = decodeMode;
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cameraManager = new CameraManager(context);
        beepManager = new BeepManager(activity);
        getScanPreview().getHolder().addCallback(this);
    }

    public void setDecodeMode(int decodeMode) {
        this.decodeMode = decodeMode;
        reStartCapture();
    }

    public void reStartCapture() {
        closeCaptrue();
        start();
    }

    public void remove() {
        getScanPreview().getHolder().removeCallback(this);
    }

    public void start() {
        openCaptrue();
    }

    public void stop() {
        closeCaptrue();
        beepManager.close();
    }


    public void handleDecode(Result rawResult, Bundle bundle) {
        beepManager.playBeepSoundAndVibrate();
        onHandleDecode(rawResult, bundle);
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(CaptureActivityHandler.RESTART_PREVIEW, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    public void setFlashlightState(boolean flashlightState) {
        if (isFlashlightOpen != flashlightState) {
            isFlashlightOpen = flashlightState;
            cameraManager.setTorch(isFlashlightOpen); // 打开闪光灯
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        openCamera(getScanPreview().getHolder());
        openCaptrue();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCaptrue();
        closeCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private void openCaptrue() {
        if (!cameraManager.isOpen()) {
            return;
        }
        try {
            if (handler == null) {
                handler = new CaptureActivityHandler(this, activity, cameraManager, decodeMode);
            }
        } catch (Exception e) {
            handler = null;
            onError();
            return;
        }
        initCrop();
        if (!TextUtils.isEmpty(path)) {
            handler.scanningImage(path);
            path = null;
        }
    }

    private void closeCaptrue() {
        if (!cameraManager.isOpen()) {
            return;
        }
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
    }

    private void openCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
        } catch (Exception e) {
            onError();
        }
    }

    private void closeCamera() {
        if (!cameraManager.isOpen()) {
            return;
        }
        cameraManager.closeDriver();
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        onInitCrop(cameraManager.getCameraResolution().y, cameraManager.getCameraResolution().x);
    }

    protected SurfaceView getScanPreview() {
        return captureImpl.getScanPreview();
    }


    protected int[] getStartPoint() {
        return captureImpl.getStartPoint();
    }

    protected int[] getScanContainerSize() {
        return captureImpl.getScanContainerSize();
    }

    protected int[] getScanCropViewSize() {
        return captureImpl.getScanCropViewSize();
    }

    protected void onHandleDecode(Result rawResult, Bundle bundle) {
        captureImpl.onHandleDecode(rawResult, bundle);
    }

    protected void onError() {
        captureImpl.onError();
    }

    CaptureImpl captureImpl;

    public void setCaptureImpl(CaptureImpl captureImpl) {
        this.captureImpl = captureImpl;
    }

    public interface CaptureImpl {
        SurfaceView getScanPreview();

        int[] getStartPoint();

        int[] getScanContainerSize();

        int[] getScanCropViewSize();

        void onHandleDecode(Result rawResult, Bundle bundle);

        void onError();
    }


    protected void onInitCrop(int cameraWidth, int cameraHeight) {

        /** 获取布局中扫描框的位置信息 */
        int[] point = getStartPoint();
        int cropLeft = point[0];
        int cropTop = point[1];

        int[] cropSize = getScanCropViewSize();

        /** 获取布局容器的宽高 */
        int[] containerSize = getScanContainerSize();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerSize[0];
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerSize[1];

        /** 计算最终截取的矩形的宽度 */
        int width = cropSize[0] * cameraWidth / containerSize[0];
        /** 计算最终截取的矩形的高度 */
        int height = cropSize[1] * cameraHeight / containerSize[1];

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }


    /**
     * 压缩图片
     *
     * @param bitmap   源图片
     * @param width    想要的宽度
     * @param height   想要的高度
     * @param isAdjust 是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩
     * @return Bitmap
     * @author wangyongzheng
     */
    public Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {
        // 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图
        if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
            return bitmap;
        }
        // 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode);
        // scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃
        float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();
        float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();
        if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸
            sx = (sx < sy ? sx : sy);
            sy = sx;// 哪个比例小一点，就用哪个比例
        }
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    String path = null;

    public void decode(String path) {
        if (handler != null) {
            handler.scanningImage(path);
        } else {
            this.path = path;
        }
    }
}
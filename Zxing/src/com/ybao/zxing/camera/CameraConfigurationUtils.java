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
package com.ybao.zxing.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮箱: 1076559197@qq.com | tauchen1990@gmail.com
 * <p/>
 * 作者: 陈涛
 * <p/>
 * 日期: 2014年8月20日
 * <p/>
 * 描述: 该类主要负责设置相机的参数信息，获取最佳的预览界面
 */

public final class CameraConfigurationUtils {

    public static List<Camera.Size> getResolutionList(Camera mCamera) {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public static Camera.Size getResolution(Camera mCamera) {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size s = params.getPreviewSize();
        return s;
    }

    public static void setResolution(Camera camera, Camera.Size resolution) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(resolution.width, resolution.height);
        camera.setParameters(parameters);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void focusOnTouch(Camera camera, MotionEvent event) {
        Rect focusRect = calculateTapArea(camera, event.getRawX(), event.getRawY(), 1f);
        Rect meteringRect = calculateTapArea(camera, event.getRawX(), event.getRawY(), 1.5f);

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        int version = Build.VERSION.SDK_INT;
        if (version > Build.VERSION_CODES.ICE_CREAM_SANDWICH && parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(focusRect, 1000));

            parameters.setFocusAreas(focusAreas);
        }

        if (version > Build.VERSION_CODES.ICE_CREAM_SANDWICH && parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
            meteringAreas.add(new Camera.Area(meteringRect, 1000));

            parameters.setMeteringAreas(meteringAreas);
        }

        camera.setParameters(parameters);
    }

    public static void setAutoFocusCallback(Camera camera, Camera.AutoFocusCallback autoFocusCallback) {
        camera.autoFocus(autoFocusCallback);

    }

    /**
     * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
     */
    private static Rect calculateTapArea(Camera camera, float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = (int) (x / getResolution(camera).width - 1000);
        int centerY = (int) (y / getResolution(camera).height - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public static void setFocusMode(Camera mCamera, Context item, int type) {
        Camera.Parameters params = mCamera.getParameters();
        List<String> FocusModes = params.getSupportedFocusModes();

        switch (type) {
            case 0:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                else
                    Toast.makeText(item, "Auto Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                else
                    Toast.makeText(item, "Continuous Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_EDOF))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
                else
                    Toast.makeText(item, "EDOF Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                else
                    Toast.makeText(item, "Fixed Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                else
                    Toast.makeText(item, "Infinity Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_MACRO))
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                else
                    Toast.makeText(item, "Macro Mode not supported", Toast.LENGTH_SHORT).show();
                break;
        }

        mCamera.setParameters(params);
    }

    public static void setFlashMode(Camera mCamera, Context item, int type) {
        Camera.Parameters params = mCamera.getParameters();
        List<String> FlashModes = params.getSupportedFlashModes();

        switch (type) {
            case 0:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                else
                    Toast.makeText(item, "Auto Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_OFF))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                else
                    Toast.makeText(item, "Off Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_ON))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                else
                    Toast.makeText(item, "On Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_RED_EYE))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_RED_EYE);
                else
                    Toast.makeText(item, "Red Eye Mode not supported", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                if (FlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH))
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                else
                    Toast.makeText(item, "Torch Mode not supported", Toast.LENGTH_SHORT).show();
                break;
        }

        mCamera.setParameters(params);
    }
}

package com.ybao.zxing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class CreateDCode {
    public static Bitmap CreateOneDCode(String content, int w, int h,
                                        int qr_color, int bg_color) {
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, w, h);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = qr_color;
                    } else {
                        pixels[y * width + x] = bg_color;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 通过像素数组生成bitmap,具体参考api
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (Exception e) {
            return null;
        }

    }

    public static Bitmap CreateOneDCode(String content, int w, int h) {
        try {
            return CreateOneDCode(content, w, h, 0xff000000, 0x00ffffff);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Bitmap CreateQRCode(String content, int size,
                                      int qr_color, int bg_color) throws WriterException {
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = qr_color;
                } else { // 无信息设置像素点为白色
                    pixels[y * width + x] = bg_color;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
        return bitmap;
    }


    public static Bitmap CreateQRCode(String content, int size) {
        try {
            return CreateQRCode(content, size, 0xff000000, 0x00ffffff);
        } catch (Exception ex) {
            return null;
        }
    }


    /**
     * 获取二维码位置信息
     *
     * @param matrix
     * @param rect
     * @return
     */
    private static int checkSize(BitMatrix matrix, Rect rect) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        int startX = 0;
        int startY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    startX = x;
                    startY = y;
                    x = width;
                    y = height;
                }
            }
        }
        int endX = 0;
        for (int x = width - 1; x >= 0; x--) {
            if (matrix.get(x, startY)) {
                endX = x;
                x = -1;
            }
        }
        int endY = 0;
        for (int y = height - 1; y >= 0; y--) {
            if (matrix.get(startX, y)) {
                endY = y;
                y = -1;
            }
        }
        int cellWidth = 1;
        while (true) {
            int pX = startX + cellWidth;
            int pY = startY + cellWidth;
            if (pX <= endX && pY <= endY && matrix.get(pX, pY)) {
                cellWidth++;
                continue;
            }
            break;
        }
        rect.left = startX;
        rect.top = startY;
        rect.right = endX;
        rect.bottom = endY;
        return cellWidth;
    }

    /**
     * 圆点二维码
     *
     * @param content
     * @param size
     * @return
     */
    public static Bitmap CreateQRCodeDot(String content, int size) {
        try {

            // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            Rect codeRect = new Rect();
            int cellWidth = checkSize(matrix, codeRect);

            int width = matrix.getWidth();
            int height = matrix.getHeight();

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            int hcellWidth = cellWidth / 2;
            int startXp = codeRect.left + hcellWidth;
            int startYp = codeRect.top + hcellWidth;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            for (int x = startXp; x <= codeRect.right; x += cellWidth) {
                for (int y = startYp; y <= codeRect.bottom; y += cellWidth) {
                    if (matrix.get(x, y)) {
                        canvas.drawCircle(x, y, hcellWidth, paint);
                    }
                }
            }
            return bitmap;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 多边形二维码
     *
     * @param content
     * @param size
     * @return
     */

    public static Bitmap CreateQRCodePolygon(String content, int size, int lCount) {
        if (lCount < 3) {
            lCount = 3;
        }
        try {

            // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            Rect codeRect = new Rect();
            int cellWidth = checkSize(matrix, codeRect);

            int width = matrix.getWidth();
            int height = matrix.getHeight();

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            int hcellWidth = cellWidth / 2;
            int startXp = codeRect.left + hcellWidth;
            int startYp = codeRect.top + hcellWidth;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            double pr = 2 * Math.PI / lCount;
            double pr90 = 2 * Math.PI / 4;
            Canvas canvas = new Canvas(bitmap);
            int pw = 7 * cellWidth;
            for (int x = startXp; x <= codeRect.right; x += cellWidth) {
                for (int y = startYp; y <= codeRect.bottom; y += cellWidth) {
                    if (matrix.get(x, y)) {
                        if ((x > codeRect.left + pw || y > codeRect.top + pw) && (x < codeRect.right - pw || y > codeRect.top + pw) && (x > codeRect.left + pw || y < codeRect.bottom - pw)) {

                            Path path = new Path();
                            path.moveTo(x, y - hcellWidth);
                            for (int indexL = 1; indexL < lCount; indexL++) {
                                path.lineTo((int) (x + Math.cos(indexL * pr - pr90) * hcellWidth), (int) (y + Math.sin(indexL * pr - pr90) * hcellWidth));
                            }
                            path.close();
                            canvas.drawPath(path, paint);
                        } else {
                            canvas.drawRect(x - hcellWidth, y - hcellWidth, x + hcellWidth, y + hcellWidth, paint);
                        }
                    }
                }
            }
            return bitmap;
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * n角星二维码
     *
     * @param content
     * @param size
     * @return
     */
    public static Bitmap CreateQRCodeStar(String content, int size, int lCount) {
        if (lCount < 3) {
            lCount = 3;
        }
        try {

            // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            Rect codeRect = new Rect();
            int cellWidth = checkSize(matrix, codeRect);

            int width = matrix.getWidth();
            int height = matrix.getHeight();

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            int hcellWidth = cellWidth / 2;
            int hcellWidth2 = cellWidth / 4;
            int startXp = codeRect.left + hcellWidth;
            int startYp = codeRect.top + hcellWidth;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            double pr = 2 * Math.PI / lCount;
            double pr90 = 2 * Math.PI / 4;
            double pr45 = 2 * Math.PI / 8;
            Canvas canvas = new Canvas(bitmap);
            int pw = 7 * cellWidth;
            for (int x = startXp; x <= codeRect.right; x += cellWidth) {
                for (int y = startYp; y <= codeRect.bottom; y += cellWidth) {
                    if (matrix.get(x, y)) {
                        if ((x > codeRect.left + pw || y > codeRect.top + pw) && (x < codeRect.right - pw || y > codeRect.top + pw) && (x > codeRect.left + pw || y < codeRect.bottom - pw)) {

                            Path path = new Path();
                            path.moveTo(x, y - hcellWidth);
                            path.lineTo((int) (x + Math.cos(-pr45) * hcellWidth2), (int) (y + Math.sin(-pr45) * hcellWidth2));
                            for (int indexL = 1; indexL < lCount; indexL++) {
                                path.lineTo((int) (x + Math.cos(indexL * pr - pr90) * hcellWidth), (int) (y + Math.sin(indexL * pr - pr90) * hcellWidth));
                                path.lineTo((int) (x + Math.cos(indexL * pr - pr45) * hcellWidth2), (int) (y + Math.sin(indexL * pr - pr45) * hcellWidth2));
                            }
                            path.close();
                            canvas.drawPath(path, paint);
                        } else {
                            canvas.drawRect(x - hcellWidth, y - hcellWidth, x + hcellWidth, y + hcellWidth, paint);
                        }
                    }
                }
            }
            return bitmap;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 随机大小的圆点
     * 定位区不可随机大小
     *
     * @param content
     * @param size
     * @return
     */
    public static Bitmap CreateQRCodeSDot(String content, int size) {
        try {

            // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            Rect codeRect = new Rect();
            int cellWidth = checkSize(matrix, codeRect);

            int width = matrix.getWidth();
            int height = matrix.getHeight();

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            int pw = 7 * cellWidth;
            int hcellWidth = cellWidth / 2;
            int startXp = codeRect.left + hcellWidth;
            int startYp = codeRect.top + hcellWidth;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            for (int x = startXp; x <= codeRect.right; x += cellWidth) {
                for (int y = startYp; y <= codeRect.bottom; y += cellWidth) {
                    if (matrix.get(x, y)) {
                        int r = hcellWidth;
                        if ((x > codeRect.left + pw || y > codeRect.top + pw) && (x < codeRect.right - pw || y > codeRect.top + pw) && (x > codeRect.left + pw || y < codeRect.bottom - pw)) {
                            r = (int) ((0.75f + Math.random() * 0.5f) * hcellWidth);
                        }
                        canvas.drawCircle(x, y, r, paint);
                    }
                }
            }
            return bitmap;
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 顶角平滑
     *
     * @param content
     * @param size
     * @param psR
     * @return
     */
    public static Bitmap CreateQRCodeSmooth(String content, int size, float psR) {
        try {

            // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            Rect codeRect = new Rect();
            int cellWidth = checkSize(matrix, codeRect);

            int width = matrix.getWidth();
            int height = matrix.getHeight();

            Paint BPaint = new Paint();
            BPaint.setColor(Color.BLACK);
            BPaint.setStyle(Paint.Style.FILL);
            BPaint.setAntiAlias(true);
            Paint WPaint = new Paint();
            WPaint.setColor(Color.WHITE);
            WPaint.setStyle(Paint.Style.FILL);
            WPaint.setAntiAlias(true);
            int hcellWidth = cellWidth / 2;
            int R = (int) (hcellWidth * psR);
            int startXp = codeRect.left + hcellWidth;
            int startYp = codeRect.top + hcellWidth;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            for (int x = startXp; x <= codeRect.right; x += cellWidth) {
                for (int y = startYp; y <= codeRect.bottom; y += cellWidth) {
                    int brL = x - hcellWidth;
                    int brT = y - hcellWidth;
                    int brR = x + hcellWidth;
                    int brB = y + hcellWidth;
                    int pX = x - cellWidth;
                    int nX = x + cellWidth;
                    int pY = y - cellWidth;
                    int nY = y + cellWidth;
                    boolean l = false;
                    boolean t = false;
                    boolean r = false;
                    boolean b = false;
                    if (pX >= codeRect.left) {
                        l = matrix.get(pX, y);
                    }
                    if (pY >= codeRect.top) {
                        t = matrix.get(x, pY);
                    }
                    if (nX <= codeRect.right) {
                        r = matrix.get(nX, y);
                    }
                    if (nY <= codeRect.bottom) {
                        b = matrix.get(x, nY);
                    }
                    if (matrix.get(x, y)) {
                        boolean tl = false;
                        boolean tr = false;
                        boolean br = false;
                        boolean bl = false;
                        if (pX >= codeRect.left && pY >= codeRect.top) {
                            tl = matrix.get(pX, pY);
                        }
                        if (nX <= codeRect.right && pY >= codeRect.top) {
                            tr = matrix.get(nX, pY);
                        }
                        if (nX <= codeRect.right && nY <= codeRect.bottom) {
                            br = matrix.get(nX, nY);
                        }
                        if (pX >= codeRect.left && nY <= codeRect.bottom) {
                            bl = matrix.get(pX, nY);
                        }
                        Path path = new Path();
                        if (!tl && !t && !l) {
                            path.moveTo(brL, brT + R);
                            path.arcTo(new RectF(brL, brT, brL + 2 * R, brT + 2 * R), -180, 90, false);
                        } else {
                            path.moveTo(brL, brT);
                        }
                        if (!tr && !t && !r) {
                            path.lineTo(brR - R, brT);
                            path.arcTo(new RectF(brR - 2 * R, brT, brR, brT + 2 * R), -90, 90, false);
                        } else {
                            path.lineTo(brR, brT);
                        }
                        if (!br && !b && !r) {
                            path.lineTo(brR, brB - R);
                            path.arcTo(new RectF(brR - 2 * R, brB - 2 * R, brR, brB), 0, 90, false);
                        } else {
                            path.lineTo(brR, brB);
                        }
                        if (!bl && !b && !l) {
                            path.lineTo(brL + R, brB);
                            path.arcTo(new RectF(brL, brB - 2 * R, brL + 2 * R, brB), 90, 90, false);
                        } else {
                            path.lineTo(brL, brB);
                        }
                        path.close();
                        canvas.drawPath(path, BPaint);
                    } else {
                        if (t && l) {
                            Path path = new Path();
                            path.moveTo(brL, brT + R);
                            path.lineTo(brL, brT);
                            path.lineTo(brL + R, brT);
                            path.arcTo(new RectF(brL, brT, brL + 2 * R, brT + 2 * R), -90, -90, false);
                            path.close();
                            canvas.drawPath(path, BPaint);
                        }
                        if (t && r) {
                            Path path = new Path();
                            path.moveTo(brR - R, brT);
                            path.lineTo(brR, brT);
                            path.lineTo(brR, brT + R);
                            path.arcTo(new RectF(brR - 2 * R, brT, brR, brT + 2 * R), 0, -90, false);
                            path.close();
                            canvas.drawPath(path, BPaint);
                        }
                        if (b && r) {
                            Path path = new Path();
                            path.moveTo(brR, brB - R);
                            path.lineTo(brR, brB);
                            path.lineTo(brR - R, brB);
                            path.arcTo(new RectF(brR - 2 * R, brB - 2 * R, brR, brB), 90, -90, false);
                            path.close();
                            canvas.drawPath(path, BPaint);
                        }
                        if (b && l) {
                            Path path = new Path();
                            path.moveTo(brL + R, brB);
                            path.lineTo(brL, brB);
                            path.lineTo(brL, brB - R);
                            path.arcTo(new RectF(brL, brB - 2 * R, brL + 2 * R, brB), 180, -90, false);
                            path.close();
                            canvas.drawPath(path, BPaint);
                        }
                    }
                }
            }
            return bitmap;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 附加icon
     *
     * @param QRCode
     * @param icon
     * @param scale
     * @return
     */
    public static Bitmap withIcon(Bitmap QRCode, Bitmap icon, float scale) {
        Bitmap bitmap = null;
        try {
            bitmap = QRCode;
            Canvas canvas = new Canvas(bitmap);

            int width = bitmap.getWidth();
            int heigth = bitmap.getHeight();
            int iwidth = icon.getWidth();
            int iheigth = icon.getHeight();

            Rect src = new Rect();
            src.left = 0;
            src.top = 0;
            src.right = iwidth;
            src.bottom = iheigth;

            float aIWidth = width * scale;
            float aIHeigth = heigth * scale;

            RectF dst = new RectF();
            dst.left = (width - aIWidth) / 2;
            dst.top = (heigth - aIHeigth) / 2;
            dst.right = dst.left + aIWidth;
            dst.bottom = dst.top + aIHeigth;
            Paint paint = new Paint();
            canvas.drawBitmap(icon, src, dst, paint);
        } catch (Exception e) {

        }

        return bitmap;
    }
}

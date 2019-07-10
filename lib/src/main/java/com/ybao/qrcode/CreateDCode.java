package com.ybao.qrcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * author：Ybao
 * QQ:392579823
 * 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
 */
public class CreateDCode {

    /*
     * 条形码
     */
    public static Bitmap CreateOneDCode(String content, int[] sizes, int[] colors) throws Exception {
        int outputWidth = sizes[0], outputHeight = sizes[1];
        int qrColor = colors[0], bgColor = colors[1];
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, outputWidth, outputHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = qrColor;
                } else {
                    pixels[y * width + x] = bgColor;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, outputWidth, 0, 0, outputWidth, outputHeight);
        return bitmap;
    }

    /*
     * 普通二维码
     */
    public static Bitmap CreateQRCode(String content, int[] sizes, int[] colors) throws Exception {
        int outputWidth = sizes[0], outputHeight = sizes[1];
        int qrColor = colors[0], bgColor = colors[1];

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, outputWidth, outputHeight, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = qrColor;
                } else {
                    pixels[y * width + x] = bgColor;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, outputWidth, 0, 0, outputWidth, outputHeight);
        return bitmap;
    }

    /**
     * 圆点二维码
     *
     * @param content
     * @param sizes
     * @return
     */
    public static Bitmap CreateQRCodeDot(String content, float psRandom, int[] sizes, int[] colors) throws Exception {
        int outputWidth = sizes[0], outputHeight = sizes[1];
        int qrColor = colors[0], bgColor = colors[1];

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        QRCode qrCode = Encoder.encode(content, ErrorCorrectionLevel.H, hints);

        ByteMatrix matrix = qrCode.getMatrix();
        int originalWidth = matrix.getWidth();
        int originalHeight = matrix.getHeight();

        outputWidth = Math.max(originalWidth, outputWidth);
        outputHeight = Math.max(originalHeight, outputHeight);

        int cellWidth = Math.min(outputWidth / originalWidth, outputHeight / originalHeight);
        double randomRange = 0.25 * psRandom;
        outputWidth = (int) (outputWidth + cellWidth * randomRange);
        outputHeight = (int) (outputWidth + cellWidth * randomRange);

        int outputLeft = (outputWidth - cellWidth * originalWidth) / 2;
        int outputTop = (outputHeight - cellWidth * originalHeight) / 2;

        int cellMid = cellWidth / 2;

        Paint paint = new Paint();
        paint.setColor(qrColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(bgColor);
        Canvas canvas = new Canvas(bitmap);

        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                int outputY = outputTop + y * cellWidth + cellMid;
                int outputX = outputLeft + x * cellWidth + cellMid;
                if (matrix.get(x, y) == 1) {
                    int r;
                    if (randomRange == 0 || (x < 7 && y < 7) || (x >= originalWidth - 7 && y < 7) || (x < 7 && y >= originalHeight - 7)) {
                        r = cellMid;
                    } else {
                        r = (int) ((1 + (Math.random() - 0.5) * randomRange * 2) * cellMid);
                    }
                    canvas.drawCircle(outputX, outputY, r, paint);
                }
            }
        }
        return bitmap;
    }

    /**
     * 多边形二维码
     *
     * @param content
     * @param sizes
     * @param lCount
     * @return
     */

    public static Bitmap CreateQRCodePolygon(String content, int lCount, int[] sizes, int[] colors) throws Exception {
        if (lCount < 3) {
            lCount = 3;
        }

        int outputWidth = sizes[0], outputHeight = sizes[1];
        int qrColor = colors[0], bgColor = colors[1];

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        QRCode qrCode = Encoder.encode(content, ErrorCorrectionLevel.H, hints);

        ByteMatrix matrix = qrCode.getMatrix();
        int originalWidth = matrix.getWidth();
        int originalHeight = matrix.getHeight();

        outputWidth = Math.max(originalWidth, outputWidth);
        outputHeight = Math.max(originalHeight, outputHeight);

        int cellWidth = Math.min(outputWidth / originalWidth, outputHeight / originalHeight);

        int outputLeft = (outputWidth - cellWidth * originalWidth) / 2;
        int outputTop = (outputHeight - cellWidth * originalHeight) / 2;

        int cellMid = cellWidth / 2;

        double pr = 2 * Math.PI / lCount;
        double pr90 = 2 * Math.PI / 4;

        Paint paint = new Paint();
        paint.setColor(qrColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(bgColor);
        Canvas canvas = new Canvas(bitmap);

        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                if (matrix.get(x, y) == 1) {
                    int outputY = outputTop + y * cellWidth;
                    int outputX = outputLeft + x * cellWidth;
                    if ((x < 7 && y < 7) || (x >= originalWidth - 7 && y < 7) || (x < 7 && y >= originalHeight - 7)) {
                        canvas.drawRect(outputX, outputY, outputX + cellWidth, outputY + cellWidth, paint);
                    } else {
                        Path path = new Path();
                        path.moveTo(outputX + cellMid, outputY);
                        for (int indexL = 1; indexL < lCount; indexL++) {
                            path.lineTo((int) (outputX + cellMid + Math.cos(indexL * pr - pr90) * cellMid), (int) (outputY + cellMid + Math.sin(indexL * pr - pr90) * cellMid));
                        }
                        path.close();
                        canvas.drawPath(path, paint);
                    }
                }
            }
        }
        return bitmap;
    }

    /**
     * n角星二维码
     *
     * @param content
     * @param sizes
     * @return
     */
    public static Bitmap CreateQRCodeStar(String content, int lCount, int[] sizes, int[] colors) throws Exception {
        if (lCount < 3) {
            lCount = 3;
        }

        int outputWidth = sizes[0], outputHeight = sizes[1];
        int qrColor = colors[0], bgColor = colors[1];

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        QRCode qrCode = Encoder.encode(content, ErrorCorrectionLevel.H, hints);

        ByteMatrix matrix = qrCode.getMatrix();
        int originalWidth = matrix.getWidth();
        int originalHeight = matrix.getHeight();

        outputWidth = Math.max(originalWidth, outputWidth);
        outputHeight = Math.max(originalHeight, outputHeight);

        int cellWidth = Math.min(outputWidth / originalWidth, outputHeight / originalHeight);

        int outputLeft = (outputWidth - cellWidth * originalWidth) / 2;
        int outputTop = (outputHeight - cellWidth * originalHeight) / 2;

        int cellMid = cellWidth / 2;
        int cellFour = cellWidth / 4;

        double pr = 2 * Math.PI / lCount;
        double pr90 = Math.PI / 2;

        Paint paint = new Paint();
        paint.setColor(qrColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(bgColor);
        Canvas canvas = new Canvas(bitmap);

        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                if (matrix.get(x, y) == 1) {
                    int outputY = outputTop + y * cellWidth;
                    int outputX = outputLeft + x * cellWidth;
                    if ((x < 7 && y < 7) || (x >= originalWidth - 7 && y < 7) || (x < 7 && y >= originalHeight - 7)) {
                        canvas.drawRect(outputX, outputY, outputX + cellWidth, outputY + cellWidth, paint);
                    } else {
                        Path path = new Path();
                        double r1;
                        double r2 = pr / 2 - pr90;
                        path.moveTo(outputX + cellMid, outputY);
                        path.lineTo((int) (outputX + cellMid + Math.cos(r2) * cellFour), (int) (outputY + cellMid + Math.sin(r2) * cellFour));
                        for (int indexL = 1; indexL < lCount; indexL++) {
                            r1 = indexL * pr - pr90;
                            path.lineTo((int) (outputX + cellMid + Math.cos(r1) * cellMid), (int) (outputY + cellMid + Math.sin(r1) * cellMid));
                            r2 = indexL * pr + pr / 2 - pr90;
                            path.lineTo((int) (outputX + cellMid + Math.cos(r2) * cellFour), (int) (outputY + cellMid + Math.sin(r2) * cellFour));
                        }
                        path.close();
                        canvas.drawPath(path, paint);
                    }
                }
            }
        }
        return bitmap;
    }

    /**
     * 顶角平滑
     *
     * @param content
     * @param sizes
     * @param psR
     * @return
     */
    public static Bitmap CreateQRCodeSmooth(String content, float psR, int[] sizes, int[] colors) throws Exception {
        int outputWidth = sizes[0], outputHeight = sizes[1];
        int qrColor = colors[0], bgColor = colors[1];

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        QRCode qrCode = Encoder.encode(content, ErrorCorrectionLevel.H, hints);

        ByteMatrix matrix = qrCode.getMatrix();
        int originalWidth = matrix.getWidth();
        int originalHeight = matrix.getHeight();

        outputWidth = Math.max(originalWidth, outputWidth);
        outputHeight = Math.max(originalHeight, outputHeight);

        int cellWidth = Math.min(outputWidth / originalWidth, outputHeight / originalHeight);

        int outputLeft = (outputWidth - cellWidth * originalWidth) / 2;
        int outputTop = (outputHeight - cellWidth * originalHeight) / 2;

        int cellMid = cellWidth / 2;

        int R = (int) (cellMid * psR);

        Paint qrPaint = new Paint();
        qrPaint.setColor(qrColor);
        qrPaint.setStyle(Paint.Style.FILL);
        qrPaint.setAntiAlias(true);

        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(bgColor);
        Canvas canvas = new Canvas(bitmap);
        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                int pX = x - 1;
                int nX = x + 1;
                int pY = y - 1;
                int nY = y + 1;
                int brL = outputLeft + x * cellWidth;
                int brT = outputTop + y * cellWidth;
                int brR = brL + cellWidth;
                int brB = brT + cellWidth;
                boolean l = pX >= 0 && matrix.get(pX, y) == 1;
                boolean t = pY >= 0 && matrix.get(x, pY) == 1;
                boolean r = nX < originalWidth && matrix.get(nX, y) == 1;
                boolean b = nY < originalHeight && matrix.get(x, nY) == 1;
                if (matrix.get(x, y) == 1) {
                    boolean tl = !(t || l || (pX >= 0 && pY >= 0 && matrix.get(pX, pY) == 1));
                    boolean tr = !(t || r || (nX < originalWidth && pY >= 0 && matrix.get(nX, pY) == 1));
                    boolean br = !(b || r || (nX < originalWidth && nY < originalHeight && matrix.get(nX, nY) == 1));
                    boolean bl = !(b || l || (pX >= 0 && nY < originalHeight && matrix.get(pX, nY) == 1));
                    Path path = new Path();
                    if (tl) {
                        path.moveTo(brL, brT + R);
                        path.arcTo(new RectF(brL, brT, brL + 2 * R, brT + 2 * R), -180, 90, false);
                    } else {
                        path.moveTo(brL, brT);
                    }
                    if (tr) {
                        path.lineTo(brR - R, brT);
                        path.arcTo(new RectF(brR - 2 * R, brT, brR, brT + 2 * R), -90, 90, false);
                    } else {
                        path.lineTo(brR, brT);
                    }
                    if (br) {
                        path.lineTo(brR, brB - R);
                        path.arcTo(new RectF(brR - 2 * R, brB - 2 * R, brR, brB), 0, 90, false);
                    } else {
                        path.lineTo(brR, brB);
                    }
                    if (bl) {
                        path.lineTo(brL + R, brB);
                        path.arcTo(new RectF(brL, brB - 2 * R, brL + 2 * R, brB), 90, 90, false);
                    } else {
                        path.lineTo(brL, brB);
                    }
                    path.close();
                    canvas.drawPath(path, qrPaint);
                } else {
                    if (t && l) {
                        Path path = new Path();
                        path.moveTo(brL, brT + R);
                        path.lineTo(brL, brT);
                        path.lineTo(brL + R, brT);
                        path.arcTo(new RectF(brL, brT, brL + 2 * R, brT + 2 * R), -90, -90, false);
                        path.close();
                        canvas.drawPath(path, qrPaint);
                    }
                    if (t && r) {
                        Path path = new Path();
                        path.moveTo(brR - R, brT);
                        path.lineTo(brR, brT);
                        path.lineTo(brR, brT + R);
                        path.arcTo(new RectF(brR - 2 * R, brT, brR, brT + 2 * R), 0, -90, false);
                        path.close();
                        canvas.drawPath(path, qrPaint);
                    }
                    if (b && r) {
                        Path path = new Path();
                        path.moveTo(brR, brB - R);
                        path.lineTo(brR, brB);
                        path.lineTo(brR - R, brB);
                        path.arcTo(new RectF(brR - 2 * R, brB - 2 * R, brR, brB), 90, -90, false);
                        path.close();
                        canvas.drawPath(path, qrPaint);
                    }
                    if (b && l) {
                        Path path = new Path();
                        path.moveTo(brL + R, brB);
                        path.lineTo(brL, brB);
                        path.lineTo(brL, brB - R);
                        path.arcTo(new RectF(brL, brB - 2 * R, brL + 2 * R, brB), 180, -90, false);
                        path.close();
                        canvas.drawPath(path, qrPaint);
                    }
                }
            }
        }
        return bitmap;
    }

    /**
     * 图像二维码
     *
     * @param content
     * @param sizes
     * @return
     */
    public static Bitmap CreateQRCodeBitmap(String content, int[] sizes, Bitmap[] bitmaps, int bgColor) throws Exception {
        int outputWidth = sizes[0], outputHeight = sizes[1];
        int count = bitmaps.length;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        QRCode qrCode = Encoder.encode(content, ErrorCorrectionLevel.H, hints);

        ByteMatrix matrix = qrCode.getMatrix();
        int originalWidth = matrix.getWidth();
        int originalHeight = matrix.getHeight();

        outputWidth = Math.max(originalWidth, outputWidth);
        outputHeight = Math.max(originalHeight, outputHeight);

        int cellWidth = Math.min(outputWidth / originalWidth, outputHeight / originalHeight);

        int outputLeft = (outputWidth - cellWidth * originalWidth) / 2;
        int outputTop = (outputHeight - cellWidth * originalHeight) / 2;

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(bgColor);
        Canvas canvas = new Canvas(bitmap);

        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                if (matrix.get(x, y) == 1) {
                    int outputY = outputTop + y * cellWidth;
                    int outputX = outputLeft + x * cellWidth;
                    Bitmap bm;
                    if (count == 1) {
                        bm = bitmaps[0];
                    } else {
                        int i = (int) (Math.random() * count);
                        if (i >= count) {
                            i = count - 1;
                        }
                        bm = bitmaps[i];
                    }
                    Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
                    RectF rectf = new RectF(outputX, outputY, outputX + cellWidth, outputY + cellWidth);
                    canvas.drawBitmap(bm, rect, rectf, paint);
                }
            }
        }
        return bitmap;
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

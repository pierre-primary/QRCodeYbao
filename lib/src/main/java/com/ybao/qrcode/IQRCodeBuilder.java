package com.ybao.qrcode;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

interface IQRCodeBuilder {
    IQRCodeBuilder size(int size);

    IQRCodeBuilder size(int width, int height);

    IQRCodeBuilder padding(int padding);

    IQRCodeBuilder padding(int horizontal, int vertical);

    IQRCodeBuilder characterSet(String characterSet);

    IQRCodeBuilder errorCorrectionLevel(ErrorCorrectionLevel level);

    IQRCodeBuilder withIcon(Bitmap icon);

    IQRCodeBuilder withIcon(int id);

    IQRCodeBuilder withIcon(Drawable drawable);

    Bitmap build(String content) throws WriterException;
}

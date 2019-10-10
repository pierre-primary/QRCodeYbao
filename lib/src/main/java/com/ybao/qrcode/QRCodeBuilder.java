package com.ybao.qrcode;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.HashMap;
import java.util.Map;

public class QRCodeBuilder implements IQRCodeBuilder {
    private static String DEF_CHARACTER_SET = "utf-8";
    private int width, height;
    private int paddingHorizontal, paddingVertical;
    private String characterSet;
    private ErrorCorrectionLevel level = ErrorCorrectionLevel.M;
    private int iconId;
    private Drawable iconDrawable;
    private Bitmap icon;

    @Override
    public IQRCodeBuilder size(int size) {
        this.width = size;
        this.height = size;
        return this;
    }

    @Override
    public IQRCodeBuilder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public IQRCodeBuilder padding(int padding) {
        this.paddingHorizontal = padding;
        this.paddingVertical = padding;
        return this;
    }

    @Override
    public IQRCodeBuilder padding(int paddingHorizontal, int paddingVertical) {
        this.paddingHorizontal = paddingHorizontal;
        this.paddingVertical = paddingVertical;
        return this;
    }

    @Override
    public IQRCodeBuilder characterSet(String characterSet) {
        this.characterSet = characterSet;
        return this;
    }

    @Override
    public IQRCodeBuilder errorCorrectionLevel(ErrorCorrectionLevel level) {
        this.level = level;
        return this;
    }

    @Override
    public IQRCodeBuilder withIcon(Bitmap icon) {
        this.icon = icon;
        this.iconId = 0;
        this.iconDrawable = null;
        return this;
    }

    @Override
    public IQRCodeBuilder withIcon(int id) {
        this.icon = null;
        this.iconId = id;
        this.iconDrawable = null;
        return this;
    }

    @Override
    public IQRCodeBuilder withIcon(Drawable drawable) {
        this.icon = null;
        this.iconId = 0;
        this.iconDrawable = drawable;
        return this;
    }

    @Override
    public Bitmap build(String content) throws WriterException {
        QRCode qrCode = CreateQRCode(content, level, characterSet == null || characterSet.length() <= 0 ? DEF_CHARACTER_SET : characterSet);
        return null;
    }

    private static QRCode CreateQRCode(String content, ErrorCorrectionLevel level, String characterSet) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, characterSet == null || characterSet.length() <= 0 ? "utf-8" : characterSet);
        return Encoder.encode(content, level, hints);
    }
}

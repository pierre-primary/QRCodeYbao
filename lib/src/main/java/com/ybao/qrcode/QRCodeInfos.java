package com.ybao.qrcode;

import android.graphics.Rect;

import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

public class QRCodeInfos {
    private QRCode qrCode;

    public QRCodeInfos(QRCode qrCode) {
        this.qrCode = qrCode;
    }

    public ByteMatrix getMatrix() {
        return this.qrCode.getMatrix();
    }

    public Rect[] getPositionPatternAreas() {
        return null;
    }

    public Rect[] getAliganmentPatternAreas() {
        return null;
    }

    public Rect[] getTimingPatternAreas() {
        return null;
    }

    public Rect[] getVersionInfoAreas() {
        return null;
    }

    public Rect[] getFormatInfoAreas() {
        return null;
    }

}

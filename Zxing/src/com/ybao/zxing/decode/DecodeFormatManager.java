/*
 * Copyright (C) 2010 ZXing authors
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

import com.google.zxing.BarcodeFormat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DecodeFormatManager {

    // 1D解码
    private static final Set<BarcodeFormat> PRODUCT_FORMATS;
    private static final Set<BarcodeFormat> ONE_D_FORMATS;
    private static final Set<BarcodeFormat> QR_CODE_FORMATS;
    private static final Set<BarcodeFormat> TWO_D_FORMATS;

    static {
        PRODUCT_FORMATS = new HashSet<BarcodeFormat>(5);
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);   // UPC标准码(通用商品)
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);   // UPC缩短码(商品短码)
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_EAN_EXTENSION);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
        PRODUCT_FORMATS.add(BarcodeFormat.RSS_14);
        PRODUCT_FORMATS.add(BarcodeFormat.RSS_EXPANDED);


        ONE_D_FORMATS = new HashSet<BarcodeFormat>(PRODUCT_FORMATS.size() + 4);
        ONE_D_FORMATS.addAll(PRODUCT_FORMATS);    //此处将PRODUCT_FORMATS中添加的码加入到ONE_D_FORMATS
        ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
        ONE_D_FORMATS.add(BarcodeFormat.ITF);
        ONE_D_FORMATS.add(BarcodeFormat.CODABAR);


        QR_CODE_FORMATS = new HashSet<BarcodeFormat>(1);
        QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);


        TWO_D_FORMATS = new HashSet<BarcodeFormat>(QR_CODE_FORMATS.size() + 3);
        TWO_D_FORMATS.addAll(QR_CODE_FORMATS);
        TWO_D_FORMATS.add(BarcodeFormat.AZTEC);//也属于一种二维码
        TWO_D_FORMATS.add(BarcodeFormat.PDF_417);//也属于一种二维码
        TWO_D_FORMATS.add(BarcodeFormat.DATA_MATRIX);//也属于一种二维码
    }


    public static Collection<BarcodeFormat> getQrCodeFormats() {
        return QR_CODE_FORMATS;
    }

    public static Collection<BarcodeFormat> getProductFormats() {
        return PRODUCT_FORMATS;
    }

    public static Collection<BarcodeFormat> getOneDFormats() {
        return ONE_D_FORMATS;
    }

    public static Collection<BarcodeFormat> getTwoDFormats() {
        return TWO_D_FORMATS;
    }


}

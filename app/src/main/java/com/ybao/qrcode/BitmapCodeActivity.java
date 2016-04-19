package com.ybao.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybao.zxing.CreateDCode;

public class BitmapCodeActivity extends AppCompatActivity {
    String cev = "作者：江鹏源; QQ:392579823";

    ImageView imageView;
    View bt;
    Bitmap[] bitmaps;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        imageView = (ImageView) findViewById(R.id.img_code);

        findViewById(R.id.tt).setVisibility(View.GONE);
        bt = findViewById(R.id.bt);
        bitmaps = new Bitmap[7];
        bitmaps[0] = Utils.readBitMap(this, R.mipmap.deg);
        bitmaps[1] = Utils.readBitMap(this, R.mipmap.ebo);
        bitmaps[2] = Utils.readBitMap(this, R.mipmap.ecn);
        bitmaps[3] = Utils.readBitMap(this, R.mipmap.eco);
        bitmaps[4] = Utils.readBitMap(this, R.mipmap.eep);
        bitmaps[5] = Utils.readBitMap(this, R.mipmap.eer);
        bitmaps[6] = Utils.readBitMap(this, R.mipmap.eft);
        bm= Utils.readBitMap(this, R.mipmap.kys);
        imageView.setImageBitmap(CreateDCode.CreateQRCodeBitmap(cev, 1000, bitmaps, bm));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = CreateDCode.CreateQRCodeBitmap(cev, 1000, bitmaps, bm);
                bitmap = CreateDCode.withIcon(bitmap, Utils.readBitMap(BitmapCodeActivity.this, R.mipmap.ic_launcher), 0.2f);
                imageView.setImageBitmap(bitmap);
            }
        });

    }
}

package com.ybao.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.ybao.zxing.CreateDCode;

public class RCodeActivity extends AppCompatActivity {
    String cev = "作者：江鹏源; QQ:392579823";

    ImageView imageView;
    View bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        imageView = (ImageView) findViewById(R.id.img_code);

        findViewById(R.id.tt).setVisibility(View.GONE);
        bt = findViewById(R.id.bt);
        imageView.setImageBitmap(CreateDCode.CreateQRCodeDot(cev, 1000));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = CreateDCode.CreateQRCodeDot(cev, 1000);
                bitmap = CreateDCode.withIcon(bitmap, Utils.readBitMap(RCodeActivity.this, R.mipmap.ic_launcher), 0.2f);
                imageView.setImageBitmap(bitmap);
            }
        });

    }
}

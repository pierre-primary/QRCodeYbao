package com.ybao.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybao.zxing.CreateDCode;

import java.io.InputStream;

public class STCodeActivity extends AppCompatActivity {
    String cev = "作者：江鹏源; QQ:392579823";

    ImageView imageView;
    View m;
    View a;
    TextView nemc;
    int nem = 3;
    View bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        imageView = (ImageView) findViewById(R.id.img_code);

        findViewById(R.id.tt).setVisibility(View.VISIBLE);
        m = findViewById(R.id.m);
        a = findViewById(R.id.a);
        nemc = (TextView) findViewById(R.id.nem);
        bt = findViewById(R.id.bt);
        nemc.setText(nem + "");
        ref();
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nem > 3) {
                    nem--;
                    nemc.setText(nem + "");
                    ref();
                }
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nem < 10) {
                    nem++;
                    nemc.setText(nem + "");
                    ref();
                }

            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = CreateDCode.CreateQRCodeStar(cev, 1000, nem);
                bitmap = CreateDCode.withIcon(bitmap, Utils.readBitMap(STCodeActivity.this, R.mipmap.ic_launcher), 0.2f);
                imageView.setImageBitmap(bitmap);
            }
        });

    }

    private void ref() {
        imageView.setImageBitmap(CreateDCode.CreateQRCodeStar(cev, 1000, nem));
    }
}

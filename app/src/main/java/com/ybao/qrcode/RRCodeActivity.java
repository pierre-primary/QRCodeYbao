package com.ybao.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybao.zxing.CreateDCode;

public class RRCodeActivity extends AppCompatActivity {
    String cev = "作者：江鹏源; QQ:392579823";

    ImageView imageView;
    View m;
    View a;
    TextView nemc;
    float nem = 0.1f;
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
                if (nem > 0.1) {
                    nem -= 0.1;
                    nemc.setText(nem + "");
                    ref();
                }
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nem < 1) {
                    nem += 0.1;
                    nemc.setText(nem + "");
                    ref();
                }

            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = CreateDCode.CreateQRCodeSmooth(cev, 1000, nem);
                bitmap = CreateDCode.withIcon(bitmap, Utils.readBitMap(RRCodeActivity.this, R.mipmap.ic_launcher), 0.2f);
                imageView.setImageBitmap(bitmap);
            }
        });

    }

    private void ref() {
        imageView.setImageBitmap(CreateDCode.CreateQRCodeSmooth(cev, 1000, nem));
    }
}

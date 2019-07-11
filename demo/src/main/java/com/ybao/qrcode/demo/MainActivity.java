package com.ybao.qrcode.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import com.ybao.qrcode.CreateDCode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CONTENT = "作者：江鹏源; QQ:392579823";
    private static final int SIZE = 1000;

    private static final int QR_COLOR = 0xff000000, BG_COLOR = 0x0;
    private static final int PADDING = 50;

    private Bitmap[] bitmaps;

    private int typeId = 0;
    private boolean isWithIcon = false;
    private int num = 0;

    private ImageView imageView;
    private RadioGroup radioGroup;
    private SeekBar seekBar;
    private Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.img_code);
        radioGroup = findViewById(R.id.radio_group);
        seekBar = findViewById(R.id.seekBar);
        switch1 = findViewById(R.id.switch1);

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            init(radioGroup.getCheckedRadioButtonId());
            update();
        });
        switch1.setOnCheckedChangeListener((_v, val) -> {
            isWithIcon = val;
            update();
        });
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        num = i;
                        update();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
        initImgs();
        init(radioGroup.getCheckedRadioButtonId());
        update();
    }

    public void init(int typeId) {
        this.typeId = typeId;
        switch (typeId) {
            case R.id.type_dot:
            case R.id.type_dot_puls:
            case R.id.type_polygon:
            case R.id.type_star:
            case R.id.type_smooth:
                seekBar.setEnabled(true);
                break;
            default:
                seekBar.setEnabled(false);
                break;
        }
    }

    public void update() {
        Bitmap bitmap = create();
        if (isWithIcon) {
            bitmap = withIcon(bitmap);
        }
        imageView.setImageBitmap(bitmap);
    }

    private Bitmap create() {
        try {
            switch (typeId) {
                case R.id.type_general:
                    return CreateDCode.CreateQRCode(CONTENT, new int[]{SIZE, SIZE}, new int[]{QR_COLOR, BG_COLOR},
                            PADDING);
                case R.id.type_dot:
                    float value = num / 100f;
                    return CreateDCode.CreateQRCodeDot(CONTENT, value, new int[]{SIZE, SIZE}, new int[]{QR_COLOR, BG_COLOR},
                            PADDING);
                case R.id.type_polygon:
                    value = num / 100f * (10 - 3) + 3;
                    return CreateDCode.CreateQRCodePolygon(CONTENT, (int) value, new int[]{SIZE, SIZE}, new int[]{QR_COLOR, BG_COLOR},
                            PADDING);
                case R.id.type_star:
                    value = num / 100f * (10 - 3) + 3;
                    return CreateDCode.CreateQRCodeStar(CONTENT, (int) value, new int[]{SIZE, SIZE}, new int[]{QR_COLOR, BG_COLOR},
                            PADDING);
                case R.id.type_smooth:
                    value = num / 100f;
                    return CreateDCode.CreateQRCodeSmooth(CONTENT, value, new int[]{SIZE, SIZE}, new int[]{QR_COLOR, BG_COLOR},
                            PADDING);
                case R.id.type_img:
                    return CreateDCode.CreateQRCodeBitmap(CONTENT, new int[]{1000, 1000}, bitmaps, BG_COLOR,
                            PADDING);
                case R.id.type_dot_puls:
                    value = num / 100f;
                    return CreateDCode.CreateQRCodeDotPlus(CONTENT, value, new int[]{SIZE, SIZE}, new int[]{QR_COLOR, BG_COLOR},
                            PADDING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap withIcon(Bitmap bitmap) {
        try {
            return CreateDCode.withIcon(bitmap, ReadBitMap(this, R.mipmap.ic_launcher), 0.2f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void initImgs() {
        List<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(ReadBitMap(this, R.mipmap.deg));
        bitmaps.add(ReadBitMap(this, R.mipmap.ebo));
        bitmaps.add(ReadBitMap(this, R.mipmap.ecn));
        bitmaps.add(ReadBitMap(this, R.mipmap.eco));
        bitmaps.add(ReadBitMap(this, R.mipmap.eep));
        bitmaps.add(ReadBitMap(this, R.mipmap.eer));
        bitmaps.add(ReadBitMap(this, R.mipmap.eft));
        bitmaps.add(ReadBitMap(this, R.mipmap.kys));
        this.bitmaps = bitmaps.toArray(new Bitmap[bitmaps.size()]);
    }

    private static Bitmap ReadBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}

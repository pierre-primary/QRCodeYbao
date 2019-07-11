package com.ybao.qrcode.demo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.ybao.qrcode.CreateDCode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val CONTENT: String = "作者：江鹏源; QQ:392579823"
        private const val SIZE: Int = 1000

        private const val QR_COLOR: Int = Color.BLACK
        private const val BG_COLOR: Int = Color.WHITE
        private const val PADDING: Int = 0

        private fun readBitMap(context: Context, resId: Int): Bitmap? {
            val opt = BitmapFactory.Options()
            opt.inPreferredConfig = Bitmap.Config.RGB_565
            opt.inPurgeable = true
            opt.inInputShareable = true

            val inputStream = context.resources.openRawResource(resId)
            return BitmapFactory.decodeStream(inputStream, null, opt)
        }
    }

    private var bitmaps: Array<Bitmap>? = null

    private var typeId = 0
    private var isWithIcon = false
    private var num = 0
    private var bitmap: Bitmap? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qrcodeContentEdt.setText(CONTENT)
        qrcodeContentEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                update()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        qrcodeTypeRGroup.setOnCheckedChangeListener { rv, _ ->
            init(rv.checkedRadioButtonId)
            update()
        }
        qrcodeWithIconSt.setOnCheckedChangeListener { _, value ->
            isWithIcon = value
            update()
        }
        qrcodeSeekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                        num = i
                        update()
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                    }
                }
        )
        initImgs()
        init(qrcodeTypeRGroup.checkedRadioButtonId)
        update()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermisionUtils.checkRequestResult(this, permissions, grantResults) { granted, failPermission ->
            if (granted) {
                saveQRCode()
            } else {
                if (failPermission == null || PermisionUtils.isDisableVerifyMultiple(this, failPermission)) {
                    Snackbar.make(this.window.decorView, "读写手机存储权限获取失败", Snackbar.LENGTH_INDEFINITE).setAction("去授权") { PermisionUtils.gotoApplicationDetails(this) }.show()
                } else {
                    Snackbar.make(this.window.decorView, "读写手机存储权限获取失败", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        PermisionUtils.verifyStorage(this, 0) { saveQRCode() }
        return super.onOptionsItemSelected(item)
    }

    private fun init(typeId: Int) {
        this.typeId = typeId
        when (typeId) {
            R.id.qrcodeTypeDotRBtn, R.id.qrcodeTypeDotPulsRBtn, R.id.qrcodeTypePolygonRBtn, R.id.qrcodeTypeStarRBtn, R.id.qrcodeTypeSmoothRBtn -> qrcodeSeekBar.isEnabled = true
            else -> qrcodeSeekBar.isEnabled = false
        }
        name = findViewById<RadioButton>(typeId).text.toString()
    }

    fun update() {
        var bitmap = create()
        if (isWithIcon && bitmap != null) {
            bitmap = withIcon(bitmap)
        }
        this.bitmap = bitmap
        qrcodeImg.setImageBitmap(this.bitmap)
    }

    private fun create(): Bitmap? {
        val content = qrcodeContentEdt.text.toString()
        try {
            when (typeId) {
                R.id.qrcodeTypeGeneralRBtn -> return CreateDCode.CreateQRCode(content, intArrayOf(SIZE, SIZE), intArrayOf(QR_COLOR, BG_COLOR),
                        PADDING)
                R.id.qrcodeTypeDotRBtn -> {
                    val value = num / 100f
                    return CreateDCode.CreateQRCodeDot(content, value, intArrayOf(SIZE, SIZE), intArrayOf(QR_COLOR, BG_COLOR),
                            PADDING)
                }
                R.id.qrcodeTypePolygonRBtn -> {
                    val value = num / 100f * (10 - 3) + 3
                    return CreateDCode.CreateQRCodePolygon(content, value.toInt(), intArrayOf(SIZE, SIZE), intArrayOf(QR_COLOR, BG_COLOR),
                            PADDING)
                }
                R.id.qrcodeTypeStarRBtn -> {
                    val value = num / 100f * (10 - 3) + 3
                    return CreateDCode.CreateQRCodeStar(content, value.toInt(), intArrayOf(SIZE, SIZE), intArrayOf(QR_COLOR, BG_COLOR),
                            PADDING)
                }
                R.id.qrcodeTypeSmoothRBtn -> {
                    val value = num / 100f
                    return CreateDCode.CreateQRCodeSmooth(content, value, intArrayOf(SIZE, SIZE), intArrayOf(QR_COLOR, BG_COLOR),
                            PADDING)
                }
                R.id.qrcodeTypeImgRBtn -> return CreateDCode.CreateQRCodeBitmap(content, intArrayOf(1000, 1000), bitmaps, BG_COLOR,
                        PADDING)
                R.id.qrcodeTypeDotPulsRBtn -> {
                    val value = num / 100f
                    return CreateDCode.CreateQRCodeDotPlus(content, value, intArrayOf(SIZE, SIZE), intArrayOf(QR_COLOR, BG_COLOR),
                            PADDING)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun withIcon(bitmap: Bitmap): Bitmap? {
        try {
            return CreateDCode.withIcon(bitmap, readBitMap(this, R.mipmap.ic_launcher), 0.2f)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun initImgs() {
        val bitmaps = ArrayList<Bitmap>()
        readBitMap(this, R.mipmap.deg)?.let { bitmaps.add(it) }
        readBitMap(this, R.mipmap.ebo)?.let { bitmaps.add(it) }
        readBitMap(this, R.mipmap.ecn)?.let { bitmaps.add(it) }
        readBitMap(this, R.mipmap.eco)?.let { bitmaps.add(it) }
        readBitMap(this, R.mipmap.eep)?.let { bitmaps.add(it) }
        readBitMap(this, R.mipmap.eer)?.let { bitmaps.add(it) }
        readBitMap(this, R.mipmap.eft)?.let { bitmaps.add(it) }
        readBitMap(this, R.mipmap.kys)?.let { bitmaps.add(it) }
        this.bitmaps = bitmaps.toTypedArray()
    }

    fun saveQRCode() {
        if (this.bitmap != null) {
            val path = Utils.saveBitmap(this, this.bitmap!!, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "${name}二维码", Bitmap.CompressFormat.PNG)
            Utils.updatePhoto(this, path)
            Snackbar.make(this.window.decorView, "成功保存二维码至：$path", Snackbar.LENGTH_LONG).show()
            return
        }
    }
}

package com.rain.framework

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.core.camera.CameraCallback
import com.core.camera.CameraView
import com.core.camera.option.Flash
import com.core.camera.option.Hdr
import com.core.camera.option.WhiteBalance
import com.core.camera.utils.CameraUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "Camera"
        val flashList = arrayListOf(Flash.OFF, Flash.ON, Flash.TORCH, Flash.AUTO)
        val wbList = arrayListOf(
            WhiteBalance.AUTO,
            WhiteBalance.CLOUDY,
            WhiteBalance.DAYLIGHT,
            WhiteBalance.FLUORESCENT,
            WhiteBalance.INCANDESCENT
        )
        val hdrList = arrayListOf(Hdr.OFF, Hdr.ON)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<CameraView>(R.id.camera).setLifecycleOwner(this)
    }

    override fun onContentChanged() {
        super.onContentChanged()

        val flashAdapter = ArrayAdapter<Flash>(
            this,
            android.R.layout.simple_spinner_item,
            flashList
        )

        val wbAdapter = ArrayAdapter<WhiteBalance>(
            this,
            android.R.layout.simple_spinner_item,
            wbList
        )

        val hdrAdaper = ArrayAdapter<Hdr>(
            this, android.R.layout.simple_spinner_item,
            hdrList
        )

        val btn = findViewById<Button>(R.id.btn_switch)
        btn.setOnClickListener {
            camera.switchCamera()
        }

        val btnWb = findViewById<Spinner>(R.id.btn_wb)
        btnWb.adapter = wbAdapter


        btnWb.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val wb = wbList[position]
                camera.whiteBalance = wb
            }
        }

        val btnHdr = findViewById<Spinner>(R.id.btn_hdr)
        btnHdr.adapter = hdrAdaper
        btnHdr.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val hdr = hdrList[position]
                camera.hdr = hdr
            }
        }

        val btnFlash = findViewById<Spinner>(R.id.btn_flash)
        btnFlash.adapter = flashAdapter
        btnFlash.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val flash = flashList[position]
                camera.flash = flash
            }
        }

        btn_capture.setOnClickListener {
            camera.takePicture {
                try {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 1

//                    val bitmap = BitmapFactory.decodeByteArray(it.data, 0, it.data.size, options)

                    val bitmap = CameraUtils.yuv2Image(it.data,ImageFormat.NV21,it.width,it.height)

                    val rotaBitmap = CameraUtils.rotaingImageView(this,bitmap,camera.facing)

                    val file = File(Environment.getExternalStorageDirectory().absolutePath+"/"+"test.jpg")
                    try {
                        val out = FileOutputStream(file)
                        if (rotaBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                            out.flush()
                            out.close()
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }


//                    val file = File(Environment.getExternalStorageDirectory().absolutePath +"/"+"test.jpg")
//                    file.createNewFile()
//                    val outPut = FileOutputStream(file)
//                    outPut.write(it.data)
//                    outPut.flush()
//                    outPut.close()
                } catch (e:Exception) {
                    Log.i("FILE",e.toString())
                } finally {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        camera.setCameraCallback { frame ->
            Log.i(TAG, "${frame!!.data.size},${frame.format},${frame.width},${frame.height}")
        }
    }
}

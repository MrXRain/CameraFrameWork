package com.rain.framework

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.core.camera.CameraCallback
import com.core.camera.CameraView
import com.core.camera.Frame
import com.core.camera.option.Facing
import com.core.camera.option.Flash
import com.core.camera.option.Hdr
import com.core.camera.option.WhiteBalance
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "Camera"
        val flashList = arrayListOf(Flash.OFF, Flash.ON, Flash.TORCH, Flash.AUTO)
        val wbList = arrayListOf(
            WhiteBalance.CLOUDY,
            WhiteBalance.DAYLIGHT,
            WhiteBalance.FLUORESCENT,
            WhiteBalance.INCANDESCENT,
            WhiteBalance.AUTO
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

    }

    override fun onResume() {
        super.onResume()

        camera.setCameraCallback { frame ->
            Log.i(TAG, "${frame!!.data.size},${frame.format},${frame.width},${frame.height}")
        }
    }
}

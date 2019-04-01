package com.rain.framework

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.core.camera.CameraCallback
import com.core.camera.CameraView
import com.core.camera.Frame
import com.core.camera.option.Facing
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "Camera"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<CameraView>(R.id.camera).setLifecycleOwner(this)
    }

    override fun onContentChanged() {
        super.onContentChanged()

        val btn = findViewById<Button>(R.id.btn_switch)
        btn.setOnClickListener {
            camera.switchCamera()
        }
    }

    override fun onResume() {
        super.onResume()

        camera.setCameraCallback { frame ->
            Log.i(TAG,"${frame!!.data.size},${frame.format},${frame.width},${frame.height}") }
    }
}

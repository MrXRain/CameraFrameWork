package com.terminus.framework

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.core.camera.CameraView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<CameraView>(R.id.camera).setLifecycleOwner(this)
    }
}

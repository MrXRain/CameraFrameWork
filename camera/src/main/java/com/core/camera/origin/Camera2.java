package com.core.camera.origin;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.WorkerThread;
import com.core.camera.CameraCallback;
import com.core.camera.CameraController;
import com.core.camera.option.*;

/**
 * @author rain
 * @date 2019/3/20
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2 extends CameraController {

    private CameraManager mCameraManager;
    private String mCameraId;

    public Camera2(Context context,CameraCallback callback) {
        super(context,callback);

        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    @WorkerThread
    protected void onStart() {

    }

    @Override
    protected void onPause() {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void setFacing(Facing facing) {

    }

    @Override
    protected void setWhiteBalance(WhiteBalance whiteBalance) {
        try {
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setHdr(Hdr hdr) {

    }

    @Override
    protected void setFlash(Flash flash) {
        try {
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setPreviewSize(PreviewSize previewSize) {

    }

    @Override
    public void onSurfaceAvailable() {

    }

    @Override
    public void onSurfaceChanged() {

    }
}

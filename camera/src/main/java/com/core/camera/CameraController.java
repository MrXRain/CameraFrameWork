package com.core.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.core.camera.option.*;
import com.core.camera.utils.Size;
import com.core.camera.utils.WorkHandler;

/**
 * @author rain
 * @date 2019/3/20
 */
public abstract class CameraController implements CameraPreview.PreviewCallback {

    protected CameraCallback mCameraCallback;
    protected WorkHandler mHandler;
    protected CameraPreview mPreview;
    protected Context mContext;
    protected final FrameManger mFrameManger;
    protected WhiteBalance mWhiteBalance;
    protected Hdr mHdr;
    protected Flash mFlash;
    protected PreviewSize mPreSize;
    protected Size mSize;
    protected Facing mFacing;

    protected CameraController(Context context) {
        mHandler = WorkHandler.get("CameraWork");
        mContext = context;
        mFrameManger = new FrameManger(2);
    }

    protected void setPreview(CameraPreview preview) {
        preview.setPreviewCallback(this);
        mPreview = preview;
    }

    final void startCamera() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onStart();
            }
        });
    }

    final void stopCamera() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onStop();
            }
        });
    }

    final void stopPreview() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onPause();
            }
        });
    }

    protected abstract void onStart();

    protected abstract void onPause();

    protected abstract void onStop();

    protected abstract void setFacing(Facing facing);

    protected abstract void setWhiteBalance(WhiteBalance whiteBalance);

    protected abstract void setHdr(Hdr hdr);

    protected abstract void setFlash(Flash flash);

    protected abstract void setPreviewSize(PreviewSize previewSize);

    final Facing getFacing() {
        return mFacing;
    }

    final WhiteBalance getWhiteBalance() {
        return mWhiteBalance;
    }

    final Hdr getHdr() {
        return mHdr;
    }

    final Flash getFlash() {
        return mFlash;
    }

    final PreviewSize getPreviewSize() {
        return mPreSize;
    }

    /**
     * 根据摄像头Id来设置屏幕旋转角度
     *
     * @param caremaId 摄像头Id
     * @return 旋转的角度
     */
    protected int getDisplayOrientation(int caremaId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(caremaId, info);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }

        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
}

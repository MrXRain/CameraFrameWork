package com.core.camera.origin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;
import android.widget.Toast;
import com.core.camera.*;
import com.core.camera.option.*;
import com.core.camera.utils.Size;
import com.rain.camera.R;

import java.io.IOException;

/**
 * @author rain
 * @date 2019/3/20
 */
public class Camera1 extends CameraController implements Camera.PreviewCallback {

    private Camera mCamera;

    private int mCameraID;

    private CameraOptions mCameraOptions;

    private Mapper.Mapper1 mapper1;

    private byte[] mPreBuffer;

    private boolean isOpen;

    private Camera.Parameters parameters;

    public Camera1(Context context) {
        super(context);

        mapper1 = new Mapper.Mapper1();
    }

    @Override
    @WorkerThread
    protected void onStart() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, R.string.camera_permission,Toast.LENGTH_SHORT).show();
                return;
            }

            if (mCamera == null) {
                mCamera = Camera.open(mCameraID);
            }

            parameters = mCamera.getParameters();
            mCameraOptions = new CameraOptions(parameters);

            applyCameraParams();

            isOpen = true;

            if (isCameraAvaliable()) {
                bindSurface();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyCameraParams() {
        if (mCameraOptions.getSupport(mWhiteBalance)) {
            String wbMode = mapper1.getWhiteBalance(mWhiteBalance);
            parameters.setWhiteBalance(wbMode);
        }

        if (mCameraOptions.getSupport(mHdr)) {
            String hdrMode = mapper1.getHdr(mHdr);
            parameters.setSceneMode(hdrMode);
        }

        if (mCameraOptions.getSupport(mFlash)) {
            String flashMode = mapper1.getFlash(mFlash);
            parameters.setFlashMode(flashMode);
        }

        if (mCameraOptions.getSupport(mPreSize)) {
            mSize = mapper1.getPreviewSize(mPreSize);
            parameters.setPreviewSize(mSize.getWidth(), mSize.getHeight());
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPictureFormat(ImageFormat.JPEG);

        mCamera.setParameters(parameters);
    }

    private boolean isCameraAvaliable() {
        return mPreview != null && mPreview.isReady() && isOpen && mCamera != null;
    }

    @Override
    @WorkerThread
    protected void onPause() {
        mCamera.setPreviewCallbackWithBuffer(null);
        mCamera.stopPreview();
        isOpen = false;
    }

    @Override
    @WorkerThread
    protected void onStop() {
        mCamera.setPreviewCallbackWithBuffer(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        isOpen = false;
    }

    @Override
    protected void setFacing(Facing facing) {
        mFacing = facing;
        mCameraID = facing.value();
    }

    @Override
    protected void setWhiteBalance(WhiteBalance whiteBalance) {
        mWhiteBalance = whiteBalance;

        if (isCameraAvaliable()) {
            applyCameraParams();
        }
    }

    @Override
    protected void setHdr(Hdr hdr) {
        mHdr = hdr;

        if (isCameraAvaliable()) {
            applyCameraParams();
        }
    }

    @Override
    protected void setFlash(Flash flash) {
        mFlash = flash;

        if (isCameraAvaliable()) {
            applyCameraParams();
        }
    }

    @Override
    protected void setPreviewSize(PreviewSize previewSize) {
        mPreSize = previewSize;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (mPreBuffer == null) {
            mPreBuffer = new byte[mSize.getWidth() * mSize.getHeight() * (ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
        }

        mCamera.addCallbackBuffer(mPreBuffer);

        Frame frame = mFrameManger.getframe(data, mSize.getWidth(), mSize.getHeight(), ImageFormat.NV21);
        if (mCameraCallback == null) {
            try {
                throw new Exception("cameraCallback must init");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mCameraCallback.dispathFrame(frame);
        }
    }

    @Override
    public void onSurfaceAvailable() {
        if (isCameraAvaliable()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    bindSurface();
                }
            });
        }
    }

    @WorkerThread
    private void bindSurface() {
        if (mPreview != null && mPreview.isReady()) {
            Object object = mPreview.getOut();
            try {
                if (object instanceof SurfaceHolder) {
                    mCamera.setPreviewDisplay((SurfaceHolder) object);
                } else if (object instanceof SurfaceTexture) {
                    mCamera.setPreviewTexture((SurfaceTexture) object);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mSize != null) {
                mPreBuffer = new byte[mSize.getWidth() * mSize.getHeight() * (ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
            }

            mCamera.addCallbackBuffer(mPreBuffer);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.setDisplayOrientation(getDisplayOrientation(mCameraID));
            mCamera.startPreview();
        }
    }

    @Override
    public void onSurfaceChanged() {
        mCamera.stopPreview();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                bindSurface();
            }
        });
    }
}

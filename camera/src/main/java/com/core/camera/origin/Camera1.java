package com.core.camera.origin;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.WorkerThread;
import android.view.SurfaceHolder;
import com.core.camera.*;
import com.core.camera.option.*;
import com.core.camera.utils.Size;

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

    private Size mSize;

    private byte[] mPreBuffer;

    private boolean isOpen;

    public Camera1(Context context, CameraCallback callback) {
        super(context, callback);

        mapper1 = new Mapper.Mapper1();
    }

    @Override
    @WorkerThread
    protected void onStart() {
        try {
            if (mCamera == null) {
                mCamera = Camera.open(mCameraID);
            }

            Camera.Parameters parameters = mCamera.getParameters();
            mCameraOptions = new CameraOptions(parameters, mCamera);

            applyCameraParams(parameters);

            isOpen = true;

            if (isCameraAvaliable()) {
                bindSurface();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyCameraParams(Camera.Parameters parameters) {
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
        mCameraID = facing.value();
    }

    @Override
    protected void setWhiteBalance(WhiteBalance whiteBalance) {
        mWhiteBalance = whiteBalance;

        if (isCameraAvaliable()) {
            Camera.Parameters parameters = mCamera.getParameters();

            if (mCameraOptions.getSupport(whiteBalance)) {
                String mWb = mapper1.getWhiteBalance(whiteBalance);
                parameters.setWhiteBalance(mWb);
            }
            mCamera.setParameters(parameters);
        }
    }

    @Override
    protected void setHdr(Hdr hdr) {
        mHdr = hdr;

        if (isCameraAvaliable()) {
            Camera.Parameters parameters = mCamera.getParameters();

            if (mCameraOptions.getSupport(hdr)) {
                String mHdr = mapper1.getHdr(hdr);
                parameters.setSceneMode(mHdr);
            }
            mCamera.setParameters(parameters);
        }
    }

    @Override
    protected void setFlash(Flash flash) {
        mFlash = flash;

        if (isCameraAvaliable()) {
            Camera.Parameters parameters = mCamera.getParameters();

            if (mCameraOptions.getSupport(flash)) {
                String mFlash = mapper1.getFlash(flash);
                parameters.setFlashMode(mFlash);
            }
            mCamera.setParameters(parameters);
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
        mCameraCallback.dispathFrame(frame);
    }

    @Override
    public void onSurfaceAvailable() {
        if (isCameraAvaliable()) {
            bindSurface();
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

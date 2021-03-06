package com.core.camera.origin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.ToneGenerator;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;
import com.core.camera.*;
import com.core.camera.option.*;
import com.core.camera.utils.CameraUtils;
import com.rain.camera.R;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rain
 * @date 2019/3/20
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2 extends CameraController {

    private CameraManager mCameraManager;

    private String mCameraId;

    /**
     * the byteArray of preview
     */
    private ImageReader mImageReader;

    private CameraCaptureSession mPreviewSession;

    private CameraDevice mCameraDevice;

    private boolean isCameraOpened;

    private CaptureRequest.Builder mPreviewBuilder;

    private CaptureRequest.Builder mCaptureBuilder;

    private CameraOptions mCameraOptions;

    private Mapper.Mapper2 mapper2;

    public Camera2(Context context) {
        super(context);

        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (mCameraManager != null) {
            mCameraOptions = new CameraOptions(mCameraManager);

        }
        mapper2 = new Mapper.Mapper2();
    }

    @Override
    @WorkerThread
    protected void onStart() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, R.string.camera_permission, Toast.LENGTH_SHORT).show();
                return;
            }

            mSize = mapper2.getPreviewSize(mPreSize);

            mCameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;
                    isCameraOpened = true;

                    if (isCameraAvaliable()) {
                        bindSurface();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    isCameraOpened = false;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    mCameraDevice = null;
                    isCameraOpened = false;
                }
            }, mHandler.getHandler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void bindSurface() {
        try {
            setImageReader();
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            List<Surface> surfaces = new ArrayList<>();
            Object object = mPreview.getOut();
            if (object instanceof SurfaceTexture) {
                SurfaceTexture texture = (SurfaceTexture) object;
                texture.setDefaultBufferSize(mSize.getWidth(), mSize.getHeight());
            }

            Surface textureSurface = mPreview.getSurface();
            Surface imageSurface = mImageReader.getSurface();
            surfaces.add(textureSurface);
            surfaces.add(imageSurface);
            mPreviewBuilder.addTarget(textureSurface);
            mPreviewBuilder.addTarget(imageSurface);

            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (mCameraDevice == null) {
                        return;
                    }

                    mPreviewSession = session;
                    applyCameraParams();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mHandler.getHandler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void applyCameraParams() {
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            if (mCameraOptions.getSupport(mWhiteBalance)) {
                int value = mapper2.getWhiteBalance(mWhiteBalance);
                mPreviewBuilder.set(CaptureRequest.CONTROL_AWB_MODE, value);
            }

            if (mCameraOptions.getSupport(mFlash)) {
                int value = mapper2.getFlash(mFlash);
                mPreviewBuilder.set(CaptureRequest.FLASH_MODE, value);
            }

            if (mCameraOptions.getSupport(mHdr)) {
                int value = mapper2.getHdr(mHdr);
                mPreviewBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, value);
            }

            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler.getHandler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setImageReader() {
        mImageReader = ImageReader.newInstance(mSize.getWidth(), mSize.getHeight(),
                ImageFormat.YUV_420_888, 2);

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();
                byte[] data = CameraUtils.YUV_420_888toNV21(image);

                Frame frame = mFrameManger.getframe(data, mSize.getWidth(), mSize.getHeight(), ImageFormat.NV21);
                if (mCameraCallback == null) {
                    throw new RuntimeException("cameraCallback must init");
                } else {
                    mCameraCallback.dispatchFrame(frame);
                }

                if (mJpegCallback != null && isTakePic) {
                    isTakePic = false;
                    mJpegCallback.dispatchPic(new Picture(data, mSize.getWidth(), mSize.getHeight()));
                }

                image.close();
            }
        }, mHandler.getHandler());


    }

    private boolean isCameraAvaliable() {
        return mPreview != null && mPreview.isReady() && isCameraOpened && mCameraDevice != null;
    }

    private byte[] getDataFromImage(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return bytes;
    }

    @Override
    protected void onPause() {
        stopCamera();
    }

    @Override
    protected void onStop() {
        stopCamera();
    }

    private void stopCamera() {
        if (mPreviewSession != null && mCameraDevice != null && mImageReader != null) {
            mCameraDevice.close();
            mPreviewSession.close();
            mImageReader.close();
            mImageReader = null;
            mPreviewSession = null;
            mCameraDevice = null;
        }
    }

    @Override
    protected void setFacing(Facing facing) {
        mFacing = facing;

        if (mCameraOptions.getSupport(facing)) {
            int value = mapper2.getFacing(facing);
            mCameraId = String.valueOf(value);

            mCameraOptions.initCameraParams(mCameraManager, mapper2, mCameraId);
        }
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

    private boolean isTakePic;
    @Override
    protected void takePicture() {
        try {
            isTakePic = true;
            mCaptureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mCaptureBuilder.addTarget(mImageReader.getSurface());
            mCaptureBuilder.set(CaptureRequest.CONTROL_AF_MODE,mPreviewBuilder.get(CaptureRequest.CONTROL_AF_MODE));
            mPreviewSession.stopRepeating();
            mTone.stop();
            mTone.play();
            mPreviewSession.capture(mCaptureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    try {
                        mPreviewSession.capture(mPreviewBuilder.build(),this,null);
                        mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler.getHandler());
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
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

    @Override
    public void onSurfaceChanged() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                bindSurface();
            }
        });
    }
}

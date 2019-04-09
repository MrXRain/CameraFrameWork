package com.core.camera.uvc;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.support.annotation.WorkerThread;
import android.view.SurfaceHolder;
import com.core.camera.*;
import com.core.camera.option.*;
import com.core.camera.utils.Size;
import com.serenegiant.usb.*;
import com.rain.camera.R;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author rain
 * @description UVC相机
 */
public class Camera3 extends CameraController {

    private USBMonitor mUSBMonitor;

    private UVCCamera mUVCCamera;

    private Context mContext;

    private CameraOptions mCameraOptions;

    private Mapper.Mapper3 mapper3;

    private Size mSize;

    private byte[] mBuffer;

    public Camera3(Context context) {
        super(context);

        mContext = context;
        mCameraOptions = new CameraOptions();
        mapper3 = new Mapper.Mapper3();
    }

    private boolean isCameraConnect;

    @Override
    @WorkerThread
    protected void onStart() {

        mUVCCamera = new UVCCamera();

        mUSBMonitor = new USBMonitor(mContext, new USBMonitor.OnDeviceConnectListener() {
            @Override
            public void onAttach(UsbDevice usbDevice) {
                List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(mContext, R.xml.device_filter);
                mUSBMonitor.requestPermission(mUSBMonitor.getDeviceList(filter.get(0)).get(0));
            }

            @Override
            public void onDettach(UsbDevice usbDevice) {

            }

            @Override
            public void onConnect(UsbDevice usbDevice, USBMonitor.UsbControlBlock usbControlBlock, boolean b) {
                if (!isCameraConnect) {
                    mUVCCamera.open(usbControlBlock);
                }
                isCameraConnect = true;
                if (isCameraAvaliable()) {
                    applyCameraParam();
                    bindSurface();
                }
            }

            @Override
            public void onDisconnect(UsbDevice usbDevice, USBMonitor.UsbControlBlock usbControlBlock) {
                mUVCCamera.close();
                isCameraConnect = false;
            }

            @Override
            public void onCancel(UsbDevice usbDevice) {

            }
        });

        mUSBMonitor.register();
    }

    private void applyCameraParam() {
        int value = mapper3.getWhiteBalance(mWhiteBalance);
        mUVCCamera.setWhiteBlance(value);

        mUVCCamera.setPreviewSize(mSize.getWidth(), mSize.getHeight(), UVCCamera.PIXEL_FORMAT_NV21);
        mBuffer = new byte[mSize.getWidth() * mSize.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
    }

    private boolean isCameraAvaliable() {
        return mPreview != null && mPreview.isReady() && isCameraConnect && mUVCCamera != null;
    }

    @Override
    protected void onPause() {
        mUVCCamera.stopPreview();
        mUSBMonitor.unregister();
    }

    @Override
    protected void onStop() {
        mUVCCamera.destroy();
        mUSBMonitor.destroy();
    }

    @Override
    protected void setFacing(Facing facing) {

    }

    @Override
    protected void setWhiteBalance(WhiteBalance whiteBalance) {
        mWhiteBalance = whiteBalance;

        if (isCameraAvaliable()) {
            applyCameraParam();
        }
    }

    @Override
    protected void setHdr(Hdr hdr) {
        mHdr = hdr;
    }

    @Override
    protected void setFlash(Flash flash) {
        mFlash = flash;
    }

    @Override
    protected void setPreviewSize(PreviewSize previewSize) {
        mSize = mapper3.getPreviewSize(previewSize);
    }

    @Override
    public void onSurfaceAvailable() {
        if (isCameraAvaliable()) {
            bindSurface();
        }
    }

    private void bindSurface() {
        Object object = mPreview.getOut();
        if (object instanceof SurfaceHolder) {
            mUVCCamera.setPreviewDisplay((SurfaceHolder) object);
        } else if (object instanceof SurfaceTexture) {
            mUVCCamera.setPreviewDisplay(mPreview.getSurface());
        }

        mUVCCamera.setPreviewSize(mSize.getWidth(), mSize.getHeight(), UVCCamera.PIXEL_FORMAT_NV21);
        mBuffer = new byte[mSize.getWidth() * mSize.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
        mUVCCamera.startPreview();

        mUVCCamera.setFrameCallback(new IFrameCallback() {
            @Override
            public void onFrame(ByteBuffer byteBuffer) {
                int n = byteBuffer.limit();
                if (mBuffer.length < n) {
                    mBuffer = new byte[n];
                }
                byteBuffer.get(mBuffer, 0, n);

                Frame frame = mFrameManger.getframe(mBuffer, mSize.getWidth(), mSize.getHeight(), ImageFormat.NV21);

                if (mCameraCallback != null) {
                    mCameraCallback.dispathFrame(frame);
                } else {
                    throw new RuntimeException("cameraCallback must init");
                }
            }
        }, UVCCamera.PIXEL_FORMAT_NV21);
    }

    @Override
    public void onSurfaceChanged() {
        mUVCCamera.stopPreview();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                bindSurface();
            }
        });
    }
}

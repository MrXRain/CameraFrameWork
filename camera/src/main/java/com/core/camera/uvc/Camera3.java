package com.core.camera.uvc;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.support.annotation.WorkerThread;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.core.camera.*;
import com.core.camera.option.*;
import com.serenegiant.usb.*;
import com.terminus.camera.R;

import java.nio.ByteBuffer;
import java.util.List;

/**
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

    protected Camera3(Context context, CameraCallback callback) {
        super(context, callback);

        mContext = context;
        mUVCCamera = new UVCCamera();
        mCameraOptions = new CameraOptions();
        mapper3 = new Mapper.Mapper3();
    }

    private boolean isCameraConnect;
    @Override
    @WorkerThread
    protected void onStart() {
        if (mUSBMonitor != null) {
            mUSBMonitor.register();
        }

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
                mUVCCamera.open(usbControlBlock);
                isCameraConnect = true;
                if (isCameraAvaliable()) {
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
    }

    private boolean isCameraAvaliable() {
        return mPreview != null && mPreview.isReady() && isCameraConnect;
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

    }

    @Override
    protected void setHdr(Hdr hdr) {

    }

    @Override
    protected void setFlash(Flash flash) {

    }

    @Override
    protected void setPreviewSize(PreviewSize previewSize) {
        if (mCameraOptions.getSupport(previewSize)) {
            mSize = mapper3.getPreviewSize(previewSize);
            mUVCCamera.setPreviewSize(mSize.width, mSize.height, UVCCamera.PIXEL_FORMAT_NV21);
            mBuffer = new byte[mSize.width * mSize.height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
        }

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

        mUVCCamera.startPreview();

        mUVCCamera.setFrameCallback(new IFrameCallback() {
            @Override
            public void onFrame(ByteBuffer byteBuffer) {
                int n = byteBuffer.limit();
                if (mBuffer.length < n) {
                    mBuffer = new byte[n];
                }
                byteBuffer.get(mBuffer, 0, n);

                Frame frame = mFrameManger.getframe(mBuffer, mSize.width, mSize.height, ImageFormat.NV21);
                mCameraCallback.dispathFrame(frame);
            }
        }, UVCCamera.PIXEL_FORMAT_NV21);
    }

    @Override
    public void onSurfaceChanged() {
        mUVCCamera.stopPreview();
        mUSBMonitor.unregister();
    }
}

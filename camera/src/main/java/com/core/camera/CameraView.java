package com.core.camera;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.core.camera.option.*;
import com.core.camera.origin.Camera1;
import com.core.camera.origin.Camera2;
import com.core.camera.preview.CameraSurfaceView;
import com.core.camera.preview.CameraTextureView;
import com.core.camera.utils.WorkHandler;
import com.core.camera.uvc.Camera3;
import com.terminus.camera.R;

/**
 * @author rain
 * @date 2019/3/19
 */
public class CameraView extends FrameLayout implements LifecycleObserver {

    private CameraController mCameraController;

    private CameraCallback mCameraCallback;

    private Context mContext;

    private CameraPreview mPreview;

    private Lifecycle mLifecycle;

    public CameraView(@NonNull Context context) {
        this(context, null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        init(context, attrs);
    }

    /**
     * init Param
     */
    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CameraView, 0, 0);
        CameraType type = CameraType.fromValue(ta.getInteger(R.styleable.CameraView_cameraApiType, CameraType.DEFAULT.value()));
        Facing facing = Facing.fromValue(ta.getInteger(R.styleable.CameraView_cameraFacing, Facing.DEFAULT.value()));
        WhiteBalance whiteBalance = WhiteBalance.fromValue(ta.getInteger(R.styleable.CameraView_cameraWhiteBalance, WhiteBalance.AUTO.value()));
        Hdr hdr = Hdr.fromValue(ta.getInteger(R.styleable.CameraView_cameraHdr, Hdr.DEFAULT.value()));
        Flash flash = Flash.fromValue(ta.getInteger(R.styleable.CameraView_cameraFlash, Flash.DEFAULT.value()));
        PreviewSize previewSize = PreviewSize.fromValue(ta.getInteger(R.styleable.CameraView_cameraPreviewSize, PreviewSize.DEFAULT.value()));

        ta.recycle();

        mCameraController = initCameraController(type);

        setFacing(facing);
        setWhiteBalance(whiteBalance);
        setHdr(hdr);
        setFlash(flash);
        setPreviewSize(previewSize);
    }

    private CameraController initCameraController(CameraType type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && type == CameraType.CAMERA_2) {
            mCameraController = new Camera2(mContext, mCameraCallback);
        } else if (type == CameraType.CAMERA_1){
            mCameraController = new Camera1(mContext, mCameraCallback);
        } else if (type == CameraType.CAMERA_3) {
            mCameraController = new Camera3(mContext, mCameraCallback);
        }

        return mCameraController;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mPreview == null) {
            initPreview();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        WorkHandler.destroy();
    }

    private void initPreview() {
        if (isHardwareAccelerated()) {
            mPreview = new CameraTextureView(getContext(), this);
        } else {
            mPreview = new CameraSurfaceView(getContext(), this);
        }

        mCameraController.setPreview(mPreview);
    }

    public void setCameraCallback(CameraCallback cameraCallback) {
        this.mCameraCallback = cameraCallback;
    }

    public void setLifecycleOwner(LifecycleOwner owner) {
        if (mLifecycle != null) mLifecycle.removeObserver(this);
        mLifecycle = owner.getLifecycle();
        mLifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void start() {
        if (!isEnabled()) return;

        mCameraController.startCamera();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stop() {
        mCameraController.stopPreview();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroy() {
        mCameraController.stopCamera();
    }

    /**
     * @param facing 摄像头的ID
     * @see Facing#FRONT
     * @see Facing#BACK
     */
    public void setFacing(Facing facing) {
        mCameraController.setFacing(facing);
    }

    /**
     * @param whiteBalance 白平衡模式
     * @see WhiteBalance#AUTO
     * @see WhiteBalance#CLOUDY
     * @see WhiteBalance#DAYLIGHT
     * @see WhiteBalance#FLUORESCENT
     * @see WhiteBalance#INCANDESCENT
     */
    public void setWhiteBalance(WhiteBalance whiteBalance) {
        mCameraController.setWhiteBalance(whiteBalance);
    }

    /**
     * @param hdr hdr模式
     * @see Hdr#OFF
     * @see Hdr#ON
     */
    public void setHdr(Hdr hdr) {
        mCameraController.setHdr(hdr);
    }

    /**
     * @param flash 闪光灯模式
     * @see Flash#OFF
     * @see Flash#ON
     * @see Flash#AUTO
     * @see Flash#TORCH
     */
    public void setFlash(Flash flash) {
        mCameraController.setFlash(flash);
    }

    /**
     * @param previewSize 预览尺寸
     * @see PreviewSize#V480P
     * @see PreviewSize#V720P
     * @see PreviewSize#V1080P
     */
    private void setPreviewSize(PreviewSize previewSize) {
        mCameraController.setPreviewSize(previewSize);
    }

}

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
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.core.camera.option.*;
import com.core.camera.origin.Camera1;
import com.core.camera.origin.Camera2;
import com.core.camera.preview.CameraSurfaceView;
import com.core.camera.preview.CameraTextureView;
import com.core.camera.utils.WorkHandler;
import com.core.camera.uvc.Camera3;
import com.rain.camera.R;

/**
 * @author rain
 * @date 2019/3/19
 */
public class CameraView extends FrameLayout implements LifecycleObserver {

    private CameraController mCameraController;

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
     * @param context the view
     * @param attrs A collection of attributes
     */
    private void init(@NonNull Context context,AttributeSet attrs) {
        setWillNotDraw(false);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CameraView, 0, 0);
        CameraType type = CameraType.fromValue(ta.getInteger(R.styleable.CameraView_cameraApiType, CameraType.DEFAULT.value()));
        Facing facing = Facing.fromValue(ta.getInteger(R.styleable.CameraView_cameraFacing, Facing.DEFAULT.value()));
        WhiteBalance whiteBalance = WhiteBalance.fromValue(ta.getInteger(R.styleable.CameraView_cameraWhiteBalance, WhiteBalance.DEFAULT.value()));
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
            mCameraController = new Camera2(mContext);
        } else if (type == CameraType.CAMERA_1) {
            mCameraController = new Camera1(mContext);
        } else if (type == CameraType.CAMERA_3) {
            mCameraController = new Camera3(mContext);
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

        mCameraController.mFrameManger.release();
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
        mCameraController.mCameraCallback = cameraCallback;
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
     * switch cameraID
     * It is used camera1、camera2
     */
    public void switchCamera() {
        if (mCameraController instanceof Camera3) {
            return;
        }

        Facing mCameraId = mCameraController.getFacing();
        switch (mCameraId) {
            case BACK:
                setFacing(Facing.FRONT);
                break;
            case FRONT:
                setFacing(Facing.BACK);
                break;
            default:
                break;
        }

        destroy();
        start();
    }

    /**
     * @param facing cameraId
     * @see Facing#FRONT
     * @see Facing#BACK
     */
    public void setFacing(Facing facing) {
        mCameraController.setFacing(facing);
    }

    /**
     * @return the cameraId
     * @see Facing
     */
    public Facing getFacing() {
        return mCameraController.getFacing();
    }

    /**
     * @param whiteBalance Whitebalance mode
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
     * @return the whiteBalance mode
     * @see WhiteBalance
     */
    public WhiteBalance getWhiteBalance() {
        return mCameraController.getWhiteBalance();
    }

    /**
     * @param hdr hdr mode
     * @see Hdr#OFF
     * @see Hdr#ON
     */
    public void setHdr(Hdr hdr) {
        mCameraController.setHdr(hdr);
    }

    /**
     * @return the hdr mode
     * @see Hdr
     */
    public Hdr getHdr() {
        return mCameraController.getHdr();
    }

    /**
     * @param flash Flash mode
     * @see Flash#OFF
     * @see Flash#ON
     * @see Flash#AUTO
     * @see Flash#TORCH
     */
    public void setFlash(Flash flash) {
        mCameraController.setFlash(flash);
    }

    /**
     * @return the flash mode
     * @see Flash
     */
    public Flash getFlash() {
        return mCameraController.getFlash();
    }

    /**
     * @param previewSize Preview Size
     * @see PreviewSize#V480P
     * @see PreviewSize#V720P
     * @see PreviewSize#V1080P
     */
    public void setPreviewSize(PreviewSize previewSize) {
        mCameraController.setPreviewSize(previewSize);
    }

    /**
     * @return the size of preview
     * @see PreviewSize
     */
    public PreviewSize getPreviewSize() {
        return mCameraController.getPreviewSize();
    }

}

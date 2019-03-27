package com.core.camera;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author rain
 * @param <T>
 * @param <Out>
 */
public abstract class CameraPreview<T extends View, Out> {

    private T mView;
    private PreviewCallback mCallback;
    private int mSurfaceWidth,mSurfaceHeight;

    interface PreviewCallback {
        void onSurfaceAvailable();

        void onSurfaceChanged();
    }

    public CameraPreview(Context context, ViewGroup parent) {
        mView = onCreateView(context, parent);
    }

    public void setPreviewCallback(PreviewCallback callback) {
        this.mCallback = callback;
    }

    protected void onSurfaceAvalible(int width,int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;

        mCallback.onSurfaceAvailable();
    }

    protected void onSurfaceChanged(int width,int height) {
        if (width != mSurfaceWidth || height != mSurfaceHeight) {
            mCallback.onSurfaceChanged();
        }
    }

    protected void onSurfaceDestroyed() {
        mSurfaceWidth = -1;
        mSurfaceHeight = -1;
    }

    public final boolean isReady() {
        return mSurfaceWidth > 0 && mSurfaceHeight > 0;
    }

    @NonNull
    protected abstract T onCreateView(Context context, ViewGroup parent);

    @NonNull
    public abstract Out getOut();

    @NonNull
    public abstract Surface getSurface();
}

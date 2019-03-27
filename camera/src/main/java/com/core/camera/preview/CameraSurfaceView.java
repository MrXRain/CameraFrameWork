package com.core.camera.preview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.*;
import com.core.camera.CameraPreview;
import com.terminus.camera.R;

public class CameraSurfaceView extends CameraPreview<SurfaceView, SurfaceHolder> {

    private SurfaceView mSurfaceView;
    private boolean isFirst = true;

    public CameraSurfaceView(Context context, ViewGroup parent) {
        super(context, parent);
    }

    @NonNull
    @Override
    protected SurfaceView onCreateView(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.cameraview_surface_view, parent, false);
        parent.addView(view, 0);
        mSurfaceView = view.findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (isFirst) {
                    onSurfaceAvalible(width, height);
                    isFirst = false;
                } else {
                    onSurfaceChanged(width, height);
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isFirst = true;
                onSurfaceDestroyed();
            }
        });

        return mSurfaceView;
    }

    @NonNull
    @Override
    public SurfaceHolder getOut() {
        return mSurfaceView.getHolder();
    }

    @NonNull
    @Override
    public Surface getSurface() {
        return getOut().getSurface();
    }
}

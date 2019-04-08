package com.core.camera.preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.*;
import com.core.camera.CameraPreview;
import com.rain.camera.R;

/**
 * @author rain
 * @date 2019/3/21
 */
public class CameraTextureView extends CameraPreview<TextureView, SurfaceTexture> {

    private TextureView mTextureView;

    public CameraTextureView(Context context, ViewGroup parent) {
        super(context, parent);
    }

    @NonNull
    @Override
    protected TextureView onCreateView(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.cameraview_texture_view, parent, false);
        parent.addView(view, 0);
        mTextureView = view.findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                onSurfaceAvalible(width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                onSurfaceChanged(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                onSurfaceDestroyed();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        return mTextureView;
    }

    @NonNull
    @Override
    public SurfaceTexture getOut() {
        return mTextureView.getSurfaceTexture();
    }

    @NonNull
    @Override
    public Surface getSurface() {
        return new Surface(getOut());
    }

    @NonNull
    @Override
    public TextureView getView() {
        return mTextureView;
    }


}

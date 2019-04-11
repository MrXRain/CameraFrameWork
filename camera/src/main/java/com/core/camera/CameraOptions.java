package com.core.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import com.core.camera.option.*;
import com.core.camera.utils.Size;

import java.util.*;

/**
 * @author rain
 * @date 2019/3/22
 */
public class CameraOptions {

    private Set<WhiteBalance> supportedWhiteBalance = new HashSet<>(5);
    private Set<Facing> supportedFacing = new HashSet<>(2);
    private Set<Flash> supportedFlash = new HashSet<>(4);
    private Set<Hdr> supportedHdr = new HashSet<>(2);
    private Set<PreviewSize> supportSize = new HashSet<>(3);

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraOptions(CameraManager cameraManager) {
        Mapper.Mapper2 mapper2 = new Mapper.Mapper2();

        try {
            // Facing
            String[] cameraNumbers = cameraManager.getCameraIdList();
            for (int i = 0; i < cameraNumbers.length; i++) {
                Facing value = mapper2.put(Facing.class, i);
                if (value != null) supportedFacing.add(value);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void initCameraParams(CameraManager cameraManager, Mapper.Mapper2 mapper2, String cameraId) {
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

            // PreviewSize
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map != null) {
                android.util.Size[] sizes = map.getOutputSizes(ImageFormat.JPEG);
                if (sizes != null) {
                    for (android.util.Size size : sizes) {
                        Size mSize = new Size(size.getWidth(), size.getHeight());
                        PreviewSize value = mapper2.put(PreviewSize.class, mSize);
                        if (value != null) supportSize.add(value);
                    }
                }
            }

            // Flash
            int[] flashs = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
            if (flashs != null) {
                for (int flash : flashs) {
                    Flash value = mapper2.put(Flash.class, flash);
                    if (value != null) supportedFlash.add(value);
                }
            }

            // whiteBalance
            int[] wbModes = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
            if (wbModes != null) {
                for (int mode : wbModes) {
                    WhiteBalance value = mapper2.put(WhiteBalance.class, mode);
                    if (value != null) supportedWhiteBalance.add(value);
                }
            }

            // Hdr
            int[] hdrModes = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
            if (hdrModes != null) {
                for (int mode : hdrModes) {
                    Hdr value = mapper2.put(Hdr.class, mode);
                    if (value != null) supportedHdr.add(value);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public CameraOptions() {
        Mapper.Mapper1 mapper = new Mapper.Mapper1();

        // Facing
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            Facing value = mapper.put(Facing.class, cameraInfo.facing);
            if (value != null) supportedFacing.add(value);
        }
    }

    public void initCameraParams(Camera.Parameters parameters, Mapper.Mapper1 mapper) {
        List<String> strings;

        // WB
        strings = parameters.getSupportedWhiteBalance();
        if (strings != null) {
            for (String string : strings) {
                WhiteBalance value = mapper.put(WhiteBalance.class, string);
                if (value != null) supportedWhiteBalance.add(value);
            }
        }

        // Flash
        strings = parameters.getSupportedFlashModes();
        if (strings != null) {
            for (String string : strings) {
                Flash value = mapper.put(Flash.class, string);
                if (value != null) supportedFlash.add(value);
            }
        }

        // Hdr
        strings = parameters.getSupportedSceneModes();
        if (strings != null) {
            for (String string : strings) {
                Hdr value = mapper.put(Hdr.class, string);
                if (value != null) supportedHdr.add(value);
            }
        }

        // PreviewSize
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        if (sizes != null) {
            for (Camera.Size size : sizes) {
                Size mSize = new Size(size.width, size.height);
                PreviewSize value = mapper.put(PreviewSize.class, mSize);
                if (value != null) supportSize.add(value);
            }
        }
    }

    public boolean getSupport(Control control) {
        return getSupportControl(control.getClass()).contains(control);
    }

    private <T extends Control> Collection<T> getSupportControl(@NonNull Class<T> tClass) {
        if (tClass.equals(WhiteBalance.class)) {
            return (Collection<T>) getSupportWhiteBalance();
        } else if (tClass.equals(Facing.class)) {
            return (Collection<T>) getSupportedFacing();
        } else if (tClass.equals(Flash.class)) {
            return (Collection<T>) getSupportFlash();
        } else if (tClass.equals(Hdr.class)) {
            return (Collection<T>) getSupportedHdr();
        } else if (tClass.equals(PreviewSize.class)) {
            return (Collection<T>) getSupportSize();
        }

        return Collections.emptyList();
    }

    @NonNull
    private Set<Flash> getSupportFlash() {
        return Collections.unmodifiableSet(supportedFlash);
    }

    @NonNull
    private Set<WhiteBalance> getSupportWhiteBalance() {
        return Collections.unmodifiableSet(supportedWhiteBalance);
    }

    @NonNull
    private Set<Facing> getSupportedFacing() {
        return Collections.unmodifiableSet(supportedFacing);
    }

    @NonNull
    private Set<Hdr> getSupportedHdr() {
        return Collections.unmodifiableSet(supportedHdr);
    }

    @NonNull
    private Set<PreviewSize> getSupportSize() {
        return Collections.unmodifiableSet(supportSize);
    }

}

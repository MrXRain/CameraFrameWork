package com.core.camera;

import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
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
    private Set<Focus> supportFocus = new HashSet<>(2);
    private Set<PreviewSize> supportSize = new HashSet<>(3);

    public CameraOptions() {

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraOptions(CameraManager cameraManager,String cameraId) {
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

            cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE).booleanValue();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public CameraOptions(Camera.Parameters parameters, Camera camera) {

        List<String> strings;

        Mapper.Mapper1 mapper = new Mapper.Mapper1();

        // Facing
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            Facing value = mapper.put(Facing.class, cameraInfo.facing);
            if (value != null) supportedFacing.add(value);
        }

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

        // Focus
        strings = parameters.getSupportedFocusModes();
        if (strings != null) {
            for (String string : strings) {
                Focus value = mapper.put(Focus.class, string);
                if (value != null) supportFocus.add(value);
            }
        }

        // PreviewSize
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        if (sizes != null) {
            for (Camera.Size size : sizes) {
                Size mSize = new Size(size.width,size.height);
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
        } else if (tClass.equals(Focus.class)) {
            return (Collection<T>) getSupportFocus();
        } else if (tClass.equals(PreviewSize.class)) {
            return (Collection<T>) getSupportSize();
        }

        return Collections.emptyList();
    }

    @NonNull
    private Set<Focus> getSupportFocus() {
        return Collections.unmodifiableSet(supportFocus);
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

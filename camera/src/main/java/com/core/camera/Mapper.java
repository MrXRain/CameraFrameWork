package com.core.camera;

import android.hardware.Camera;
import android.os.Build;
import com.core.camera.option.*;
import com.core.camera.utils.Size;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author rain
 * @date 2019/3/22
 */
public abstract class Mapper {

    public abstract <T> T put(Class<T> cls, Object obj);

    public abstract <T> T getFlash(Flash flash);

    public abstract <T> T getWhiteBalance(WhiteBalance whiteBalance);

    public abstract <T> T getFacing(Facing facing);

    public abstract <T> T getHdr(Hdr hdr);

    public abstract <T> T getFocus(Focus focus);

    public abstract <T> T getPreviewSize(PreviewSize previewSize);

    public static class Mapper3 extends Mapper {

        private static final HashMap<PreviewSize, Size> SIZE = new HashMap<>();

        static {
            SIZE.put(PreviewSize.V480P, new Size(640, 480));
            SIZE.put(PreviewSize.V720P, new Size(1280, 720));
            SIZE.put(PreviewSize.V1080P, new Size(1920, 1080));
        }

        @Override
        public <T> T put(Class<T> cls, Object obj) {
            if (cls.equals(PreviewSize.class)) {
                return (T) map(SIZE, obj);
            }
            return null;
        }

        private <T> T map(HashMap<T, ?> map, Object obj) {
            for (T t : map.keySet()) {
                if (map.get(t).equals(obj)) {
                    return t;
                }
            }

            return null;
        }

        @Override
        public <T> T getFlash(Flash flash) {
            return null;
        }

        @Override
        public <T> T getWhiteBalance(WhiteBalance whiteBalance) {
            return null;
        }

        @Override
        public <T> T getFacing(Facing facing) {
            return null;
        }

        @Override
        public <T> T getHdr(Hdr hdr) {
            return null;
        }

        @Override
        public <T> T getFocus(Focus focus) {
            return null;
        }

        @Override
        public <T> T getPreviewSize(PreviewSize previewSize) {
            return null;
        }
    }

    public static class Mapper1 extends Mapper {

        private static final TreeMap<Flash, String> FLASH = new TreeMap<>();
        private static final TreeMap<WhiteBalance, String> WB = new TreeMap<>();
        private static final TreeMap<Facing, Integer> FACING = new TreeMap<>();
        private static final TreeMap<Hdr, String> HDR = new TreeMap<>();
        private static final TreeMap<Focus, String> FOCUS = new TreeMap<>();
        private static final TreeMap<PreviewSize, Size> SIZE = new TreeMap<>();

        static {
            FLASH.put(Flash.OFF, Camera.Parameters.FLASH_MODE_OFF);
            FLASH.put(Flash.ON, Camera.Parameters.FLASH_MODE_ON);
            FLASH.put(Flash.AUTO, Camera.Parameters.FLASH_MODE_AUTO);
            FLASH.put(Flash.TORCH, Camera.Parameters.FLASH_MODE_TORCH);
            FACING.put(Facing.BACK, Camera.CameraInfo.CAMERA_FACING_BACK);
            FACING.put(Facing.FRONT, Camera.CameraInfo.CAMERA_FACING_FRONT);
            WB.put(WhiteBalance.AUTO, Camera.Parameters.WHITE_BALANCE_AUTO);
            WB.put(WhiteBalance.INCANDESCENT, Camera.Parameters.WHITE_BALANCE_INCANDESCENT);
            WB.put(WhiteBalance.FLUORESCENT, Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
            WB.put(WhiteBalance.DAYLIGHT, Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
            WB.put(WhiteBalance.CLOUDY, Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
            FOCUS.put(Focus.CONTINUOUS_PICTURE, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            FOCUS.put(Focus.CONTINUOUS_VIDEO, Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            SIZE.put(PreviewSize.V480P, new Size(640, 480));
            SIZE.put(PreviewSize.V720P, new Size(1280, 720));
            SIZE.put(PreviewSize.V1080P, new Size(1920, 1080));
            HDR.put(Hdr.OFF, Camera.Parameters.SCENE_MODE_AUTO);
            if (Build.VERSION.SDK_INT >= 17) {
                HDR.put(Hdr.ON, Camera.Parameters.SCENE_MODE_HDR);
            } else {
                HDR.put(Hdr.ON, "hdr");
            }
        }

        @Override
        public <T> T put(Class<T> cls, Object obj) {
            if (cls.equals(Flash.class)) {
                return (T) map(FLASH, obj);
            } else if (cls.equals(WhiteBalance.class)) {
                return (T) map(WB, obj);
            } else if (cls.equals(Hdr.class)) {
                return (T) map(HDR, obj);
            } else if (cls.equals(Facing.class)) {
                return (T) map(FACING, obj);
            } else if (cls.equals(PreviewSize.class)) {
                return (T) map(SIZE, obj);
            }
            return null;
        }

        @Override
        public <T> T getFlash(Flash flash) {
            return (T) FLASH.get(flash);
        }

        @Override
        public <T> T getWhiteBalance(WhiteBalance whiteBalance) {
            return (T) WB.get(whiteBalance);
        }

        @Override
        public <T> T getFacing(Facing facing) {
            return (T) FACING.get(facing);
        }

        @Override
        public <T> T getHdr(Hdr hdr) {
            return (T) HDR.get(hdr);
        }

        @Override
        public <T> T getFocus(Focus focus) {
            return (T) FOCUS.get(focus);
        }

        @Override
        public <T> T getPreviewSize(PreviewSize previewSize) {
            return (T) SIZE.get(previewSize);
        }

        private <T> T map(TreeMap<T, ?> map, Object obj) {
            for (T t : map.keySet()) {
                if (t instanceof PreviewSize) {
                    Size size = (Size) map.get(t);
                    if (size.getWidth() == ((Size) obj).getWidth()) {
                        return t;
                    }
                } else {
                    if (map.get(t).equals(obj)) {
                        return t;
                    }
                }
            }

            return null;
        }
    }
}

package com.core.camera.option;

/**
 * @author rain
 * @date 2019/3/20
 */
public enum CameraType {

    /**
     * Camera1
     */
    CAMERA_1(1),

    /**
     * Camera2
     */
    CAMERA_2(2),

    /**
     * uvc camera
     */
    CAMERA_3(3);

    public static final CameraType DEFAULT = CAMERA_1;

    private int value;

    CameraType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static CameraType fromValue(int value) {
        CameraType[] list = CameraType.values();

        for (CameraType type : list) {
            if (type.value() == value) {
                return type;
            }
        }

        return null;
    }
}

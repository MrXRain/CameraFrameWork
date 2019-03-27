package com.core.camera.option;

/**
 * @author rain
 * @date 2019/3/20
 */
public enum CameraType {

    /**
     * Camera1
     */
    CAMERA_TYPE_1(1),

    /**
     * Camera2
     */
    CAMERA_TYPE_2(2);

    public static final CameraType DEFAULT = CAMERA_TYPE_1;

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

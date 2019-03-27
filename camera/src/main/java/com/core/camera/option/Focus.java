package com.core.camera.option;

/**
 * @author rain
 * @date 2019/3/22
 */
public enum Focus implements Control {

    CONTINUOUS_VIDEO(0),
    CONTINUOUS_PICTURE(1);

    private int value;

    Focus(int value) {
        this.value = value;
    }

    int value() {
        return value;
    }

    public static Focus fromValue(int value) {
        Focus[] list = Focus.values();

        for (Focus focus : list) {
            if (focus.value() == value) {
                return focus;
            }
        }

        return null;
    }
}

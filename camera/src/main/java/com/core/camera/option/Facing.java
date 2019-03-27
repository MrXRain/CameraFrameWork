package com.core.camera.option;

/**
 * @author rain
 * @date 2019/3/20
 */
public enum Facing implements Control {

    /**
     * 前置摄像头
     */
    FRONT(1),

    /**
     * 后置摄像头
     */
    BACK(0);

    public static final Facing DEFAULT = BACK;

    private int value;

    Facing(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Facing fromValue(int value) {
        Facing[] list = Facing.values();

        for (Facing facing : list) {
            if (facing.value() == value) {
                return facing;
            }
        }

        return null;
    }
}

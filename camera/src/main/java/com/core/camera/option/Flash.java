package com.core.camera.option;

/**
 * @author rain
 * @date 2019/3/20
 */
public enum Flash implements Control{

    /**
     * Flash is always off.
     */
    OFF(0),

    /**
     * Flash will be on when capturing.
     * This is not guaranteed to be supported.
     *
     */
    ON(1),


    /**
     * Flash mode is chosen by the camera.
     * This is not guaranteed to be supported.
     *
     */
    AUTO(2),


    /**
     * Flash is always on, working as a torch.
     * This is not guaranteed to be supported.
     *
     */
    TORCH(3);

    public static final Flash DEFAULT = OFF;

    private int value;

    Flash(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Flash fromValue(int value) {
        Flash[] list = Flash.values();
        for (Flash flash : list) {
            if (flash.value() == value) {
                return flash;
            }
        }

        return null;
    }
}

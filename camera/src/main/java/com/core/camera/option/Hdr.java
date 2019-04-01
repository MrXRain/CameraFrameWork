package com.core.camera.option;

/**
 * @author rain
 * @date 2019/3/20
 */
public enum Hdr implements Control {

    /**
     * OFF mode
     */
    OFF(0),

    /**
     * ON mode
     */
    ON(1);

    private int value;

    public static final Hdr DEFAULT = OFF;

    Hdr(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Hdr fromValue(int value) {
        Hdr[] list = Hdr.values();

        for (Hdr hdr : list) {
            if (hdr.value() == value) {
                return hdr;
            }
        }

        return null;
    }
}

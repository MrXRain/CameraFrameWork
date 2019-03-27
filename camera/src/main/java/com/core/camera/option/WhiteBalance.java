package com.core.camera.option;

/**
 * @author rain
 * @date 2019/3/20
 */
public enum WhiteBalance implements Control {

    /**
     * Automatic white balance selection (AWB).
     * This is not guaranteed to be supported.
     *
     */
    AUTO(0),

    /**
     * White balance appropriate for incandescent light.
     * This is not guaranteed to be supported.
     *
     */
    INCANDESCENT(1),

    /**
     * White balance appropriate for fluorescent light.
     * This is not guaranteed to be supported.
     *
     */
    FLUORESCENT(2),

    /**
     * White balance appropriate for daylight captures.
     * This is not guaranteed to be supported.
     *
     */
    DAYLIGHT(3),

    /**
     * White balance appropriate for pictures in cloudy conditions.
     * This is not guaranteed to be supported.
     *
     */
    CLOUDY(4);

    public static final WhiteBalance DEFAULT = AUTO;

    private int value;

    WhiteBalance(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static WhiteBalance fromValue(int value) {
        WhiteBalance[] list = WhiteBalance.values();
        for (WhiteBalance balance : list) {
            if (balance.value() == value) {
                return balance;
            }
        }

        return  null;
    }

}

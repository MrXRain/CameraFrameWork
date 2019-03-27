package com.core.camera.option;

/**
 * @author rain
 * @date 2019/3/21
 */
public enum PreviewSize implements Control {

    /**
     * 480p
     */
    V480P(0),

    /**
     * 720p
     */
    V720P(1),

    /**
     * 1080p
     */
    V1080P(2);

    public final static PreviewSize DEFAULT = V720P;

    private int value;

    PreviewSize(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static PreviewSize fromValue(int value) {
        PreviewSize[] list = PreviewSize.values();
        for (PreviewSize previewSize : list) {
            if (previewSize.value() == value) {
                return previewSize;
            }
        }

        return null;
    }
}

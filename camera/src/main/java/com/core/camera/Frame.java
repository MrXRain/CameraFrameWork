package com.core.camera;


/**
 * @author rain
 * @date 2019/3/20
 */
public class Frame {

    private byte[] mData;
    private int mWidth,mHeight;
    private int mFormat = -1;

    public void setData(byte[] data, int width, int height, int format) {
        this.mData = data;
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
    }

    void release() {
        mData = null;
        mWidth = -1;
        mHeight = -1;
        mFormat = -1;
    }
}

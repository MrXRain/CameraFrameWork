package com.core.camera;

/**
 * @author rain
 * @date 2019/4/16
 */
public class Picture {

    private byte[] mData;
    private int mWidth, mHeight;

    public Picture(byte[] mData, int mWidth, int mHeight) {
        this.mData = mData;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] mData) {
        this.mData = mData;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }
}

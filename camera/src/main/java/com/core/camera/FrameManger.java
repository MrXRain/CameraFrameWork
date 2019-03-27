package com.core.camera;

import android.hardware.Camera;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author rain
 * @date 2019/3/20
 */
public class FrameManger {

    private LinkedBlockingQueue<Frame> mQueue;

    public FrameManger(int poolsize) {
        mQueue = new LinkedBlockingQueue<>(poolsize);
    }

    public void release() {
        for (Frame frame : mQueue) {
            frame.release();
        }

        mQueue.clear();
        mQueue = null;
    }

    public Frame getframe(byte[] data, int width, int height, int format) {
        Frame mFrame = mQueue.poll();

        if (mFrame == null) {
            mFrame = new Frame();
        }

        mFrame.setData(data, width, height, format);
        mQueue.add(mFrame);
        return mFrame;
    }
}

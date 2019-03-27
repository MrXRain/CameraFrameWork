package com.core.camera.utils;

import android.os.Handler;
import android.os.HandlerThread;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rain
 * @date 2019/3/21
 */
public class WorkHandler {

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    final static ConcurrentHashMap<String, WeakReference<WorkHandler>> mCache = new ConcurrentHashMap<>(4);

    public static WorkHandler get(String name) {
        if (mCache.containsKey(name)) {
            WeakReference wf = mCache.get(name);
            if (wf != null) {
                WorkHandler wk = (WorkHandler) wf.get();
                if (wk != null) {
                    HandlerThread mThread = wk.mHandlerThread;
                    if (mThread.isAlive() && !mThread.isInterrupted()) {
                        return wk;
                    }
                } else {
                    mCache.remove(name);
                }
            }
        }

        WorkHandler workHandler = new WorkHandler(name);
        mCache.put(name, new WeakReference<>(workHandler));
        return workHandler;
    }

    private WorkHandler(String name) {
        mHandlerThread = new HandlerThread(name);
        mHandlerThread.setDaemon(true);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public static void destroy() {
        for (String s : mCache.keySet()) {
            WeakReference<WorkHandler> wr = mCache.get(s);
            WorkHandler workHandler = wr.get();
            if (workHandler != null && workHandler.getThread().isAlive()) {
                workHandler.getThread().interrupt();
            }

            wr.clear();
        }

        mCache.clear();
    }

    public Thread getThread() {
        return mHandlerThread;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }
}

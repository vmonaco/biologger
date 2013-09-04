package com.vmonaco.bbl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A buffer for storing events with an overflow buffer while events are being 
 * uploaded. The network IO is _not_ done asynchronously and reads directly from
 * this buffer. This needs to operate in real time to receive events. 
 * 
 * @author vinnie
 *
 */
public class Buffer implements Runnable {

    private Database mDatabase;

    private List<Mappable> mBuffer1;
    private List<Mappable> mBuffer2;

    private boolean mRun;
    private boolean mFlushing1;
    private boolean mBusy;

    public Buffer(Database database) {
        mDatabase = database;
        mFlushing1 = false;
        mBusy = false;

        mBuffer1 = new LinkedList<Mappable>();
        mBuffer2 = new LinkedList<Mappable>();
    }

    public void addEvent(Mappable e) {
        if (!mFlushing1) {
            mBuffer1.add(e);
        } else {
            mBuffer2.add(e);
        }
    }
    
    private void flush() {
        mBusy = true;
        flush1();
        flush2();
        mBusy = false;
    }

    public void finish() {
        while (mBusy) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                CustomExceptionHandler.submitCrashReport(e);
            }
        }
        flush();
    }

    public void flush1() {
        mFlushing1 = true;
        Map[] data = new Map[mBuffer1.size()];
        for (int i = 0; i < mBuffer1.size(); i++) {
            data[i] = mBuffer1.get(i).toMap();
        }
        mDatabase.insertEntities(data);
        mBuffer1.clear();
        mFlushing1 = false;
    }

    public void flush2() {
        Map[] data = new Map[mBuffer2.size()];
        for (int i = 0; i < mBuffer2.size(); i++) {
            data[i] = mBuffer2.get(i).toMap();
        }
        mDatabase.insertEntities(data);
        mBuffer2.clear();
    }

    public void stop() {
        mRun = false;
    }

    @Override
    public void run() {
        mRun = true;
        while (mRun) {
            flush();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                CustomExceptionHandler.submitCrashReport(e);
            }

        }
    }

}

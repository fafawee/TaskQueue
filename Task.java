package com.txhome.adupload.QueueLibs;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

/**
 * Created by tianjiangwei on 2017/9/28.
 */

public class Task implements Runnable {


    private int id;
    private TaskCallBack mTaskCallBack;
    // Thread Helper Functions
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private  Handler threadHandler;
    private HandlerThread handlerThread;
    private TaskQueue queue;
    private boolean isFinish=false;
    private int             priority = Process.THREAD_PRIORITY_DEFAULT;


    public Task(TaskQueue queue)
    {
        this.queue=queue;
        handlerThread=new HandlerThread("therad"+id,priority);
        handlerThread.start();
        threadHandler=new Handler(handlerThread.getLooper());


    }
    public void setTaskId(int id)
    {
        this.id=id;
    }
    public int getTaskId()
    {
        return id;
    }
    @Override
    public void run() {

        onTask(queue);

    }


    public synchronized void onTask(TaskQueue queue)
    {


    }

    public boolean isFinish()
    {
        return isFinish;
    }

    public void finish()
    {
        isFinish=true;
        runOnMainThread(new Runnable() {
            @Override
            public void run() {

                if (mTaskCallBack!=null)
                {
                    mTaskCallBack.onFinish(Task.this);
                }
            }
        });

    }
    public void runOnMainThread(Runnable runnable)
    {
        mainHandler.post(runnable);
    }
    public void runOnMainThreadOnDelay(Runnable runnable,long delay)
    {
        mainHandler.postDelayed(runnable,delay);
    }
    public void runOnThread(Runnable runnable)
    {
        threadHandler.post(runnable);
    }
    public void runOnThreadOnDelay(Runnable runnable,long delay)
    {
        threadHandler.postDelayed(runnable,delay);
    }

    public void sleep(long sleepTime)
    {
        try
        {
            Thread.sleep(sleepTime);
        }
        catch (Exception e)
        {

        }

    }
    public void setTaskCallBack(TaskCallBack callBack)
    {
        this.mTaskCallBack=callBack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task that = (Task) o;

        return this.id==that.id?true:false;

    }

    @Override
    public int hashCode() {
        int result = hashCode();
        result = 31 * result + id;
        return result;
    }


}

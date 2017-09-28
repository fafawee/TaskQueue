package com.txhome.adupload.QueueLibs;

import android.content.Context;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tianjiangwei on 2017/9/28.
 */

public class TaskQueue implements Serializable{


    private int taskid=0;
    private String name="default";
    private boolean isRunning=false;
    private TaskQueueListener mTaskQueueListener;
    private LinkedBlockingQueue<Task> arrayBlockingQueue=new LinkedBlockingQueue<Task>();
    private ArrayList<Task> copyTaskList=new ArrayList<>();
    private Task currentTask;
    private int currentIndex=0;
    private int allTaskNumber=0;


    public TaskQueue()
    {
        this("default_"+System.currentTimeMillis());
    }

    public TaskQueue(String name)
    {
        this.name=name;

    }


    public boolean isRunning()
    {
        return isRunning;
    }


    public synchronized  void start(){
        if (isRunning)return;
        isRunning=true;
        if (!arrayBlockingQueue.isEmpty())
        {
            try
            {

                currentTask=arrayBlockingQueue.take();
                new Thread(currentTask).start();

                if (mTaskQueueListener!=null)
                {
                    mTaskQueueListener.onStart();
                }

            }
            catch (Exception e)
            {

            }


        }

    }


    public void reStart()
    {
        isRunning=false;
        currentIndex=0;
        arrayBlockingQueue=getCopyTaskQueue();
        start();

    }

    private LinkedBlockingQueue<Task> getCopyTaskQueue()
    {
        LinkedBlockingQueue<Task> queue=new LinkedBlockingQueue<>();
        if (copyTaskList!=null)
        {

            for (int i = 0; i <copyTaskList.size() ; i++) {
                try
                {
                    queue.put(copyTaskList.get(i));
                }
                catch (Exception e)
                {

                }

            }
        }
        return queue;
    }


    private void removeTaskFromArrayList(Task task)
    {
        if (copyTaskList!=null)
        {

            for (int i = 0; i <copyTaskList.size() ; i++) {
                try
                {
                   if (copyTaskList.get(i).equals(task))
                   {

                   }
                }
                catch (Exception e)
                {

                }

            }
        }
    }


    public void pause()
    {
        if (isRunning)
        {
            isRunning=false;
            if (mTaskQueueListener!=null)
            {
                mTaskQueueListener.onPause();
            }
        }
    }
    public void resume()
    {
        if (!isRunning)
        {
            isRunning=true;
            if (mTaskQueueListener!=null)
            {
                mTaskQueueListener.onResume();
            }
            currentTask=arrayBlockingQueue.poll();
            if (currentTask!=null)
            {
                currentIndex++;

                new Thread(currentTask).start();

            }
        }
    }
    public void cancel()
    {
        if (isRunning)
        {
            isRunning=false;
            arrayBlockingQueue.clear();

            if (mTaskQueueListener!=null)
            {
                mTaskQueueListener.onCancel();
            }

        }
    }
    public void addTask(Task task)
    {
        try
        {
            allTaskNumber++;
            task.setTaskId(taskid);
            taskid++;
            arrayBlockingQueue.put(task);
            copyTaskList.add(task);
            task.setTaskCallBack(new TaskCallBack() {
                @Override
                public void onFinish(Task task) {
                    try
                    {

                        if (isRunning)
                        {
                            currentTask=arrayBlockingQueue.poll();
                            currentIndex++;
                            if (currentTask!=null)
                            {

                                new Thread(currentTask).start();

                            }
                            else
                            {
                                if (mTaskQueueListener!=null)
                                {
                                    mTaskQueueListener.onFinish();
                                }
                            }

                            if (mTaskQueueListener!=null)
                            {
                                mTaskQueueListener.onProgress(getProgress(),task);

                            }

                        }


                    }
                    catch (Exception e)
                    {

                    }
                }
            });

        }
        catch (Exception e)
        {

        }


    }



    public void removeTask(Task task)
    {
        try
        {
            boolean issuccess=arrayBlockingQueue.remove(task);
            removeTaskFromArrayList(task);
            if (issuccess)
            {
                allTaskNumber--;
            }
        }
        catch (Exception e)
        {

        }
    }
    public void setTaskQueueListener(TaskQueueListener listener)
    {
        this.mTaskQueueListener=listener;

    }


    public float getProgress()
    {
        DecimalFormat format=new DecimalFormat("#.00");
        return Float.parseFloat(format.format(((double) currentIndex/allTaskNumber)));

    }








}

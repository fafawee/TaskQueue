package com.txhome.adupload.QueueLibs;

import java.io.Serializable;

/**
 * Created by tianjiangwei on 2017/9/28.
 */

public interface TaskQueueListener extends Serializable{
    void onStart();
    void onPause();
    void onResume();
    void onCancel();
    void onProgress(float progress,Task task);
    void onFinish();

}

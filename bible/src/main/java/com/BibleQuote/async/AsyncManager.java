/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: AsyncManager.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.async;

import android.content.Context;

import com.BibleQuote.utils.Task;

public class AsyncManager implements OnTaskCompleteListener {

    private Context context;
    private AsyncTaskManager mAsyncTaskManager;
    private AsyncTaskManager mWaitTaskManager;
    private OnTaskCompleteListener taskCompleteListener;
    private volatile Task waitTask;    // the task is waiting its execution

    public synchronized boolean isWorking() {
        return mAsyncTaskManager != null && mAsyncTaskManager.isWorking();
    }

    @Override
    public synchronized void onTaskComplete(Task task) {
        if (mWaitTaskManager != null) {
            mWaitTaskManager.retainTask();
            mWaitTaskManager = null;
        }
        try {
            Task newTask = waitTask;
            waitTask = null;
            setupTask(newTask, taskCompleteListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getContext() {
        return context;
    }

    public synchronized void handleRetainedTask(Task task, OnTaskCompleteListener taskCompleteListener) {
        this.taskCompleteListener = taskCompleteListener;
        mAsyncTaskManager = new AsyncTaskManager(taskCompleteListener);
        mAsyncTaskManager.handleRetainedTask(task, taskCompleteListener);
    }

    public synchronized Task retainTask() {
        if (mWaitTaskManager != null) {
            mWaitTaskManager.retainTask();
            mWaitTaskManager = null;
        }
        waitTask = null;
        if (mAsyncTaskManager != null) {
            Task retainTask = (Task) mAsyncTaskManager.retainTask();
            mAsyncTaskManager = null;
            return retainTask;
        }
        return null;
    }

    public synchronized void setupTask(Task task, OnTaskCompleteListener taskCompleteListener) {
        this.taskCompleteListener = taskCompleteListener;
        this.context = taskCompleteListener.getContext();
        AsyncTaskManager newAsyncTaskManager = new AsyncTaskManager(taskCompleteListener);
        if (mAsyncTaskManager != null && mAsyncTaskManager.isWorking()) {
            // Override the next task only if a new task is a foreground task (with a progress dialog visible)
            if (waitTask == null || !task.isHidden()) {
                waitTask = task;
            }

            if (mWaitTaskManager == null) {
                // Start a wait thread until mAsyncTaskManager has completed
                mWaitTaskManager = new AsyncTaskManager(this);
                mWaitTaskManager.setupTask(new AsyncWait("Please wait ...", false, mAsyncTaskManager));
            }
        } else {
            mAsyncTaskManager = newAsyncTaskManager;
            Task nextTask;
            if (waitTask != null) {
                nextTask = waitTask;
                waitTask = task;
            } else {
                nextTask = task;
            }
            mAsyncTaskManager.setupTask(nextTask);
        }
    }
}

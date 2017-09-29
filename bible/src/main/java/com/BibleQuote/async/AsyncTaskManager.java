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
 * File: AsyncTaskManager.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.BibleQuote.utils.IProgressTracker;
import com.BibleQuote.utils.Logger;
import com.BibleQuote.utils.Task;

import java.lang.ref.WeakReference;

public final class AsyncTaskManager implements IProgressTracker, OnCancelListener {

    private Task mAsyncTask;
    private ProgressDialog mProgressDialog;
    private OnTaskCompleteListener taskCompleteListener;
    private WeakReference<Context> weakContext;

    public AsyncTaskManager(OnTaskCompleteListener taskCompleteListener) {
        this.taskCompleteListener = taskCompleteListener;
        Context context = taskCompleteListener.getContext();
        this.weakContext = new WeakReference<>(context);
        setupProgressDialog(context);
    }

    @Override
    public void onProgress(String message) {
        if (mAsyncTask.isHidden()) {
            return;
        }

        Context context = weakContext.get();
        if (context == null) {
            return;
        }

        // Show dialog if it wasn't shown yet or was removed on configuration
        // (rotation) change
        try {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show current message in progress dialog
        mProgressDialog.setMessage(message);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mAsyncTask == null) {
            return;
        }
        // Cancel task
        mAsyncTask.cancel(true);
        // Notify activity about completion
        taskCompleteListener.onTaskComplete(mAsyncTask);
        // Reset task
        mAsyncTask = null;
    }

    @Override
    public void onComplete() {
        // Close progress dialog
        try {
            mProgressDialog.cancel();
        } catch (IllegalArgumentException e) {
            Logger.e(this, "View not attached to window manager");
        }

        // Reset task
        Task completedTask = mAsyncTask;
        mAsyncTask = null;

        // Notify activity about completion
        taskCompleteListener.onTaskComplete(completedTask);
    }

    public void setupTask(Task asyncTask) {
        // Keep task
        mAsyncTask = asyncTask;
        // Wire task to tracker (this)
        mAsyncTask.setProgressTracker(this);
        // Start task
        mAsyncTask.execute();
    }

    void handleRetainedTask(Task task, OnTaskCompleteListener taskCompleteListener) {
        this.taskCompleteListener = taskCompleteListener;
        setupProgressDialog(taskCompleteListener.getContext());

        // Restore retained task and attach it to tracker (this)
        mAsyncTask = task;
        mAsyncTask.setProgressTracker(this);
    }

    boolean isWorking() {
        // Track current status
        return mAsyncTask != null;
    }

    Object retainTask() {
        // Close progress dialog
        mProgressDialog.cancel();

        // Detach task from tracker (this) before retain
        if (mAsyncTask != null) {
            mAsyncTask.setProgressTracker(null);
        }
        // Retain task
        return mAsyncTask;
    }

    private void setupProgressDialog(Context context) {
        // Setup progress dialog
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnCancelListener(this);
    }
}
/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pockethub.core.repo;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.alorma.github.sdk.services.repo.actions.UnstarRepoClient;
import com.github.pockethub.R;
import com.github.pockethub.ui.ProgressDialogTask;
import com.google.inject.Inject;

import com.alorma.github.sdk.bean.dto.response.Repo;
import org.eclipse.egit.github.core.service.WatcherService;

/**
 * Task to unstar a repository
 */
public class UnstarRepositoryTask extends ProgressDialogTask<Void> {

    private static final String TAG = "UnstarRepositoryTask";

    @Inject
    private WatcherService service;

    private final Repo repo;

    /**
     * Create task for context and id provider
     *
     * @param context
     * @param repo
     */
    public UnstarRepositoryTask(Context context, Repo repo) {
        super(context);

        this.repo = repo;
    }

    /**
     * Execute the task with a progress dialog displaying.
     * <p>
     * This method must be called from the main thread.
     */
    public void start() {
        showIndeterminate(R.string.unstarring_repository);

        execute();
    }

    @Override
    public Void run(Account account) throws Exception {
        new UnstarRepoClient(context, repo.owner.login, repo.name).executeSync();
        return null;
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception unstarring repository", e);
    }
}

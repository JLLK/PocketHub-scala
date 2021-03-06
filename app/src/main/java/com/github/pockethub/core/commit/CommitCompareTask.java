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
package com.github.pockethub.core.commit;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.alorma.github.sdk.bean.dto.response.CompareCommit;
import com.alorma.github.sdk.services.repo.CompareCommitsClient;
import com.github.pockethub.accounts.AuthenticatedUserTask;
import com.github.pockethub.util.InfoUtils;
import com.google.inject.Inject;

import com.alorma.github.sdk.bean.dto.response.Repo;
import org.eclipse.egit.github.core.RepositoryCommitCompare;
import org.eclipse.egit.github.core.service.CommitService;

/**
 * Task to compare two commits
 */
public class CommitCompareTask extends AuthenticatedUserTask<CompareCommit> {

    private static final String TAG = "CommitCompareTask";

    private final Repo repository;

    private final String base;

    private final String head;

    /**
     * @param context
     * @param repository
     * @param base
     * @param head
     */
    public CommitCompareTask(Context context, Repo repository,
            String base, String head) {
        super(context);

        this.repository = repository;
        this.base = base;
        this.head = head;
    }

    @Override
    public CompareCommit run(Account account) throws Exception {
        return new CompareCommitsClient(context,
                InfoUtils.createRepoInfo(repository), base, head).executeSync();
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception loading commit compare", e);
    }
}

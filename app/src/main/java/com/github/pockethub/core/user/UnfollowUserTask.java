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
package com.github.pockethub.core.user;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.github.pockethub.R;
import com.github.pockethub.ui.ProgressDialogTask;
import com.google.inject.Inject;

import com.alorma.github.sdk.bean.dto.response.User;
import org.eclipse.egit.github.core.service.UserService;

/**
 * Task to unfollow a user
 */
public class UnfollowUserTask extends ProgressDialogTask<User> {

    private static final String TAG = "UnfollowUserTask";

    @Inject
    private UserService service;

    private final String login;

    /**
     * Create task for context and login
     *
     * @param context
     * @param login
     */
    public UnfollowUserTask(final Context context, final String login) {
        super(context);

        this.login = login;
    }

    /**
     * Execute the task with a progress dialog displaying.
     * <p>
     * This method must be called from the main thread.
     */
    public void start() {
        showIndeterminate(R.string.unfollowing_user);

        execute();
    }

    @Override
    public User run(final Account account) throws Exception {
        service.unfollow(login);

        return null;
    }

    @Override
    protected void onException(final Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception unfollowing user", e);
    }
}

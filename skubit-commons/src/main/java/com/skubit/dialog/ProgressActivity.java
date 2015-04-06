/**
 * Copyright 2015 Skubit
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.skubit.dialog;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

public abstract class ProgressActivity<T> extends FragmentActivity
        implements ProgressActivityCallback<T> {

    protected View mLoading;

    protected String mUIState;

    protected View mContainer;

    @Override
    public void load(T data) {
        load(data, BaseLoader.TYPE_DEFAULT);
    }

    public void replaceFragment(Fragment fragment, String tag) {
        mUIState = tag;
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .replace(R.id.container, fragment, tag).commitAllowingStateLoss();
    }

    public void showMessage(String message) {
        if (isAlive()) {
            mUIState = "MESSAGE";
            replaceFragment(ShowMessageFragment
                            .newInstance(message, mContainer.getWidth(), mContainer.getHeight()),
                    "MESSAGE");
            hideProgress();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_loading_dialog_frame);
        mLoading = findViewById(R.id.progress_bar);
        mContainer = findViewById(R.id.container);

        if (savedInstanceState != null) {
            mUIState = savedInstanceState.getString("UI_STATE");
        }
    }

    @Override
    public void sendResultsBackToCaller(int resultCode, String message) {
        sendResultsBackToCaller(resultCode, message, true);
    }

    @Override
    public void sendResultsBackToCaller(int resultCode, String message, boolean finish) {
        System.out.println("foo:" + resultCode + ":" + message);
        if (TextUtils.isEmpty(message)) {
            setResult(resultCode);
        } else {
            setResult(1001, new Intent().putExtra("response", message));
        }
        if(finish) {
            finish();
        }
    }

    @Override
    public void sendResultsBackToCaller(int resultCode, Bundle data) {
        sendResultsBackToCaller(resultCode, data, true);
    }

    @Override
    public void sendResultsBackToCaller(int resultCode, Bundle data, boolean finish) {
        System.out.println("foo:" + resultCode + ": bundle");
        setResult(resultCode, new Intent().replaceExtras(data));
        if(finish) {
            finish();
        }
    }

    @Override
    public void cancel() {
        System.out.println("foo: cancel");
        setResult(ResultCode.USER_CANCELED);
        finish();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mUIState = savedInstanceState.getString("UI_STATE");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUIState != null) {
            outState.putString("UI_STATE", mUIState);
        }
    }

    @Override
    public void hideProgress() {
        if (isAlive()) {
            mContainer.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showProgress() {
        if (isAlive()) {
            mContainer.setVisibility(View.INVISIBLE);
            mLoading.setVisibility(View.VISIBLE);
        }
    }

    public boolean isAlive() {
        return mContainer != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("foo: result: " + resultCode);
        //setResult(resultCode, data);
        //finish();
    }

}

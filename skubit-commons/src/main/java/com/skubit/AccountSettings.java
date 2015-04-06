/**
 * Copyright 2014 Skubit
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

package com.skubit;

import android.content.Context;
import android.content.SharedPreferences;

public class AccountSettings {

    public static final String SHARED_PREFERENCE = "com.skubit.shared";

    private static final String BITCOIN_ADDRESS = "bitcoinAddress";

    private static final String TOKEN = "token";

    private static final String LOCKER_ITEM = "lockerItem";

    private static final String BITID = "bitId";

    private static final String INDEX = "index";

    private static volatile AccountSettings sInstance = null;

    private Context context;

    private AccountSettings(Context context) {
        this.context = context;
    }

    public static AccountSettings get(Context context) {
        if (sInstance == null) {
            synchronized (AccountSettings.class) {
                if (sInstance == null) {
                    sInstance = new AccountSettings(context);
                }
            }
        }
        return sInstance;
    }

    public int getCurrentIndex() {
        return retrieveIntPreference(INDEX);
    }

    public void setCurrentIndex(int index) {
        saveIntPreference(INDEX, index);
    }

    public String retrieveBitcoinAddress() {
        return retrieveStringPreference(BITCOIN_ADDRESS);
    }

    public String retrieveToken() {
        return retrieveStringPreference(TOKEN);
    }

    public String retrieveBitId() {
        return retrieveStringPreference(BITID);
    }

    private int retrieveIntPreference(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    private String retrieveStringPreference(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public void saveBitcoinAddress(String address) {
        saveStringPreference(BITCOIN_ADDRESS, address);
    }

    public void saveToken(String token) {
        saveStringPreference(TOKEN, token);
    }

    public void saveBitId(String bitId) {
        saveStringPreference(BITID, bitId);
    }

    private void saveIntPreference(String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void saveStringPreference(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}

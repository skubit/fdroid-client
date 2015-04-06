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
package com.skubit.navigation;

import com.skubit.AccountSettings;
import com.skubit.Events;
import com.skubit.market.provider.accounts.AccountsCursor;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;

public class AccountDropDownClickEvent implements DropDownClickEvent {

    private final AccountsCursor accountsCursor;

    private final Context mContext;

    public AccountDropDownClickEvent(Context context, Cursor c) {
        accountsCursor = new AccountsCursor(c);
        mContext = context;
    }

    @Override
    public String onClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        accountsCursor.moveToPosition(position);

        String bitId = accountsCursor.getBitid();
        AccountSettings.get(mContext).saveBitId(bitId);
        AccountSettings.get(mContext).saveToken(accountsCursor.getToken());

        Events.accountChange(mContext, bitId);
        return bitId;
    }
}

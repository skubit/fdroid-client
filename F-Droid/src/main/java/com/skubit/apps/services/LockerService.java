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
package com.skubit.apps.services;


import com.skubit.Constants;
import com.skubit.apps.services.rest.LockerRestService;

import org.fdroid.fdroid.BuildConfig;

import android.content.Context;

public class LockerService extends BaseService<LockerRestService> {

    public LockerService(String account, Context context) {
        super(account, context);
    }

    @Override
    public Class<LockerRestService> getClazz() {
        return LockerRestService.class;
    }

    @Override
    public String getEndpoint() {
        if (BuildConfig.FLAVOR.startsWith("prod")) {
            return Constants.LOCKER_URI_PROD;
        } else if (BuildConfig.FLAVOR.startsWith("dev")) {
            return Constants.LOCKER_URI_TEST;
        }

        return null;
    }
}

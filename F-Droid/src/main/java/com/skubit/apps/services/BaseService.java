/* Copyright 2015 Skubit
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.fdroid.fdroid.BuildConfig;

import android.content.Context;

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;


public abstract class BaseService<T> {

    private T mRestService;

    public BaseService(String account, Context context) {
        RestAdapter.Builder builder = new RestAdapter.Builder();

        CookieInterceptor interceptor = new CookieInterceptor(
                account, context);
        builder.setRequestInterceptor(interceptor);

        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL)
                    .setConverter(new JacksonConverter());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            builder.setConverter(new JacksonConverter(mapper));
        }

        builder.setEndpoint(getEndpoint());

        mRestService = builder.build().create(getClazz());
    }

    public abstract Class<T> getClazz();

    public T getRestService() {
        return mRestService;
    }

    public abstract String getEndpoint();
}

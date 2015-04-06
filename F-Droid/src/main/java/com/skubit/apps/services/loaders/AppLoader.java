package com.skubit.apps.services.loaders;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.skubit.AccountSettings;
import com.skubit.apps.services.AppService;
import com.skubit.dialog.BaseLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.shared.dto.AppDto;
import com.skubit.shared.dto.ErrorMessage;

import android.content.Context;

import java.io.IOException;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;


public class AppLoader extends BaseLoader<LoaderResult<AppDto>> {

    private final AppService mAppService;

    private final String mPackageName;

    public AppLoader(Context context, String packageName) {
        super(context);
        mPackageName = packageName;
        String account = AccountSettings.get(context).retrieveBitId();
        mAppService = new AppService(account, context);
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<AppDto> loadInBackground() {

        LoaderResult result = new LoaderResult();
        try {
            result.result = mAppService.getRestService().getApp(mPackageName);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                ErrorMessage message = readRetrofitError(e);
                if (message == null) {
                    result.errorMessage = "Bad server request";
                    return result;
                }
                if (message.getMessages() != null && message.getMessages().length > 0) {
                    result.errorMessage = message.getMessages()[0].getMessage();
                }
                return result;
            }
        }
        result.errorMessage = "Bad server request";
        return result;
    }

    public static ErrorMessage readRetrofitError(Exception e) {
        RetrofitError error = (RetrofitError) e;
        if (error.getResponse() != null) {
            String json = new String(((TypedByteArray) error.getResponse().getBody())
                    .getBytes());
            try {
                return new Gson().fromJson(json, ErrorMessage.class);
            } catch (JsonSyntaxException e1) {
            }
        }

        return null;
    }
}

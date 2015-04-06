package com.skubit.apps.services.loaders;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.skubit.AccountSettings;

import com.skubit.Utils;
import com.skubit.apps.services.LockerService;
import com.skubit.dialog.BaseLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.shared.dto.ErrorMessage;
import com.skubit.shared.dto.LockerItemListDto;

import android.content.Context;

import java.io.IOException;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

public class LockerLoader extends BaseLoader<LoaderResult<LockerItemListDto>> {

    private final LockerService mLockerService;

    private final String mApplication;

    public LockerLoader(Context context, String application) {
        super(context);
        String account = AccountSettings.get(context).retrieveBitId();
        mLockerService = new LockerService(account, context);
        mApplication = application;
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<LockerItemListDto> loadInBackground() {
        LoaderResult result = new LoaderResult();
        try {
            result.result = mLockerService.getRestService()
                    .getLockerItems(mApplication, 0, 500, null, false);//TODO
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

package com.skubit;

import com.skubit.android.billing.IBillingService;
import com.skubit.market.provider.accounts.AccountsColumns;
import com.skubit.market.provider.accounts.AccountsContentValues;

import org.fdroid.fdroid.BuildConfig;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
;
import java.util.Date;

public class Utils {

    public static final int AUTHORIZATION_CODE = 100;

    public static final int PLAY_CODE = 200;

    public static Intent getBillingServiceIntent() {
        String serviceName = BuildConfig.FLAVOR.startsWith("dev") ? Constants.IAB_TEST
                : Constants.IAB_PROD;

        Intent service = new Intent(serviceName + ".billing.IBillingService.BIND");
        service.setPackage(serviceName);
        return service;

    }
    public static String getIabPackageName() {
        return BuildConfig.FLAVOR.startsWith("dev") ? Constants.IAB_TEST
                : Constants.IAB_PROD;
    }

    public static Intent getIabIntent() {
        try {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getIabPackageName()));
        } catch (android.content.ActivityNotFoundException anfe) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getIabPackageName()));
        }
    }

    public static boolean isIabInstalled(PackageManager pm) {
        try {
            pm.getPackageInfo(getIabPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static void startAuthorization(Activity activity, IBillingService service) {
        try {
            Bundle bundle = service.getAuthorizationIntent(1,
                    BuildConfig.APPLICATION_ID, Permissions.IAB_DEFAULT);
            PendingIntent pendingIntent = bundle
                    .getParcelable("AUTHORIZATION_INTENT");

            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), AUTHORIZATION_CODE,
                    null, 0, 0, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public static void createNewAccount(Activity context, Intent data) {
        String[] tokens = data.getStringExtra("response").split("[:]");
        String account = tokens[0];

        AccountsContentValues kcv = new AccountsContentValues();
        kcv.putBitid(account);
        kcv.putToken(tokens[1]);
        kcv.putDate(new Date().getTime());

        context.getContentResolver().delete(AccountsColumns.CONTENT_URI,
                AccountsColumns.BITID + "=?",
                new String[]{
                        account
                });
        context.getContentResolver().insert(AccountsColumns.CONTENT_URI, kcv.values());

        AccountSettings.get(context).saveToken(tokens[1]);
        AccountSettings.get(context).saveBitId(account);
        Events.accountChange(context, account);

    }
}

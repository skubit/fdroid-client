package org.fdroid.fdroid.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.fdroid.fdroid.R;
import org.fdroid.fdroid.Utils;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "fdroid.DBHelper";

    public static final String DATABASE_NAME = "fdroid";

    public static final String TABLE_REPO = "fdroid_repo";

    // The TABLE_APK table stores details of all the application versions we
    // know about. Each relates directly back to an entry in TABLE_APP.
    // This information is retrieved from the repositories.
    public static final String TABLE_APK = "fdroid_apk";

    private static final String CREATE_TABLE_REPO = "create table "
            + TABLE_REPO + " (_id integer primary key, "
            + "address text not null, "
            + "name text, description text, inuse integer not null, "
            + "priority integer not null, pubkey text, fingerprint text, "
            + "maxage integer not null default 0, "
            + "version integer not null default 0, "
            + "lastetag text, lastUpdated string,"
            + "isSwap integer boolean default 0);";

    private static final String CREATE_TABLE_APK =
            "CREATE TABLE " + TABLE_APK + " ( "
            + "id text not null, "
            + "version text not null, "
            + "repo integer not null, "
            + "hash text not null, "
            + "vercode int not null,"
            + "apkName text not null, "
            + "size int not null, "
            + "sig string, "
            + "srcname string, "
            + "minSdkVersion integer, "
            + "maxSdkVersion integer, "
            + "permissions string, "
            + "features string, "
            + "nativecode string, "
            + "hashType string, "
            + "added string, "
            + "compatible int not null, "
            + "incompatibleReasons text, "
            + "primary key(id, vercode)"
            + ");";

    public static final String TABLE_APP = "fdroid_app";
    private static final String CREATE_TABLE_APP = "CREATE TABLE " + TABLE_APP
            + " ( "
            + "id text not null, "
            + "name text not null, "
            + "summary text not null, "
            + "icon text, "
            + "description text not null, "
            + "license text not null, "
            + "webURL text, "
            + "trackerURL text, "
            + "sourceURL text, "
            + "suggestedVercode text,"
            + "upstreamVersion text,"
            + "upstreamVercode integer,"
            + "antiFeatures string,"
            + "donateURL string,"
            + "bitcoinAddr string,"
            + "litecoinAddr string,"
            + "dogecoinAddr string,"
            + "flattrID string,"
            + "requirements string,"
            + "categories string,"
            + "added string,"
            + "lastUpdated string,"
            + "compatible int not null,"
            + "ignoreAllUpdates int not null,"
            + "ignoreThisUpdate int not null,"
            + "iconUrl text, "
            + "price text, "
            + "currencySymbol text, "
            + "productId text, "
            + "satoshi int, "
            + "primary key(id));";

    public static final String TABLE_INSTALLED_APP = "fdroid_installedApp";
    private static final String CREATE_TABLE_INSTALLED_APP = "CREATE TABLE " + TABLE_INSTALLED_APP
            + " ( "
            + InstalledAppProvider.DataColumns.APP_ID + " TEXT NOT NULL PRIMARY KEY, "
            + InstalledAppProvider.DataColumns.VERSION_CODE + " INT NOT NULL, "
            + InstalledAppProvider.DataColumns.VERSION_NAME + " TEXT NOT NULL, "
            + InstalledAppProvider.DataColumns.APPLICATION_LABEL + " TEXT NOT NULL "
            + " );";

    private static final int DB_VERSION = 47;

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        createAppApk(db);
        createInstalledApp(db);
        db.execSQL(CREATE_TABLE_REPO);
/*
        insertRepo(
            db,
            context.getString(R.string.fdroid_repo_name),
            context.getString(R.string.fdroid_repo_address),
            context.getString(R.string.fdroid_repo_description),
            context.getString(R.string.fdroid_repo_pubkey),
            context.getResources().getInteger(R.integer.fdroid_repo_inuse),
            context.getResources().getInteger(R.integer.fdroid_repo_priority)
        );
        */

        insertRepo(
            db,
            context.getString(R.string.skubit_main_repo_name),
            context.getString(R.string.skubit_main_repo_address),
            context.getString(R.string.skubit_main_repo_description),
            context.getString(R.string.skubit_main_repo_pubkey),
            context.getResources().getInteger(R.integer.skubit_main_repo_inuse),
            context.getResources().getInteger(R.integer.skubit_main_repo_priority)
        );

    }

    private void insertRepo(
        SQLiteDatabase db, String name, String address, String description,
        String pubKey, int inUse, int priority) {

        ContentValues values = new ContentValues();
        values.put(RepoProvider.DataColumns.ADDRESS, address);
        values.put(RepoProvider.DataColumns.NAME, name);
        values.put(RepoProvider.DataColumns.DESCRIPTION, description);
        values.put(RepoProvider.DataColumns.PUBLIC_KEY, pubKey);
        values.put(RepoProvider.DataColumns.FINGERPRINT, Utils.calcFingerprint(pubKey));
        values.put(RepoProvider.DataColumns.MAX_AGE, 0);
        values.put(RepoProvider.DataColumns.IN_USE, inUse);
        values.put(RepoProvider.DataColumns.PRIORITY, priority);
        values.put(RepoProvider.DataColumns.LAST_ETAG, (String)null);

        Log.i(TAG, "Adding repository " + name);
        db.insert(TABLE_REPO, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from v" + oldVersion + " v"
                + newVersion);
    }

    private static void createAppApk(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP);
        db.execSQL("create index app_id on " + TABLE_APP + " (id);");
        db.execSQL(CREATE_TABLE_APK);
        db.execSQL("create index apk_vercode on " + TABLE_APK + " (vercode);");
        db.execSQL("create index apk_id on " + TABLE_APK + " (id);");
    }

    private void createInstalledApp(SQLiteDatabase db) {
        Log.d(TAG, "Creating 'installed app' database table.");
        db.execSQL(CREATE_TABLE_INSTALLED_APP);
    }

    private static boolean columnExists(SQLiteDatabase db,
            String table, String column) {
        return (db.rawQuery("select * from " + table + " limit 0,1", null)
                .getColumnIndex(column) != -1);
    }

}

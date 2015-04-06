package com.skubit.market.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.skubit.market.provider.accounts.AccountsColumns;

import org.fdroid.fdroid.BuildConfig;

public class MarketSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = MarketSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "market.db";
    private static final int DATABASE_VERSION = 1;
    private static MarketSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final MarketSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_ACCOUNTS = "CREATE TABLE IF NOT EXISTS "
            + AccountsColumns.TABLE_NAME + " ( "
            + AccountsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AccountsColumns.BITID + " TEXT, "
            + AccountsColumns.TOKEN + " TEXT, "
            + AccountsColumns.DATE + " INTEGER "
            + " );";

    public static final String SQL_CREATE_INDEX_ACCOUNTS_BITID = "CREATE INDEX IDX_ACCOUNTS_BITID "
            + " ON " + AccountsColumns.TABLE_NAME + " ( " + AccountsColumns.BITID + " );";

    // @formatter:on

    public static MarketSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static MarketSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static MarketSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new MarketSQLiteOpenHelper(context);
    }

    private MarketSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new MarketSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static MarketSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new MarketSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private MarketSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new MarketSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_ACCOUNTS);
        db.execSQL(SQL_CREATE_INDEX_ACCOUNTS_BITID);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}

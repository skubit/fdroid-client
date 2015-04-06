/*
 * Copyright (C) 2010-12  Ciaran Gultnieks, ciaran@ciarang.com
 * Copyright (C) 2009  Roberto Jacinto, roberto.jacinto@caixamagica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.fdroid.fdroid;

import com.skubit.navigation.NavigationDrawerFragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.fdroid.fdroid.compat.TabManager;
import org.fdroid.fdroid.data.AppProvider;
import org.fdroid.fdroid.data.NewRepoConfig;
import org.fdroid.fdroid.views.AppListFragmentPagerAdapter;
import org.fdroid.fdroid.views.ManageReposActivity;
import org.fdroid.fdroid.views.swap.ConnectSwapActivity;
import org.fdroid.fdroid.views.swap.SwapActivity;

public class FDroid extends ActionBarActivity {

    private static final String TAG = "fdroid.FDroid";


    public static final String EXTRA_TAB_UPDATE = "extraTab";

    private ViewPager viewPager;

    private TabManager tabManager = null;

    public static final String ACTION_ADD_REPO = "org.fdroid.fdroid.FDroid.ACTION_ADD_REPO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fdroid);
        createViews();

        getTabManager().createTabs();

        Intent i = getIntent();
        Uri data = i.getData();
        String appid = null;
        if (data != null) {
            if (data.isHierarchical()) {
                // http(s)://f-droid.org/repository/browse?fdid=app.id
                appid = data.getQueryParameter("fdid");
            }
        } else if (i.hasExtra(EXTRA_TAB_UPDATE)) {
            boolean showUpdateTab = i.getBooleanExtra(EXTRA_TAB_UPDATE, false);
            if (showUpdateTab) {
                getTabManager().selectTab(2);
            }
        }
        if (appid != null && appid.length() > 0) {
            Intent call = new Intent(this, AppDetails.class);
            call.putExtra(AppDetails.EXTRA_APPID, appid);
            //TODO
          //  startActivityForResult(call, REQUEST_APPDETAILS);
        }

        Uri uri = AppProvider.getContentUri();
        getContentResolver().registerContentObserver(uri, true, new AppObserver());
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getTabManager().onConfigurationChanged(newConfig);
    }

    private void createViews() {
        viewPager = (ViewPager)findViewById(R.id.main_pager);
        AppListFragmentPagerAdapter viewPagerAdapter = new AppListFragmentPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getTabManager().selectTab(position);
            }
        });
    }

    /**
     * The first time the app is run, we will have an empty app list.
     * If this is the case, we will attempt to update with the default repo.
     * However, if we have tried this at least once, then don't try to do
     * it automatically again, because the repos or internet connection may
     * be bad.
     */
    public boolean updateEmptyRepos() {
        final String TRIED_EMPTY_UPDATE = "triedEmptyUpdate";
        boolean hasTriedEmptyUpdate = getPreferences(MODE_PRIVATE).getBoolean(TRIED_EMPTY_UPDATE, false);
        if (!hasTriedEmptyUpdate) {
            Log.d(TAG, "Empty app list, and we haven't done an update yet. Forcing repo update.");
            getPreferences(MODE_PRIVATE).edit().putBoolean(TRIED_EMPTY_UPDATE, true).commit();
            //updateRepos();TODO
            return true;
        } else {
            Log.d(TAG, "Empty app list, but it looks like we've had an update previously. Will not force repo update.");
            return false;
        }
    }

    private TabManager getTabManager() {
        if (tabManager == null) {
            tabManager = new TabManager(this, viewPager);
        }
        return tabManager;
    }

    public void refreshUpdateTabLabel() {
        getTabManager().refreshTabLabel(TabManager.INDEX_CAN_UPDATE);
    }

    public void removeNotification(int id) {
        NotificationManager nMgr = (NotificationManager) getBaseContext()
            .getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(id);
    }

    private class AppObserver extends ContentObserver {

        public AppObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            FDroid.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshUpdateTabLabel();
                }
            });
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

    }

}

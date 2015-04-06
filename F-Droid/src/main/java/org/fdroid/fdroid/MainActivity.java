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
package org.fdroid.fdroid;

import com.skubit.Constants;
import com.skubit.android.billing.IBillingService;
import com.skubit.navigation.NavigationDrawerCallbacks;
import com.skubit.navigation.NavigationDrawerFragment;

import org.fdroid.fdroid.data.NewRepoConfig;
import org.fdroid.fdroid.views.ManageReposActivity;
import org.fdroid.fdroid.views.fragments.PreferenceFragment;
import org.fdroid.fdroid.views.swap.ConnectSwapActivity;
import org.fdroid.fdroid.views.swap.SwapActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private int mCurrentPosition;

    public static final int REQUEST_APPDETAILS = 0;
    public static final int REQUEST_MANAGEREPOS = 1;
    public static final int REQUEST_PREFS = 2;
    public static final int REQUEST_ENABLE_BLUETOOTH = 3;
    public static final int REQUEST_SWAP = 4;

    public static final String ACTION_ADD_REPO = "org.fdroid.fdroid.FDroid.ACTION_ADD_REPO";

    private FDroidApp fdroidApp = null;

    private IBillingService mService;

    private ServiceConnection mServiceConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IBillingService.Stub.asInterface(service);
            if(doLogin) {
                doLogin = false;
                com.skubit.Utils.startAuthorization(MainActivity.this, mService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       fdroidApp = ((FDroidApp) getApplication());
     //   fdroidApp.applyTheme(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(
                        R.id.fragment_drawer);
        mNavigationDrawerFragment
                .setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);

        // Start a search by just typing
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        bindService(com.skubit.Utils.getBillingServiceIntent(), mServiceConn, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (fdroidApp.bluetoothAdapter == null) {
            // ignore on devices without Bluetooth
            MenuItem btItem = menu.findItem(R.id.action_bluetooth_apk);
            btItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    // Force a repo update now. A progress dialog is shown and the UpdateService
    // is told to do the update, which will result in the database changing. The
    // UpdateReceiver class should get told when this is finished.
    public void updateRepos() {
        UpdateService.updateNow(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_APPDETAILS:
                break;
            case REQUEST_MANAGEREPOS:
                if (data != null && data.hasExtra(ManageReposActivity.REQUEST_UPDATE)) {
                    AlertDialog.Builder ask_alrt = new AlertDialog.Builder(this);
                    ask_alrt.setTitle(getString(R.string.repo_update_title));
                    ask_alrt.setIcon(android.R.drawable.ic_menu_rotate);
                    ask_alrt.setMessage(getString(R.string.repo_alrt));
                    ask_alrt.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    updateRepos();
                                }
                            });
                    ask_alrt.setNegativeButton(getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    // do nothing
                                }
                            });
                    AlertDialog alert = ask_alrt.create();
                    alert.show();
                }
                break;
            case REQUEST_PREFS:
                // The automatic update settings may have changed, so reschedule (or
                // unschedule) the service accordingly. It's cheap, so no need to
                // check if the particular setting has actually been changed.
                UpdateService.schedule(getBaseContext());

                if ((resultCode & PreferencesActivity.RESULT_RESTART) != 0) {
                    ((FDroidApp) getApplication()).reloadTheme();
                    final Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }
                break;
            case REQUEST_ENABLE_BLUETOOTH:
                fdroidApp.sendViaBluetooth(this, resultCode, "org.fdroid.fdroid");
                break;
        }

        if (requestCode == com.skubit.Utils.AUTHORIZATION_CODE && data != null && !TextUtils
                .isEmpty(data.getStringExtra("response"))) {
            com.skubit.Utils.createNewAccount(this, data);
        } else if(requestCode == com.skubit.Utils.PLAY_CODE) {
            doLogin = true;
            bindService(com.skubit.Utils.getBillingServiceIntent(), mServiceConn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                if(mService != null) {
                    com.skubit.Utils.startAuthorization(this, mService);
                } else {
                    if(!com.skubit.Utils.isIabInstalled(getPackageManager())) {
                        startActivityForResult(com.skubit.Utils.getIabIntent(), com.skubit.Utils.PLAY_CODE);
                    }
                }
                return true;
            case R.id.action_update_repo:
                updateRepos();
                return true;

            case R.id.action_manage_repos:
                Intent i = new Intent(this, ManageReposActivity.class);
                startActivityForResult(i, REQUEST_MANAGEREPOS);
                return true;

            case R.id.action_settings:
                Intent prefs = new Intent(getBaseContext(), PreferencesActivity.class);
                startActivityForResult(prefs, REQUEST_PREFS);
                return true;

            case R.id.action_swap:
                startActivity(new Intent(this, SwapActivity.class));
                return true;

            case R.id.action_search:
                onSearchRequested();
                return true;

            case R.id.action_bluetooth_apk:
                /*
                 * If Bluetooth has not been enabled/turned on, then enabling
                 * device discoverability will automatically enable Bluetooth
                 */
                Intent discoverBt = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverBt.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 121);
                startActivityForResult(discoverBt, REQUEST_ENABLE_BLUETOOTH);
                // if this is successful, the Bluetooth transfer is started
                return true;

            case R.id.action_about:
                View view = null;
                if (Build.VERSION.SDK_INT >= 11) {
                    LayoutInflater li = LayoutInflater.from(this);
                    view = li.inflate(R.layout.about, null);
                } else {
                    view = View.inflate(
                            new ContextThemeWrapper(this, R.style.AboutDialogLight),
                            R.layout.about, null);
                }

                // Fill in the version...
                try {
                    PackageInfo pi = getPackageManager()
                            .getPackageInfo(getApplicationContext()
                                    .getPackageName(), 0);
                    ((TextView) view.findViewById(R.id.version))
                            .setText(pi.versionName);
                } catch (Exception e) {
                }

                AlertDialog.Builder p = null;
                if (Build.VERSION.SDK_INT >= 11) {
                    p = new AlertDialog.Builder(this).setView(view);
                } else {
                    p = new AlertDialog.Builder(
                            new ContextThemeWrapper(
                                    this, R.style.AboutDialogLight)
                    ).setView(view);
                }
                final AlertDialog alrt = p.create();
                alrt.setIcon(R.drawable.ic_launcher_market);
                alrt.setTitle(getString(R.string.about_title));
                alrt.setButton(AlertDialog.BUTTON_NEUTRAL,
                        getString(R.string.about_website),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                Uri uri = Uri.parse("https://f-droid.org");
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            }
                        });
                alrt.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                            }
                        });
                alrt.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // AppDetails and RepoDetailsActivity set different NFC actions, so reset here
        NfcHelper.setAndroidBeam(this, getApplication().getPackageName());
        checkForAddRepoIntent();
    }

    private void checkForAddRepoIntent() {
        // Don't handle the intent after coming back to this view (e.g. after hitting the back button)
        // http://stackoverflow.com/a/14820849
        if (!getIntent().hasExtra("handled")) {
            NewRepoConfig parser = new NewRepoConfig(this, getIntent());
            if (parser.isValidRepo()) {
                getIntent().putExtra("handled", true);
                if (parser.isFromSwap()) {
                    startActivityForResult(new Intent(ACTION_ADD_REPO, getIntent().getData(), this, ConnectSwapActivity.class), REQUEST_SWAP);
                } else {
                    startActivity(new Intent(ACTION_ADD_REPO, getIntent().getData(), this, ManageReposActivity.class));
                }
            } else if (parser.getErrorMessage() != null) {
                Toast.makeText(this, parser.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean doLogin;

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == 0) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new FDroidFragment(), "fdroid")
                    .commit();
            setTitle("Catalog");
        }  else if (position == 1) {
            PreferenceFragment preferenceFragment = new PreferenceFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, preferenceFragment, "pref")
                    .commit();

        } else if (position == 2) {
            String serviceName = BuildConfig.FLAVOR.startsWith("dev") ? Constants.IAB_TEST
                    : Constants.IAB_PROD;
            Intent i = new Intent(Constants.IAB_TEST + ".MAIN");
            startActivity(i);

        }
        mCurrentPosition = position;

    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            this.unbindService(mServiceConn);
        }
    }
}

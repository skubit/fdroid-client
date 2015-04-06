package com.skubit.navigation;

import com.skubit.AccountSettings;
import com.skubit.Intents;
import com.skubit.market.provider.accounts.AccountsColumns;

import org.fdroid.fdroid.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poliveira on 24/10/2014.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private static final String PREFERENCES_FILE = "my_app_settings";

    private NavigationDrawerCallbacks mCallbacks;

    private RecyclerView mDrawerList;

    private View mFragmentContainerView;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private boolean mUserLearnedDrawer;

    private boolean mFromSavedInstanceState;

    private int mCurrentSelectedPosition;

    private BitIdAccountView mAccountView;

    private BroadcastReceiver mAccountChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAccountView != null) {
                mAccountView.displayAccountName(intent.getStringExtra(Intents.ACCOUNT_NAME));
            }

        }
    };

    public NavigationDrawerFragment() {
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx
                .getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx
                .getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAccountView.displayAccountName(AccountSettings.get(getActivity()).retrieveBitId());

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mAccountChangeReceiver,
                Intents.accountChangeFilter());

    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mAccountChangeReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);

        mAccountView = (BitIdAccountView) view.findViewById(R.id.bitid_accounts);

        List<NavigationItem> navigationItems = getMenu();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems);
        adapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(adapter);
        selectItem(mCurrentSelectedPosition);

        Cursor c = getActivity().getContentResolver()
                .query(AccountsColumns.CONTENT_URI, null, null, null, null);
        mAccountView.initialize(getActivity(),
                new AccountDropDownClickEvent(getActivity().getBaseContext(), c), c,
                AccountsColumns.BITID, AccountSettings.get(getActivity()).retrieveBitId());

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean
                .valueOf(readSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
        mActionBarDrawerToggle = actionBarDrawerToggle;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "true");
                }

                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public List<NavigationItem> getMenu() {
        List<NavigationItem> items = new ArrayList<NavigationItem>();

        Drawable shop = getResources().getDrawable(R.drawable.ic_shop_black_18dp);
        shop.setAlpha(180);
        items.add(new NavigationItem("Catalog", shop));

        Drawable account = getResources().getDrawable(R.drawable.ic_account_circle_black_18dp);
        account.setAlpha(180);
        items.add(new NavigationItem("Preferences", account));

        Drawable wallet = getResources()
                .getDrawable(R.drawable.ic_account_balance_wallet_black_18dp);
        wallet.setAlpha(180);
        items.add(new NavigationItem("Wallet", wallet));
         /*
        Drawable publisher = getResources().getDrawable(R.drawable.ic_domain_black_18dp);
        Drawable series = getResources().getDrawable(R.drawable.ic_menu_black_18dp);
        Drawable collections = getResources().getDrawable(R.drawable.ic_view_module_black_18dp);

        Drawable myComics = getResources().getDrawable(R.drawable.ic_apps_black_18dp);
        Drawable locker = getResources().getDrawable(R.drawable.ic_archive_black_18dp);
        Drawable wallet = getResources()
                .getDrawable(R.drawable.ic_account_balance_wallet_black_18dp);


        myComics.setAlpha(180);
        locker.setAlpha(180);

        publisher.setAlpha(180);
        series.setAlpha(180);
        collections.setAlpha(180);


        items.add(new NavigationItem("My Comics", myComics));
        items.add(new NavigationItem("My Collections", collections));
        items.add(new NavigationItem("Locker", locker));
        */
        return items;
    }

    void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }

        if (position != 2) {//Don't highlight wallet
            ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }
}
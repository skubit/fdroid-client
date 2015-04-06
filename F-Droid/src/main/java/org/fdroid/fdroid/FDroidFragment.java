package org.fdroid.fdroid;

import org.fdroid.fdroid.views.AppListFragmentPagerAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FDroidFragment extends Fragment {

    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fdroid, null);
        createViews(view);
        return view;
    }

    private void createViews(View v) {
        viewPager = (ViewPager) v.findViewById(R.id.main_pager);

        AppListFragmentPagerAdapter viewPagerAdapter = new AppListFragmentPagerAdapter(
                (ActionBarActivity) getActivity());

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
              //  getTabManager().selectTab(position);
            }
        });
    }
}

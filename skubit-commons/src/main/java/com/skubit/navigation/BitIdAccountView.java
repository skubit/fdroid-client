/**
 * Copyright 2014 Skubit
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
package com.skubit.navigation;

import com.skubit.dialog.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class BitIdAccountView extends LinearLayout {

    protected ListView mDropdownList;

    private SimpleCursorAdapter mAdapter;

    private Context mContext;

    private View mDivider;

    private ImageView mExpanderIcon;

    private View mSpinner;

    public BitIdAccountView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.drawer_account_bitid, this, true);
    }

    public BitIdAccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.drawer_account_bitid, this, true);
    }

    public BitIdAccountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.drawer_account_bitid, this, true);
    }

    private static void styleAccountEmail(TextView view, String email) {
        view.setText(email);
        //    view.setTypeface(FontManager.REGULAR);
    }

    public void closeList() {
        mDropdownList.setVisibility(View.GONE);
        mExpanderIcon.setImageResource(R.drawable.ic_arrow_drop_down_black_36dp);
        mDivider.setVisibility(View.GONE);
    }

    public void initialize(final Activity activity, final DropDownClickEvent event,
            final Cursor c, String accountColName, String currentAccountName) {

        mDropdownList = (ListView) findViewById(R.id.account_dropdown);

        mAdapter = new SimpleCursorAdapter(activity,
                R.layout.drawer_account_drop_item, c,
                new String[]{accountColName}, new int[]{R.id.account_name},
                CursorAdapter.FLAG_AUTO_REQUERY);

        mSpinner = findViewById(R.id.account_bitid);
        mSpinner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageView expander = (ImageView) mSpinner.findViewById(R.id.expander);

                if (!mDropdownList.isShown()) {
                    mDropdownList.setVisibility(View.VISIBLE);
                    mDivider.setVisibility(View.VISIBLE);
                    expander.setImageResource(R.drawable.ic_arrow_drop_up_black_36dp);
                } else {
                    mDropdownList.setVisibility(View.GONE);
                    expander.setImageResource(R.drawable.ic_arrow_drop_down_black_36dp);
                    mDivider.setVisibility(View.GONE);
                }

            }
        });

        mExpanderIcon = (ImageView) mSpinner.findViewById(R.id.expander);
        mExpanderIcon.setImageResource(R.drawable.ic_arrow_drop_down_black_36dp);
        mDivider = findViewById(R.id.account_divider_bottom);
        mDivider.setVisibility(View.GONE);
        mDropdownList.setAdapter(mAdapter);

        mDropdownList
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                            View view, int position, long arg3) {
                        ImageView expander = (ImageView) mSpinner.findViewById(R.id.expander);
                        expander.setImageResource(R.drawable.ic_arrow_drop_down_black_36dp);

                        displayAccountName(event.onClick(adapterView, view, position, arg3));
                        //   Intent data = new Intent("signout");
                        //   data.putExtra(AccountManager.KEY_ACCOUNT_NAME, ac.getBitid());
                        //   LocalBroadcastManager.getInstance(mContext).sendBroadcast(data);

                        mDivider.setVisibility(View.GONE);
                        mDropdownList.setVisibility(View.GONE);

                    }
                });
        displayAccountName(currentAccountName);
    }

    public void displayAccountName(String name) {
        mAdapter.notifyDataSetChanged();

        TextView tv = (TextView) mSpinner.findViewById(R.id.account_name);
        styleAccountEmail(tv, name);
    }
}

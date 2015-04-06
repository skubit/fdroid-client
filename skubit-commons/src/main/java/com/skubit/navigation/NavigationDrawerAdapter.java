/**
 * Copyright 2015 Skubit
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

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NavigationDrawerAdapter
        extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    private List<NavigationItem> mData;

    private NavigationDrawerCallbacks mNavigationDrawerCallbacks;

    private int mSelectedPosition;

    private int mTouchedPosition = -1;

    public NavigationDrawerAdapter(List<NavigationItem> data) {
        mData = data;
    }

    public NavigationDrawerCallbacks getNavigationDrawerCallbacks() {
        return mNavigationDrawerCallbacks;
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks navigationDrawerCallbacks) {
        mNavigationDrawerCallbacks = navigationDrawerCallbacks;
    }

    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.drawer_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.textView.setText(mData.get(i).getText());

        viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                                                   @Override
                                                   public boolean onTouch(View v,
                                                           MotionEvent event) {

                                                       switch (event.getAction()) {
                                                           case MotionEvent.ACTION_DOWN:
                                                               touchPosition(i);
                                                               return false;
                                                           case MotionEvent.ACTION_CANCEL:
                                                               touchPosition(-1);
                                                               return false;
                                                           case MotionEvent.ACTION_MOVE:
                                                               return false;
                                                           case MotionEvent.ACTION_UP:
                                                               touchPosition(-1);
                                                               return false;
                                                       }
                                                       return true;
                                                   }
                                               }
        );
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       if (mNavigationDrawerCallbacks != null) {
                                                           mNavigationDrawerCallbacks
                                                                   .onNavigationDrawerItemSelected(
                                                                           i);
                                                       }
                                                   }
                                               }
        );

        //TODO: selected menu position, change layout accordingly
        viewHolder.textView
                .setCompoundDrawablesWithIntrinsicBounds(mData.get(i).getDrawable(), null, null,
                        null);

        if (mSelectedPosition == i || mTouchedPosition == i) {
            viewHolder.textView.setTextColor(viewHolder.itemView.getContext().getResources()
                    .getColor(R.color.primary_color));
            viewHolder.textView
                    .setTypeface(viewHolder.textView.getTypeface(), Typeface.BOLD);
            viewHolder.itemView.setBackgroundColor(
                    viewHolder.itemView.getContext().getResources().getColor(R.color.selected_nav));
        } else {
            viewHolder.textView
                    .setTypeface(null, Typeface.NORMAL);
            viewHolder.textView.setTextColor(viewHolder.itemView.getContext().getResources()
                    .getColor(R.color.default_text_color));
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void touchPosition(int position) {
        int lastPosition = mTouchedPosition;
        mTouchedPosition = position;
        if (lastPosition >= 0) {
            notifyItemChanged(lastPosition);
        }
        if (position >= 0) {
            notifyItemChanged(position);
        }
    }

    public void selectPosition(int position) {
        int lastPosition = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}

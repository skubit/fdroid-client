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
package com.skubit.dialog;

import com.gc.materialdesign.views.ButtonFlat;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowMessageFragment extends Fragment {

    public static Fragment newInstance(String message, int width, int height) {
        ShowMessageFragment messageFragment = new ShowMessageFragment();
        Bundle data = new Bundle();
        data.putString("com.skubit.dialog.MESSAGE", message);
        data.putInt("com.skubit.dialog.WIDTH", width);
        data.putInt("com.skubit.dialog.HEIGHT", height);
        messageFragment.setArguments(data);
        return messageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final String message = getArguments().getString("com.skubit.dialog.MESSAGE");
        int width = getArguments().getInt("com.skubit.dialog.WIDTH");
        int height = getArguments().getInt("com.skubit.dialog.HEIGHT");

        View view = inflater.inflate(R.layout.fragment_message, container, false);
        View frame = view.findViewById(R.id.finished_message);

        frame.setMinimumWidth(width);
        frame.setMinimumHeight(height);

        TextView finishedText = (TextView) view.findViewById(R.id.finished_text);
        finishedText.setText(message);

        ButtonFlat cancelBtn = (ButtonFlat) view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }
}

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

import android.os.Bundle;

public interface ProgressCallback {

    void sendResultsBackToCaller(int resultCode, String message);

    void sendResultsBackToCaller(int resultCode, String message, boolean finish);

    void sendResultsBackToCaller(int resultCode, Bundle data);

    void sendResultsBackToCaller(int resultCode, Bundle data, boolean finish);

    void cancel();

    void showProgress();

    void hideProgress();

    void showMessage(String message);
}

/*
 * Copyright 2013 Benjamin Linskey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.benlinskey.greekreference;

/**
 * Created by blinskey on 1/4/14.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public abstract class DetailFragment extends Fragment {
    // We use a single Toast object to prevent overlapping toasts when the user repeatedly taps an
    // icon that displays a toast.
    protected Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
    }

    protected void displayToast(String message) {
        mToast.setText(message);
        mToast.show();
    }
}
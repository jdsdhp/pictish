/*
 * Copyright (c) 2020 jesusd0897.
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

package com.jesusd0897.pictish.util

import android.os.SystemClock
import android.view.View

internal class SafeClickListener(
    private var defaultInterval: Int = 500,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {

    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) return
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }

}

internal class SafeLongClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnLongClickListener {

    private var lastTimeClicked: Long = 0

    override fun onLongClick(v: View): Boolean {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) return false
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
        return true
    }

}
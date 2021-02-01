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

package com.jesusd0897.pictish

interface OnPictishLoadListener {
    fun onThumbLoadSuccess(url: String)
    fun onThumbLoadError(url: String, e: Exception?)
    fun onFullLoadSuccess(url: String) = Unit
    fun onFullLoadError(url: String?, e: Exception?)
}

interface OnPictishClickListener {
    fun onClick(fullUrl: String?, thumbUrl: String?)
    fun onLongClick(fullUrl: String?, thumbUrl: String?)
}

interface OnPictishButtonClickListener {
    fun onIdleButtonClick(fullUrl: String?, thumbUrl: String?)
    fun onCancelButtonClick(fullUrl: String?, thumbUrl: String?)
    fun onFinishButtonClick(fullUrl: String?, thumbUrl: String?)
}
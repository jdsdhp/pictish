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

package com.jesusd0897.pictish.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jesusd0897.pictish.OnPictishButtonClickListener
import com.jesusd0897.pictish.OnPictishClickListener
import com.jesusd0897.pictish.OnPictishLoadListener
import com.jesusd0897.pictish.Pictish
import com.jesusd0897.pictish.util.provideTrustingOkHttp
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

const val TAG = "tag/dev"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val id = Random.nextInt(1, 500)

        val pictish = Pictish.Builder(context = this, parentView = container)
            .setThumbUrl("https://picsum.photos/id/$id/50")
            .setFullUrl("https://picsum.photos/id/$id/1000")
            .setPicasso(
                Picasso.Builder(this).downloader(OkHttp3Downloader(provideTrustingOkHttp())).build()
            )
            .isCancelable(true)
            .useCache(false)
            .setButtonWith(100)
            .setButtonHeight(100)
            .setIconWith(64)
            .setIconHeight(64)
            .setImageBorderRadius(16f)
            .setProgressIndeterminateSweepAngle(90)
            .setProgressMargin(4)
            .setBlurRadius(10)
            .setEmptyPlaceholder(ContextCompat.getDrawable(this, R.drawable.ic_default_placeholder))
            .setButtonIdleIcon(ContextCompat.getDrawable(this, R.drawable.ic_default_idle_icon))
            .setButtonCancelIcon(ContextCompat.getDrawable(this, R.drawable.ic_default_cancel_icon))
            .setButtonFinishIcon(ContextCompat.getDrawable(this, R.drawable.ic_default_finish_icon))
            .setIdleBgColor(ContextCompat.getColor(this, R.color.color_default_idle_background))
            .setErrorBgColor(ContextCompat.getColor(this, R.color.color_default_error_background))
            .setFinishBgColor(ContextCompat.getColor(this, R.color.color_default_finish_background))
            .setIndeterminateBgColor(
                ContextCompat.getColor(this, R.color.color_default_indeterminate_background)
            )
            .setDeterminateBgColor(
                ContextCompat.getColor(this, R.color.color_default_determinate_background)
            )
            .setProgressDeterminateColor(
                ContextCompat.getColor(this, R.color.color_default_determinate_progress)
            )
            .setProgressIndeterminateColor(
                ContextCompat.getColor(this, R.color.color_default_indeterminate_progress)
            )
            .build()
            .preLoad()

        pictish.onPictishLoadListener = object : OnPictishLoadListener {
            override fun onThumbLoadSuccess(url: String) {
                Log.d(TAG, "onThumbLoadSuccess: url = $url")
            }

            override fun onThumbLoadError(url: String, e: Exception?) {
                Log.d(TAG, "onThumbLoadError: url = $url\n e = $e")
            }

            override fun onFullLoadSuccess(url: String) {
                Log.d(TAG, "onFullLoadSuccess: url = $url")
            }

            override fun onFullLoadError(url: String?, e: Exception?) {
                Log.d(TAG, "onFullLoadError: url = $url\n e = $e")
            }
        }

        pictish.onPictishClickListener = object : OnPictishClickListener {
            override fun onClick(fullUrl: String?, thumbUrl: String?) {
                Toast.makeText(
                    this@MainActivity,
                    "onClick: fullUrl = $fullUrl\nthumbUrl = $thumbUrl",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onLongClick(fullUrl: String?, thumbUrl: String?) {
                Toast.makeText(
                    this@MainActivity,
                    "onLongClick: fullUrl = $fullUrl\nthumbUrl = $thumbUrl",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        pictish.onPictishButtonClickListener = object : OnPictishButtonClickListener {
            override fun onIdleButtonClick(fullUrl: String?, thumbUrl: String?) {
                Log.d(TAG, "onIdleButtonClick: fullUrl = $fullUrl\nthumbUrl = $thumbUrl")
            }

            override fun onCancelButtonClick(fullUrl: String?, thumbUrl: String?) {
                Log.d(TAG, "onCancelButtonClick: fullUrl = $fullUrl\nthumbUrl = $thumbUrl")
            }

            override fun onFinishButtonClick(fullUrl: String?, thumbUrl: String?) {
                Log.d(TAG, "onFinishButtonClick: fullUrl = $fullUrl\nthumbUrl = $thumbUrl")
            }
        }

    }

}
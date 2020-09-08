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
import androidx.appcompat.app.AppCompatActivity
import com.jesusd0897.pictish.ImageViewDownloader
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val id = Random.nextInt(1, 500)//101

        val imageDownloadView: ImageViewDownloader = findViewById(R.id.image_view_downloader)
        imageDownloadView.preLoad(
            thumbUrl = "https://picsum.photos/id/$id/50",
            fullUrl = "https://picsum.photos/id/$id/1000"
        )
    }

}
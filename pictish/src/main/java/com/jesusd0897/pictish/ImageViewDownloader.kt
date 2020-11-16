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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.jesusd0897.pictish.util.fastblur
import com.jesusd0897.pictish.util.provideOkHttpClient
import com.jesusd0897.pictish.util.setSafeOnClickListener
import com.nihaskalam.progressbuttonlibrary.CircularProgressButton
import com.squareup.picasso.*

class ImageViewDownloader : FrameLayout {

    lateinit var shapeableImageView: ShapeableImageView
    lateinit var progressButton: CircularProgressButton

    var thumbUrl: String? = null
    var fullUrl: String? = null

    @DimenRes
    var imageBorderRadius = R.dimen.dim_card_corner_radius

    var isIndeterminate = true

    private val blurTransformation = object : Transformation {
        override fun transform(source: Bitmap): Bitmap? {
            val blurred = fastblur(context, source, 10)
            source.recycle()
            return blurred
        }

        override fun key(): String? {
            return "blur()"
        }
    }

    private val clickListener = OnClickListener {
        progressButton.showIdle()
        progressButton.showProgress()
        fullUrl?.let { it1 ->
            picassoLoad(
                shapeableImageView,
                it1,
                placeholder = shapeableImageView.drawable,
                useCache = false,
                callback = object : Callback {
                    override fun onSuccess() {
                        progressButton.visibility = View.INVISIBLE
                        progressButton.showComplete()
                        progressButton.showCancel()
                    }

                    override fun onError(e: java.lang.Exception?) {
                        progressButton.showError()
                    }

                })
        }
    }

    constructor(context: Context) : super(context) {
        initAttributes(context, null)
        initUI()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttributes(context, attrs)
        initUI()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initAttributes(context, attrs)
        initUI()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttributes(context, attrs)
        initUI()
    }

    private fun initUI() {
        val view = LayoutInflater.from(context).inflate(R.layout.pic_item, null)
        shapeableImageView = view.findViewById(R.id.image_view)
        progressButton = view.findViewById(R.id.download_btn)
        progressButton.setSafeOnClickListener { clickListener.onClick(it) }

        progressButton.isIndeterminateProgressMode = isIndeterminate
        shapeableImageView.shapeAppearanceModel = shapeableImageView.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, imageBorderRadius.toFloat())
            .build()

        if (!thumbUrl.isNullOrBlank() && !fullUrl.isNullOrBlank()) preLoad(thumbUrl!!, fullUrl!!)
        addView(view)
    }

    open fun initAttributes(context: Context, attributeSet: AttributeSet?) {
        val attr = getTypedArray(context, attributeSet, R.styleable.ImageViewDownloader)
        try {
            //thumbUrl = attr.getString(R.styleable.ImageViewDownloader_ivd_url_thumb)!!
            //fullUrl = attr.getString(R.styleable.ImageViewDownloader_ivd_url_full)!!
            //progressColor = attr.getResourceId(R.styleable.ImageViewDownloader_ivd_color_progress,0)
            //indicatorColor =  attr.getResourceId(R.styleable.ImageViewDownloader_ivd_color_indicator,0)
            //indicatorBackgroundColor =  attr.getResourceId(R.styleable.ImageViewDownloader_ivd_color_indicator_background,0)
            imageBorderRadius = attr.getDimensionPixelSize(
                R.styleable.ImageViewDownloader_ivd_image_border_radius, 0
            )
            isIndeterminate =
                attr.getBoolean(R.styleable.ImageViewDownloader_ivd_is_indeterminate, true)
        } finally {
            attr.recycle()
        }

    }

    protected fun getTypedArray(context: Context, attributeSet: AttributeSet?, attr: IntArray) =
        context.obtainStyledAttributes(attributeSet, attr, 0, 0)

    fun preLoad(thumbUrl: String, fullUrl: String) {
        this.thumbUrl = thumbUrl
        this.fullUrl = fullUrl
        this.fullUrl?.let {
            picassoLoad(shapeableImageView, it, useCache = true, callback = object : Callback {
                override fun onSuccess() {
                    progressButton.visibility = View.GONE
                    //Do nothing. Image was loaded successfully.
                }

                override fun onError(e: Exception?) {
                    progressButton.isIndeterminateProgressMode = isIndeterminate
                    progressButton.showCancel()
                    progressButton.visibility = View.VISIBLE
                    this@ImageViewDownloader.thumbUrl?.let { it1 ->
                        picassoLoad(
                            shapeableImageView,
                            it1,
                            useCache = false,
                            transformation = blurTransformation
                        )
                    }
                }
            })
        }
    }

    private fun picassoLoad(
        imageView: ImageView,
        url: String,
        @DrawableRes placeholder: Int = R.drawable.ic_default_placeholder,
        useCache: Boolean = false,
        transformation: Transformation? = null,
        callback: Callback? = null
    ) {
        val picasso =
            Picasso.Builder(imageView.context)
                .downloader(OkHttp3Downloader(provideOkHttpClient()))
                .build()
                .load(url)
                .placeholder(placeholder)
        if (useCache) picasso.networkPolicy(NetworkPolicy.OFFLINE)
        if (transformation != null) picasso.transform(transformation)
        picasso.into(imageView, callback)
    }

    private fun picassoLoad(
        imageView: ImageView,
        url: String,
        placeholder: Drawable,
        useCache: Boolean = false,
        transformation: Transformation? = null,
        callback: Callback? = null
    ) {
        val picasso =
            Picasso.Builder(imageView.context)
                .downloader(OkHttp3Downloader(provideOkHttpClient()))
                .build()
                .load(url)
                .placeholder(placeholder)
        if (useCache) picasso.networkPolicy(NetworkPolicy.OFFLINE)
        if (transformation != null) picasso.transform(blurTransformation)
        picasso.into(imageView, callback)
    }

}
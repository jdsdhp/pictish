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
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.github.abdularis.buttonprogress.DownloadButtonProgress
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.jesusd0897.pictish.util.fastBlur
import com.squareup.picasso.*


class Pictish(builder: Builder) {

    private val picasso: Picasso = builder.picasso

    private val thumbUrl: String? = builder.thumbUrl
    private val fullUrl: String? = builder.fullUrl

    private val imageBorderRadius: Float = builder.imageBorderRadius
    private val blurRadius: Int = builder.blurRadius
    private val progress: Int = builder.progress
    private val maxProgress: Int = builder.maxProgress
    private val progressMargin: Int = builder.progressMargin
    private val progressIndeterminateSweepAngle: Int = builder.progressIndeterminateSweepAngle
    private val iconWith: Int = builder.iconWith
    private val iconHeight: Int = builder.iconHeight
    private val buttonWith: Int = builder.buttonWith
    private val buttonHeight: Int = builder.buttonHeight

    //private val isIndeterminate: Boolean = builder.isIndeterminate
    private val useCache: Boolean = builder.useCache
    private val isCancelable: Boolean = builder.isCancelable

    private val buttonIdleIcon: Drawable? = builder.buttonIdleIcon
    private val buttonCancelIcon: Drawable? = builder.buttonCancelIcon
    private val buttonFinishIcon: Drawable? = builder.buttonFinishIcon
    private val buttonErrorIcon: Drawable? = builder.buttonErrorIcon
    private val emptyPlaceholder: Drawable? = builder.emptyPlaceholder

    @ColorInt
    private val idleBgColor: Int = builder.idleBgColor

    @ColorInt
    private val errorBgColor: Int = builder.errorBgColor

    @ColorInt
    private val finishBgColor: Int = builder.finishBgColor

    @ColorInt
    private val determinateBgColor: Int = builder.determinateBgColor

    @ColorInt
    private val indeterminateBgColor: Int = builder.indeterminateBgColor

    @ColorInt
    private val progressDeterminateColor: Int = builder.progressDeterminateColor

    @ColorInt
    private val progressIndeterminateColor: Int = builder.progressIndeterminateColor

    private val parentView: ViewGroup = builder.parentView

    private val blurTransformation = object : Transformation {
        override fun transform(source: Bitmap): Bitmap? {
            val blurred = fastBlur(source, blurRadius)
            source.recycle()
            return blurred
        }

        override fun key(): String {
            return "blur()"
        }
    }

    var isFullLoaded = false
    var onPictishLoadListener: OnPictishLoadListener? = null
    var onPictishClickListener: OnPictishClickListener? = null
    var onPictishButtonClickListener: OnPictishButtonClickListener? = null

    lateinit var shapeableImageView: ShapeableImageView
    lateinit var progressButton: DownloadButtonProgress

    private val clickListener = OnClickListener {
        progressButton.setIndeterminate()
        picassoLoad(
            imageView = shapeableImageView,
            url = fullUrl,
            placeholder = shapeableImageView.drawable,
            useCache = false,
            transformation = blurTransformation,
            callback = object : Callback {
                override fun onSuccess() {
                    progressButton.visibility = View.INVISIBLE
                    progressButton.finishIcon = buttonFinishIcon
                    progressButton.finishBgColor = finishBgColor
                    progressButton.setFinish()
                    fullUrl?.let { it1 ->
                        isFullLoaded = true
                        onPictishLoadListener?.onFullLoadSuccess(it1)
                    }
                }

                override fun onError(e: Exception?) {
                    progressButton.finishIcon = buttonErrorIcon
                    progressButton.finishBgColor = errorBgColor
                    progressButton.setFinish()
                    isFullLoaded = false
                    onPictishLoadListener?.onFullLoadError(fullUrl, e)
                }
            })
    }

    init {
        val view = LayoutInflater.from(parentView.context).inflate(R.layout.pic_item, null)
        shapeableImageView = view.findViewById(R.id.image_view)
        progressButton = view.findViewById(R.id.progress_btn)

        progressButton.apply {
            //if (isIndeterminate) setIndeterminate() else setDeterminate()
            isCancelable = this@Pictish.isCancelable
            currentProgress = this@Pictish.progress
            maxProgress = this@Pictish.maxProgress
            progressMargin = this@Pictish.progressMargin
            progressIndeterminateSweepAngle = this@Pictish.progressIndeterminateSweepAngle
            this@Pictish.iconWith.apply {
                idleIconWidth = this
                cancelIconWidth = this
                finishIconWidth = this
            }
            this@Pictish.iconHeight.apply {
                idleIconHeight = this
                cancelIconHeight = this
                finishIconHeight = this
            }
            progressButton.layoutParams.apply {
                width = this@Pictish.buttonWith
                height = this@Pictish.buttonHeight
            }

            idleIcon = this@Pictish.buttonIdleIcon
            cancelIcon = this@Pictish.buttonCancelIcon
            finishIcon = this@Pictish.buttonFinishIcon

            idleBgColor = this@Pictish.idleBgColor
            finishBgColor = this@Pictish.finishBgColor
            determinateBgColor = this@Pictish.determinateBgColor
            indeterminateBgColor = this@Pictish.indeterminateBgColor
            progressDeterminateColor = this@Pictish.progressDeterminateColor
            progressIndeterminateColor = this@Pictish.progressIndeterminateColor

            addOnClickListener(object : DownloadButtonProgress.OnClickListener {
                override fun onIdleButtonClick(view: View) {
                    clickListener.onClick(view)
                    onPictishButtonClickListener?.onIdleButtonClick(fullUrl, thumbUrl)
                }

                override fun onCancelButtonClick(view: View) {
                    picasso.cancelRequest(shapeableImageView)
                    if (!isFullLoaded) progressButton.setIdle()
                    onPictishButtonClickListener?.onCancelButtonClick(fullUrl, thumbUrl)
                }

                override fun onFinishButtonClick(view: View) {
                    if (!isFullLoaded) clickListener.onClick(view)
                    onPictishButtonClickListener?.onFinishButtonClick(fullUrl, thumbUrl)
                }
            })
        }

        shapeableImageView.shapeAppearanceModel = shapeableImageView.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, imageBorderRadius)
            .build()
        shapeableImageView.setOnClickListener {
            if (isFullLoaded) onPictishClickListener?.onClick(
                fullUrl = fullUrl,
                thumbUrl = thumbUrl
            )
        }
        shapeableImageView.setOnLongClickListener {
            if (isFullLoaded) onPictishClickListener?.onLongClick(
                fullUrl = fullUrl,
                thumbUrl = thumbUrl
            )
            true
        }

        if (!thumbUrl.isNullOrBlank() && !fullUrl.isNullOrBlank()) preLoad()
        parentView.addView(
            view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    fun preLoad(): Pictish {
        picassoLoad(
            imageView = shapeableImageView,
            url = fullUrl,
            placeholder = null,
            useCache = useCache,
            transformation = blurTransformation,
            callback = object : Callback {
                override fun onSuccess() {
                    progressButton.visibility = View.GONE
                    progressButton.finishIcon = buttonFinishIcon
                    progressButton.finishBgColor = finishBgColor
                    progressButton.setFinish()
                    fullUrl?.let {
                        isFullLoaded = true
                        onPictishLoadListener?.onFullLoadSuccess(it)
                    }
                }

                override fun onError(e: Exception?) {
                    progressButton.setIdle()
                    progressButton.visibility = View.VISIBLE
                    isFullLoaded = false
                    onPictishLoadListener?.onFullLoadError(fullUrl, e)
                    picassoLoad(
                        imageView = shapeableImageView,
                        url = thumbUrl,
                        placeholder = emptyPlaceholder,
                        useCache = false,
                        transformation = blurTransformation,
                        callback = object : Callback {
                            override fun onSuccess() {
                                thumbUrl?.let { onPictishLoadListener?.onThumbLoadSuccess(it) }
                            }

                            override fun onError(e: Exception?) {
                                thumbUrl?.let { onPictishLoadListener?.onThumbLoadError(it, e) }
                            }
                        },
                    )
                }
            })
        return this
    }

    private fun picassoLoad(
        imageView: ImageView,
        url: String?,
        placeholder: Drawable?,
        useCache: Boolean,
        transformation: Transformation?,
        callback: Callback?,
    ) {
        val creator = picasso.load(url)
        placeholder?.let { creator.placeholder(it) }
        transformation?.let { creator.transform(it) }
        if (useCache) creator.networkPolicy(NetworkPolicy.OFFLINE)
        creator.into(imageView, callback)
    }

    fun forceFullLoad(): Pictish {
        progressButton.visibility = View.GONE
        picassoLoad(
            imageView = shapeableImageView,
            url = fullUrl,
            placeholder = emptyPlaceholder,
            useCache = false,
            transformation = null,
            callback = object : Callback {
                override fun onSuccess() {
                    fullUrl?.let {
                        isFullLoaded = true
                        onPictishLoadListener?.onFullLoadSuccess(it)
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    isFullLoaded = false
                    onPictishLoadListener?.onFullLoadError(fullUrl, e)
                }

            },
        )
        return this
    }

    class Builder(internal val context: Context, internal val parentView: ViewGroup) {

        internal var picasso: Picasso = Picasso.get()

        internal var thumbUrl: String? = null
        internal var fullUrl: String? = null

        internal var imageBorderRadius: Float = 0f
        internal var blurRadius: Int = 10
        internal var progress: Int = 0
        internal var maxProgress: Int = 100
        internal var progressMargin: Int = 4
        internal var progressIndeterminateSweepAngle: Int = 90
        internal var iconWith: Int = 64
        internal var iconHeight: Int = 64
        internal var buttonWith: Int = 100
        internal var buttonHeight: Int = 100

        //internal var isIndeterminate = true
        internal var useCache: Boolean = true
        internal var isCancelable: Boolean = false

        internal var buttonIdleIcon: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_default_idle_icon)
        internal var buttonCancelIcon: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_default_cancel_icon)
        internal var buttonFinishIcon: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_default_finish_icon)
        internal var buttonErrorIcon: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_default_error_icon)
        internal var emptyPlaceholder: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_default_placeholder)

        @ColorInt
        internal var idleBgColor: Int =
            ContextCompat.getColor(context, R.color.color_default_idle_background)

        @ColorInt
        internal var errorBgColor: Int =
            ContextCompat.getColor(context, R.color.color_default_error_background)

        @ColorInt
        internal var finishBgColor: Int =
            ContextCompat.getColor(context, R.color.color_default_finish_background)

        @ColorInt
        internal var indeterminateBgColor: Int =
            ContextCompat.getColor(context, R.color.color_default_indeterminate_background)

        @ColorInt
        internal var determinateBgColor: Int =
            ContextCompat.getColor(context, R.color.color_default_determinate_background)

        @ColorInt
        internal var progressDeterminateColor: Int =
            ContextCompat.getColor(context, R.color.color_default_determinate_progress)

        @ColorInt
        internal var progressIndeterminateColor: Int =
            ContextCompat.getColor(context, R.color.color_default_indeterminate_progress)

        fun build() = Pictish(this)

        fun setPicasso(picasso: Picasso) = apply { this@Builder.picasso = picasso }

        fun setThumbUrl(thumbUrl: String?) = apply { this@Builder.thumbUrl = thumbUrl }
        fun setFullUrl(fullUrl: String?) = apply { this@Builder.fullUrl = fullUrl }
        fun setImageBorderRadius(imageBorderRadius: Float) =
            apply { this@Builder.imageBorderRadius = imageBorderRadius }

        //fun setProgress(progress: Int) = apply { this@Builder.progress = progress }
        //fun setMaxProgress(maxProgress: Int) = apply { this@Builder.maxProgress = maxProgress }
        fun setProgressIndeterminateSweepAngle(progressIndeterminateSweepAngle: Int) =
            apply { this@Builder.progressIndeterminateSweepAngle = progressIndeterminateSweepAngle }

        fun setProgressMargin(progressMargin: Int) =
            apply { this@Builder.progressMargin = progressMargin }

        fun setBlurRadius(blurRadius: Int) = apply { this@Builder.blurRadius = blurRadius }
        fun setIconWith(iconWith: Int) = apply { this@Builder.iconWith = iconWith }
        fun setIconHeight(iconHeight: Int) = apply { this@Builder.iconHeight = iconHeight }
        fun setButtonWith(buttonWith: Int) = apply { this@Builder.buttonWith = buttonWith }
        fun setButtonHeight(buttonHeight: Int) = apply { this@Builder.buttonHeight = buttonHeight }

        /*fun isIndeterminate(isIndeterminate: Boolean = true) =
            apply { this@Builder.isIndeterminate = isIndeterminate }*/

        fun isCancelable(isCancelable: Boolean = false) =
            apply { this@Builder.isCancelable = isCancelable }

        fun useCache(useCache: Boolean = true) =
            apply { this@Builder.useCache = useCache }

        fun setButtonIdleIcon(buttonIdleIcon: Drawable?) =
            apply { this@Builder.buttonIdleIcon = buttonIdleIcon }

        fun setButtonCancelIcon(buttonCancelIcon: Drawable?) =
            apply { this@Builder.buttonCancelIcon = buttonCancelIcon }

        fun setButtonFinishIcon(buttonFinishIcon: Drawable?) =
            apply { this@Builder.buttonFinishIcon = buttonFinishIcon }

        fun setEmptyPlaceholder(emptyPlaceholder: Drawable?) =
            apply { this@Builder.emptyPlaceholder = emptyPlaceholder }

        fun setIdleBgColor(@ColorInt idleBgColor: Int) =
            apply { this@Builder.idleBgColor = idleBgColor }

        fun setErrorBgColor(@ColorInt errorBgColor: Int) =
            apply { this@Builder.errorBgColor = errorBgColor }

        fun setFinishBgColor(@ColorInt finishBgColor: Int) =
            apply { this@Builder.finishBgColor = finishBgColor }

        fun setIndeterminateBgColor(@ColorInt indeterminateBgColor: Int) =
            apply { this@Builder.indeterminateBgColor = indeterminateBgColor }

        fun setDeterminateBgColor(@ColorInt determinateBgColor: Int) =
            apply { this@Builder.determinateBgColor = determinateBgColor }

        fun setProgressDeterminateColor(@ColorInt progressDeterminateColor: Int) =
            apply { this@Builder.progressDeterminateColor = progressDeterminateColor }

        fun setProgressIndeterminateColor(@ColorInt progressIndeterminateColor: Int) =
            apply { this@Builder.progressIndeterminateColor = progressIndeterminateColor }

    }

}
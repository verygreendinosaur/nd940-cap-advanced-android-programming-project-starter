package com.example.android.politicalpreparedness.representative.adapter

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R

const val PROFILE_IMAGE_PROPERTY_NAME = "profileImage"
const val STATE_PROPERTY_NAME = "stateValue"
const val HTTPS_SCHEME_NAME = "https"

@BindingAdapter(PROFILE_IMAGE_PROPERTY_NAME)
fun fetchImage(view: ImageView, src: String?) {
    val uri = src.orEmpty()
            .toUri()
            .buildUpon()
            .scheme(HTTPS_SCHEME_NAME)
            .build()

    Glide.with(view)
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(view)
}

@BindingAdapter(STATE_PROPERTY_NAME)
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}

package com.zhuinden.eventemittersample.core.navigation

import android.os.Bundle
import android.os.Parcelable
import com.zhuinden.simplestack.ScopeKey

/**
 * Created by Zhuinden on 2018.09.17.
 */
interface BaseKey : ScopeKey, Parcelable {
    val fragmentTag: String
        get() = toString()

    fun newInstance(): BaseFragment = createFragment().apply {
        arguments = (arguments ?: Bundle()).also { bundle ->
            bundle.putParcelable("KEY", this@BaseKey)
        }
    }

    override fun getScopeTag(): String = fragmentTag

    fun createFragment(): BaseFragment
}

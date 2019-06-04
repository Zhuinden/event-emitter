package com.zhuinden.eventemittersample.features.words

import com.zhuinden.eventemittersample.core.navigation.BaseFragment
import com.zhuinden.eventemittersample.core.navigation.BaseKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018.09.17.
 */
@Parcelize
data class NewWordKey(val placeholder: String = "") : BaseKey {
    override fun createFragment(): BaseFragment = NewWordFragment()
}

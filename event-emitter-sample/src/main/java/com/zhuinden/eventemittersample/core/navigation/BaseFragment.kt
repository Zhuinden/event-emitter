package com.zhuinden.eventemittersample.core.navigation

import android.support.v4.app.Fragment
import com.zhuinden.eventemittersample.utils.requireArguments

/**
 * Created by Zhuinden on 2018.09.17.
 */

open class BaseFragment : Fragment() {
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun <T : BaseKey> getKey(): T = requireArguments.getParcelable<T>("KEY")
}

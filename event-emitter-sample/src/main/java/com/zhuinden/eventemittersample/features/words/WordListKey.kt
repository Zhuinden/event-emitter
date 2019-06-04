package com.zhuinden.eventemittersample.features.words

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.eventemittersample.core.navigation.BaseKey
import com.zhuinden.eventemittersample.core.scoping.HasServices
import com.zhuinden.eventemittersample.utils.add
import com.zhuinden.eventemittersample.utils.get
import com.zhuinden.eventemittersample.utils.rebind
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018.09.17.
 */
@Parcelize
data class WordListKey(val placeholder: String = "") : BaseKey, HasServices {
    override fun getScopeTag(): String = fragmentTag

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(WordController(backstack))
            rebind<WordListFragment.DataProvider>(get<WordController>())
            rebind<WordListFragment.ActionHandler>(get<WordController>())
            rebind<NewWordFragment.ActionHandler>(get<WordController>())
            add(get<WordController>().eventEmitter)
        }
    }

    override fun createFragment() = WordListFragment()
}

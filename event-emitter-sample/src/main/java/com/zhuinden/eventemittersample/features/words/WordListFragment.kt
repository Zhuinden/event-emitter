package com.zhuinden.eventemittersample.features.words

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.eventemittersample.R
import com.zhuinden.eventemittersample.utils.CompositeNotificationToken
import com.zhuinden.eventemittersample.core.navigation.BaseFragment
import com.zhuinden.eventemittersample.utils.lookup
import com.zhuinden.eventemittersample.utils.onClick
import com.zhuinden.eventemittersample.utils.safe
import com.zhuinden.eventemittersample.utils.showToast
import kotlinx.android.synthetic.main.word_list_view.*

/**
 * Created by Zhuinden on 2018.09.17.
 */

class WordListFragment : BaseFragment() {
    interface ActionHandler {
        fun onAddNewWordClicked(wordListFragment: WordListFragment)
    }

    interface DataProvider {
        val wordList: LiveData<List<String>>
    }

    private val actionHandler by lazy { lookup<ActionHandler>() }
    private val dataProvider by lazy { lookup<DataProvider>() }
    private val wordList by lazy { dataProvider.wordList }

    private val controllerEvents by lazy { lookup<WordEventEmitter>() }

    val adapter = WordListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.word_list_view, container, false)

    @Suppress("NAME_SHADOWING")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        buttonGoToAddNewWord.onClick { view ->
            actionHandler.onAddNewWordClicked(this)
        }

        wordList.observe(this /*getViewLifecycle()*/, Observer { words ->
            adapter.updateWords(words!!)
        })
    }

    private val notificationTokens = CompositeNotificationToken()

    override fun onStart() {
        super.onStart()
        notificationTokens += controllerEvents.startListening { event ->
            when (event) {
                is WordController.Events.NewWordAdded -> showToast("Added ${event.word}")
            }.safe()
        }
    }

    override fun onStop() {
        notificationTokens.stopListening()
        super.onStop()
    }
}


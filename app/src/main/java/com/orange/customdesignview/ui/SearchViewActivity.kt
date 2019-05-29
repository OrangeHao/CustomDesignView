package com.orange.customdesignview.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.orange.customdesignview.R
import kotlinx.android.synthetic.main.activity_search_view.*

class SearchViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_view)

        btn0.setOnClickListener {
            testView.startLoading()
            searchingView.setNoneState()
        }
        btn1.setOnClickListener {
            searchingView.setStartState()
        }
        btn2.setOnClickListener {
            searchingView.setSearchingState()
        }
        btn3.setOnClickListener {
            searchingView.setEndState()
        }
    }
}

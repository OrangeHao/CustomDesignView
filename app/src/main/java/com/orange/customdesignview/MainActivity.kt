package com.orange.customdesignview

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.orange.customdesignview.ui.DragableViewActivity
import com.orange.customdesignview.ui.LeafLoadingActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            startActivity(Intent(this,LeafLoadingActivity::class.java))
        }

        button2.setOnClickListener {
            startActivity(Intent(this,DragableViewActivity::class.java))
        }
    }

}

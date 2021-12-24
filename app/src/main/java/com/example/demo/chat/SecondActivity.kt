package com.example.demo.chat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.demo.R

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val name = intent.getStringExtra("name")
        findViewById<TextView>(R.id.textView3).text = "接收到的数据为：$name"

        findViewById<TextView>(R.id.textView3).setOnClickListener {
            val intent = Intent().apply {
                putExtra("result", "Hello，依然范特西稀，我是回传的数据！")
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
package org.mightyfrog.android.kotlincoroutinesample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Coroutines simplify asynchronous programming by putting the complications into libraries.
 * The logic of the program can be expressed sequentially in a coroutine, and the underlying library
 * will figure out the asynchrony for us.
 *
 * https://kotlinlang.org/docs/reference/coroutines.html
 *
 * @author Shigehiro Soejima
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            async(UI) {
                (findViewById<TextView>(R.id.textView)).text = loadCats().await()
            }
        }
    }

    private fun loadCats() = async(CommonPool) {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("https://www.reddit.com/r/cats/.json")
                .build()

        val response = client.newCall(request).execute()
        return@async if (response.isSuccessful) {
            response.body()?.string()
        } else {
            response.message()
        }
    }
}

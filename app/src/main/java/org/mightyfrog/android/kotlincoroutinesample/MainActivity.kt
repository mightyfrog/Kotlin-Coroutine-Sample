package org.mightyfrog.android.kotlincoroutinesample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
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

    companion object {
        const val TAG = "coroutine test"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            launchTest()
//            asyncTest()
//            runBlockingTest()
//            suspendFunTest()
        }
    }

    /**
     * This starts a new coroutine. By default, coroutines are run on a shared pool of threads.
     * Threads still exist in a program based on coroutines, but one thread can run many coroutines,
     * so there's no need for too many threads.
     */
    private fun launchTest() { // 1 -> 4 -> 2 -> 3
        Log.e(TAG, "1>>>" + System.currentTimeMillis())
        launch(UI) {
            Log.e(TAG, "2>>>" + System.currentTimeMillis())
            (findViewById<TextView>(R.id.textView)).text = loadCats().await()
            Log.e(TAG, "3>>>" + System.currentTimeMillis())
        }
        Log.e(TAG, "4>>>" + System.currentTimeMillis())
    }

    /**
     * Another way of starting a coroutine is async {}. It is like launch {}, but returns an
     * instance of Deferred<T> (non-blocking cancellable future), which has an await() function that
     * returns the result of the coroutine.
     */
    private fun asyncTest() { // 1 -> 4 -> 2 -> 3
        Log.e(TAG, "1>>>" + System.currentTimeMillis())
        async(UI) {
            Log.e(TAG, "2>>>" + System.currentTimeMillis())
            (findViewById<TextView>(R.id.textView)).text = loadCats().await()
            Log.e(TAG, "3>>>" + System.currentTimeMillis())
        }
        Log.e(TAG, "4>>>" + System.currentTimeMillis())
    }

    private fun runBlockingTest() { // 1 -> -, blocks the starting thread
        Log.e(TAG, "1>>>" + System.currentTimeMillis())
        runBlocking(UI) {
            Log.e(TAG, "2>>>" + System.currentTimeMillis())
            (findViewById<TextView>(R.id.textView)).text = loadCats().await()
            Log.e(TAG, "3>>>" + System.currentTimeMillis())
        }
        Log.e(TAG, "4>>>" + System.currentTimeMillis())
    }

    private fun suspendFunTest() {
        async {
            Log.e(TAG, "1>>>" + System.currentTimeMillis())
            val str = suspendFunc()
            Log.e(TAG, str)
            launch(UI) {
                (findViewById<TextView>(R.id.textView)).text = str
                Log.e(TAG, "2>>>" + System.currentTimeMillis())
            }
        }
    }

    private suspend fun suspendFunc(): String? {
        Log.e(TAG, "3>>>" + System.currentTimeMillis())
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("https://www.reddit.com/r/cats/.json")
                .build()
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            Log.e(TAG, "4>>>" + System.currentTimeMillis())
            response.body()?.string()
        } else {
            Log.e(TAG, "4>>>" + System.currentTimeMillis())
            response.message()
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

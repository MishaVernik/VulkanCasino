package stoswb.aas.vulvukan.schedule.activities

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import stoswb.aas.vulvukan.schedule.PostRequset.PostRequset
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

abstract class BaseActivity : AppCompatActivity(), PostRequset {

    abstract fun layoutID(): Int

    open fun showWebView(url: String) {
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutID())
    }

    override fun onPostRequest(link: String) {

        showWebView(link)
        OkHttpClient().newCall(Request.Builder().url(link).build()).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
            }
        })
    }
}
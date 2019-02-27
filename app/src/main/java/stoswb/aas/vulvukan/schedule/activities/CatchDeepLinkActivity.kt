package stoswb.aas.vulvukan.schedule.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.YoYo
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.facebook.applinks.AppLinkData
import stoswb.aas.vulvukan.AppPreferences
import stoswb.aas.vulvukan.R
import stoswb.aas.vulvukan.schedule.PulseAnimator
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

class CatchDeepLinkActivity : AppCompatActivity() {

    var isBoost = false

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppPreferences(this).deepLink().isNotEmpty()) {

            startActivity<SuccessDeepLinkActivity>()
            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        counter_view.init(1_000_000, 7)
        diamond_view.onClick {
            YoYo.with(PulseAnimator()).duration(500L).playOn(it)
            counter_view.click(if (isBoost) 2 else 1)
        }

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)

        boost.onClick {
            if (!isBoost) {
                isBoost = true
                initTimer(boost, 60_000L) {
                    boost.text = "Boost"
                    isBoost = false
                }
            }
        }

        val extras = intent.extras
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true)
        }

        AppLinkData.fetchDeferredAppLinkData(this@CatchDeepLinkActivity) { appLinkData ->
            if (appLinkData != null && appLinkData.targetUri != null) {

                if (appLinkData.argumentBundle.get("target_url") != null) {


                    val params = appLinkData.argumentBundle.get("target_url")?.toString()?.replaceBefore("fbkraken", "")
                        ?: ""
                    Log.d("AppLinkData", "http://$params")

                    AppPreferences(this).deepLink("http://$params")

                    startActivity(Intent(this@CatchDeepLinkActivity, SuccessDeepLinkActivity::class.java))
                    finish()
                }
            } else {
//                startActivity(Intent(this@CatchDeepLinkActivity, SuccessDeepLinkActivity::class.java))
//                finish()
            }
        }
    }

    private fun initTimer(view: TextView, time: Long, finish: () -> Unit) = object : CountDownTimer(time, 1_000L) {

        @SuppressLint("SetTextI18n")
        override fun onTick(diff: Long) {
            view.text = diff.toTime()
        }

        override fun onFinish() {
            finish()
        }
    }.start()

    fun Long.toTime(): String {
        val days = this / (24 * 60 * 60 * 1000)
        val hours = this / (60 * 60 * 1000) % 24
        val minutes = this / (60 * 1000) % 60
        val seconds = this / 1000 % 60

        fun getTimeString(time: Long) = if (time == 0L) "" else "${if (time < 10) "0$time" else time}"

        return "${if (getTimeString(hours).isNotEmpty()) "${getTimeString(hours)}:" else ""}${if (minutes < 10) "0$minutes" else minutes}:${if (seconds < 10) "0$seconds" else seconds}"
    }
}

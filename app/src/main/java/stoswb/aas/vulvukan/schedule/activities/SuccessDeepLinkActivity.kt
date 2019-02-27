package stoswb.aas.vulvukan.schedule.activities

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebSettings.PluginState.ON
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_deep_link_success.*
import stoswb.aas.vulvukan.AppPreferences
import stoswb.aas.vulvukan.schedule.utils.Constants
import org.jetbrains.anko.toast
import java.lang.System.exit
import kotlin.concurrent.thread


class SuccessDeepLinkActivity : BaseActivity() {

    private val REQUEST_SELECT_FILE = 713
    private val FILECHOOSER_RESULTCODE = 411
    private var mUploadMessage: ValueCallback<Uri>? = null
    private var uploadMessage: ValueCallback<Array<Uri>>? = null
    private val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"

    override fun layoutID() = stoswb.aas.vulvukan.R.layout.activity_deep_link_success

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val prefs = AppPreferences(this)

        web_view.settings.userAgentString = USER_AGENT
        web_view.settings.javaScriptEnabled = true
        web_view.settings.useWideViewPort = true
        web_view.settings.loadWithOverviewMode = true
        web_view.settings.allowFileAccess = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true
        web_view.settings.builtInZoomControls = true
        web_view.settings.pluginState = ON
        web_view.settings.setSupportZoom(true)
        web_view.settings.allowContentAccess = true
        web_view.settings.domStorageEnabled = true
        web_view.settings.setSupportMultipleWindows(true)
        if (Build.VERSION.SDK_INT >= 21) {
            web_view.settings.mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun showWebView(url: String) {

        web_view.loadUrl(url)

        thread {
            Thread.sleep(5000)
            runOnUiThread {
                web_view.visibility = View.VISIBLE
                image_logo.visibility = View.GONE
                //progress_bar.visibility = View.GONE
            }
        }

        web_view.webChromeClient = object : WebChromeClient() {
            protected fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
            }


            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: WebChromeClient.FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage?.onReceiveValue(null)
                    uploadMessage = null
                }

                uploadMessage = filePathCallback

                val intent = fileChooserParams.createIntent()
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    toast("Ошибка в доступе к файлам")
                    return false
                }

                return true
            }

            protected fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
                mUploadMessage = uploadMsg
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE)
            }

            protected fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE)
            }
        }

        web_view.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                thread {
                    Thread.sleep(1000)
                    runOnUiThread {
                        web_view.visibility = View.VISIBLE
                        image_logo.visibility = View.GONE
                       // progress_bar.visibility = View.GONE
                    }
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Log.d("SuccessDeepLinkActivity", "error -> $description")
                web_view.visibility = View.GONE
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError) {
                Log.d("SuccessDeepLinkActivity", "error -> ${rerr.description}")
                onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return
                uploadMessage?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                uploadMessage = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessage?.onReceiveValue(result)
            mUploadMessage = null
        } else toast("Ошибка при загрузук файла")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && web_view.canGoBack()) {
            web_view.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
    override fun onBackPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            finishAffinity()
        }
    }
}

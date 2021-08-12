package com.tal.dhybirddemo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.dahai.dhybird.DHybird

class MainActivity : AppCompatActivity(), DHybird.IDHybird {

    private lateinit var containerView: FrameLayout

    private var dHybird: DHybird? = null

    private val testUrl = "https://mp.weixin.qq.com/s/psqx5AdOFP-gIQBw8zcMZw"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        containerView = findViewById(R.id.layout_fl)
        initDHybird()
    }

    private fun initDHybird() {
        val hybirdUrl = "https://threepiece-app.test-dahai.com/qingwen/common_questions?course=%7B%22normal%22%3A0,%22excellent%22%3A0%7D&utm_source=dahai_app_parent"
        val cookieMap = HashMap<String,String>()
        cookieMap["test11"] = "232"
        cookieMap["test12"] = "433"

        dHybird = DHybird.Builder()
            .with(this)
            .setCore(DHybird.CORE_NATIVE)
            .setContainer(containerView)
            .setIDHybird(this)
            .setCookies(cookieMap)
            .go(testUrl)
            .build()

        findViewById<Button>(R.id.btn_sent).setOnClickListener {
            dHybird!!.sendEventMessageToJS("refreshToken", null)
        }
    }

    override fun pageLoadProgress(progress: Int) {
    }

    override fun webViewTitle(title: String?) {
       Log.e("MainActivity","webViewTitle:$title")
    }

    override fun selectFile(valueCallback: ValueCallback<Array<Uri>>?) {
    }

    override fun onLoadError() {
        Log.e("magic", "onLoadError")
    }

    override fun docDownloadFinish(path: String?) {
    }

    override fun onBackPressed() {
        dHybird?.canGoBack()?.apply {
            if (this) {
                dHybird!!.goBack()
            } else {
                super.onBackPressed()
            }
        }
    }
}

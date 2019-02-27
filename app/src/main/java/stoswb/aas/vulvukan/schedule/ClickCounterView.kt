package stoswb.aas.vulvukan.schedule

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.get
import stoswb.aas.vulvukan.R
import kotlinx.android.synthetic.main.click_counter_view.view.*

class ClickCounterView : FrameLayout {

    var countClick = 1_000_000
    var maxDigit = 7

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context?) {
        inflate(context, R.layout.click_counter_view, this)
    }

    fun init(startCount: Int, maxDigit: Int): ClickCounterView {
        this.maxDigit = maxDigit
        countClick = startCount
        (0 until maxDigit).map {
            container.addView(ClickCountItemView(context))
        }
        click(0)
        return this
    }

    fun click(minusCount: Int) {
        countClick -= minusCount
        val s = addZero(countClick.toString())
        (0..container.childCount).map { index ->
            if (index == maxDigit) return
            val view = container[index]
            if (view is ClickCountItemView) {
                view.setText(s[index].toString())
            }
        }
    }

    private fun addZero(startCount: String): CharArray {
        val needZeroCunt = maxDigit - startCount.length
        var s = ""
        (0 until needZeroCunt).map { s += "0" }
        return "$s$startCount".toCharArray()
    }

    fun reset() {
        countClick = 1_000_000
    }

    fun getNotClickedCount() = countClick
}
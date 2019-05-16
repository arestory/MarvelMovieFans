package arestory.com.marvelmoviefans.util;

import android.content.Context
import android.util.TypedValue

class MeasUtils {
    companion object {
        fun pxToDp(px: Int, context: Context): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px.toFloat(),
                    context.getResources().getDisplayMetrics()).toInt()

        }

        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                    context.getResources().getDisplayMetrics()).toInt()
        }

    }
}
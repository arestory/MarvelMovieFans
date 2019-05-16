package arestory.com.marvelmoviefans.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class UnableScrollLayoutManager extends LinearLayoutManager {
    public UnableScrollLayoutManager(Context context) {
        super(context);
    }

    public UnableScrollLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public UnableScrollLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public boolean canScrollVertically() {
        return  false;
    }
}
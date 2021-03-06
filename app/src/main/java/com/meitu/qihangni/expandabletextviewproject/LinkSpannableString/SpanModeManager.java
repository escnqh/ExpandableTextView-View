package com.meitu.qihangni.expandabletextviewproject.LinkSpannableString;

import android.text.SpannableString;
import android.widget.TextView;

import com.meitu.qihangni.expandabletextviewproject.LinkSpannableString.spanModes.BaseSpanMode;
import com.meitu.qihangni.expandabletextviewproject.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nqh 2018/7/12
 */
public class SpanModeManager {

    private static List<BaseSpanMode> spanModeList = new ArrayList<>();

    public SpanModeManager addMode(BaseSpanMode spanMode) {
        for (BaseSpanMode baseSpanMode1 : spanModeList) {
            if (spanMode == baseSpanMode1) {
                return this;
            }
        }
        spanModeList.add(spanMode);
        return this;
    }

    public SpanModeManager linkSpan(SpannableString spannableString, ExpandableTextView textView) {
        LinkSpanTool.linkSpan(spannableString, textView, this);
        return this;
    }

    public List<BaseSpanMode> getSpanModeList() {
        return spanModeList;
    }

}

package com.meitu.qihangni.expandabletextviewproject.expandableview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meitu.qihangni.expandabletextviewproject.R;

/**
 * 文字可展开控件，应当设置最大行数，不设置默认为2行
 * 当需要对内部TextView进行操作时，请通过{@see getTvContent()}取出TextView
 *
 * @author nqh 2018/7/21
 */
public class ExpandableView extends LinearLayout {

    private TextView mTvContent;
    private TextView mTvExpandHint;
    private int mMaxLines = 2;
    private int mLineCount;
    private Layout mLayout;

    public ExpandableView(Context context) {
        this(context, null);
    }

    public ExpandableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_expandtextview, this, true);
        mTvContent = findViewById(R.id.tv_content);
        //当onResume()回调后获取行数进行判断
        mTvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLineCount = mTvContent.getLineCount();
                if (mLineCount > mMaxLines) {
                    mTvExpandHint.setVisibility(VISIBLE);
                    mTvContent.setMaxLines(mMaxLines);
//                    mTvContent.setEllipsize(TextUtils.TruncateAt.END);
                }
                if (mLineCount > 0) {
                    mTvContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        mTvExpandHint = findViewById(R.id.tv_hint);
        mTvExpandHint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvContent.setMaxLines(10000);
                mTvExpandHint.setVisibility(GONE);
            }
        });
    }

    /**
     * 获取可展开部分TextView
     */
    public TextView getTvContent() {
        return mTvContent;
    }

    public void setMaxLines(int mMaxLines) {
        this.mMaxLines = mMaxLines;
    }

    public void setText(CharSequence text) {
        mTvContent.setText(text);
    }
}

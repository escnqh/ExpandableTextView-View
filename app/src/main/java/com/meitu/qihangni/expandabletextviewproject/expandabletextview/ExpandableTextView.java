package com.meitu.qihangni.expandabletextviewproject.expandabletextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.meitu.qihangni.expandabletextviewproject.R;


/**
 * 可展开（收起）的TextView，setText()即生效，控制只显示固定行数
 */
@SuppressLint("AppCompatCustomView")
public class ExpandableTextView extends TextView {

    public static final int STATE_SHRINK = 0;
    public static final int STATE_EXPAND = 1;
    private static final String ELLIPSIS_HINT = "...";
    private static final String GAP_TO_EXPAND_HINT = "  ";
    private static final String GAP_TO_SHRINK_HINT = "  ";
    private static final int MAX_LINES_ON_SHRINK = 2;
    private static final int TO_EXPAND_HINT_COLOR = 0xFF3498DB;
    private static final int TO_SHRINK_HINT_COLOR = 0xFFE74C3C;
    private static final int TO_EXPAND_HINT_COLOR_BG_PRESSED = 0x55999999;
    private static final int TO_SHRINK_HINT_COLOR_BG_PRESSED = 0x55999999;
    private static final boolean TOGGLE_ENABLE = true;
    private static final boolean SHOW_TO_EXPAND_HINT = true;
    private static final boolean SHOW_TO_SHRINK_HINT = true;

    private String mEllipsisHint;//省略号提示
    private String mToExpandHint;
    private String mToShrinkHint;
    private String mGapToExpandHint = GAP_TO_EXPAND_HINT;
    private String mGapToShrinkHint = GAP_TO_SHRINK_HINT;
    private boolean mToggleEnable = TOGGLE_ENABLE;
    private boolean mShowToExpandHint = SHOW_TO_EXPAND_HINT;
    private boolean mShowToShrinkHint = SHOW_TO_SHRINK_HINT;
    private int mMaxLinesOnShrink = MAX_LINES_ON_SHRINK;
    private int mToExpandHintColor = TO_EXPAND_HINT_COLOR;
    private int mToShrinkHintColor = TO_SHRINK_HINT_COLOR;
    private int mToExpandHintColorBgPressed = TO_EXPAND_HINT_COLOR_BG_PRESSED;
    private int mToShrinkHintColorBgPressed = TO_SHRINK_HINT_COLOR_BG_PRESSED;
    private int mCurrState = STATE_SHRINK;
    //用于在限定行数下添加的“展开”和“收起”
    private TouchableSpan mTouchableSpan;
    private BufferType mBufferType = BufferType.NORMAL;
    private TextPaint mTextPaint;
    private Layout mLayout;
    private int mTextLineCount = -1;
    private int mLayoutWidth = 0;
    private int mFutureTextViewWidth = 0;

    //原始文本
    private CharSequence mOrigText;

    private OnExpandListener mOnExpandListener;

    public ExpandableTextView(Context context) {
        super(context);
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        if (a == null) {
            return;
        }
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ExpandableTextView_etv_MaxLinesOnShrink) {
                mMaxLinesOnShrink = a.getInteger(attr, MAX_LINES_ON_SHRINK);
            } else if (attr == R.styleable.ExpandableTextView_etv_EllipsisHint) {
                mEllipsisHint = a.getString(attr);
            } else if (attr == R.styleable.ExpandableTextView_etv_ToExpandHint) {
                mToExpandHint = a.getString(attr);
            } else if (attr == R.styleable.ExpandableTextView_etv_ToShrinkHint) {
                mToShrinkHint = a.getString(attr);
            } else if (attr == R.styleable.ExpandableTextView_etv_EnableToggle) {
                mToggleEnable = a.getBoolean(attr, TOGGLE_ENABLE);
            } else if (attr == R.styleable.ExpandableTextView_etv_ToExpandHintShow) {
                mShowToExpandHint = a.getBoolean(attr, SHOW_TO_EXPAND_HINT);
            } else if (attr == R.styleable.ExpandableTextView_etv_ToShrinkHintShow) {
                mShowToShrinkHint = a.getBoolean(attr, SHOW_TO_SHRINK_HINT);
            } else if (attr == R.styleable.ExpandableTextView_etv_ToExpandHintColor) {
                mToExpandHintColor = a.getInteger(attr, TO_EXPAND_HINT_COLOR);
            } else if (attr == R.styleable.ExpandableTextView_etv_ToShrinkHintColor) {
                mToShrinkHintColor = a.getInteger(attr, TO_SHRINK_HINT_COLOR);
            } else if (attr == R.styleable.ExpandableTextView_etv_ToExpandHintColorBgPressed) {
                mToExpandHintColorBgPressed = a.getInteger(attr, TO_EXPAND_HINT_COLOR_BG_PRESSED);
            } else if (attr == R.styleable.ExpandableTextView_etv_ToShrinkHintColorBgPressed) {
                mToShrinkHintColorBgPressed = a.getInteger(attr, TO_SHRINK_HINT_COLOR_BG_PRESSED);
            } else if (attr == R.styleable.ExpandableTextView_etv_InitState) {
                mCurrState = a.getInteger(attr, STATE_SHRINK);
            } else if (attr == R.styleable.ExpandableTextView_etv_GapToExpandHint) {
                mGapToExpandHint = a.getString(attr);
            } else if (attr == R.styleable.ExpandableTextView_etv_GapToShrinkHint) {
                mGapToShrinkHint = a.getString(attr);
            }
        }
        a.recycle();
    }

    private void init() {
        mTouchableSpan = new TouchableSpan();
        setMovementMethod(TouchMovementMethod.getInstance());
        //初始化文字资源
        if (TextUtils.isEmpty(mEllipsisHint)) {
            mEllipsisHint = "...";
        }
        if (TextUtils.isEmpty(mToExpandHint)) {
            mToExpandHint = "展开";
        }
        if (TextUtils.isEmpty(mToShrinkHint)) {
            mToShrinkHint = "";
        }
        //当onResume()回调后利用getNewTextByConfig()获取效果
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                setTextInternal(getNewTextByConfig(), mBufferType);
            }
        });
    }

    /**
     * 获取展开状态
     */
    public int getExpandState() {
        return mCurrState;
    }

    /**
     * 刷新并按当前配置获取将要显示的文本
     */
    private CharSequence getNewTextByConfig() {
        if (TextUtils.isEmpty(mOrigText)) {
            return mOrigText;
        }
        //先获取文字可显示宽度，方便计算在哪里截断文字
        mLayout = getLayout();
        if (mLayout != null) {
            mLayoutWidth = mLayout.getWidth();
        }
        if (mLayoutWidth <= 0) {
            if (getWidth() == 0) {
                if (mFutureTextViewWidth == 0) {
                    return mOrigText;
                } else {
                    mLayoutWidth = mFutureTextViewWidth - getPaddingLeft() - getPaddingRight();
                }
            } else {
                mLayoutWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            }
        }
        mTextPaint = getPaint();

        mTextLineCount = -1;
        switch (mCurrState) {
            case STATE_SHRINK: {
                //获取行数
                mLayout = new DynamicLayout(mOrigText, mTextPaint, mLayoutWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                mTextLineCount = mLayout.getLineCount();
                //如果没有超过限制直接返回
                if (mTextLineCount <= mMaxLinesOnShrink) {
                    return mOrigText;
                }
                //获取达到限制行最后字符的偏移量
                int indexEnd = getValidLayout().getLineEnd(mMaxLinesOnShrink - 1) - 4;
                //获取达到限制行第一个字符的偏移量
                int indexStart = getValidLayout().getLineStart(mMaxLinesOnShrink - 1);
                //计算修改后（去掉剩下的部分，补上省略号）应该到达哪一个字符的偏移量
                int indexEndTrimmed = indexEnd - getLengthOfString(mEllipsisHint)
                        - (mShowToExpandHint ? getLengthOfString(mToExpandHint) + getLengthOfString(mGapToExpandHint) : 0);
                if (indexEndTrimmed <= indexStart) {
                    indexEndTrimmed = indexEnd;
                }
                int remainWidth = getValidLayout().getWidth()
                        - (int) (mTextPaint.measureText(mOrigText.subSequence(indexStart, indexEndTrimmed).toString()) + 0.5);
                float widthTailReplaced = mTextPaint.measureText(getContentOfString(mEllipsisHint)
                        + (mShowToExpandHint ? (getContentOfString(mToExpandHint) + getContentOfString(mGapToExpandHint)) : ""));
//                float widthTailReplaced = mTextPaint.measureText(getContentOfString(mEllipsisHint));
                int indexEndTrimmedRevised = indexEndTrimmed;
                if (remainWidth > widthTailReplaced) {
                    int extraOffset = 0;
                    int extraWidth = 0;
                    while (remainWidth > widthTailReplaced + extraWidth) {
                        extraOffset++;
                        if ((indexEndTrimmed + extraOffset) <= mOrigText.length()) {
                            extraWidth = (int) (mTextPaint.measureText(mOrigText.subSequence(indexEndTrimmed, indexEndTrimmed + extraOffset).toString()) + 0.5);
                        } else {
                            break;
                        }
                    }
                    indexEndTrimmedRevised += extraOffset - 1;
                } else {
                    int extraOffset = 0;
                    int extraWidth = 0;
                    while (remainWidth + extraWidth < widthTailReplaced) {
                        extraOffset--;
                        if (indexEndTrimmed + extraOffset > indexStart) {
                            extraWidth = (int) (mTextPaint.measureText(mOrigText.subSequence(indexEndTrimmed + extraOffset, indexEndTrimmed).toString()) + 0.5);
                        } else {
                            break;
                        }
                    }
                    indexEndTrimmedRevised += extraOffset;
                }
                CharSequence fixText = removeEndLineBreak(mOrigText.subSequence(0, indexEndTrimmedRevised));
                SpannableStringBuilder ssbShrink = new SpannableStringBuilder(fixText).append(mEllipsisHint);
                if (mShowToExpandHint) {
                    ssbShrink.append(getContentOfString(mGapToExpandHint) + getContentOfString(mToExpandHint));
                    ssbShrink.setSpan(mTouchableSpan, ssbShrink.length() - getLengthOfString(mToExpandHint), ssbShrink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                return ssbShrink;
            }
            case STATE_EXPAND: {
                if (!mShowToShrinkHint) {
                    return mOrigText;
                }
                mLayout = new DynamicLayout(mOrigText, mTextPaint, mLayoutWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                mTextLineCount = mLayout.getLineCount();
                if (mTextLineCount <= mMaxLinesOnShrink) {
                    return mOrigText;
                }
                SpannableStringBuilder ssbExpand = new SpannableStringBuilder(mOrigText)
                        .append(mGapToShrinkHint)
                        .append(mToShrinkHint);
                ssbExpand.setSpan(mTouchableSpan, ssbExpand.length() - getLengthOfString(mToShrinkHint), ssbExpand.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return ssbExpand;
            }
        }
        return mOrigText;
    }

    /**
     * 用来去除末尾的换行符
     */
    private CharSequence removeEndLineBreak(CharSequence text) {

        String str = text.toString();
        while (str.endsWith("\n")) {
            text = text.subSequence(0, text.length() - 1);
            str = text.toString();
        }
        return text;
    }

    public void setExpandListener(OnExpandListener listener) {
        mOnExpandListener = listener;
    }

    private Layout getValidLayout() {
        return mLayout != null ? mLayout : getLayout();
    }

    /**
     * 展开或者收起
     */
    private void toggle() {
        switch (mCurrState) {
            case STATE_SHRINK:
                mCurrState = STATE_EXPAND;
                if (mOnExpandListener != null) {
                    mOnExpandListener.onExpand(this);
                }
                break;
            case STATE_EXPAND:
                mCurrState = STATE_SHRINK;
                if (mOnExpandListener != null) {
                    mOnExpandListener.onShrink(this);
                }
                break;
        }
        setTextInternal(getNewTextByConfig(), mBufferType);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mOrigText = text;
        mBufferType = type;
        setTextInternal(getNewTextByConfig(), type);
    }

    private void setTextInternal(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    private int getLengthOfString(String string) {
        if (string == null)
            return 0;
        return string.length();
    }

    private String getContentOfString(String string) {
        if (string == null)
            return "";
        return string;
    }

    public interface OnExpandListener {
        void onExpand(ExpandableTextView view);

        void onShrink(ExpandableTextView view);
    }

    public class TouchableSpan extends ClickableSpan implements ITouchableSpan {
        private boolean mIsPressed;
        private long mTouchDowntime = 0;

        public void setPressed(boolean isSelected) {
            mIsPressed = isSelected;
        }

        @Override
        public void onClick(View widget) {
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            switch (mCurrState) {
                case STATE_SHRINK:
                    ds.setColor(mToExpandHintColor);
                    ds.bgColor = mIsPressed ? mToExpandHintColorBgPressed : 0;
                    break;
                case STATE_EXPAND:
                    ds.setColor(mToShrinkHintColor);
                    ds.bgColor = mIsPressed ? mToShrinkHintColorBgPressed : 0;
                    break;
            }
            ds.setUnderlineText(false);
        }

        @Override
        public boolean onTouchDown(TextView widget) {
            mTouchDowntime = System.currentTimeMillis();
            return true;
        }

        @Override
        public boolean onTouchUp(TextView widget) {
            // 设置为常态
            long dis = System.currentTimeMillis() - mTouchDowntime;
            mTouchDowntime = 0;
            if (dis < 300 && dis > 0) {// down和up间隔0-300之间 认为是点击事件
                toggle();
            }
            return false;
        }

        @Override
        public void onTouchOutside(TextView widget) {
            mTouchDowntime = 0;
        }
    }
}
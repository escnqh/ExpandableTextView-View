package com.meitu.qihangni.expandabletextviewproject.expandabletextview;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by fanch on 2016/4/20.
 * Textview触摸时的处理类
 */
public class TouchMovementMethod extends ScrollingMovementMethod {
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {

        if (widget == null || widget.getLayout() == null) {
            return super.onTouchEvent(widget, buffer, event);
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line;
            if (y < layout.getLineTop(0) || y > layout.getLineTop(layout.getLineCount())) line = -1;
            else line = layout.getLineForVertical(y);
            if (line >= 0) {

                int off;
                if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) off = -1;
                else off = layout.getOffsetForHorizontal(line, x);
                if (off >= 0) {
                    ITouchableSpan[] touchables = buffer.getSpans(off, off, ITouchableSpan.class);
                    if (touchables.length != 0) {
                        for (ITouchableSpan span : buffer.getSpans(0, buffer.length(), ITouchableSpan.class)) {
                            if (span != touchables[0]) span.onTouchOutside(widget);
                        }
                        if (action == MotionEvent.ACTION_DOWN) {
                            return touchables[0].onTouchDown(widget);
                        } else if (action == MotionEvent.ACTION_UP) {
                            return touchables[0].onTouchUp(widget);
                        } else {
                            touchables[0].onTouchOutside(widget);
                            return super.onTouchEvent(widget, buffer, event);
                        }
                    }
                }
            }
            for (ITouchableSpan span : buffer.getSpans(0, buffer.length(), ITouchableSpan.class)) {
                span.onTouchOutside(widget);
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    public static MovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new TouchMovementMethod();

        return sInstance;
    }

    private static TouchMovementMethod sInstance;
}

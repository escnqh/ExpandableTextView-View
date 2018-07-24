package com.meitu.qihangni.expandabletextviewproject.expandabletextview;

import android.widget.TextView;

/**
 * Created by fanch on 2016/4/20.
 * 可触摸的文本替换基类
 */
public interface ITouchableSpan {

    /**
     * 触摸按下标签事件
     *
     * @param widget
     * @return
     */
    public boolean onTouchDown(TextView widget);

    /**
     * 触摸抬起标签事件
     *
     * @param widget
     * @return
     */
    public boolean onTouchUp(TextView widget);

    /**
     * 触摸标签外部事件
     *
     * @param widget
     */
    public void onTouchOutside(TextView widget);

}

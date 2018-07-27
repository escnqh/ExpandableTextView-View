package com.meitu.qihangni.expandabletextviewproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;

import com.meitu.qihangni.expandabletextviewproject.LinkSpannableString.SpanModeManager;
import com.meitu.qihangni.expandabletextviewproject.LinkSpannableString.spanModes.TopicSpanMode;
import com.meitu.qihangni.expandabletextviewproject.LinkSpannableString.spanModes.UserSpanMode;
import com.meitu.qihangni.expandabletextviewproject.LinkSpannableString.spanModes.WebSpanMode;
import com.meitu.qihangni.expandabletextviewproject.expandabletextview.ExpandableTextView;

public class MainActivity extends AppCompatActivity {
    private int etvWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExpandableTextView expandableTextView1 = findViewById(R.id.extv1);
        ExpandableTextView expandableTextView2 = findViewById(R.id.extv2);
        expandableTextView1.setText("爱仕达\n\n\n奥所多飞sadsads洒安抚撒飞洒发\n你好121421沙发沙发风飒飒发\n洒水大玩发我发我的sad撒多所大啊哇啊发挖坟挖坟挖服务");

        SpannableString spannableString = new SpannableString("#topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic# #topic#爱仕达\n奥所多飞121212洒安抚撒飞洒发121421沙发发我发我的sad撒多所大啊哇啊发挖坟挖坟挖服务");
        SpanModeManager spanModeManager = new SpanModeManager()
                .addMode(new TopicSpanMode(R.color.colorAccent, null))
                .addMode(new UserSpanMode(R.color.colorPrimaryDark, null))
                .addMode(new WebSpanMode((int) expandableTextView2.getTextSize(), R.color.colorPrimaryDark, null))
                .linkSpan(spannableString, expandableTextView2);


    }
}

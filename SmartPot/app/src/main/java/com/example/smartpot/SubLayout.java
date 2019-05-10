package com.example.smartpot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class SubLayout extends LinearLayout {

    public SubLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public SubLayout(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_sub_layout,this,true);
    }
}

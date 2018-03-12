package com.martin.taxi.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 韩帮蜜 on 2018/1/8.
 * created to:
 */

public class BaseActivity extends AppCompatActivity {
    protected String TAG = "tag";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
    }
}

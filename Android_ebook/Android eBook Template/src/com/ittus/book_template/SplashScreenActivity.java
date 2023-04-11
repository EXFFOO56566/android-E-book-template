package com.ittus.book_template;

import android.app.Activity;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReadBookApplication app = (ReadBookApplication) getApplication();
        app.getModel().initData(this);
    }
}

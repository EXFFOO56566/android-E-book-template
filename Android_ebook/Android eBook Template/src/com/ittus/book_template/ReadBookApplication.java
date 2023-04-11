package com.ittus.book_template;

import android.app.Application;

import com.ittus.book_template.model.BenNhauTronDoiModel;

public class ReadBookApplication extends Application {

    BenNhauTronDoiModel mHiepKhachHanhModel;

    @Override
    public void onCreate() {
        super.onCreate();

        mHiepKhachHanhModel = new BenNhauTronDoiModel(this);
    }

    public BenNhauTronDoiModel getModel() {
        return mHiepKhachHanhModel;
    }
}

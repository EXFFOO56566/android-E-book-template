package com.ittus.book_template.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import com.ittus.book_template.ReadBookActivity;
import com.ittus.book_template.ReadBookApplication;
import com.ittus.book_template.SplashScreenActivity;

public class BenNhauTronDoiModel {

    private ReadBookApplication mApp;
    private static ArrayList<Part> mPartList = new ArrayList<Part>();
    private SplashScreenActivity mSplashScreenActivity;

    public BenNhauTronDoiModel(ReadBookApplication app) {
        mApp = app;
    }

    public void initData(SplashScreenActivity splashScreenActivity) {
        mSplashScreenActivity = splashScreenActivity;
        new LoadDataTask().execute();
    }

    public ArrayList<Part> getPartList() {
    	if (mPartList != null || mPartList.size() == 0) {
    		mPartList = loadData();
    	}
        return mPartList;
    }

    private ArrayList<Part> loadData() {
        ArrayList<Part> partList = new ArrayList<Part>();
        AssetManager asset = mApp.getResources().getAssets();
        InputStream is = null;
        try {
            is = asset.open("structure.vas");
        } catch (IOException e1) {
            Toast.makeText(mApp, "No file found!", Toast.LENGTH_SHORT).show();
        }
        if (is != null) {
            BufferedReader buff = new BufferedReader(new InputStreamReader(is));
            String line = null;
            try {
                Part part = new Part();
                while ((line = buff.readLine()) != null) {
                    String[] tokens = line.split("@");
                    if (!TextUtils.equals(part.id, tokens[0])) {
                        part = new Part(tokens[0], tokens[1]);
                        partList.add(part);
                    }
                    part.chapters.add(new Chapter(tokens[2], tokens[3]));
                }
            } catch (IOException e) {
                Toast.makeText(mApp, "Error while loading data!",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return partList;
    }

    private class LoadDataTask extends AsyncTask<Void, Void, ArrayList<Part>> {

        @Override
        protected ArrayList<Part> doInBackground(Void... arg0) {
            return loadData();
        }

        @Override
        protected void onPostExecute(ArrayList<Part> result) {
            super.onPostExecute(result);
            mPartList = result;
            SystemClock.sleep(1000);
            mSplashScreenActivity.finish();
            mSplashScreenActivity.startActivity(new Intent(mSplashScreenActivity, ReadBookActivity.class));
        }
    }

}

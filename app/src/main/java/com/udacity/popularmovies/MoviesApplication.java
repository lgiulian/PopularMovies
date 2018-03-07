package com.udacity.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by iulian on 3/7/2018.
 */

public class MoviesApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}

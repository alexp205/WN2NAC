package org.cook_team.wn2nac;

import android.app.Application;
import android.content.Context;

public class WnApp extends Application {

    private static Context context;
    public static Context getContext(){ return context; }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}

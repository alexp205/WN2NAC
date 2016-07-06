package org.cook_team.wn2nac;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import de.greenrobot.event.EventBus;

public class ActivityMain extends AppCompatActivity implements FragmentNavigationDrawer.NavigationDrawerCallbacks {

    private static EventBus bus = EventBus.getDefault();

    /** NAVIGATION DRAWER **/
    private FragmentNavigationDrawer fragmentNavigationDrawer; // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private static int screenPosition = 0; // Used to store the last screen position.

    private Toolbar toolbar;
    private TextView debug;
    private ScrollView scrollView;
    private WnApp a = WnApp.getInstance();

    private boolean flagDestroy = true;

    final int PERMISSION_ACCESS_FINE_LOCATION = 0, PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION: case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    bus.post(new WnService.ToastEvent(getResources().getString(R.string.activitymainmsg1)));
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startService(new Intent(this, WnService.class));

        startUp();
    }

    public void startUp() {
        // Setup handler for uncaught exceptions.
        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                e.printStackTrace();
            }
        });*/

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);

        debug = (TextView) findViewById(R.id.debug);
        debug.setMovementMethod(new ScrollingMovementMethod());
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        // Set up the drawer.
        fragmentNavigationDrawer = (FragmentNavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        fragmentNavigationDrawer.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        /*// Set up toolbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);*/

        if (!bus.isRegistered(this)) bus.register(this);

        /** Start Windoo service */
        startService(new Intent(this, WnService.class));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment;
        switch(position) {
            default:
            case 0:
                fragment = new FragmentWindooMap(); break;
            case 1:
                fragment = new FragmentHistory(); break;
            case 2:
                fragment = new FragmentConfig(); break;
            case 3:
                fragment = new FragmentAbout(); break;
            case 4:
                fragment = new FragmentWindooGraph(); break;
        }
        screenPosition = position;
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.titles)[screenPosition]);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
    public static class NavigateEvent {
        final int position;
        NavigateEvent(int position) { this.position = position ;}
    }
    public void onEventMainThread(NavigateEvent event) { onNavigationDrawerItemSelected(event.position); }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getResources().getStringArray(R.array.titles)[screenPosition]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!fragmentNavigationDrawer.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen if the drawer is not showing.
            // Otherwise, let the drawer decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_navigation, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        // if (item.getItemId() == R.id.action_settings) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //startService(new Intent(this, WnService.class));
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public void onPause() {
        //stopService(new Intent(this, WnService.class)); // TODO: Let service run in background
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (flagDestroy) {
            stopService(new Intent(this, WnService.class)); // TODO: Let service run in background
            flagDestroy = true;
        }
        bus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(WnMap.GotoEvent event) { fragmentNavigationDrawer.selectItem(0); onNavigationDrawerItemSelected(0); }

    /*public void onEventMainThread(WnSettings.SetIDEvent event) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentConfig()).commit();
        pos = 2;
        mTitle = getResources().getStringArray(R.array.titles)[pos];
        getSupportActionBar().setTitle(mTitle);
        WnService.toast(getResources().getString(R.string.idset));
    }*/

   /* @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case WnService.CLOSE_ACTION:
                exit();
                break;
        }
    }

    private void exit() {
        stopService(new Intent(this, WnService.class));
        finish();
    }*/

    public void onEventMainThread(WnService.DebugEvent event) {
        debug.append(event.message + "\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void onEventMainThread(DebugOnEvent event) {
        scrollView.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(DebugOffEvent event) {
        scrollView.setVisibility(View.GONE);
    }

    public static class DebugOnEvent {}
    public static class DebugOffEvent {}

    //LANGUAGE SELECT CLASSES HERE (MOVE LATER)
    public void exitFrag(Fragment frag, boolean show) {
        getSupportFragmentManager().beginTransaction().hide(frag).remove(frag).commit();
        if (show) {
            WnApp a = WnApp.getInstance();
            if (a.getUse_en()) {
                changeLang("en");
            } else if (a.getUse_zh()) {
                changeLang("zh");
            }
        }
    }

    private void changeLang(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        a.getInstance().turnOffShow_screen();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent refresh = new Intent(this, ActivityMain.class);
        flagDestroy = false;
        startActivity(refresh);
        finish();
    }
}

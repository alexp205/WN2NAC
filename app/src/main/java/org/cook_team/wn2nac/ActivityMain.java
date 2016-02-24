package org.cook_team.wn2nac;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import de.greenrobot.event.EventBus;

public class ActivityMain extends AppCompatActivity
implements FragmentNavigationDrawer.NavigationDrawerCallbacks {

    private static EventBus bus = EventBus.getDefault();

    /** NAVIGATION DRAWER **/
    private FragmentNavigationDrawer fragmentNavigationDrawer; // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private static int screenPosition = 0; // Used to store the last screen position.

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        startService(new Intent(this, WnService.class));
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, WnService.class)); // TODO: Let service run in background
        bus.unregister(this);
        super.onDestroy();
    }

    /*public void onEventMainThread(WnSettings.SetIDEvent event) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentConfig()).commit();
        pos = 2;
        mTitle = getResources().getStringArray(R.array.titles)[pos];
        getSupportActionBar().setTitle(mTitle);
        WnService.toast("ID未設定，請先設定ID");
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
}

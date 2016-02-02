package org.cook_team.wn2nac;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import de.greenrobot.event.EventBus;

public class ActivityMain extends AppCompatActivity
implements FragmentNavigationDrawer.NavigationDrawerCallbacks {

    /** Fragment managing the behaviors, interactions and presentation of the navigation drawer. */
    private FragmentNavigationDrawer mFragmentNavigationDrawer;

    /** Used to store the last screen title. For use in {@link #restoreActionBar()}. */
    private CharSequence mTitle;

    private static EventBus bus = EventBus.getDefault();

    static public int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*// Set up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/

        mTitle = getResources().getStringArray(R.array.titles)[pos];

        // Set up the drawer.
        mFragmentNavigationDrawer = (FragmentNavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mFragmentNavigationDrawer.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (!bus.isRegistered(this)) bus.register(this);

        /** Start Windoo service */
        Intent intent = new Intent(this, WnService.class);
        startService(intent);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment;
        pos = position;
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
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        mTitle = getResources().getStringArray(R.array.titles)[position];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mTitle = getResources().getStringArray(R.array.titles)[pos];
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mFragmentNavigationDrawer.isDrawerOpen()) {
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
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button,
        // so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(this, WnService.class);
        startService(intent);
        mTitle = getResources().getStringArray(R.array.titles)[pos];
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, WnService.class);
        stopService(intent);
        //bus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(WnMap.GotoEvent event) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentWindooMap()).commit();
        pos = 0;
        mTitle = getResources().getStringArray(R.array.titles)[pos];
        getSupportActionBar().setTitle(mTitle);
    }

    public void onEventMainThread(WnMap.OpenEvent event) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentWindooMap()).commit();
        pos = 0;
        mTitle = getResources().getStringArray(R.array.titles)[pos];
        getSupportActionBar().setTitle(mTitle);
    }

    public void onEventMainThread(WnSettings.SetIDEvent event) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentConfig()).commit();
        pos = 2;
        mTitle = getResources().getStringArray(R.array.titles)[pos];
        getSupportActionBar().setTitle(mTitle);
        WnService.toast("ID未設定，請先設定ID");
    }

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

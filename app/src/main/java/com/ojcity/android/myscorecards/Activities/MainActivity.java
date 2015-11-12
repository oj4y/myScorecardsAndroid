package com.ojcity.android.myscorecards.Activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ojcity.android.myscorecards.Fragments.AddFighterFragment;
import com.ojcity.android.myscorecards.Fragments.AddMatchesFragment;
import com.ojcity.android.myscorecards.Fragments.DeleteFighterFragment;
import com.ojcity.android.myscorecards.Fragments.HomeFragment;
import com.ojcity.android.myscorecards.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private HomeFragment homeFragment = new HomeFragment();
    private AddMatchesFragment addMatchesFragment = new AddMatchesFragment();
    private AddFighterFragment addFighterFragment = new AddFighterFragment();
    private DeleteFighterFragment deleteFighterFragment = new DeleteFighterFragment();

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public AddMatchesFragment getAddMatchesFragment() {
        return addMatchesFragment;
    }

    public AddFighterFragment getAddFighterFragment() {
        return addFighterFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up top action bar and menu button
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set up navigation drawer
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        if (savedInstanceState == null) {
            startFragment(homeFragment, "home");
        }

    }

    // public so HomeFragment can access with FAB
    public void startFragment(Fragment fragment, String tag) {
        if (fragment != null && fragment.isVisible())
            return;

        Log.v(TAG, "startFragment(" + fragment.toString() + ")");

        // pass Fragment to FragmentManager and start
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment, tag);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(menuListener);
    }

    private NavigationView.OnNavigationItemSelectedListener menuListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);

                    drawerLayout.closeDrawers();

                    setupMenu(menuItem);
                    return true;
                }
            };

    private void setupMenu(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                startFragment(homeFragment, "home");
                break;
            case R.id.nav_matches:
                startFragment(addMatchesFragment, "matches");
                break;
            case R.id.nav_add_fighters:
                startFragment(addFighterFragment, "boxers");
                break;
            case R.id.nav_delete_fighters:
                startFragment(deleteFighterFragment, "deleteFighters");
                break;
            case R.id.about_dialog:
                displayAboutDialog();
                navigationView.setCheckedItem(R.id.nav_home);
                break;
            default:
                Snackbar.make(drawerLayout, "Invalid choice", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }

    // set back button to close drawer if its open, instead of closing the app
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displayAboutDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("About myScorecards")
                .content("www.ojcity.com")
                .show();
    }

}

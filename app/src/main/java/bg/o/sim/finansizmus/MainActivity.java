package bg.o.sim.finansizmus;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import bg.o.sim.finansizmus.accounts.AccountsFragment;
import bg.o.sim.finansizmus.model.Cacher;
import bg.o.sim.finansizmus.model.Category;
import bg.o.sim.finansizmus.transactionRelated.TransactionFragment;
import bg.o.sim.finansizmus.utils.Util;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private FragmentManager fm;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.main_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        fm = getFragmentManager();

        if (fm.findFragmentByTag(getString(R.string.tag_fragment_home)) == null) {
            String tag = getString(R.string.tag_fragment_home);
            fm.beginTransaction()
                    .replace(R.id.main_fragment_container, new HomeFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        }
        View headerView = navigationView.getHeaderView(0);
        TextView userProfile = headerView.findViewById(R.id.user_profile_link);
        userProfile.setText("Hi, " + Cacher.getLoggedUser().getEmail());

        LinearLayout header = headerView.findViewById(R.id.header);
        header.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        });

        fm.addOnBackStackChangedListener(() -> {
            //TODO - CHANGE DRAWER ITEMS OR WHATEVER
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fm.findFragmentByTag(getString(R.string.tag_fragment_home)) == null) {
            String tag = getString(R.string.tag_fragment_home);
            fm.beginTransaction()
                    .replace(R.id.main_fragment_container, new HomeFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        } else {
            fm.findFragmentByTag(getString(R.string.tag_fragment_home))/*.update();*/; //TODO
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                LogoutDialogFragment dialog = new LogoutDialogFragment();
                dialog.show(getSupportFragmentManager(), getString(R.string.logout));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
            return;
        }
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Util.toastLong(this, getString(R.string.message_double_back));

        Runnable delay = () -> doubleBackToExitPressedOnce = false;
        new Handler().postDelayed(delay, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.isChecked())
            return true;

        String tag = null;
        Fragment f = null;

        switch (item.getItemId()) {
            case R.id.nav_accounts:
                tag = getString(R.string.tag_fragment_accounts);
                f = new AccountsFragment();
                break;
            case R.id.nav_income:
                tag = getString(R.string.tag_fragment_transaction);
                f = TransactionFragment.getNewInstance(Category.Type.INCOME);
                break;
            case R.id.nav_expense:
                tag = getString(R.string.tag_fragment_transaction);
                f = TransactionFragment.getNewInstance(Category.Type.EXPENSE);
                break;
            case R.id.nav_calendar:
                //TODO
                return false;
        }
        if (f == null) return false;
        if (fm.getBackStackEntryCount() > 1) fm.popBackStack();
        fm.beginTransaction()
                .replace(R.id.main_fragment_container, f, tag)
                .addToBackStack(tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //TODO - think of a more elegant solution for checking the proper NavDrawer item.. seriously...

    /**
     * Change the 'checked' NavDrawer item bu view id
     * @param drawerPosition view id of the item to 'check'
     */
    public void setDrawerCheck(int drawerPosition) {
         /*TODO - validate position exists. Somehow....*/
        if (navigationView != null)
            navigationView.setCheckedItem(drawerPosition);
    }
}
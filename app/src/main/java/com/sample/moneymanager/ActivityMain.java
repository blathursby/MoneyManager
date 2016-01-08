package com.sample.moneymanager;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.InputType;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListener {

    DrawerLayout drawer;
    Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if(!prefs.getBoolean("sample", false)) {
            Database db = new Database(getBaseContext());
            db.insertExpense("Car", 25, "Transport");
            db.insertExpense("Bus", 5, "Transport");
            db.insertExpense("Fuel", 15, "Transport");
            db.insertExpense("Taxi", 9, "Transport");

            db.insertExpense("Groceries", 42, "Food");
            db.insertExpense("Meat", 33, "Food");

            db.insertExpense("Water", 12, "Utilities");
            db.insertExpense("Electricity", 86, "Utilities");
            db.insertExpense("Heating", 42, "Utilities");
            db.insertExpense("Phone", 16, "Utilities");
            db.insertExpense("Rent", 250, "Utilities");

            db.insertExpense("Accessories", 24, "Other");
            db.insertExpense("Entertainment", 60, "Other");
            db.insertExpense("Electronics", 543, "Other");
            db.insertExpense("Restaurants", 156, "Other");
            db.insertExpense("Vacations", 1140, "Other");
            db.close();
            db = null;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("sample", true);
            editor.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if(count == 1) {
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = null;
        String name = null;
        switch(id) {
            case R.id.nav_budget_counter:
                fragment = FragmentBudgetCounter.newInstance();
                name = "counter";
                break;
            case R.id.nav_input:
                fragment = FragmentSpentMoney.newInstance();
                name = "input";
                break;
            case R.id.nav_diagnostics:
                fragment = FragmentDiagnostics.newInstance();
                name = "diagnostics";
                break;
            case R.id.nav_statistics:
                fragment = FragmentStatistics.newInstance();
                name = "statistics";
                break;
            case R.id.nav_expenses:
                fragment = FragmentExpenses.newInstance();
                name = "expenses";
                break;
            case R.id.nav_settings:
                fragment = FragmentSettings.newInstance();
                name="settings";
                break;

            default:

        }

        if(fragment !=null) {
            currentFragment = fragment;
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(name);
            fragmentTransaction.commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSpentMoneyItemClicked(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add expense for " + text);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editText);
        builder.setPositiveButton("input", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int value = Integer.parseInt(editText.getText().toString());
                updateSpentMoney(text, value);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onSettingsItemClicked(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set budget for " + text);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editText);
        builder.setPositiveButton("set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int value = Integer.parseInt(editText.getText().toString());
                SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(text, value);
                editor.commit();
                if(currentFragment!=null && currentFragment instanceof  FragmentSettings) {
                    ((FragmentSettings) currentFragment).populate();
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void updateSpentMoney(String text, int value) {
        String category ="";
        if(text.equals("Car")) {
            category = "Transport";
        } else if(text.equals("Bus")) {
            category = "Transport";
        } else if(text.equals("Fuel")) {
            category = "Transport";
        } else if(text.equals("Taxi")) {
            category = "Transport";
        } else if(text.equals("Groceries")) {
            category = "Food";
        } else if(text.equals("Meat")) {
            category = "Food";
        } else if(text.equals("Water")) {
            category = "Utilities";
        } else if(text.equals("Electricity")) {
            category = "Utilities";
        } else if(text.equals("Heating")) {
            category = "Utilities";
        } else if(text.equals("Phone")) {
            category = "Utilities";
        } else if(text.equals("Rent")) {
            category = "Utilities";
        } else if(text.equals("Accessories")) {
            category = "Other";
        } else if(text.equals("Entertainment")) {
            category = "Other";
        } else if(text.equals("Electronics")) {
            category = "Other";
        } else if(text.equals("Restaurants")) {
            category = "Other";
        } else if(text.equals("Vacations")) {
            category = "Other";
        }

        Database db = new Database(getBaseContext());
        db.insertExpense(text, value, category);
        db.close();
        db = null;
        if(currentFragment!=null && currentFragment instanceof  FragmentSpentMoney) {
            ((FragmentSpentMoney) currentFragment).update();
        }
    }

    private void log(String message) {
        Log.d("MoneyManager", message);
    }
}

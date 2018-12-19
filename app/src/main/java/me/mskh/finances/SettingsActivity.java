package me.mskh.finances;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import me.mskh.finances.Model.Costs;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    public final static String Costs = "costs.json";
    Switch switchSMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Выбираем контент для вывода (среди имеющихся Include внутри app_bar_main)
        CoordinatorLayout parent = (CoordinatorLayout) findViewById(R.id.AppBar);
        parent.removeView(findViewById(R.id.content_main));
        parent.removeView(findViewById(R.id.content_stats));
        parent.removeView(findViewById(R.id.content_debts));
        parent.removeView(findViewById(R.id.content_goals));
        parent.removeView(findViewById(R.id.content_records));
        parent.removeView(findViewById(R.id.content_converter));
        //убираем кнопку ПЛЮС
        findViewById(R.id.add).setVisibility(View.GONE);

        //Добавляем toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Добавляем меню
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        switchSMS = (Switch) findViewById(R.id.switchSMS);
        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "чтение SMS включено", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "чтение SMS отлючено", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bills) {
            return true;
        } else if (id == R.id.action_period) {

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        } else if (id == R.id.nav_stats) {
            startActivity(new Intent(SettingsActivity.this, StatisticsActivity.class));

        } else if (id == R.id.nav_records) {
            startActivity(new Intent(SettingsActivity.this, RecordsActivity.class));

        } else if (id == R.id.nav_goals) {
            startActivity(new Intent(SettingsActivity.this, GoalsActivity.class));

        } else if (id == R.id.nav_debts) {
            startActivity(new Intent(SettingsActivity.this, DebtsActivity.class));

        } else if (id == R.id.nav_converter) {
            startActivity(new Intent(SettingsActivity.this, ConverterActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void delete_dialog(View view) {
        FragmentManager manager = getSupportFragmentManager();
        SetDialogDeleteAll myDialogFragment = new SetDialogDeleteAll();
        FragmentTransaction transaction = manager.beginTransaction();
        myDialogFragment.show(transaction, "dialog");
    }
    public void delete_all(){
        ArrayList<Costs> HistoryArr = HelloActivity.getCostsList();
        for (Iterator<Costs> iter = HistoryArr.iterator(); iter.hasNext(); ) {
            Costs cost = iter.next();
            iter.remove();
        }
        save();
    }

    public void save() {
        try {
            FileOutputStream outhist = openFileOutput(Costs, MODE_PRIVATE);
            ArrayList<Costs> HistoryArr = HelloActivity.getCostsList();
            JSONArray history = new JSONArray(HistoryArr);
            outhist.write(history.toString().getBytes());
            outhist.close();
        } catch (IOException e) {
            Log.e("MYAPP", "exception", e);
        }
    }
}

package me.mskh.finances;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import me.mskh.finances.Model.Costs;
import me.mskh.finances.Model.Goal;
import me.mskh.finances.Model.GoalsAdapter;

public class GoalsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<Goal> mGoalList;//список всех целей
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Выбираем контент для вывода (среди имеющихся Include внутри app_bar_main)
        CoordinatorLayout parent = (CoordinatorLayout) findViewById(R.id.AppBar);
        parent.removeView(findViewById(R.id.content_main));
        parent.removeView(findViewById(R.id.content_stats));
        parent.removeView(findViewById(R.id.content_debts));
        parent.removeView(findViewById(R.id.content_records));
        parent.removeView(findViewById(R.id.content_settings));
        parent.removeView(findViewById(R.id.content_converter));
        //убираем кнопку ПЛЮС
        //findViewById(R.id.add).setVisibility(View.GONE);

        //Добавляем кнопку ПЛЮС
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);

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

        //ring progress bar
        /*
        PieChart pieChart = (PieChart)findViewById(R.id.pie_chart);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(75f, ""));
        entries.add(new PieEntry(25f, ""));
        PieDataSet set = new PieDataSet(entries, "");
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.colorPrimary));
        colors.add(ContextCompat.getColor(this, R.color.white));
        set.setColors(colors);
        set.setDrawValues(false);
        PieData pieData = new PieData(set);
        pieChart.setData(pieData);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        pieChart.setHoleRadius(80f);
        pieChart.setCenterText("75%");
        pieChart.invalidate();
        pieChart.getDescription().setEnabled(false);
        */

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GoalsAdapter adapter = new GoalsAdapter();
        mRecyclerView.setAdapter(adapter);
        List<Goal> goalList = getGoalList();
        adapter.setItems(goalList);

    }

    private List<Goal> getGoalList() {
        return Arrays.asList(
                new Goal(45000, 20000, Calendar.getInstance(),
                        "Купить видеокарту"),
                new Goal(5000, 5000, Calendar.getInstance(),
                        "Поменять шкаф в спальне"),
                new Goal(33296, 10987, Calendar.getInstance(),
                        "Оплатить учебу"),
                new Goal(90000, 63000, Calendar.getInstance(),
                        "Поездка на море"),
                new Goal(3500450, 420054, Calendar.getInstance(),
                        "Купить машину")
        );
    }

    public void generateGoals() { //временный метод генерации целей

        mGoalList = new ArrayList<>();
        Random rnd = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) { //пусть будет 10 целей

            double totalSum = rnd.nextInt(9999) + 1; //от 1 до 10000, генерируем общую сумму цели
            double percent = rnd.nextInt(100);//от 0 до 100, генерируем процент от общей суммы
            double contributedSum = totalSum * percent / 100;
            Calendar date = Calendar.getInstance();
            Goal goal = new Goal(totalSum, contributedSum, date, "");
            mGoalList.add(goal);

        }

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
        }
        else if (id == R.id.action_period){

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(GoalsActivity.this, MainActivity.class));
        } else if (id == R.id.nav_stats) {
            startActivity(new Intent(GoalsActivity.this, StatisticsActivity.class));

        } else if (id == R.id.nav_records) {
            startActivity(new Intent(GoalsActivity.this, RecordsActivity.class));

        } else if (id == R.id.nav_goals) {
            startActivity(new Intent(GoalsActivity.this, GoalsActivity.class));

        } else if (id == R.id.nav_debts) {
            startActivity(new Intent(GoalsActivity.this, DebtsActivity.class));

        } else if (id == R.id.nav_converter) {
            startActivity(new Intent(GoalsActivity.this, ConverterActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(GoalsActivity.this, SettingsActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}



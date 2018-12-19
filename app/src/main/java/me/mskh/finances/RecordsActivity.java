package me.mskh.finances;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import me.mskh.finances.Model.Category;
import me.mskh.finances.Model.Costs;

public class RecordsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public final static String Costs = "costs.json";
    SimpleDateFormat formatter =  new SimpleDateFormat("dd.MM.yyyy");

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


        //Загружаем список операция текущего пользователя из HelloActivity
        ArrayList<Costs> HistoryArr = HelloActivity.getCostsList();

        ListView listView = (ListView) findViewById(R.id.CostsList); //находим ListView на странице
        final RecordsFullAdapter mArrayAdapter = new RecordsFullAdapter(this, R.layout.record_item, HistoryArr);
        listView.setAdapter(mArrayAdapter);
        AdapterView.OnItemLongClickListener itemListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View v,final int position, long id) {
                String title = "Отмена";
                String message = "Вы уверены что хотите удалить выбранный элемент?";
                String button1String = "Нет";
                String button2String = "Да";

                AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(
                            RecordsActivity.this);

                builder.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Элемент был удален", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                Costs selectedItem = (Costs)parent.getItemAtPosition(position);
                                mArrayAdapter.remove(selectedItem);
                                mArrayAdapter.notifyDataSetChanged();
                                save(selectedItem);
                            }
                        })
                        .show();

                return true;
            }
        };
        listView.setOnItemLongClickListener(itemListener);
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
            startActivity(new Intent(RecordsActivity.this, MainActivity.class));
        } else if (id == R.id.nav_stats) {
            startActivity(new Intent(RecordsActivity.this, StatisticsActivity.class));

        } else if (id == R.id.nav_records) {
            startActivity(new Intent(RecordsActivity.this, RecordsActivity.class));

        } else if (id == R.id.nav_goals) {
            startActivity(new Intent(RecordsActivity.this, GoalsActivity.class));

        } else if (id == R.id.nav_debts) {
            startActivity(new Intent(RecordsActivity.this, DebtsActivity.class));

        } else if (id == R.id.nav_converter) {
            startActivity(new Intent(RecordsActivity.this, ConverterActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(RecordsActivity.this, SettingsActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void save(Costs selectedItem) {
        try {
            FileOutputStream outhist = openFileOutput(Costs, MODE_PRIVATE);
            ArrayList<Costs> HistoryArr = HelloActivity.getCostsList();
            HistoryArr.remove(selectedItem);
            JSONArray history = new JSONArray();
            for (Costs cost : HistoryArr) {
                JSONObject tmp = new JSONObject();
                tmp.put("sum", Double.toString(cost.getSum()));
                tmp.put("isprofit", Boolean.toString(cost.isIsprofit()));
                tmp.put("date", formatter.format(cost.getDate().getTime())).toString();
                tmp.put("description", cost.getDescription());
                tmp.put("categories", cost.getCat());
                history.put(tmp);
            }
            outhist.write(history.toString().getBytes());
            outhist.close();
        } catch (IOException e) {
            Log.e("MYAPP", "exception", e);
        } catch (JSONException e) {
            Log.e("MYAPP", "exception", e);
        }
    }
}

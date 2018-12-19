package me.mskh.finances;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

import me.mskh.finances.Model.Costs;
import me.mskh.finances.Model.SMS;
import me.mskh.finances.Model.SMSParser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        TextView balance;
        TextView spending;
        TextView incoming;
        double bal;
        double spen;
        double income;
    public static ArrayList<SMS> allsms=new ArrayList<>();
    public static Calendar timesmsupdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Выбираем контент для вывода (среди имеющихся Include внутри app_bar_main)
        CoordinatorLayout parent = (CoordinatorLayout) findViewById(R.id.AppBar);
        parent.removeView(findViewById(R.id.content_records));
        parent.removeView(findViewById(R.id.content_stats));
        parent.removeView(findViewById(R.id.content_debts));
        parent.removeView(findViewById(R.id.content_goals));
        parent.removeView(findViewById(R.id.content_settings));
        parent.removeView(findViewById(R.id.content_converter));

        //Добавляем toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Добавляем кнопку ПЛЮС
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });

        //Добавляем меню
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Выбираем необходимые элементы со страницы
        balance =(TextView)findViewById(R.id.currentBalance);
         spending =(TextView)findViewById(R.id.currentExpense);
        incoming =(TextView)findViewById(R.id.currentRevenue) ;
        bal=0;
        income=0;
        spen=0;
        //Загружаем список операция текущего пользователя из HelloActivity
        ArrayList<Costs> HistoryArr = HelloActivity.getCostsList();
        if(isSmsPermissionGranted()) {
            loadsms();


            for (SMS i : allsms) {
                Costs tmp = SMSParser.parse(i);
                boolean have = false;
                for (Costs j : HistoryArr) {
                    if (j.getSum() == tmp.getSum() && j.getDescription().equals(tmp.getDescription())) {
                        have = true;
                    }
                }
                if (!have && tmp.getSum() > 0) {
                    HistoryArr.add(tmp);
                }
            }
        }
        //Вычисляем текущий баланс
        for(Costs cost : HistoryArr)
        {
            if(cost.isIsprofit()) income+=cost.getSum();
            else spen+=cost.getSum();
        }
        bal=income-spen;

        //Устанавливаем значения текстовых полей
        String fullBalance = String.format(Locale.getDefault(),"%1$,.2f",bal)+" руб.";
        balance.setText(fullBalance);
        String fullExpense = String.format(Locale.getDefault(),"%1$,.2f",spen)+" руб.";
        spending.setText(fullExpense);
        //spending.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.expense));
        String fullIncome = String.format(Locale.getDefault(),"%1$,.2f",income)+" руб.";
        incoming.setText(fullIncome);
        //incoming.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.income));


        //Загружаем список ViewList последних записи(операции)
        ListView listView = (ListView) findViewById(R.id.lastCostsList); //находим ListView на странице
        ArrayList<Costs> shortCostsList = new ArrayList<Costs>(HistoryArr);

        if(shortCostsList.size()>=6) {
            int k=0;
            for (Iterator<Costs> iter = shortCostsList.iterator(); iter.hasNext(); ) {
                Costs cost = iter.next();
                if(k>5)
                    iter.remove();

                k++;
            }
            findViewById(R.id.more).setVisibility(View.VISIBLE);
        }

        final RecordsAdapter mArrayAdapter = new RecordsAdapter(this, R.layout.record_item, shortCostsList);
        listView.setAdapter(mArrayAdapter);
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
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else if (id == R.id.nav_stats) {
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));

        } else if (id == R.id.nav_records) {
            startActivity(new Intent(MainActivity.this, RecordsActivity.class));

        } else if (id == R.id.nav_goals) {
            startActivity(new Intent(MainActivity.this, GoalsActivity.class));

        } else if (id == R.id.nav_debts) {
            startActivity(new Intent(MainActivity.this, DebtsActivity.class));

        } else if (id == R.id.nav_converter) {
            startActivity(new Intent(MainActivity.this, ConverterActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void loadsms()
    {
        Uri uriSms = Uri.parse("content://sms/");
        Context context=this;
        Cursor cur = context.getContentResolver().query(uriSms, null,null,null,null);
        startManagingCursor(cur);
        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
        if (cur.getCount() > 0){
            while (cur.moveToNext()){

                if(cur.getString(2)!=null&&cur.getString(2).equals("900")) {
                    SMS ones=new SMS();
                    try {
                        Date date = format1.parse(format1.format(cur.getLong(4)));
                        Calendar time= new GregorianCalendar();
                        time.setTime(date);
                        ones.setDate(time);
                    }
                    catch (ParseException e)
                    {
                        Log.e("ERROR",e.toString());
                    }
                    ones.setAddres(cur.getString(2));
                    ones.setBody(cur.getString(12));
               allsms.add(ones);
                }
            }
        }
    }
    public void loadsmstime()
    {

    }
    public  boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }
    public void showAllRecords(View view){
        startActivity(new Intent(MainActivity.this, RecordsActivity.class));
    }
}

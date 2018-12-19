package me.mskh.finances;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import me.mskh.finances.Model.Category;
import me.mskh.finances.Model.Costs;

public class StatisticsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<Costs> HistoryArr = new ArrayList<Costs>(); //все доходы и расходы
    TreeSet<Category> CategoriesArr;
    private ViewPager mViewPager;
    private StatisticsPagerAdapter adapter;
    private GestureDetector mDetector; //обработчик swipe действий для кнопки выбора дат
    private ViewFlipper viewFlipper; //для прокрутки кнопок
    private int currentPeriodIndex;//индекс для определения, какой текст отображается на кнопке
    private Button mButtonDate; //кнопка выбора периода
    String[] periodNames; //названия периодов на mButtonDate
    private List<Pay> listOfPays;
    //MyOnPageChangeListener mMyOnPageChangeListener;
    FragmentManager mFragmentManager;

    public List<Pay> getListOfPaysByDate(Calendar firstDate) {
        if (listOfPays == null) { //если списка еще неt

            Calendar dateBeforeYear = Calendar.getInstance();
            dateBeforeYear.add(Calendar.YEAR, -1);
            listOfPays = generatePays(dateBeforeYear);
        }
        Calendar dateOfPay = Calendar.getInstance();
        int i=0;
        List<Pay> tmp = new ArrayList<>();
       for( i=0; i < listOfPays.size(); i++)
        {
            Log.d("gg",Long.toString(listOfPays.get(i).getDate().getTime()));
            Log.d("gg",Long.toString(firstDate.getTime().getTime()));
            //if (listOfPays.get(i).getDate().getTime() >= firstDate.getTime().getTime()
                    //&& dateOfPay.getTime().getTime() > listOfPays.get(i).getDate().getTime())
            Calendar dateOfPayInList = Calendar.getInstance();
            dateOfPayInList.setTime(listOfPays.get(i).getDate());
            if ((dateOfPayInList.get(Calendar.YEAR) > firstDate.get(Calendar.YEAR)) ||
                    ((dateOfPayInList.get(Calendar.YEAR) == firstDate.get(Calendar.YEAR)) &&
                            (dateOfPayInList.get(Calendar.MONTH) > firstDate.get(Calendar.MONTH))) ||
                    ((dateOfPayInList.get(Calendar.YEAR) == firstDate.get(Calendar.YEAR)) &&
                            (dateOfPayInList.get(Calendar.MONTH) == firstDate.get(Calendar.MONTH)) &&
                            (dateOfPayInList.get(Calendar.DATE) >= firstDate.get(Calendar.DATE))))
            {
               tmp.add(listOfPays.get(i));
            }


        }

        return tmp;
    }

    public List<Pay> getListOfPays() {
        if (listOfPays == null) { //если списка еще неt

            Calendar dateBeforeYear = Calendar.getInstance();
            dateBeforeYear.add(Calendar.YEAR, -1);
            listOfPays = generatePays(dateBeforeYear);
        }
        return listOfPays;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_statistics);
        setContentView(R.layout.activity_main);

        //Выбираем контент для вывода (среди имеющихся Include внутри app_bar_main)
        CoordinatorLayout parent = (CoordinatorLayout) findViewById(R.id.AppBar);
        parent.removeView(findViewById(R.id.content_main));
        parent.removeView(findViewById(R.id.content_goals));
        parent.removeView(findViewById(R.id.content_debts));
        parent.removeView(findViewById(R.id.content_records));
        parent.removeView(findViewById(R.id.content_settings));
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


        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
        getSupportActionBar().setElevation(0);//удаляем тень между appbar и tabs

        //Определяем текущую дату
        Calendar currDate = Calendar.getInstance();
        //Отсчитываем дату неделю назад
        currDate.add(Calendar.DATE, -6);
        //Date dateBeforeWeek = currDate.getTime();
        //Генерируем список платежей
        //listOfPays = this.generatePays(currDate);

        mViewPager = (ViewPager)findViewById(R.id.statistics_view_pager);
        mFragmentManager = getSupportFragmentManager();

        adapter = new StatisticsPagerAdapter(this, mFragmentManager);
        adapter.setListOfPays(getListOfPays());
        Calendar dateBeforeWeek = Calendar.getInstance(); //дата по умолчанию
        dateBeforeWeek.add(Calendar.DATE, -6);
        Resources res = getResources();
        String[] tabNames = res.getStringArray(R.array.statistics_tab_names); //получаем названия вкладок
        adapter.addFragmentNames(tabNames);//сообщаем названия вкладок адаптеру
        currentPeriodIndex = 0;//индекс периода по умолчанию
        adapter.setCurrentPeriodIndex(currentPeriodIndex); //сообщаем индекс текущего периода
        adapter.setFirstDate(dateBeforeWeek); //сообщаем адаптеру дату по умолчанию
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(1);
        //mMyOnPageChangeListener = new MyOnPageChangeListener();
        //mViewPager.addOnPageChangeListener(mMyOnPageChangeListener);

        int a  = 0;

        //Кнопка выбора даты
        mButtonDate = (Button)findViewById(R.id.period_button);
        //делаем проверку, так как кнопки для дат нет в альбомном положении
        if (mButtonDate != null) {
            mDetector = new GestureDetector(this, new MyGestureListener());

            //все события передаем Gesture Detector
            mButtonDate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mDetector.onTouchEvent(event);
                    return true;
                }
            });

            periodNames = getResources().getStringArray(R.array.periods);
            mButtonDate.setText(periodNames[currentPeriodIndex]);
        }

        //Используется для прокрутки кнопок
        //viewFlipper = (ViewFlipper) findViewById(R.id.buttons_period_and_date);
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");

            // нужно возвращать true, а иначе остальные методы не будут работать
            return true;
        }



        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;
        //Здесь происходит обработка swipe действия
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            Log.d("TAG", "onFling: ");
            float sensitivity = 50;

            if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (currentPeriodIndex == periodNames.length - 1) {
                    currentPeriodIndex = 0;
                } else {
                    currentPeriodIndex++;
                }

            } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                if (currentPeriodIndex == 0) {
                    currentPeriodIndex = periodNames.length - 1;
                } else {
                    currentPeriodIndex--;
                }
            }

            //если произошел swipe, не важно, влево или вправо
            if (Math.abs(event2.getX() - event1.getX()) > SWIPE_MIN_DISTANCE) {
                //получаем начальную дату в соответствии с периодом
                Calendar firstDate = getDateForCurrentPeriod(currentPeriodIndex);
                //оповещаем adapter об изменении начальной даты
                adapter.setFirstDate(firstDate);
                adapter.setCurrentPeriodIndex(currentPeriodIndex); //сообщаем индекс нового периода
                //изменяем текст на кнопке в соответствии с периодои
                mButtonDate.setText(periodNames[currentPeriodIndex]);
                //Получаем текущий фрагмент
                //позиция текущего фрагмента
                //int position = mMyOnPageChangeListener.getCurrentPagePosition();
                UpdatingUI currFragment = (UpdatingUI) adapter.getCurrentFragment();
                currFragment.setCurrentPeriodIndex(currentPeriodIndex);
                currFragment.updateUI(firstDate);
                currFragment.setFirstDate(firstDate);

            }
            return true;
        }

    }

    //Интерфейс для вызова метода updateUI фрагментов
    public interface UpdatingUI {
        void updateUI(Calendar firstDate);
        void setFirstDate(Calendar firstDate);
        void setCurrentPeriodIndex(int currentPeriodIndex);
        Calendar getFirstDate();
    }

    //Метод для получения даты для выбранного периода
    private Calendar getDateForCurrentPeriod(int currentPeriodIndex) {
        //Определяем текущую дату
        Calendar dateBefore = Calendar.getInstance();
        java.util.Date dt = dateBefore.getTime();
        switch(currentPeriodIndex) {
            case 0:
                //Отсчитываем дату неделю назад
                dateBefore.add(Calendar.DATE, -6);
                break;
            case 1:
                //Отсчитываем дату месяц назад
                dateBefore.add(Calendar.MONTH, -1);
                dt = dateBefore.getTime();
                break;
            case 2:
                //Отсчитываем дату 12 недель назад
                //dateBefore.add(Calendar.MONTH, -3);
                dateBefore.add(Calendar.DATE, -83);
                break;
            case 3:
                //Отсчитываем дату 6 месяцев назад
                dateBefore.add(Calendar.DATE, -181);
                break;
            case 4:
                //Отсчитываем дату год назад
                dateBefore.add(Calendar.YEAR, -1);
                dateBefore.set(Calendar.DATE, 1);
                dateBefore.add(Calendar.MONTH, 1);
                break;
        }
        return dateBefore;
    }

    //метод для генерации платежей
    private List<Pay> generatePays(Calendar dateBefore) {
        CategoriesArr = HelloActivity.getCategoriesList();
        HistoryArr = HelloActivity.getCostsList();
        listOfPays = new ArrayList<>();//
        for(Costs tmp : HistoryArr)
        {
            int id=-1;
            for(Category i : CategoriesArr)
            {
                if(tmp.getCat().equals(i.getCategoryName()))
                {
                    id=i.getStatsId();
                }
            }

            //если дата позже, чем сегодняшняя, пропускаем платеж
            Calendar today = Calendar.getInstance();
            if (((tmp.getDate().get(Calendar.YEAR) > today.get(Calendar.YEAR)) ||
                    ((tmp.getDate().get(Calendar.YEAR) == today.get(Calendar.YEAR)) &&
                            (tmp.getDate().get(Calendar.MONTH) > today.get(Calendar.MONTH))) ||
                    ((tmp.getDate().get(Calendar.YEAR) == today.get(Calendar.YEAR)) &&
                            (tmp.getDate().get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                            (tmp.getDate().get(Calendar.DATE) > today.get(Calendar.DATE)))))
            {
                continue;
            }

            if(((tmp.getDate().get(Calendar.YEAR) > dateBefore.get(Calendar.YEAR)) ||
                    ((tmp.getDate().get(Calendar.YEAR) == dateBefore.get(Calendar.YEAR)) &&
                        (tmp.getDate().get(Calendar.MONTH) > dateBefore.get(Calendar.MONTH))) ||
                ((tmp.getDate().get(Calendar.YEAR) == dateBefore.get(Calendar.YEAR)) &&
                        (tmp.getDate().get(Calendar.MONTH) == dateBefore.get(Calendar.MONTH)) &&
                        (tmp.getDate().get(Calendar.DATE) >= dateBefore.get(Calendar.DATE))))&&id!=-1)
            {
                Pay pay = new Pay(tmp.getSum(),tmp.getDate().getTime(),id);
                listOfPays.add(pay);
            }
        }

//        Random rnd = new Random(System.currentTimeMillis());
//        int countOfPays = 0; //число платежей в день
//        int sumOfPay = 0;//сумма отдельного платежа
//        int categoryOfPay = 0; //категория платежа
//        Calendar currDate = Calendar.getInstance();
//        currDate.add(Calendar.DATE, 1);
//        /*
//        for (Calendar d = dateBefore;
//             d.get(Calendar.YEAR) < currDate.get(Calendar.YEAR)
//                     || d.get(Calendar.MONTH) < currDate.get(Calendar.MONTH)
//                     || d.get(Calendar.DATE) < currDate.get(Calendar.DATE);
//             d.add(Calendar.DATE, 1)) */
//        for (Calendar d = dateBefore; (currDate.get(Calendar.YEAR) > d.get(Calendar.YEAR)) ||
//                ((currDate.get(Calendar.YEAR) == d.get(Calendar.YEAR)) &&
//                        (currDate.get(Calendar.MONTH) > d.get(Calendar.MONTH))) ||
//                ((currDate.get(Calendar.YEAR) == d.get(Calendar.YEAR)) &&
//                        (currDate.get(Calendar.MONTH) == d.get(Calendar.MONTH)) &&
//                        (currDate.get(Calendar.DATE) > d.get(Calendar.DATE)));
//             d.add(Calendar.DATE, 1)){
//            countOfPays = rnd.nextInt(4); //генерируем число платежей в текущий день
//            //генерируем платежи в текущий день
//            int a = 0;
//            for (int i = 0; i <= countOfPays; i++) {
//                sumOfPay = rnd.nextInt(4999) + 1; //от 1 до 5000
//                //AnnotationCategory annotationCategory = new AnnotationCategory(rnd.nextInt(8));
//                int category = rnd.nextInt(11);
//                Pay pay = new Pay(sumOfPay, d.getTime(), category);
//                listOfPays.add(pay);
//                //Log.d("TAG2","Sum: " + Double.toString(pay.getSum()) + '\n');
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                //Log.d("TAG2","Date: " + sdf.format(pay.getDate().getTime()) + "\n\n");
//            }
//
//        }
//
//        int a = 0;

        return listOfPays;

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
            startActivity(new Intent(StatisticsActivity.this, MainActivity.class));
        } else if (id == R.id.nav_stats) {
            startActivity(new Intent(StatisticsActivity.this, StatisticsActivity.class));

        } else if (id == R.id.nav_records) {
            startActivity(new Intent(StatisticsActivity.this, RecordsActivity.class));

        } else if (id == R.id.nav_goals) {
            startActivity(new Intent(StatisticsActivity.this, GoalsActivity.class));

        } else if (id == R.id.nav_debts) {
            startActivity(new Intent(StatisticsActivity.this, DebtsActivity.class));

        } else if (id == R.id.nav_converter) {
            startActivity(new Intent(StatisticsActivity.this, ConverterActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(StatisticsActivity.this, SettingsActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}



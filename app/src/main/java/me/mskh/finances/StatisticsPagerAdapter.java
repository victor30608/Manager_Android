package me.mskh.finances;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.List;

public class StatisticsPagerAdapter extends FragmentPagerAdapter {

    //private List<Fragment> mFragmentsList = new ArrayList<>(); //список фрагментов
    private String[] mFragmentNamesList; //список названий фрагментов
    private Context context = null;
    private int position;
    private List<Pay> listOfPays;
    private Fragment currentFragment;
    private int currentPeriodIndex; //индекс текущего периода
    private Calendar firstDate;//текущая начальная дата
    private boolean firstRun = true; //для проверки, первый ли раз было запущено StatisticsActivity

    public void setListOfPays (List<Pay> listOfPays) {
        this.listOfPays = listOfPays;
    }

    public void setFirstDate(Calendar period) {
        firstDate = period;
    }

    public void setCurrentPeriodIndex(int currentPeriodIndex) {
        this.currentPeriodIndex = currentPeriodIndex;
    }

    public void addFragmentNames(String[] fragmentNamesList) {
        mFragmentNamesList = fragmentNamesList;
        this.context = context;
    }

    public StatisticsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentNamesList[position];
    }

    @Override
    public Fragment getItem(int i) {
        //currentFragment = null;
        //fragment = null;
        switch(i) {
            case 0:
                currentFragment = ExpensesPieChartFragment.newInstance();
                ((ExpensesPieChartFragment)currentFragment).setFirstDate(firstDate);
                //return ExpensesPieChartFragment.newInstance();
                break;
            case 1:
                currentFragment = ExpensesBarChartFragment.newInstance();
                ((ExpensesBarChartFragment)currentFragment).setFirstDate(firstDate);
                //return ExpensesBarChartFragment.newInstance();
                break;
            //case 2:
                //currentFragment = AccountCashesLineChartFragment.newInstance();
                //return AccountCashesLineChartFragment.newInstance();
                //break;
        }
        position = i;
        return currentFragment;
        //return null;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public int getPosition() {
        return position;
    }

    //Этот метод указывает адаптеру, какая из вкладок сейчас отображается
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
            int a = 0;
            //для столбчатых диаграмм установим также индекс периода
            //для более точных расчетов
            if (currentFragment instanceof ExpensesBarChartFragment) {
                ((ExpensesBarChartFragment)currentFragment).setCurrentPeriodIndex(currentPeriodIndex);
            }
                ((StatisticsActivity.UpdatingUI)currentFragment).updateUI(firstDate);
                ((StatisticsActivity.UpdatingUI)currentFragment).setFirstDate(firstDate);
        }
        firstRun = false;
        super.setPrimaryItem(container, position, object);
    }
}

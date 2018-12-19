package me.mskh.finances;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Random;

public class SavingsBarChartFragment extends Fragment {

    private BarData mBarData;


    public SavingsBarChartFragment() {
        // Required empty public constructor
    }

    //Пока нет никаких аргументов
    public static ExpensesBarChartFragment newInstance() {
        ExpensesBarChartFragment fragment = new ExpensesBarChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //Возвращает заголовок для названий вкладок
    public static String getTitle() {
        return "Накопления"; //засунуть в strings.xml!!
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Random rnd = new Random(System.currentTimeMillis());
        ArrayList<BarEntry> entries = new ArrayList<>();
        int number = 0;
        int sum = 0;
        for (int i = 1; i < 32; i++) {
            number = rnd.nextInt(500);
            sum += number;
            entries.add(new BarEntry((float)i, (float)sum));
        }
        BarDataSet expensesSet = new BarDataSet(entries, "");
        expensesSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorExpensesBar));
        mBarData = new BarData(expensesSet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expenses_bar_chart, container, false);
        BarChart barChart = (BarChart)view.findViewById(R.id.bar_chart);
        barChart.setData(mBarData);
        barChart.setFitBars(true);
        barChart.invalidate();
        return view;
    }


}

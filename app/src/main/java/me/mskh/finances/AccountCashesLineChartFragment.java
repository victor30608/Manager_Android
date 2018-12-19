package me.mskh.finances;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountCashesLineChartFragment extends Fragment {

    private LineData mLineData;


    public AccountCashesLineChartFragment() {
        // Required empty public constructor
    }

    //Пока нет никаких аргументов
    public static AccountCashesLineChartFragment newInstance() {
        AccountCashesLineChartFragment fragment = new AccountCashesLineChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //Возвращает заголовок для названий вкладок
    //public static String getTitle() {
        //return "Суммы расходов";
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Random rnd = new Random(System.currentTimeMillis());
        //Создать 3 счета, заполнить их данными за месяц, добавить каждый в свой set
        List<Entry> account1 = new ArrayList<>();
        List<Entry> account2 = new ArrayList<>();
        List<Entry> account3 = new ArrayList<>();
        int number;
        for (int i = 0; i < 32; i+=2) {
            number = rnd.nextInt(20000 + 1) + 40000;
            account1.add(new Entry(i + 1, number));
            number = rnd.nextInt(20000 + 1) + 40000;
            account2.add(new Entry(i + 1, number));
            number = rnd.nextInt(20000 + 1) + 40000;
            account3.add(new Entry(i + 1, number));
        }
        //сделать отдельный метод для форматирования графика
        LineDataSet setAccount1 = new LineDataSet(account1, "Счет 1");
        setAccount1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setAccount1.setDrawValues(false);
        setAccount1.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccount1));
        setAccount1.setLineWidth(1.5f);
        //setAccount1.setDrawFilled(true); заполнение пространства под графиком цветом
        //setAccount1.setFillColor(ContextCompat.getColor(getActivity(), R.color.colorAccount1));
        LineDataSet setAccount2 = new LineDataSet(account2, "Счет 2");
        setAccount2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setAccount2.setDrawValues(false);
        setAccount2.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccount2));
        setAccount2.setLineWidth(1.5f);
        LineDataSet setAccount3 = new LineDataSet(account3, "Счет 3");
        setAccount3.setAxisDependency(YAxis.AxisDependency.LEFT);
        setAccount3.setDrawValues(false);
        setAccount3.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccount3));
        setAccount3.setLineWidth(1.5f);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setAccount1);
        dataSets.add(setAccount2);
        dataSets.add(setAccount3);

        mLineData = new LineData(dataSets);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_cashes_line_chart, container, false);
        LineChart mLineChart = (LineChart)view.findViewById(R.id.line_chart);
        mLineChart.setData(mLineData);
        mLineChart.setVisibleXRangeMaximum(10f);//на экране видны 10 значений
        mLineChart.invalidate();
        return view;
    }

}

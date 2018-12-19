package me.mskh.finances;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class ExpensesBarChartFragment extends Fragment implements me.mskh.finances.StatisticsActivity.UpdatingUI {

    private BarData mBarData;
    private BarChart mBarChart;
    private int currentPeriodIndex; //индекс текущего периода
    private Calendar firstDate; //начальная дата отсчета
    private List<Pay> listOfPays;
    private List<BarEntry> entries;
    private Calendar lastDateInFirstBar; //для отображения на оси диаграммы
    private Calendar firstDateInLastBar; //для отображения на оси диаграммы
    private Calendar firstDateInFirstBar; //для отображения на оси диаграммы
    private TextView mTextViewFirstDate;
    private TextView mTextViewLastDate;


    public ExpensesBarChartFragment() {
        // Required empty public constructor
    }

    //Пока нет никаких аргументов
    public static ExpensesBarChartFragment newInstance() {
        ExpensesBarChartFragment fragment = new ExpensesBarChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setFirstDate(Calendar period) {
        firstDate = period;
    }

    @Override
    public Calendar getFirstDate() {
        return firstDate;
    }

    public void setCurrentPeriodIndex(int currentPeriodIndex) {
        this.currentPeriodIndex = currentPeriodIndex;
    }

    //Возвращает заголовок для названий вкладок
    public static String getTitle() {
        return "Суммы расходов"; //засунуть в strings.xml!!
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Определяем текущую дату
        Calendar dateBeforeWeek = Calendar.getInstance();
        //Отсчитываем дату неделю назад
        dateBeforeWeek.add(Calendar.DATE, -6);
        firstDate = dateBeforeWeek;
        //получаем список платежей в соответствии с текущей датой

        listOfPays = ((StatisticsActivity)getActivity()).getListOfPaysByDate(firstDate);
        if (listOfPays.size() != 0) { //если список платежей не пуст
            entries = convertPaysToEntries(firstDate);
            //получаем соответствующий список entries
            BarDataSet expensesSet = new BarDataSet(entries, "");
            expensesSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorExpensesBar));
            mBarData = new BarData(expensesSet);
            currentPeriodIndex = 0;
        }


  /*
        Random rnd = new Random(System.currentTimeMillis());
        ArrayList<BarEntry> entries = new ArrayList<>();
        int number = 0;
        for (int i = 1; i < 32; i++) {
            number = rnd.nextInt(10000 + 1);
            entries.add(new BarEntry((float)i, (float)number));
        }
        BarDataSet expensesSet = new BarDataSet(entries, "");
        expensesSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorExpensesBar));
        mBarData = new BarData(expensesSet);
*/

    }

    private List<BarEntry> convertPaysToEntries(Calendar firstDate) {
        List<Pay> newpay = new ArrayList<>();
        for(int i = listOfPays.size()-1; i>=0; i--)
        {
            newpay.add(listOfPays.get(i));
        }
        Log.d("TAG","convertPaysToEntries:");
        entries = new ArrayList<>();
        Calendar currDate = Calendar.getInstance();
        //Проходимся до текущей даты
        int daysInBar = 0; //число дней в столбце
        int hlpCounter = 0;//вспомогательный счетчик
        float counterOfBars = 1f; //счетчик столбцов
        Calendar date = firstDate;
        //daysInBar = generateDaysBeforeNewEntry(hlpCounter, date);
        //Calendar calendar = Calendar.getInstance();
        //BarEntry entry = new BarEntry(counter, 0f);
        double value = 0; //общая сумма
        //генерируем первый диапазон
        //день перед первым в столбце
        Calendar beforeFirstDayInBar = (GregorianCalendar)firstDate.clone();
        beforeFirstDayInBar.add(Calendar.DATE, -1);
        Calendar afterLastDayInBar = (GregorianCalendar)firstDate.clone();
        //день после последнего в столбце
        Pay p = null;
        afterLastDayInBar.add(Calendar.DATE, generateDaysBeforeNewEntry(hlpCounter, firstDate));
        firstDateInFirstBar = (GregorianCalendar)firstDate.clone();
        lastDateInFirstBar = (GregorianCalendar)afterLastDayInBar.clone();
        lastDateInFirstBar.add(Calendar.DATE, -1);
        //Log.d("TAG","Before first day: " + beforeFirstDayInBar.getTime());
        //Log.d("TAG","After last day: " + afterLastDayInBar.getTime());
        for (int i = 0; i < newpay.size(); i++) {
            p = newpay.get(i);
            //Log.d("TAG","Sum: " + Double.toString(p.getSum()) + '\n');
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            //Log.d("TAG","Date: " + sdf.format(p.getDate().getTime()) + "\n\n");
            //если дата платежа попадает в диапазон по верхней границе
            Calendar tmp = Calendar.getInstance();
            tmp.setTime(p.getDate());
            if ((afterLastDayInBar.get(Calendar.YEAR) > tmp.get(Calendar.YEAR)) ||
                    ((afterLastDayInBar.get(Calendar.YEAR) == tmp.get(Calendar.YEAR)) &&
                            (afterLastDayInBar.get(Calendar.MONTH) > tmp.get(Calendar.MONTH))) ||
                    ((afterLastDayInBar.get(Calendar.YEAR) == tmp.get(Calendar.YEAR)) &&
                            (afterLastDayInBar.get(Calendar.MONTH) == tmp.get(Calendar.MONTH)) &&
                            (afterLastDayInBar.get(Calendar.DATE) > tmp.get(Calendar.DATE)))) {
                value += p.getSum();
                int a = 0;
            } else { //если не попадает
                //Заполняем столбец
                BarEntry entry = new BarEntry(counterOfBars, (float)value);
                entries.add(entry);
                value = 0; //Сбрасываем value
                counterOfBars++; //Увеличиваем счетчик столбцов
                while(true) { //выполняем цикл до тех пор, пока не найдем диапазон,
                    //в который попадет дата платежа
                    beforeFirstDayInBar = (GregorianCalendar) afterLastDayInBar.clone();
                    beforeFirstDayInBar.add(Calendar.DATE, -1);
                    int a = generateDaysBeforeNewEntry(hlpCounter, afterLastDayInBar);
                    afterLastDayInBar.add(Calendar.DATE, a);
                    //Log.d("TAG","Before first day: " + beforeFirstDayInBar.getTime());
                    //Log.d("TAG","After last day: " + afterLastDayInBar.getTime());
                    //Calendar tmp = Calendar.getInstance();
                    tmp.setTime(p.getDate());
                    if ((afterLastDayInBar.get(Calendar.YEAR) > tmp.get(Calendar.YEAR)) ||
                            ((afterLastDayInBar.get(Calendar.YEAR) == tmp.get(Calendar.YEAR)) &&
                                    (afterLastDayInBar.get(Calendar.MONTH) > tmp.get(Calendar.MONTH))) ||
                            ((afterLastDayInBar.get(Calendar.YEAR) == tmp.get(Calendar.YEAR)) &&
                                    (afterLastDayInBar.get(Calendar.MONTH) == tmp.get(Calendar.MONTH)) &&
                                    (afterLastDayInBar.get(Calendar.DATE) > tmp.get(Calendar.DATE)))) { //если попал в сгенерированный диапазон
                        value += p.getSum();
                        break;
                    } else {
                        entry = new BarEntry(counterOfBars, 0f); //в диапазон не попадает ни один платеж
                        entries.add(entry);
                        counterOfBars++;
                    }
                }
            }

        }
        firstDateInLastBar = (GregorianCalendar)beforeFirstDayInBar.clone();
        firstDateInLastBar.add(Calendar.DATE, 1);
        //последний столбец
        BarEntry entry = new BarEntry(counterOfBars, (float)value);
        entries.add(entry);
        //если заполнились не все столбцы
        int numberOfEmptyColumns = 0; //число незаполненных столбцов
        switch(currentPeriodIndex) {
            case 0://неделя
                numberOfEmptyColumns = 7 - entries.size();
                break;
            case 1:
                numberOfEmptyColumns = 31 - entries.size();
                break;
            case 2:
                numberOfEmptyColumns = 12 - entries.size();
            case 3:
            case 4:
                break;
        }

        //заполняем оставшиеся столбцы
        for (int i = 0; i < numberOfEmptyColumns; i++) {
            counterOfBars++;
            entry = new BarEntry(counterOfBars, 0f);
            entries.add(entry);
        }

        Log.d("TAG","\n\n");
        return entries;

    }

    //метод определяет, через сколько дней суммы должны начать записываться в новый entry
    private int generateDaysBeforeNewEntry(int hlpCounter, Calendar date) {
        int daysInBar = 0; //шаг (число дней в столбце на диаграмме)
        switch(currentPeriodIndex) {
            case 0: //неделя
                daysInBar = 1; //для недели в столбце 1 день
                break;
            case 1: //месяц
                daysInBar = 1;
                break;//для месяца в столбце 1 день
            case 2: //12 недель
                    daysInBar = 7;
                    break;//для 12 недель в столбце 7 дней
            case 3: //6 месяцев(182 дня)
                daysInBar = 14; //для 6 месяцев в столбце 14 дней
                break;
            case 4: //год
                daysInBar = date.getActualMaximum(Calendar.DAY_OF_MONTH);
                int a = 0;
                break;
        }
        return daysInBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("TAG","onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_expenses_bar_chart, container, false);
        mBarChart = (BarChart)view.findViewById(R.id.bar_chart);
        mTextViewFirstDate = (TextView)view.findViewById(R.id.text_first_date);
        mTextViewLastDate = (TextView)view.findViewById(R.id.text_last_date);
        if (listOfPays.size() == 0) return view;
        mBarData.setDrawValues(false);
        mBarChart.setData(mBarData);
        mBarChart.invalidate();
        formatChart();
        return view;
    }

    public void updateUI(Calendar firstDate) {
        int a = 0;

        //если дата не изменилась
        if (this.firstDate != null
               && this.firstDate.get(Calendar.YEAR) == firstDate.get(Calendar.YEAR)
                && this.firstDate.get(Calendar.MONTH) == firstDate.get(Calendar.MONTH)
                && this.firstDate.get(Calendar.DATE) == firstDate.get(Calendar.DATE)) return;

        StatisticsActivity activity = (StatisticsActivity)getActivity();
        //получаем новый список платежей
        listOfPays = new ArrayList<>(activity.getListOfPaysByDate(firstDate));

        //удаляем старый сет, если он есть
        if (mBarData != null) {
            mBarData.removeDataSet(0);
            //mBarChart.notifyDataSetChanged();
            mBarChart.clear();
        }
        //если список платежей пуст
        if(listOfPays.size() == 0) return;

        //преобразуем список платежей в список entries
        //entries = convertPaysToEntries(firstDate);
        convertPaysToEntries(firstDate);
        BarDataSet expensesSet = new BarDataSet(entries, "");
        expensesSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorExpensesBar));
        if (mBarData == null) {
            mBarData = new BarData(expensesSet);
        } else {
            mBarData.addDataSet(expensesSet);
        }
        mBarData.setDrawValues(false);

        if (mBarChart.getData() == null) {
            mBarChart.setData(mBarData);
        } else {
            //отмечаем, что вид диаграммы должен измениться
            mBarChart.notifyDataSetChanged();
        }
        mBarChart.invalidate();

        formatChart();

        /*
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Toast toast = Toast.makeText(getActivity(), sdf.format(firstDate.getTime()), Toast.LENGTH_LONG);
        toast.show();
        */


    }

    private void formatChart() {

        float xRangeMax = 0f;
        switch (currentPeriodIndex) {
            case 0:
                xRangeMax = 7f;
                break;
            case 1:
                xRangeMax = 10f;
                break;
            case 3:
                xRangeMax = 13f;
                break;
            default:
                xRangeMax = 12f;
        }

        //mBarChart.setVisibleXRangeMinimum(xRangeMax);
        //mBarChart.setFitBars(true);
        mBarChart.setVisibleXRangeMaximum(xRangeMax);
        mBarChart.setVisibleXRangeMinimum(xRangeMax);
        mBarChart.setDrawGridBackground(false);
        //mBarChart.setBackgroundColor(Color.WHITE);
        mBarChart.getAxisRight().setDrawGridLines(false);
        //mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getAxisLeft().setDrawGridLines(false);
        mBarChart.getXAxis().setDrawGridLines(false);
        XAxis xAxis = mBarChart.getXAxis();
        int labelCount = 0;
        switch (currentPeriodIndex) {
            case 0:
               labelCount = 7;
               break;
            case 1:
                labelCount = 7;
                break;
            case 2:
                labelCount = 12;
                break;
            case 3:
                labelCount = 13;
                break;
            case 4:
                labelCount = 12;
                break;
        }
        //xAxis.setLabelCount(labelCount, true);

        /*
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getAxisLabel((int)value);
            }
        });
        */
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getDescription().setEnabled(false);
        mTextViewFirstDate.setText(getAxisLabel(1));
        mTextViewLastDate.setText(getAxisLabel(entries.size()));

    }

    private String getAxisLabel(int value) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        if (currentPeriodIndex == 0 || currentPeriodIndex == 1) {
            int a = 0;
            if (value == 1) {
                return sdf.format(firstDateInFirstBar.getTime());
            } else if (value == entries.size()) {
                return sdf.format(Calendar.getInstance().getTime());
            } else {
                return "";
            }
        } else if (currentPeriodIndex == 2 || currentPeriodIndex == 3) {
            if (value == 1) {
                return sdf.format(firstDateInFirstBar.getTime()) + "\r\n" + " - "
                        + "\r\n" + sdf.format(lastDateInFirstBar.getTime());
            } else if (value == entries.size()) {
                return sdf.format(firstDateInLastBar.getTime()) + "\r\n" + " - "
                        + "\r\n" + sdf.format(Calendar.getInstance().getTime());
            } else {
                return "";
            }
        } else if (currentPeriodIndex == 4) {
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM yyyy");
            if (value == 1) {
                return sdf2.format(firstDateInFirstBar.getTime());
            } else if (value == entries.size()) {
                return sdf2.format(Calendar.getInstance().getTime());
            } else {
                return "";
            }
        }

        return "";

    }


}

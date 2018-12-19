package me.mskh.finances;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class ExpensesPieChartFragment extends Fragment implements OnChartValueSelectedListener,
StatisticsActivity.UpdatingUI {

    private static final String ARG_PAYS_LIST = "pays_list";

    private PieData mPieData;
    private List<PieEntry> entries;
    private List<Integer> colorsOfPiePieces;
    private List<Pay> listOfPays;
    private PieChart mPieChart;
    private Calendar firstDate;
    private int currentPeriodIndex; //индекс текущего периода

    public ExpensesPieChartFragment() {
        // Required empty public constructor
    }

    //Пока нет никаких фрагментов
    public static ExpensesPieChartFragment newInstance() {
        ExpensesPieChartFragment fragment = new ExpensesPieChartFragment();
        Bundle args = new Bundle();
        //args.putSerializable(ARG_PAYS_LIST, listOfPays);
        fragment.setArguments(args);
        return fragment;
    }

    //Возвращает заголовок для названий вкладок
    public static String getTitle() {
        return "Категории расходов"; //засунуть в strings.xml!!
    }

    public void setFirstDate(Calendar period) {
        firstDate = period;
    }

    @Override
    public void setCurrentPeriodIndex(int currentPeriodIndex) {
        this.currentPeriodIndex = currentPeriodIndex;
    }

    @Override
    public Calendar getFirstDate() {
        return firstDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Определяем текущую дату
        Calendar dateBeforeWeek = Calendar.getInstance();
        //Отсчитываем дату неделю назад
        dateBeforeWeek.add(Calendar.DATE, -6);
        //получаем список платежей
        listOfPays = new ArrayList<Pay>(((StatisticsActivity)getActivity()).getListOfPaysByDate(dateBeforeWeek));
        if (listOfPays.size() != 0) { //если список платежей не пуст
            entries = convertPaysToEntries();
        }

    }

    private List<PieEntry> convertPaysToEntries() {

        colorsOfPiePieces = new ArrayList<>();
        entries = new ArrayList<>();

        //Сортируем платежи по категориям
        Collections.sort(listOfPays, new Comparator<Pay>(){
            public int compare(Pay o1, Pay o2){
                if(o1.getCategory() == o2.getCategory())
                    return 0;
                return (int)o1.getCategory() < o2.getCategory() ? -1 : 1;
            }
        });

        //получаем первую категорию в списке
        @AnnotationCategory.Category int category = listOfPays.get(0).getCategory();
        //здесь будет итоговая сумма для определенной категории
        double value = 0;
        double totalSum = 0; // общая сумма платежей
        Pay p;
        for (int i = 0; i < listOfPays.size(); i++) {
            p = listOfPays.get(i);
            totalSum += p.getSum();
        }
        Pay h = new Pay(1,Calendar.getInstance().getTime(),-1);
        listOfPays.add(h);
        for (int i = 0; i < listOfPays.size(); i++) {

            p = listOfPays.get(i);
            if(category == p.getCategory()) {
                value += p.getSum();
            } else {
                double tmp = value/totalSum*100; //рассчитваем процент от общей суммы
                Drawable d = getImageOfPiece(category);
                PieEntry entry;
                if (d != null && tmp > 5f) {
                    entry = new PieEntry((float)tmp, d);
                } else {
                    entry = new PieEntry((float) tmp, Integer.toString(category)); //пока без картинок
                }
                entries.add(entry);
                //определяем цвет для entry
                colorsOfPiePieces.add(getColorOfPiece(category));
                value = p.getSum();
                category = p.getCategory();
            }

        }

        int a = 0;

        return entries;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expenses_pie_chart, container, false);
        mPieChart = (PieChart)view.findViewById(R.id.pie_chart);
        mPieChart.setRotationEnabled(false);
        Legend legend = mPieChart.getLegend();
        legend.setEnabled(false);
        mPieChart.setOnChartValueSelectedListener(this);
        mPieChart.setDrawEntryLabels(false);
        mPieChart.setExtraOffsets(5, 0, 8, 0);
        mPieChart.invalidate();
        mPieChart.getDescription().setEnabled(false);
        if (listOfPays.size() == 0) return view;
        PieDataSet set = new PieDataSet(entries, "Расходы по категориям");
        //устанавливаем внешний вид сета
        set = setFormattingToSet(set);
        mPieData = new PieData(set);
        mPieData.setValueFormatter(new PercentFormatter()); //отображение процентов для значений
        mPieData.setValueTextColors(colorsOfPiePieces);
        mPieData.setValueTextSize(14f); //размер текста процентов
        //int black = Color.BLACK;
        //mPieData.setValueTextColor(black);
        mPieChart.setData(mPieData);
        int aa = 0;
        return view;
    }

    private PieDataSet setFormattingToSet (PieDataSet set) {

        set.setDrawIcons(true);
        set.setColors(colorsOfPiePieces);
        set.setSelectionShift(30f);
        //Линии, ведущие к процентам
        //set.setValueLinePart1OffsetPercentage(80.f);
        //set.setValueLinePart1Length(0.4f);
        //set.setValueLinePart2Length(0.3f);
        set.setValueLineWidth(1f); //толщина линии процентов
        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        return set;

    }

    private int getColorOfPiece(int category) {

        int colorOfPiece = 0;

        switch(category) {
            case 0:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorCafePie);
                break;
            case 1:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorPetsPie);
                break;
            case 2:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorHealthAndBeautyPie);
                break;
            case 3:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorShoppingPie);
                break;
            case 4:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorTransportPie);
                break;
            case 5:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorTravellingPie);
                break;
            case 6:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorHousePie);
                break;
            case 7:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorChildrenPie);
                break;
            case 8:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorClothesPie);
                break;
            case 9:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorEducationPie);
                break;
            case 10:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorEntertainmentPie);
                break;
            default:
                colorOfPiece = ContextCompat.getColor(getActivity(), R.color.colorOtherPie);

        }

        return colorOfPiece;

    }

    //Метод для нахождения иконки категории
    private Drawable getImageOfPiece(int category) {
        Drawable d;
        switch (category) {
            case 0:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_coctail_bar);
                break;
            case 1:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_pets);
                break;
            case 2:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_health_and_beauty);
                break;
            case 3:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_shopping);
                break;
            case 4:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_transport);
                break;
            case 5:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_travelling);
                break;
            case 6:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_house);
                break;
            case 7:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_children);
                break;
            case 8:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_clothes);
                break;
            case 9:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_education);
                break;
            case 10:
                d = getActivity().getResources().getDrawable(R.drawable.ic_wb_entertainment);
                break;
            default:
                return null;

        }
        return  d;
    }

    //Если выбран определенный slice
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast toast = Toast.makeText(getActivity(), ((PieEntry)e).getLabel(), Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onNothingSelected() {

    }

    //Обновление диаграммы при выборе другого периода
    public void updateUI(Calendar firstDate) {

        //если дата не изменилась
        if (this.firstDate != null
                && this.firstDate.get(Calendar.YEAR) == firstDate.get(Calendar.YEAR)
                && this.firstDate.get(Calendar.MONTH) == firstDate.get(Calendar.MONTH)
                && this.firstDate.get(Calendar.DATE) == firstDate.get(Calendar.DATE)) return;

        StatisticsActivity activity = (StatisticsActivity)getActivity();
        //если активность только запустилась
        if (activity == null) return;
        listOfPays = new ArrayList<>((activity).getListOfPaysByDate(firstDate));

        //необхлдимо удалить из диаграммы старый сет и установить новый

        //удаляем старый сет, если он есть
        if (mPieData != null) {
            mPieData.removeDataSet(0);
            //mPieChart.notifyDataSetChanged();
            //mPieChart.invalidate();
            mPieChart.clear();
        }

        //если список платежей пуст
        if (listOfPays.size() == 0 ) return;

        //преобразуем список платежей в список entries
        //entries = convertPaysToEntries();
        convertPaysToEntries();
        //создаем новый сет
        PieDataSet set = new PieDataSet(entries, "Расходы по категориям");
        //устанавливаем внешний вид сета
        set = setFormattingToSet(set);
        if (mPieData == null) {
            mPieData = new PieData(set);
        } else {
            mPieData.setDataSet(set);
        }
        mPieData.setValueFormatter(new PercentFormatter()); //отображение процентов для значений
        mPieData.setValueTextColors(colorsOfPiePieces);
        mPieData.setValueTextSize(14f); //размер текста процентов
        //отмечаем, что вид диаграммы должен измениться
        if (mPieChart.getData() == null) {
            mPieChart.setData(mPieData);
        }
        else {
            mPieChart.notifyDataSetChanged();
        }
        mPieChart.invalidate();
        mPieChart.getDescription().setEnabled(false);

        /*
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Toast toast = Toast.makeText(getActivity(), sdf.format(firstDate.getTime()), Toast.LENGTH_LONG);
        toast.show();
        */

    }
}

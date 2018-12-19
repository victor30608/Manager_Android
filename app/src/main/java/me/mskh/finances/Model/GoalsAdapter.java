package me.mskh.finances.Model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.mskh.finances.R;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder> {

    private List<Goal> mGoalList = new ArrayList<>();
    private Context context;

    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_item, parent, false);

        context = parent.getContext();
        return new GoalsViewHolder(view);

    }

    @Override
    public int getItemCount() {
        return mGoalList.size();
    }

    @Override
    public void onBindViewHolder(GoalsViewHolder holder, int position) {
        holder.bind(mGoalList.get(position));
    }

    class GoalsViewHolder extends RecyclerView.ViewHolder {

        //все view компоненты и компоненты для piechart
        private PieChart progressPieChart;
        private List<PieEntry> entries;
        private PieDataSet pieDataSet;
        private PieData pieData;
        private List<Integer> colors;
        private TextView nameTextView;
        private TextView totalSumTextView;
        private TextView remainingSumTextView;
        private int percent;

        public GoalsViewHolder(View itemView) {

            super(itemView);
            progressPieChart = itemView.findViewById(R.id.pie_chart);
            nameTextView = itemView.findViewById(R.id.goal_name);
            totalSumTextView = itemView.findViewById(R.id.goal_total_sum);
            remainingSumTextView = itemView.findViewById(R.id.goal_remaining_sum);

        }

        public void bind(Goal goal) {

            entries = new ArrayList<>();
            //процент внесенной суммы
            percent = (int)Math.round(goal.getContributedSum()/goal.getTotalSum() * 100);
            entries.add(new PieEntry(percent, ""));
            entries.add(new PieEntry(100 - percent, ""));
            pieDataSet = new PieDataSet(entries, "");
            colors = new ArrayList<>();
            int colorOfProgress = 0;
            if (0 <= percent && percent <= 19) {
                colorOfProgress = ContextCompat.getColor(context, R.color.expense);
            } else if (20 <= percent && percent <= 40) {
                colorOfProgress = ContextCompat.getColor(context, R.color.color20to40);
            } else if (41 <= percent && percent <= 60) {
                colorOfProgress = ContextCompat.getColor(context, R.color.colorShoppingPie);
            } else if (61 <= percent && percent <= 80) {
                colorOfProgress = ContextCompat.getColor(context, R.color.colorPaleGreen);
            } else if (81 <= percent && percent <= 100) {
                colorOfProgress = ContextCompat.getColor(context, R.color.colorPrimary);
            }
            colors.add(colorOfProgress);
            //colors.add(ContextCompat.getColor(context, R.color.colorPrimary));
            colors.add(ContextCompat.getColor(context, R.color.colorPaleGray));
            pieDataSet.setColors(colors);
            pieDataSet.setDrawValues(false);
            pieData = new PieData(pieDataSet);
            progressPieChart.setData(pieData);
            Legend legend = progressPieChart.getLegend();
            legend.setEnabled(false);
            progressPieChart.setHoleRadius(80f);
            progressPieChart.setCenterText(Integer.toString(percent) + "%");
            progressPieChart.setCenterTextColor(ContextCompat.getColor(context, R.color.primaryTextColor));
            progressPieChart.notifyDataSetChanged();
            progressPieChart.invalidate();
            progressPieChart.getDescription().setEnabled(false);

            nameTextView.setText(goal.getName());
            totalSumTextView.setText("Общая сумма: " + Integer.toString((int)goal.getTotalSum()));
            double remainingSum = goal.getTotalSum() - goal.getContributedSum();
            remainingSumTextView.setText("Осталось внести: " + Integer.toString((int)remainingSum));

        }

    }

    public void setItems(List<Goal> goals) {

        mGoalList.addAll(goals);
        notifyDataSetChanged();

    }

    public void clearItems() {

        mGoalList.clear();
        notifyDataSetChanged();

    }

}

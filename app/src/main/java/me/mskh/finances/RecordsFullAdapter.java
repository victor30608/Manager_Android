package me.mskh.finances;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TreeSet;

import me.mskh.finances.Model.Category;
import me.mskh.finances.Model.Costs;

import static android.view.View.GONE;

public class RecordsFullAdapter extends ArrayAdapter<Costs>{
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Costs> costs;
    SimpleDateFormat formatter =  new SimpleDateFormat("dd.MM.yyyy");
    Calendar calendar = Calendar.getInstance();

    public RecordsFullAdapter(Context context, int resource, ArrayList<Costs> costs) {
        super(context, resource, costs);
        this.costs = costs;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.categoryImage);
        TextView category = (TextView) view.findViewById(R.id.recordCategory);
        TextView amount = (TextView) view.findViewById(R.id.recordAmount);
        TextView time = (TextView) view.findViewById(R.id.recordTime);
        TextView description = (TextView) view.findViewById(R.id.recordDescription);


        Costs cost = costs.get(position);

        icon.setImageResource(this.getCategoryIcon(cost.getCat()));
        category.setText(cost.getCat());
        String sum = String.format(Locale.getDefault(),"%1$,.2f",cost.getSum()) + " руб.";
        amount.setText(sum);
        time.setText(formatter.format(cost.getDate().getTime()));
        if(formatter.format(cost.getDate().getTime()).equals(formatter.format(calendar.getTime()))){
            time.setText("Сегодня");
        }else{
            time.setText(formatter.format(cost.getDate().getTime()));
        }
        if(!cost.getDescription().isEmpty()) {
            String descr = cost.getDescription().substring(0,1).toUpperCase()+cost.getDescription().substring(1);
            description.setText("\""+descr+"\"");
        }else{
            description.setVisibility(GONE);
        }
        if(cost.isIsprofit()){
            amount.setTextColor(ContextCompat.getColor(getContext(), R.color.income));
        }
        else{
            amount.setTextColor(ContextCompat.getColor(getContext(), R.color.expense));
        }

        return view;
    }

    public int getCategoryIcon(String name){
        TreeSet<Category> CategoriesArr = HelloActivity.getCategoriesList();
        for (Category cat:CategoriesArr) {
            if(name.equals("Доход")){
                return R.mipmap.ic_cat_income;
            }
            if(cat.getCategoryName().equals(name)){
                return cat.getCategoryIcon();
            }
        }
        return -1;
    }
}






package me.mskh.finances;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import me.mskh.finances.Model.Category;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private LayoutInflater inflater;
    private int layout;
    private Category[] categories;
    private int selectedItem = -1;

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public CategoryAdapter(Context context, int resource, Category[] categories) {
        super(context, resource, categories);
        this.categories = categories;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.categoryIcon);
        TextView name = (TextView) view.findViewById(R.id.categoryName);

        Category cat = categories[position];

        icon.setImageResource(cat.getCategoryIcon());
        name.setText(cat.getCategoryName());

        if (position == selectedItem) {
            view.findViewById(R.id.category_row).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.chosenCat));
        }

        return view;
    }
}

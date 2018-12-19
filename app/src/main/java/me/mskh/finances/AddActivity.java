package me.mskh.finances;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.json.JSONException;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

import me.mskh.finances.Model.*;
public class AddActivity extends AppCompatActivity {
    public final static String Configuration = "config.json";
    public final static String Categories = "categories.json";
    public final static String Costs = "costs.json";
    private ArrayList<Costs> HistoryArr = new ArrayList<Costs>(); //все доходы и расходы
    TreeSet<Category> CategoriesArr;
    EditText SumItem;
    EditText DescriptionItem;
    EditText DateItem;
    CheckBox IncomeItem;
    Category selectedCat = new Category("",0,-1,-1);
    double sum;
    String description;
    SimpleDateFormat formatter =  new SimpleDateFormat("dd.MM.yyyy");
    Calendar calendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);
        //Добавляем toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Добавляем на него кнопку назад
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Привязываем к объектам переменные
        SumItem = (EditText) findViewById(R.id.addSum);
        DescriptionItem = (EditText) findViewById(R.id.addDescription);
        DateItem = (EditText) findViewById(R.id.addDate);
        IncomeItem = (CheckBox) findViewById(R.id.income);

        //Выводим текущую дату
        DateItem.setHint(formatter.format(calendar.getTime()));

        DateItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(AddActivity.this, d, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    DateItem.setText(DateUtils.formatDateTime(AddActivity.this, calendar.getTimeInMillis(), DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_NUMERIC_DATE| DateUtils.FORMAT_SHOW_YEAR));
                } else {
                    DateItem.setText(DateUtils.formatDateTime(AddActivity.this, calendar.getTimeInMillis(), DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_NUMERIC_DATE| DateUtils.FORMAT_SHOW_YEAR));
                }
            }
        });

        CategoriesArr = HelloActivity.getCategoriesList();
        HistoryArr = HelloActivity.getCostsList();

        //Заполняем список категорий
        ListView listView = (ListView) findViewById(R.id.categoriesList); //находим ListView на странице
        Category[] catArray = {};
        catArray = CategoriesArr.toArray(new Category[CategoriesArr.size()]);
        final CategoryAdapter mArrayAdapter = new CategoryAdapter(this, R.layout.category_item, catArray);
        listView.setAdapter(mArrayAdapter);
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                selectedCat = (Category)parent.getItemAtPosition(position);
                mArrayAdapter.setSelectedItem(position);
                mArrayAdapter.notifyDataSetChanged();
            }
        };
        listView.setOnItemClickListener(itemListener);

    }
    private File getExternalPath(String FILE_NAME) {
        return(new File(Environment.getExternalStorageDirectory(), FILE_NAME));
    }
    public void save() {
        try {
            FileOutputStream outcat = openFileOutput(Categories,  MODE_PRIVATE);
//            FileOutputStream outcat =new FileOutputStream(getExternalPath(Categories));
            JSONArray categories = new JSONArray();
            for (Category cat : CategoriesArr) {
                JSONObject tmp = new JSONObject();
                tmp.put("category", cat.getCategoryName());
                tmp.put("icon", cat.getCategoryIcon());
                tmp.put("id", cat.getCategoryId());
                tmp.put("id2", cat.getStatsId());
                categories.put(tmp);
            }
            outcat.write(categories.toString().getBytes());
            outcat.close();
            FileOutputStream outhist = openFileOutput(Costs,  MODE_PRIVATE);
//            FileOutputStream outhist =new FileOutputStream(getExternalPath(Costs));
            JSONArray history = new JSONArray();
            for (Costs cost : HistoryArr) {
                JSONObject tmp = new JSONObject();
                tmp.put("sum", Double.toString(cost.getSum()));
                tmp.put("isprofit", Boolean.toString(cost.isIsprofit()));
                tmp.put("date", formatter.format(cost.getDate().getTime())).toString();
                tmp.put("description", cost.getDescription());
                tmp.put("categories", cost.getCat());
                history.put(tmp);
            }
            outhist.write(history.toString().getBytes());
            outhist.close();
        } catch (IOException e) {
            Log.e("MYAPP", "exception", e);
        } catch (JSONException e) {
            Log.e("MYAPP", "exception", e);
        }
    }


    public void saveItem(View view) {
        boolean f = true;

        boolean income = IncomeItem.isChecked();    //true - если нажат флажок ДОХОД
        //нужно проверить что данные в порядке и вызвать функцию которая добавит их в список операций
        try {
            sum = Double.parseDouble(SumItem.getText().toString());   //получаем сумму
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Введите сумму", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 50);
            toast.show();
            f = false;
        }

        try {
            calendar.setTime(formatter.parse(DateItem.getText().toString()));  //получаем дату
        } catch (ParseException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Введите дату", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 50);
            toast.show();
            f = false;
        }
        if(!income) {
            if (selectedCat.getCategoryName().isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Выберите категорию", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 50);
                toast.show();
                f = false;
            }
        }else{
            selectedCat = new Category("",0,-1,-1);
            selectedCat.setCategoryName("Доход");
            selectedCat.setCategoryIcon(R.mipmap.ic_cat_income);
            selectedCat.setCategoryId(-2);
            selectedCat.setStatsId(-2);
        }
        description = DescriptionItem.getText().toString(); //получаем описание Платежа


        //Добавляем текущую запись в историю и сохраняем все

        if(f) {
            Costs tmp = new Costs();
            System.out.println(DateItem.getText().toString()+"OUT"+calendar.getTime());
            tmp.setDate(calendar);
            tmp.setSum(Double.parseDouble(SumItem.getText().toString()));
            tmp.setDescription(description);
            tmp.setIsprofit(income);
            tmp.setCat(selectedCat.getCategoryName());
            sum=Double.parseDouble(SumItem.getText().toString());
            HistoryArr.add(tmp);
            save();
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            String title="Отмена";
            String message="Вы уверены что хотите выйти? Вы потеряете все введенные данные";
            String button1String="Нет";
            String button2String="Да";

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(
                    AddActivity.this);

            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            if (drawer.isDrawerOpen(GravityCompat.START)) {
                                drawer.closeDrawer(GravityCompat.START);
                            } else {
                                finish();
                            }
                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }


    public void pressedIncome(View view){
        if(IncomeItem.isChecked()){
            findViewById(R.id.CategoryTitle).setVisibility(View.GONE);
            findViewById(R.id.categoriesList).setVisibility(View.GONE);
        }else{
            findViewById(R.id.CategoryTitle).setVisibility(View.VISIBLE);
            findViewById(R.id.categoriesList).setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed()
    {
        String title="Отмена";
        String message="Вы уверены что хотите выйти? Вы потеряете все введенные данные";
        String button1String="Нет";
        String button2String="Да";

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(
                AddActivity.this);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else {
                            finish();
                        }
                    }
                })
                .show();
    }

}

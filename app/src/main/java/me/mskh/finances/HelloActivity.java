package me.mskh.finances;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

import me.mskh.finances.Model.Category;
import me.mskh.finances.Model.Costs;
import me.mskh.finances.Model.Person;

public class HelloActivity extends AppCompatActivity {
    public final static String Configuration="config.json";
    public final static String Categories="categories.json";
    public final static String Costs="costs.json";
    public String Name;
    private static TreeSet<Category> CategoriesArr = new TreeSet<Category>(); // Категории
    private static ArrayList<Costs> CostsArr = new ArrayList<Costs>();
    Button go_button;
    EditText userName;
    final int REQUEST_CODE_ASK_PERMISSIONS  = 123;
    public void load()
    {
        SimpleDateFormat formatter =  new SimpleDateFormat("dd.MM.yyyy");
        FileInputStream costsin = null;
        InputStreamReader inStreamReader =null;
        FileInputStream fin=null;
        InputStreamReader inputStreamReader;
        try {
            fin = openFileInput(Configuration);
            inputStreamReader = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String s = br.readLine();
            JSONObject config = new JSONObject(s);
            Name = config.getString("Name");
            userName.setText(Name);
            fin.close();

            fin = openFileInput(Categories);
//            File file = getExternalPath(Categories);
//            fin=new FileInputStream(file);
//            if(!file.exists()) return;
            inputStreamReader = new InputStreamReader(fin);
            br = new BufferedReader(inputStreamReader);
            s = br.readLine();
            JSONArray categ = new JSONArray(s);
            for (int i = 0; i < categ.length(); i++) {
                Category tmp = new Category("",0,-1,-1);
                tmp.setCategoryName(categ.getJSONObject(i).getString("category"));
                tmp.setCategoryIcon(Integer.parseInt(categ.getJSONObject(i).getString("icon")));
                tmp.setCategoryId(Integer.parseInt(categ.getJSONObject(i).getString("id")));
                tmp.setStatsId(Integer.parseInt(categ.getJSONObject(i).getString("id2")));
                CategoriesArr.add(tmp);
            }
            fin.close();

            costsin = openFileInput(Costs);
            inStreamReader = new InputStreamReader(costsin);
            BufferedReader buff = new BufferedReader(inStreamReader);
            s = buff.readLine();
            System.out.println("ERROR!"+s);
            if(s==null) s="";
            JSONArray hist = new JSONArray(s);
            for (int i = 0; i < hist.length(); i++) {
                Costs tmp = new Costs();
                tmp.setSum(Double.parseDouble(hist.getJSONObject(i).getString("sum")));
                tmp.setIsprofit(Boolean.parseBoolean(hist.getJSONObject(i).getString("isprofit")));
                tmp.setDescription(hist.getJSONObject(i).getString("description"));
                tmp.setCat(hist.getJSONObject(i).getString("categories"));
                Calendar tmpcal = Calendar.getInstance();
                Date tt = formatter.parse(hist.getJSONObject(i).getString("date").toString());
                tmpcal.setTime(tt);
                tmp.setDate(tmpcal);
                CostsArr.add(tmp);
            }
            fin.close();


        } catch (IOException e) {
            Log.e("MYAPP", "exception", e);
        } catch (JSONException e) {
            Log.e("MYAPP", "exception", e);
        } catch (ParseException e) {
            Log.e("MYAPP", "exception", e);
        } finally {

            try {
                if (costsin != null)
                    costsin.close();
                if (fin != null)
                    fin.close();
                if(inStreamReader!=null) inStreamReader.close();
            } catch (IOException e) {
                Log.e("MYAPP", "exception", e);
            }

        }
    }
    public void save()
    {
        try {
            FileOutputStream fos = openFileOutput(Configuration, MODE_PRIVATE);
            JSONObject config = new JSONObject();
            config.put("Name",Name);
            fos.write(config.toString().getBytes());
            fos.close();
        }
        catch (IOException e)
        {
            Log.e("MYAPP", "exception", e);
        }
        catch (JSONException e)
        {
            Log.e("MYAPP", "exception", e);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        userName = (EditText) findViewById(R.id.editText);
        go_button = (Button) findViewById(R.id.go);
        if(!isSmsPermissionGranted()) {
            requestReadAndSendSmsPermission();
        }
        load();
        //Загружаем список категорий по умолчанию
        initList();

    }
    public  boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_CODE_ASK_PERMISSIONS);
    }
       public void sendMessage (View view) {
           Name=userName.getText().toString();
            save();
            Intent intent = new Intent(HelloActivity.this, MainActivity.class);
//                intent.putExtra("username", userName.getText().toString());
            startActivity(intent);
        }
//    public static final int CAFE = 0;
//    public static final int PETS = 1;
//    public static final int HEALTH_AND_BEAUTY = 2;
//    public static final int SHOPPING = 3;
//    public static final int TRANSPORT = 4;
//    public static final int TRAVELLING = 5;
//    public static final int APARTMENT = 6;
//    public static final int CHILDREN = 7;
//    public static final int CLOTHES = 8;
//    public static final int EDUCATION = 9;
//    public static final int ENTERTAINMENT = 10;
    private void initList(){
        CategoriesArr.add(new Category("Жилье",R.mipmap.ic_cat_house,0,6));
        CategoriesArr.add(new Category("Покупки",R.mipmap.ic_cat_shopping2,1,3));
        CategoriesArr.add(new Category("Одежда и обувь",R.mipmap.ic_cat_clothes,2,8));
        CategoriesArr.add(new Category("Дети",R.mipmap.ic_cat_children,3,7));
        CategoriesArr.add(new Category("Питомцы",R.mipmap.ic_cat_pets,4,1));
        CategoriesArr.add(new Category("Здоровье и красота",R.mipmap.ic_cat_health_and_beauty,5,2));
        CategoriesArr.add(new Category("Образование",R.mipmap.ic_cat_education,6,9));
        CategoriesArr.add(new Category("Транспорт",R.mipmap.ic_cat_transport,7,4));
        CategoriesArr.add(new Category("Развлечения",R.mipmap.ic_cat_entertainment,8,10));
        CategoriesArr.add(new Category("Кафе, рестораны",R.mipmap.ic_cat_cafe,9,0));
        CategoriesArr.add(new Category("Путешествия",R.mipmap.ic_cat_travelling,10,5));
        //CategoriesArr.add(new Category("Создать новую",R.mipmap.ic_cat_new,1001, 1001));
        //CategoriesArr.add(new Category("Пользовательская",R.mipmap.ic_cat_custom,11, 11));
        //CategoriesArr.add(new Category("Cупер-пользовательская",R.mipmap.ic_cat_brain,1002, 1002));
    }
    public static TreeSet<Category> getCategoriesList(){
        return CategoriesArr;
    }
    public static ArrayList<Costs> getCostsList(){
        Collections.sort(CostsArr, new Comparator<me.mskh.finances.Model.Costs>() {
            public int compare(Costs o1, Costs o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        return CostsArr;
    }
}


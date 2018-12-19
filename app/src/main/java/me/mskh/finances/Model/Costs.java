package me.mskh.finances.Model;

import android.util.Log;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import 	java.util.Date;
public class Costs implements Serializable  {
    private double sum;//сумма
    private boolean isprofit;//доход или расход
    private Calendar date; //дата
    private String description; //описание
    private String cat;//категория
    public   Costs()
    {
        this.sum=0;
        this.isprofit=false;
        this.date= new GregorianCalendar();
        this.description="blablabla";
        this.cat ="Прочие расходы";
    }
    public   Costs(double _sum,boolean _isprofit,Calendar _date,String _description,String _cat)
    {
    this.sum=_sum;
    this.isprofit=_isprofit;
    this.date=_date;
    this.description=_description;
    this.cat=_cat;
    }
    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public boolean isIsprofit() {
        return isprofit;
    }

    public void setIsprofit(boolean isprofit) {
        this.isprofit = isprofit;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }





}

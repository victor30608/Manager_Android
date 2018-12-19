package me.mskh.finances.Model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SMS {
    private Calendar date;
    private String addres;
    private String body;
    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
   public SMS()
    {
        date=GregorianCalendar.getInstance();
        body="";
        addres="";
    }
    public  SMS(Calendar _date,String _body,String _addres)
    {
        date=_date;
        body=_body;
        addres=_addres;
    }
}

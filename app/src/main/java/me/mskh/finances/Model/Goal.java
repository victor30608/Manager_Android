package me.mskh.finances.Model;

import java.util.Calendar;

public class Goal {

    private double totalSum; //общая сумма
    private double contributedSum; //внесенная сумма
    private Calendar date; //дата
    private String name; //описание

    public Goal(double totalSum, double contributedSum, Calendar date, String name) {
        this.totalSum = totalSum;
        this.contributedSum = contributedSum;
        this.date = date;
        this.name = name;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public double getContributedSum() {
        return contributedSum;
    }

    public Calendar getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

}

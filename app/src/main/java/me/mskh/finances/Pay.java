package me.mskh.finances;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static me.mskh.finances.AnnotationCategory.PETS;

public class Pay implements Parcelable {

    private double sum;
    private Date date;
    @AnnotationCategory.Category  private int category;



    public Pay(double sum, Date date, int category) {
        this.sum = sum;
        this.date = date;
        this.category = category;
    }

    @AnnotationCategory.Category
    public int getCategory() {
        return category;
    }

    public double getSum() {
        return sum;
    }

    public Date getDate() {
        return date;
    }

    public void print() {
        System.out.println("Sum: " + Double.toString(sum) + '\n');
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Date: " + sdf.format(date.getTime()) + '\n');
        System.out.println();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeDouble(sum);

    }

    public static final Creator<Pay> CREATOR = new Creator<Pay>() {
        @Override
        public Pay createFromParcel(Parcel source) {
            double sum = source.readDouble();
            //AnnotationCategory category = new AnnotationCategory(PETS);
            @AnnotationCategory.Category int category = PETS;
            Date date = new Date();
            return new Pay(sum, date, category);
        }

        @Override
        public Pay[] newArray(int size) {
            return new Pay[size];
        }
    };
}

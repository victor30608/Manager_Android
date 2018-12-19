package me.mskh.finances;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AnnotationCategory {

    public static final int CAFE = 0;
    public static final int PETS = 1;
    public static final int HEALTH_AND_BEAUTY = 2;
    public static final int SHOPPING = 3;
    public static final int TRANSPORT = 4;
    public static final int TRAVELLING = 5;
    public static final int APARTMENT = 6;
    public static final int CHILDREN = 7;
    public static final int CLOTHES = 8;
    public static final int EDUCATION = 9;
    public static final int ENTERTAINMENT = 10;

    public AnnotationCategory(@Category int category) {
        System.out.println("Season :" + category);
    }

    @IntDef({CAFE, PETS, HEALTH_AND_BEAUTY, SHOPPING, TRANSPORT, TRAVELLING, APARTMENT, CHILDREN,
            CLOTHES, EDUCATION, ENTERTAINMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Category {
    }



}

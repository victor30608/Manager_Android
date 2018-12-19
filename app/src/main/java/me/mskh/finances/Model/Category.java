package me.mskh.finances.Model;

public class Category implements Comparable<Category>{
    private String categoryName;
    private int categoryIcon;
    private int categoryId;//для (так как я хочу) сортировки
    private int statsId;//Id для статистики


    public Category(String name, int resource, int MyId, int StatsId){
        this.categoryName = name;
        this.categoryIcon = resource;
        this.categoryId = MyId;
        this.statsId = StatsId;
    }

    public void setCategoryName(String name){
        this.categoryName = name;
    }
    public void setCategoryIcon(int resource){
        this.categoryIcon = resource;
    }
    public void setCategoryId(int id){
        this.categoryId = id;
    }
    public void setStatsId(int id){
        this.statsId = id;
    }
    public String getCategoryName(){
        return this.categoryName;
    }
    public int getCategoryIcon(){
        return this.categoryIcon;
    }
    public int getCategoryId(){
        return this.categoryId;
    }
    public int getStatsId(){
        return this.statsId;
    }
    public int compareTo(Category cat){

        return categoryId - cat.getCategoryId();
    }
}

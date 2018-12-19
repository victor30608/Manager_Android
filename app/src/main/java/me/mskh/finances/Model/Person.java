package me.mskh.finances.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;
public class Person implements  Serializable {
    private String name; //Имя
    private  TreeSet<String> categories; // Категории
    private  ArrayList<Costs> history; //все доходы и расходы
  public Person ()
    {
        name="default";
        this.categories= new TreeSet<String>();
        this.history= new ArrayList<Costs>();
        categories.add("Супермаркеты");
        categories.add("Здоровье и красота");
        categories.add("Коммунальные платежи, связь и интернет");
        categories.add("Прочие расходы");
    }
    public void addcat(String cat_name)
    {
        this.categories.add(cat_name);
    }
    public void add_cost(Costs tmp)
    {
        this.history.add(tmp);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Costs> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<Costs> history) {
        this.history = history;
    }

    public TreeSet<String> getCategories() {
        return categories;
    }

    public void setCategories(TreeSet<String> categories) {
        this.categories = categories;
    }
}

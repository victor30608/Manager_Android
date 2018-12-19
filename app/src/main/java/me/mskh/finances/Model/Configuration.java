//package me.mskh.finances.Model;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import java.io.*;
//
//
///**
// * Created by 8160994 on 05.10.2018.
// */
//
//public class Configuration  {
//
//    public static void loadconf(Person user) throws  IOException, JSONException
//    {
//        FileInputStream fin = null;null
//        fin = openFileInput("hh");
//        InputStreamReader inputStreamReader = new InputStreamReader(in);
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//////        mycomp.place.Setlat(lat);
//////        mycomp.place.Setlng(lon);
//////        mycomp.address=add;
////        br.close();
//////        BufferedReader read = new BufferedReader(new FileReader("workers.json"));
////        s=read.readLine();
////        JSONArray work = new JSONArray(s);
////
////        for(int i=0;i<work.length();i++)
////        {
////            Transport tmp = new Transport();
////            tmp.name= TypeOfTransport.Type.valueOf(work.getJSONObject(i).getString("name"));
////            mycomp.worker.add(tmp);
////        }
////        read.close();
//    }
//
//    public static void saveconf(Person user) throws JSONException,IOException
//    {
//        JSONObject config = new JSONObject();
//        config.put("Name",user.getName());
//        FileWriter file = new FileWriter(Configuration);
//        file.write(config.toString());
//        file.close();
//        JSONArray categories = new JSONArray();
//        for(int i=0;i<user.getCategories().size();i++)
//        {
//            JSONObject tmp=new JSONObject();
//            tmp.put("categories",user.getCategories().get(i));
//            categories.put(tmp);
//        }
//        FileWriter file1 = new FileWriter(Categories);
//        file1.write(categories.toString());
//        file1.close();
//        JSONArray costs = new JSONArray();
//        for(int i=0;i<user.getHistory().size();i++)
//        {
//            Costs cost =user.getHistory().get(i);
//            JSONObject tmp=new JSONObject();
//            tmp.put("sum",cost.getSum());
//            tmp.put("isprofit",cost.isIsprofit());
//            tmp.put("date",cost.getDate());
//            tmp.put("description",cost.getName());
//            tmp.put("categories",cost.getCat());
//            costs.put(tmp);
//        }
//        FileWriter file2 = new FileWriter(Costs);
//        file1.write(costs.toString());
//        file2.close();
//    }
//}

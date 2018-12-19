package me.mskh.finances.Model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSParser {
    public static Costs parse(SMS sample)
    {
//        CategoriesArr.add(new Category("Жилье",R.mipmap.ic_cat_house,6));
//        CategoriesArr.add(new Category("Покупки",R.mipmap.ic_cat_shopping2,3));
//        CategoriesArr.add(new Category("Одежда и обувь",R.mipmap.ic_cat_clothes,8));
//        CategoriesArr.add(new Category("Дети",R.mipmap.ic_cat_children,7));
//        CategoriesArr.add(new Category("Питомцы",R.mipmap.ic_cat_pets,1));
//        CategoriesArr.add(new Category("Здоровье и красота",R.mipmap.ic_cat_health_and_beauty,2));
//        CategoriesArr.add(new Category("Образование",R.mipmap.ic_cat_education,9));
//        CategoriesArr.add(new Category("Транспорт",R.mipmap.ic_cat_transport,4));
//        CategoriesArr.add(new Category("Развлечения",R.mipmap.ic_cat_entertainment,8));
//        CategoriesArr.add(new Category("Кафе, рестораны",R.mipmap.ic_cat_cafe,0));
//        CategoriesArr.add(new Category("Путешествия",R.mipmap.ic_cat_travelling,5));
        String[] Cafe={"SUSHI WOK", "BURGERKING", "STARYJ PEKAR"};
        String[] Shopping={"AUCHAN", "ATAK","PEREKRESTOK" ,"PYATEROCHKA", "POS IP KONDRATYUK", "VKUSSVILL"};
        String[] Healthandbeauty={"APTECHNAYA SET OZ","APTEKA"};
        String[] Clothes={"LC WAIKIKI","ZENDEN"};
        Costs value = new Costs();
        value.setDate(sample.getDate());
        String[] subStr;
        subStr=sample.getBody().split(" ");
        Pattern pokupka = Pattern.compile(".*(Покупка)+.*(Баланс:)+.*");
        Pattern perevod= Pattern.compile("(Сбербанк Онлайн)+.*(Вам)+.*(RUB)+");
        Pattern zp=Pattern.compile(".*(зачисление зарплаты)+.*");
        Matcher perevodm=perevod.matcher(sample.getBody());
        Matcher pokupkam=pokupka.matcher(sample.getBody());
        Matcher zpm=zp.matcher(sample.getBody());
        subStr=sample.getBody().split(" ");
        if(zpm.matches())
        {
            String sum = subStr[subStr.length-3];
            String sum1=sum.substring(0,sum.length()-1);
            value.setIsprofit(true);
            value.setCat("Доход");
            value.setSum(Double.parseDouble(sum1));
            value.setDescription("Зачисление зарплаты(SMS)");
        }
        if(perevodm.matches())
        {
            String sum=subStr[subStr.length-2];
            String descr="";
            for(int i=2;i<subStr.length;i++)
            {
                if(!subStr[i].equals("перевел(а)"))
                {
                    descr+=subStr[i]+" ";
                }
                else break;
            }
            descr=descr.trim();
            value.setCat("Доход");
           value.setIsprofit(true);
            value.setSum(Double.parseDouble(sum));
            value.setDescription("Перевод от "+descr+"(SMS)");
        }
        if(pokupkam.matches())
        {
           int p=0;
           for(int i=0;i<subStr.length;i++)
           {
               if(subStr[i].equals("Покупка"))
               {
                   p=i;
               }
           }
           String sum=subStr[p+1];
            String sum1=sum.substring(0,sum.length()-1);
            String descr="";
            for(int i=p+2;i<subStr.length;i++)
            {
                if(!subStr[i].equals("Баланс:"))
                {
                    descr+=subStr[i]+" ";
                }
                else break;
            }
            descr=descr.trim();
            value.setDescription(descr);
            value.setCat("Покупки");
            for(int i=0;i<Cafe.length;i++)
            {
                if(descr.contains(Cafe[i]))
                {
                    value.setCat("Кафе, рестораны");
                }
            }
            for (int i=0;i<Shopping.length;i++)
            {
                if(descr.contains(Shopping[i]))
                {
                    value.setCat("Покупки");
                }
            }
            for(String s:Healthandbeauty)
            {
                if(descr.contains(s))
                {
                    value.setCat("Здоровье и красота");
                }
            }
            for(String s:Clothes)
            {
                if(descr.contains(s))
                {
                    value.setCat("Одежда и обувь");
                }
            }
            value.setSum(Double.parseDouble(sum1));
        }
        return value;
    }
}

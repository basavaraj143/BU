package com.example.appsriv_02.loginwithfacebook;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;

public class Group implements Comparable<Group>,Comparator<Group>
{
    private String Name;
    private String price;
    private String dist;
    private String MinPrice;
    private ArrayList<Child> Items;
    private String isHappyHours;
    private  String happyHourStart;
    private String happyHourEnds;
    private  String rest_offers_happy_hour;
    private boolean checkRestaurants;
    private String placeName;
    private String res_locality;
    private String res_offers;
    private String res_phone1;
    private String res_phone2;
    private String res_lat;
    private String res_lng;


    public String getRes_locality() {
        return res_locality;
    }

    public String getRes_offers() {
        return res_offers;
    }

    public String getRes_phone1() {
        return res_phone1;
    }

    public String getRes_phone2() {
        return res_phone2;
    }

    public void setRes_locality(String res_locality) {
        this.res_locality = res_locality;
    }

    public void setRes_offers(String res_offers) {
        this.res_offers = res_offers;
    }

    public void setRes_phone1(String res_phone1) {
        this.res_phone1 = res_phone1;
    }

    public void setRes_phone2(String res_phone2) {
        this.res_phone2 = res_phone2;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setCheckRestaurants(boolean checkRestaurants) {
        this.checkRestaurants = checkRestaurants;
    }

    public boolean isCheckRestaurants()
    {
        return checkRestaurants;
    }

    public void setRest_offers_happy_hour(String rest_offers_happy_hour) {
        this.rest_offers_happy_hour = rest_offers_happy_hour;
    }


    public String getRest_offers_happy_hour() {
        return rest_offers_happy_hour;
    }

    public void setHappyHourEnds(String happyHourEnds) {
        this.happyHourEnds = happyHourEnds;
    }

    public void setHappyHourStart(String happyHourStart) {
        this.happyHourStart = happyHourStart;
    }

    public void setIsHappyHours(String isHappyHours) {
        this.isHappyHours = isHappyHours;
    }

    public String getHappyHourEnds() {
        return happyHourEnds;
    }

    public String getHappyHourStart() {
        return happyHourStart;
    }

    public String getIsHappyHours() {
        return isHappyHours;
    }

    public void setDist(String dist)
    {
        this.dist = dist;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDist()  {
        return dist;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setMinPrice(String minPrice) {
        MinPrice = minPrice;
    }

    public String getMinPrice() {
        return MinPrice;
    }

    public ArrayList<Child> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Child> Items) {
        this.Items = Items;

    }

    public String getRes_lat() {
        return res_lat;
    }

    public void setRes_lat(String res_lat) {
        this.res_lat = res_lat;
    }

    public String getRes_lng() {
        return res_lng;
    }

    public void setRes_lng(String res_lng) {
        this.res_lng = res_lng;
    }

    public static ArrayList<Group> bubblesrt(ArrayList<Group> list)
    {
        Group temp;
        if (list.size()>1) // check if the number of orders is larger than 1
        {
            for (int x=0; x<list.size()-1; x++) // bubble sort outer loop
            {
               for (int i=x+1; i < list.size(); i++)
                {

                    if (list.get(x).compareTo(list.get(i)) > 0)
                    {
                        temp = list.get(x);
                        list.set(x,list.get(i) );
                        list.set(i , temp);

                    }

                }
            }
            Log.i("bottoms", "newlayout list value " + list);
        }
        return list;

    }

    public static ArrayList<Group> bottleSort(ArrayList<Group> list)
    {

        Group temp;
        if (list.size()>1) // check if the number of orders is larger than 1
        {
            for (int x=0; x<list.size()-1; x++) // bubble sort outer loop
            {
                for (int i=x+1; i < list.size(); i++)
                {
                    if (list.get(x).compareBottle(list.get(i)) > 0)
                    {
                        temp = list.get(x);
                        list.set(x,list.get(i) );
                        list.set(i , temp);

                    }
                }
            }
            Log.i("bottoms", "newlayout list value " + list);
        }
        return list;
    }

    public int compareBottle(Group bottle)
    {
        int res = 0;
        double minprice1=Double.parseDouble(MinPrice);
        int  a=(int)minprice1;
        double minprice2=Double.parseDouble(bottle.getMinPrice());
        int b=(int)minprice2;


        if (b==0)
        {
            res=-1;
        }
        else
        {
            if(a-b>0)
            {
                res=1;
            }

            if(a-b<0&&a==0)
            {
                res =1;
            }




        }
        return res;

    }

    @Override
    public int compareTo(Group another) {

        int res=0;
        double price1=Double.parseDouble(price);
        int x=(int)price1;
        double price2=Double.parseDouble(another.getPrice());
        int y=(int)price2;

        if (y==0)
        {
            res=-1;
        }
        else
        {
            if(x-y>0)
            {
                res=1;
            }

            if(x-y<0&&x==0)
            {
                res=1;
            }

        }

        return res;
    }





    public double compare(Group group)
    {
        double res =0.0;

        double dist1=Double.parseDouble(dist);
        int dis=(int)dist1;
        double dist2=Double.parseDouble(group.getDist());
        int dis2=(int)dist2;

        if(dis>dis2)
    {
        res=1;
    }
    else if(dis<dis2)
    {
        res=1;
    }
        else if (dis==dis2)
        {

        }

        return res;
    }


    @Override
    public int compare(Group lhs, Group rhs)
    {
        return 0;
    }


}



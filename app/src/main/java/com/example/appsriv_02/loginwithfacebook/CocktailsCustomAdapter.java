package com.example.appsriv_02.loginwithfacebook;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CocktailsCustomAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<Child> data;
    private ArrayList<Group> groups;
    private static LayoutInflater inflater=null;
    public Resources res;
    int i=0;
    Context context;
    Typeface tf;

    /*************  BeerCustomAdapter Constructor *****************/
    public CocktailsCustomAdapter(Activity a, ArrayList<Child> d, ArrayList<Group> groups)
    {
        activity = a;
        data=d;
        this.groups=groups;


        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/


    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        tf = Typeface.createFromAsset(activity.getAssets(), "fonts/MYRIADPRO_REGULAR.OTF");

        if(convertView==null){

            /****** Inflate beer_list_child_item.xmlhild_item.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.beer_list_child_item, null);

            /****** View Holder Object to contain beer_list_child_itemst_child_item.xml file elements ******/
            TextView brand_name = (TextView) vi.findViewById(R.id.brand_name);
            brand_name.setTypeface(tf,Typeface.NORMAL);
        TextView price=(TextView)vi.findViewById(R.id.name);
            price.setTypeface(tf,Typeface.NORMAL);
        TextView maxprice=(TextView)vi.findViewById(R.id.maxPrice);
            maxprice.setTypeface(tf,Typeface.NORMAL);
        TextView time = (TextView)vi.findViewById(R.id.time);
            if (position==0) {
                TextView distance = (TextView) vi.findViewById(R.id.dist);
                distance.setTypeface(tf);
                distance.setText(groups.get(position).getDist()+"km");
            }


            String MIN_PRICE =checkZeroValue(data.get(position).getPrice());
            String MAX_PRICE=checkZeroValue(data.get(position).getMinPrice());
            brand_name.setText(data.get(position).getBrand());
        price.setText(MIN_PRICE);
        maxprice.setText(MAX_PRICE);
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("BeerCustomAdapter", "=====Row button clicked=====");

    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {



        }
    }

        public String checkZeroValue(String value) {
        String s;
        if (value.equalsIgnoreCase("0"))
        {
           // s= value.replace('0', '-');
            s= value.replaceAll("0","--");
        }
        else if (value.equalsIgnoreCase("0.00"))
        {
            s= value.replaceAll("0.00","--");
        }
        else
        {
            s = /*"₹" +*/value;
        }
        return s;
    }

}
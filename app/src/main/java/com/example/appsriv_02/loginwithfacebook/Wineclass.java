package com.example.appsriv_02.loginwithfacebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Wineclass extends Activity implements AdapterView.OnItemClickListener
{
    ArrayList<String> resname = new ArrayList<String>();

   static ArrayList<String> resnameInnerList = new ArrayList<String>();


    ArrayList<String> resdist = new ArrayList<String>();
   static ArrayList<Double> reslat= new ArrayList<>();
   static ArrayList<Double> reslong= new ArrayList<>();
    Context context;

   static ArrayList<Float> dist= new ArrayList<>();

    double latitude = 0;
    double longitude = 0;
    Double latitude1;
    Double longitude1;

    public static String radius="two";

    String city;
    ArrayList<RowItem>rowItems;
    ListView list;
    GPSTracker gps;
    ImageButton lookfurther;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_expandablelistwine);
       // Context mContext=null;
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build());
        //lookfurther = (ImageButton) findViewById(R.id.lookfurther);




       /* ImageButton delas_near_you = (ImageButton) findViewById(R.id.deals);
        delas_near_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //delas_near_you.setBackgroundResource(R.drawable.open);
                Intent dealsIntent = new Intent(Wineclass.this, DealsNearYou.class);
                dealsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dealsIntent);

            }
        });
*/

/*
        lookfurther.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                look_further_popup(Wineclass.this);
            }
        });

*/

        gps = new GPSTracker(Wineclass.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();


        } else
        {

            gps.showSettingsAlert();
        }
         city=getIntent().getStringExtra("city");

        if (city!=null)
        {
            InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
            GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
            ArrayList latlng = new ArrayList();
            latlng = gpsTracker.getLatLOng(city);

//            latitude1 = (Double) latlng.get(0);
          //  longitude1 = (Double) latlng.get(1);

            JSONObject jsonObject= GPSTracker.getLocationInfo(city);
            if (jsonObject!=null)
            {

                Double latitude1 = null;// = (Double) latlng.get(0);
                Double longitude1 = null;// (Double) latlng.get(1);
                for (int m = 0; m < jsonObject.length(); m++)
                {
                    try {
                        latitude1 = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        longitude1 = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    } catch (JSONException ex) {

                    }

                }
             JSONArray jarr = NearByBar.BeerAndWineShop(getApplicationContext(), latitude1.doubleValue(), longitude1.doubleValue(), 2);

             if (jarr!=null)
             {
                for (int i = 0; i < jarr.length(); i++) {
                    try {
                        JSONObject obj = jarr.getJSONObject(i);
                        JSONObject shopinfo = obj.getJSONObject("shopInfo");
                        resname.add(shopinfo.getString("res_name"));
                        resdist.add(shopinfo.getString("distance").substring(0, 5));
                        reslat.add(shopinfo.getDouble("res_lat"));
                        reslong.add(shopinfo.getDouble("res_long"));
                        resnameInnerList = resname;

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                for (int j = 0; j < jarr.length(); j++) {
                    dist.add(distFrom(latitude, longitude, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                }
            //  Log.i("bottoms", "distance from loc " + dist);
            }

            else
            {
                Toast.makeText(Wineclass.this,"Please Try different Place",Toast.LENGTH_SHORT).show();
            }
            }
            else
            {
                Toast.makeText(Wineclass.this,"Please Try different Place",Toast.LENGTH_LONG).show();

            }
        }
        else
        {

            JSONArray jarr = NearByBar.findLiquor(getApplicationContext(), latitude, longitude);
            for (int i = 0; i < jarr.length(); i++) {
                try {
                    JSONObject obj = jarr.getJSONObject(i);
                    JSONObject shopinfo = obj.getJSONObject("shopInfo");
                    resname.add(shopinfo.getString("res_name"));
                    resdist.add(shopinfo.getString("distance").substring(0, 5));
                    reslat.add(shopinfo.getDouble("res_lat"));
                    reslong.add(shopinfo.getDouble("res_long"));

                }

                        catch (JSONException e)
                        {
                            e.printStackTrace();

                        }
                    }
            for (int j = 0;j<jarr.length();j++)
            {
                dist.add(distFrom(latitude,longitude, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
            }
           // Log.i("bottoms","distance from loc " +dist);







        }
        String titles[] = new String[resdist.size()];
        String descriptions[] = new String[resdist.size()];

        for(int i=0;i<resdist.size();i++)
        {
            titles[i] = (String)resname.get(i);
            descriptions[i] = (String)resdist.get(i)+"km";
        }

        rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < titles.length; i++) {
            RowItem item = new RowItem(titles[i], descriptions[i]);
            rowItems.add(item);
        }

        list = (ListView) findViewById(R.id.listViewwine);
        CustomAdapterWine adapter = new CustomAdapterWine(this, rowItems);
        list.setAdapter(adapter);
       list.setOnItemClickListener(this);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void look_further_popup(final Context context)
    {

        LayoutInflater layoutInflater = (LayoutInflater)Wineclass.this.getSystemService(context.LAYOUT_INFLATER_SERVICE);


        final View popupView = layoutInflater.inflate(R.layout.look_further_popup, null);

        final PopupWindow popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);


        popupWindow .setTouchable(true);
        popupWindow .setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.isOutsideTouchable();


        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        ArrayList latlng = new ArrayList();
       // latlng = gpsTracker.getLatLOng(place);
        JSONObject jsonObject= GPSTracker.getLocationInfo(city);
        // (Double) latlng.get(1);
        for (int m =0; m<jsonObject.length();m++)
        {
            try {
                latitude= jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                longitude=jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            }
            catch (JSONException ex)
            {

            }

        }


     /*   if (latlng.size()>0) {*/
        //   final Double latitude = (Double) latlng.get(0);
        //final Double longitude = (Double) latlng.get(1)
        ImageView sevenkm = (ImageView) popupView.findViewById(R.id.sevenkm);
        ImageView fivekm = (ImageView) popupView.findViewById(R.id.fivekm);
        ImageView twokm = (ImageView) popupView.findViewById(R.id.twokm);
        if (radius.equalsIgnoreCase("two")) {
            twokm.setVisibility(View.GONE);
        } else if (radius.equalsIgnoreCase("five")) {
            fivekm.setVisibility(View.GONE);
        } else if (radius.equalsIgnoreCase("seven")) {
            sevenkm.setVisibility(View.GONE);
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //popupWindow.dismiss();
                return false;
            }
        });


        sevenkm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(Wineclass.this, "7 km restaurants are showing", Toast.LENGTH_SHORT).show();
                lookfurther.setBackgroundResource(R.drawable.radius_sevenkm_press);
                radius = "seven";

                runOnUiThread(new Runnable() {
                    public void run()
                    {
                        JSONArray jarr = NearByBar.BeerAndWineShop(getApplicationContext(), latitude, longitude,7);

                        for (int i = 0; i < jarr.length(); i++) {
                            try {
                                JSONObject obj = jarr.getJSONObject(i);
                                JSONObject shopinfo = obj.getJSONObject("shopInfo");
                                resname.add(shopinfo.getString("res_name"));
                                resdist.add(shopinfo.getString("distance").substring(0, 5));
                                reslat.add(shopinfo.getDouble("res_lat"));
                                reslong.add(shopinfo.getDouble("res_long"));
                                resnameInnerList=resname;

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                        for (int j = 0; j < jarr.length(); j++)
                        {
                            dist.add(distFrom(latitude,longitude, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                        }
                       // Log.i("bottoms", "distance from loc " + dist);

                        String titles[] = new String[resdist.size()];
                        String descriptions[] = new String[resdist.size()];

                        for(int i=0;i<resdist.size();i++)
                        {
                            titles[i] = (String)resname.get(i);
                            descriptions[i] = (String)resdist.get(i)+"km";
                        }

                        rowItems = new ArrayList<RowItem>();
                        for (int i = 0; i < titles.length; i++) {
                            RowItem item = new RowItem(titles[i], descriptions[i]);
                            rowItems.add(item);
                        }

                        list = (ListView) findViewById(R.id.listViewwine);
                        CustomAdapterWine adapter = new CustomAdapterWine(Wineclass.this, rowItems);
                        list.setAdapter(adapter);


                    }
                });

                popupWindow.dismiss();


            }
        });

        fivekm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Wineclass.this, "5 km restaurants are showing", Toast.LENGTH_SHORT).show();
                lookfurther.setBackgroundResource(R.drawable.radius_fivekm_press);
                radius = "five";

                runOnUiThread(new Runnable() {
                    public void run()
                    {
                        JSONArray jarr = NearByBar.BeerAndWineShop(getApplicationContext(), latitude, longitude,5);

                        for (int i = 0; i < jarr.length(); i++) {
                            try {
                                JSONObject obj = jarr.getJSONObject(i);
                                JSONObject shopinfo = obj.getJSONObject("shopInfo");
                                resname.add(shopinfo.getString("res_name"));
                                resdist.add(shopinfo.getString("distance").substring(0, 5));
                                reslat.add(shopinfo.getDouble("res_lat"));
                                reslong.add(shopinfo.getDouble("res_long"));
                                resnameInnerList=resname;

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                        for (int j = 0; j < jarr.length(); j++)
                        {
                            dist.add(distFrom(latitude,longitude, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                        }
                        Log.i("bottoms", "distance from loc " + dist);
                        String titles[] = new String[resdist.size()];
                        String descriptions[] = new String[resdist.size()];

                        for(int i=0;i<resdist.size();i++)
                        {
                            titles[i] = (String)resname.get(i);
                            descriptions[i] = (String)resdist.get(i)+"km";
                        }

                        rowItems = new ArrayList<RowItem>();
                        for (int i = 0; i < titles.length; i++) {
                            RowItem item = new RowItem(titles[i], descriptions[i]);
                            rowItems.add(item);
                        }

                        list = (ListView) findViewById(R.id.listViewwine);
                        CustomAdapterWine adapter = new CustomAdapterWine(Wineclass.this, rowItems);
                        list.setAdapter(adapter);

                    }


                });

                popupWindow.dismiss();


            }
        });

        twokm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Wineclass.this, "2 km restaurants are showing", Toast.LENGTH_SHORT).show();
                lookfurther.setBackgroundResource(R.drawable.radius_twokm_press);
                radius = "two";


                runOnUiThread(new Runnable() {
                    public void run()
                    {
                        JSONArray jarr = NearByBar.BeerAndWineShop(getApplicationContext(), latitude, longitude,2);

                        for (int i = 0; i < jarr.length(); i++) {
                            try {
                                JSONObject obj = jarr.getJSONObject(i);
                                JSONObject shopinfo = obj.getJSONObject("shopInfo");
                                resname.add(shopinfo.getString("res_name"));
                                resdist.add(shopinfo.getString("distance").substring(0, 5));
                                reslat.add(shopinfo.getDouble("res_lat"));
                                reslong.add(shopinfo.getDouble("res_long"));
                                resnameInnerList=resname;

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                        for (int j = 0; j < jarr.length(); j++)
                        {
                            dist.add(distFrom(latitude,longitude, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                        }
                        Log.i("bottoms", "distance from loc " + dist);
                        String titles[] = new String[resdist.size()];
                        String descriptions[] = new String[resdist.size()];

                        for(int i=0;i<resdist.size();i++)
                        {
                            titles[i] = (String)resname.get(i);
                            descriptions[i] = (String)resdist.get(i)+"km";
                        }

                        rowItems = new ArrayList<RowItem>();
                        for (int i = 0; i < titles.length; i++) {
                            RowItem item = new RowItem(titles[i], descriptions[i]);
                            rowItems.add(item);
                        }

                        list = (ListView) findViewById(R.id.listViewwine);
                        CustomAdapterWine adapter = new CustomAdapterWine(Wineclass.this, rowItems);
                        list.setAdapter(adapter);

                    }
                });


                popupWindow.dismiss();

            }
        });

        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        //  popupWindow.showAsDropDown(lookfurther, 20, 3, Gravity.TOP);

        if (radius.equalsIgnoreCase("look"))
        {
            popupWindow.showAsDropDown(lookfurther, 0, -lookfurther.getHeight()-lookfurther.getLayoutParams().height-lookfurther.getHeight(), Gravity.TOP);
        }
        else
        {
            popupWindow.showAsDropDown(lookfurther, 0, -lookfurther.getHeight()-lookfurther.getLayoutParams().height-lookfurther.getHeight(), Gravity.TOP);
        }


        //popupWindow.showAtLocation(lookfurther,Gravity.BOTTOM,10,10);
       /* }
        else
        {
            showAlertDialog(NewExpandaList.this,"Alert","Unable to Get Address",false);
        }
*/
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id)
{
        Toast toast = Toast.makeText(getApplicationContext()," " + (position + 1) + ": " + rowItems.get(position), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        }


    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Wineclass.this,Homescreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

}


package com.example.appsriv_02.loginwithfacebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DealsNearYou extends AppCompatActivity
{
    private ExpandableAdapterForDealsPage ExpAdapter;
    static  ArrayList<Group> ExpListItems;
    public static ExpandableListView ExpandList;
    ArrayList<Group> list = new ArrayList<Group>();
    ArrayList<Child> ch_list=null;
    GPSTracker gps;
    String city = null;
    AutoCompleteTextView atvPlaces;
    private static final String TAG = "bottoms";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deals_expandablelist_layout);
        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        ExpandList = (ExpandableListView) findViewById(R.id.exp_list1);
        ExpListItems= liquorSearch();

        ExpAdapter = new ExpandableAdapterForDealsPage(DealsNearYou.this, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        ExpAdapter.notifyDataSetChanged();

        ImageButton delas_near_you = (ImageButton) findViewById(R.id.deals);

        delas_near_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delas_near_you.setBackgroundResource(R.drawable.open);
                Intent dealsIntent = new Intent(DealsNearYou.this, DealsNearYou.class);
                dealsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dealsIntent);

            }
        });

        ImageButton winebutton=(ImageButton)findViewById(R.id.winebutton);
        winebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                double latitude = 0;
                double longitude = 0;
                // check if GPS enabled
               /* if (gps.canGetLocation())
                {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                } else
                {
                    gps.showSettingsAlert();
                }*/
                city = atvPlaces.getText().toString();
                if (city.length() != 0)
                {
                    //InternetConnectionDetector connectionDetector = newlayout InternetConnectionDetector(getApplicationContext());
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    //calling getLatLong method to get the lat and long of the entered city
                    latlng = gpsTracker.getLatLOng(city);
                    Double latitude1 = (Double) latlng.get(0);
                    Double longitude1 = (Double) latlng.get(1);

                    JSONArray nearByBar = null;
                    //calling the API to liquoar for beer or other brand for entered city in city textbox
                    nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude1.doubleValue(), longitude1.doubleValue());
                    // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                    if (nearByBar != null) {

                        Intent shopIntent = new Intent(DealsNearYou.this, Wineclass.class);
                        shopIntent.putExtra("city", city);
                        startActivity(shopIntent);
                    } else {
                        showAlertDialog(DealsNearYou.this, "Alert", "No Data Found for Current Location", false);
                    }
                } else {
                    JSONArray nearByBar = null;


                    //calling the API to liquoar for beer or other brand for current location when user doesn't enter the city instead just he touch the wine and beer shop

                    nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude, longitude);
                    // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                    if (nearByBar != null) {

                        Intent shopIntent = new Intent(DealsNearYou.this, Wineclass.class);
                        // shopIntent.putExtra("city",city);
                        startActivity(shopIntent);
                    } else {
                        showAlertDialog(DealsNearYou.this, "Alert", "No Data Found for Current Location", false);
                    }
                }
            }
        });
    }

    public static void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Showing Alert Message
        alertDialog.show();


    }

    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
                }
                strAdd = strReturnedAddress.toString();
                Log.w(TAG, "" + strReturnedAddress.toString());
            } else {

                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Canont get Address!");
        }
        return strAdd;
    }

    public ArrayList<Group> liquorSearch()
    {
        for (int i =0 ; i< 6 ; i++)
        {
            Group grp = new Group();
            grp.setName("Mela");
            grp.setCheckRestaurants(false);
            grp.setPlaceName("vikaspuri(5km)");
            //grp.setPrice("100");
            grp.setMinPrice("50");
            grp.setDist("1.2");
            Child child ;
            ch_list = new ArrayList<Child>();
            for (int j = 0; j < 5; j++) {
                child=new Child();
                child.setBrand("10 shots of vodka/100 piper whiskey+pizza");
                child.setPrice("999");
               // child.setMinPrice("777");
                ch_list.add(child);
            }
            grp.setItems(ch_list);
            list.add(grp);
        }
        return list;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(DealsNearYou.this,Homescreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

}

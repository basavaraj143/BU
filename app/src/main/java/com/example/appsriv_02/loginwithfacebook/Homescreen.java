package com.example.appsriv_02.loginwithfacebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cityautocomplete.PlaceJSONParser;

public class Homescreen extends Activity implements  AdapterView.OnItemSelectedListener
{
    //second autocompletetextview
    //rohit.dignitas@gmail.com

    public String data;
    public List<String> suggest;
    //public AutoCompleteTextView autoComplete;
    public ArrayAdapter<String> aAdapter;
    static boolean flag = false;

    private Filter filter;
    //end sec
    private ImageButton serachButton;
    ImageButton loc;
    String beer=null;
    ImageButton winebutton;
    ImageView back;
    static String switchStateOn="off";
    int oneClick=0;
    static Spinner spinner1;
    static Spinner spinner2;
    static Spinner spinner3;
    String sec_dropDown_value;


    private static final String TAG="bottoms";

    InternetConnectionDetector cd;
    Boolean isInternetPresent = false;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    String city=null;
    GPSTracker gps;
    String add;
    boolean selectedText = false;
    private static final int ADDRESS_TRESHOLD = 10;
    List<String> list = new ArrayList<String>();
    List<String> listplace = new ArrayList<String>();
    String location;
    double lat;
    double lng;
    List<String> brands=new ArrayList<String>();
    //List<String>
    ArrayList<Double> LAT_LNG = new ArrayList<Double>();

    String locality;
  //  String beer[]  = new String[1000];
    //String beer[]={"basu","navi","koti"};


    Tracker mTracker;

    public static int subBrandPosition;

    int spinner1_pos;
    int spinner2_pos;
    int spinner3_pos;
    boolean brandflag=true ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlayout);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build());
        serachButton = (ImageButton) findViewById(R.id.search);
        // loc = (ImageButton) findViewById(R.id.loc);
        winebutton = (ImageButton) findViewById(R.id.winebutton);
        back=(ImageView)findViewById(R.id.back);
        cd = new InternetConnectionDetector(getApplicationContext());
        //getCompleteAddressString( 28.63875 ,77.07380);
       //etCompleteAddressString(28.448479,77.0758733);
        //getCompleteAddressString(13.043115,77.570393);





        isInternetPresent = cd.isConnectingToInternet();
     //   ImageButton deals_near_you= (ImageButton)findViewById(R.id.getdeal);
        /*deals_near_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Homescreen.this,DealsNearYou.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
            }
        });*/
        //autocomple text view starts
        // LinearLayout autoCompleteTextViewlayout = (LinearLayout)findViewById(R.id.autoCompleteTextViewlayout);
        // autoCompleteTextViewlayout.getWidth();




        // Spinner item selection Listener
       // addListenerOnSpinnerItemSelection();

        // Button click Listener
       // addListenerOnButton();


        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        Point pointSize = new Point();

        getWindowManager().getDefaultDisplay().getSize(pointSize);


        gps = new GPSTracker(Homescreen.this);

        //  hideSoftKeyboard(this);
        hideKeyboard();

        // check if GPS enabled
        if (gps.canGetLocation())
        {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //Calling the getCompleteAddress method using GPS lat and Long

            add = getCompleteAddressString(latitude, longitude);
            atvPlaces.setText("Current Location");
        }
        else
        {
            gps.showSettingsAlert();
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String add = getCompleteAddressString(latitude, longitude);
            atvPlaces.setText(add);
        }

        filter = new Filter()
        {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null && constraint.length() > ADDRESS_TRESHOLD)
                {
                    Log.i("Filter", "doing a search ..");
                    new ParserTask().execute();

                }
                return null;
            }
        };


        spinner1 = (Spinner) findViewById(R.id.spinner1);
       // Beer, whiskey, vodka, rum ,cocktail.

        list.add("Beer");
        list.add("Whiskey");
        list.add("Vodka");
        list.add("Rum");
        list.add("Cocktail");
        spinner1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                hideKeyboard();
                return false;
            }
        });
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                hideKeyboard(v);
                hideKeyboard();
                return false;
            }
        });
        if (locality!=null) {
            if (locality.equalsIgnoreCase("New Delhi")) {
                listplace.add("Delhi");
                listplace.add("Noida");
                listplace.add("Gurgaon");
            } else if (locality.equalsIgnoreCase("Gurgaon")) {
                listplace.add("Gurgaon");
                listplace.add("Delhi");
                listplace.add("Noida");
            } else if (locality.equalsIgnoreCase("Noida")) {
                listplace.add("Noida");
                listplace.add("Gurgaon");
                listplace.add("Delhi");
            } else {
                listplace.add(locality);
                listplace.add("Delhi");
                listplace.add("Gurgaon");
                listplace.add("Noida");

            }
        }
        else
        {
            listplace.add("Delhi");
            listplace.add("Gurgaon");
            listplace.add("Noida");
        }

        // listplace.add("bangalore");
        spinner3=(Spinner)findViewById(R.id.spinner3);
        spinner3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                hideKeyboard(v);
                hideKeyboard();
                return false;
            }
        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,list);

        ArrayAdapter<String> placeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,listplace);

        placeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);



        spinner2.setAdapter(placeAdapter);
        spinner1.setAdapter(dataAdapter);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            spinner1_pos=bundle.getInt("spinner1_pos");
            spinner2_pos=bundle.getInt("spinner2_pos");
            spinner3_pos=bundle.getInt("spinner3_pos");
            spinner1.setSelection(spinner1_pos);
            spinner2.setSelection(spinner2_pos);
            spinner3.setSelection(spinner3_pos);
        }

        // this is to set the drop down after three character insted of first 3 if replaced with 1 means it will show suggestion from first char

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                isInternetPresent = cd.isConnectingToInternet();
                placesTask = new PlacesTask();
                if (isInternetPresent) {
                    placesTask.execute(s.toString());
                }
                selectedText = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override

            public void afterTextChanged(Editable s)
            {


            }
        });

        //autocomplete textview ends
        // sec autocompletetext view starts
        //   suggest = newlayout ArrayList<String>();
      //  autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        // autoComplete.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // autoComplete.setDropDownBackgroundResource(R.color.autocompletet_background_color);
       // autoComplete.setDropDownBackgroundResource(R.drawable.my_shape1);
        //atvPlaces.setDropDownBackgroundResource(R.drawable.my_shape1);
       // autoComplete.setTextColor(Color.BLACK);
        //autoComplete.setBackgroundColor(Color.BLACK);

       /* autoComplete.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                new getJson().execute(newText);
                selectedText = false;
            }

            public void afterTextChanged(Editable editable) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


        });

        */



/*
        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                selectedText = true;
                String foundItem = arg0.getItemAtPosition(arg2).toString();
                Log.i(TAG, foundItem);


                hideKeyboard();


                //  slideToLeft(serachButton);
                city = atvPlaces.getText().toString();
                final String beer = spinner3.getSelectedItem().toString();
                Context mContext = null;

                //to check both edit text should not be the empty
                if ((atvPlaces.getText().toString().length() == 0 || city.equals(" ") || atvPlaces.getText().toString() == null) ||
                        (beer.length() == 0 || beer.equals(" "))) {
                    showAlertDialog(Homescreen.this, "Alert",
                            "Please enter city & liquor.", false);

                } else {
                    // atvPlaces.requestFocus();
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    latlng = gpsTracker.getLatLOng(city);



                    JSONObject jsonObject= GPSTracker.getLocationInfo(city);
                    Double latitude=null;// = (Double) latlng.get(0);
                    Double longitude=null;// (Double) latlng.get(1);
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


                    if (jsonObject!=null) {
                        //  Double latitude = (Double) latlng.get(0);
                        //   Double longitude = (Double) latlng.get(1);


                        // check if GPS enabled
                        if (gps.canGetLocation())
                        {
                            try {
                                //Calling NearBybar from api in from the entered city place lat and long which returns the JSON
                                JSONArray nearByBar = NearByBar.getNearByBar(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue());
                                if (nearByBar != null) {
                                    //Calling liquor  api in for the entered city place lat and long and for particular brand  which returns the JSON
                                    nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), beer);
                                    int avg_price = 0;
                                    String checkLiquor = null;
                                    if (nearByBar != null) {
                                        try {
                                            for (int j = 0; j < nearByBar.length(); j++) {
//                                                avg_price = nearByBar.getJSONObject(j).getInt("avg_price");
                                                checkLiquor = nearByBar.getJSONObject(j).getString("liquor_type");

                                            }

                                            if (checkLiquor.equalsIgnoreCase("beer"))
                                            {
                                                final ProgressDialog progressDialog = ProgressDialog.show(Homescreen.this, "Please wait", "Loading...");

                                                new Thread() {

                                                    public void run() {

                                                        try {

                                                            sleep(1000);
                                                            Intent barList = new Intent(Homescreen.this, NewExpandaList.class);
                                                            barList.putExtra("brand", beer);
                                                            barList.putExtra("place", city);
                                                            barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(barList);
                                                            // overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                                                            oneClick=0;

                                                        } catch (Exception e) {

                                                            Log.e("tag", e.getMessage());

                                                        }

// dismiss the progress dialog

                                                        progressDialog.dismiss();

                                                    }

                                                }.start();


                                            } else {


                                                final ProgressDialog progressDialog = ProgressDialog.show(Homescreen.this, "Please Wait", "Loading...");

                                                new Thread() {

                                                    public void run() {

                                                        try {

                                                            sleep(1000);
                                                            Intent barList = new Intent(Homescreen.this, NonBeerExpandList.class);
                                                            barList.putExtra("brand", beer);
                                                            barList.putExtra("place", city);
                                                            barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(barList);
                                                            // overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                                                            oneClick=0;

                                                        } catch (Exception e) {

                                                            Log.e("tag", e.getMessage());

                                                        }

// dismiss the progress dialog

                                                        progressDialog.dismiss();

                                                    }

                                                }.start();


                                            }

                                        } catch (JSONException j) {
                                            j.printStackTrace();

                                        }

                                    } else {
                                        showAlertDialog(Homescreen.this, "Alert",
                                                "No data found please try different liquor.", false);

                                    }


                                } else {
                                    showAlertDialog(Homescreen.this, "Alert",
                                            "No data found please try different place.", false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            gps.showSettingsAlert();
                        }
                    }
                    else
                    {
                        showAlertDialog(Homescreen.this,"Alert","You seem to have entered invalid address. Please check",false );
                    }


                }
            }
        });*/


        /*autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedText = true;
                String foundItem = arg0.getItemAtPosition(arg2).toString();
                Log.i(TAG, foundItem);


                hideKeyboard();

                city = atvPlaces.getText().toString();
                final String beer = autoComplete.getText().toString();
                Context mContext = null;

                //to check both edit text should not be the empty
                if ((atvPlaces.getText().toString().length() == 0 || city.equals(" ") || atvPlaces.getText().toString() == null) ||
                        (beer.length() == 0 || beer.equals(" ")) || autoComplete.getText().toString() == null)
                {
                    showAlertDialog(Homescreen.this, "Alert",
                            "Please enter city & liquor.", false);

                } else
                {
                    // atvPlaces.requestFocus();

                    InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(mContext);
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();

                    //latlng =  getLatLongFromPlace(city);


                  //  final Double latitude = (Double) latlng.get(0);
                    // final Double longitude = (Double) latlng.get(1);

                   // JSONObject jsonObject= GPSTracker.getLocationInfo(city);
                    latlng=  GPSTracker.getLatitudeLongitude(city);
                    Double latitude = (Double) latlng.get(0);
                    Double longitude=(Double) latlng.get(1);

                    new testThread(city);

                 *//*   for (int m =0; m<jsonObject.length();m++)
                    {
                        try {
                            latitude= jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            longitude=jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        }
                        catch (JSONException ex)
                        {

                        }

                    }
*//*
                    *//*if (latlng.size()>0) {*//*

                        // check if GPS enabled
                        if (gps.canGetLocation()) {

                            try {

                                JSONArray nearByBar = NearByBar.getNearByBar(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue());

                                if (nearByBar != null) {
                                    //Calling liquor  api in for the entered city place lat and long and for particular brand  which returns the JSON
                                    nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), beer);
                                    int avg_price = 0;
                                    String checkLiquor = null;
                                    if (nearByBar != null) {
                                        try {
                                            for (int j = 0; j < nearByBar.length(); j++) {
                                                avg_price = nearByBar.getJSONObject(j).getInt("avg_price");
                                                checkLiquor = nearByBar.getJSONObject(j).getString("liquor_type");
                                            }

                                            if (checkLiquor.equalsIgnoreCase("beer")) {
//                                          final ProgressDialog progressDialog = ProgressDialog.show(MainPage.this, "", "Loading...");
                                                final ProgressDialog progressDialog = ProgressDialog.show(Homescreen.this, "Please Wait ", "Loading...");
                                                new Thread() {

                                                    public void run() {

                                                        try {

                                                            sleep(1500);
                                                            Intent barList = new Intent(Homescreen.this, NewExpandaList.class);
                                                            barList.putExtra("brand", beer);
                                                            barList.putExtra("place", city);
                                                            barList.putExtra("switchstate", switchStateOn);
                                                            barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            barList.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                            startActivity(barList);
                                                            //overridePendingTransition(R.anim.in_animation, R.anim.out_animation);

                                                        } catch (Exception e) {

                                                            Log.e("tag", e.getMessage());

                                                        }

// dismiss the progress dialog

                                                        progressDialog.dismiss();

                                                    }

                                                }.start();


                                            } else

                                            {
                                                final ProgressDialog progressDialog = ProgressDialog.show(Homescreen.this, "Please Wait", "Loading...");
                                                new Thread() {

                                                    public void run() {
                                                        try {

                                                            sleep(1500);
                                                            //ProgressDialog.show(mContext, "please wait","Loading");
                                                            Intent barList = new Intent(Homescreen.this, NonBeerExpandList.class);
                                                            barList.putExtra("brand", beer);
                                                            barList.putExtra("place", city);
                                                            barList.putExtra("switchstate", switchStateOn);
                                                            barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            barList.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                            startActivity(barList);
                                                         //   overridePendingTransition(R.anim.in_animation, R.anim.out_animation);

                                                        } catch (Exception e) {

                                                            Log.e("tag", e.getMessage());

                                                        }

                                                        progressDialog.dismiss();

                                                    }

                                                }.start();

                                            }

                                        } catch (JSONException j) {
                                            j.printStackTrace();

                                        }
                                    } else {
                                        showAlertDialog(Homescreen.this, "Alert",
                                                "No data found please try different liquor.", false);

                                    }
                                } else {
                                    showAlertDialog(Homescreen.this, "Alert",
                                            "No data found please try different place.", false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            gps.showSettingsAlert();
                        }
                   *//* }
                    else
                    {
                        showAlertDialog(Homescreen.this,"Alert","You seem to have entered invalid address. Please check",false);
                    }*//*
                }
            }
        });


        autoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
*/
        atvPlaces.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //sec ACTV end

            //on search button clicked in second page
            serachButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    isInternetPresent = cd.isConnectingToInternet();
                    // to get the value of place and what to have and calling api
                    if(isInternetPresent)
                    {


                      //  Toast.makeText(Homescreen.this, "Event is recorded. Check Google Analytics!", Toast.LENGTH_LONG).show();
                    //  slideToLeft(serachButton);
                        subBrandPosition=spinner3.getSelectedItemPosition();
                    city = atvPlaces.getText().toString();
                        city.replaceAll("[^a-zA-Z0-9]", "");
                    if (city.equalsIgnoreCase("Current Location"))
                    {
                        if (add!=null) {
                            city = add;
                        }
                        else
                        {
                            Toast.makeText(Homescreen.this,"Unable to fetch your location",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        city=atvPlaces.getText().toString();
                        city.replaceAll("[^a-zA-Z0-9]", "");
                    }
                    if (spinner3.getSelectedItem().toString().equalsIgnoreCase("all"))
                    {
                        beer = spinner1.getSelectedItem().toString();
                    }
                    else
                    {
                        beer = spinner3.getSelectedItem().toString();
                    }
                    if (spinner2.getSelectedItem().toString().equalsIgnoreCase("change city"))
                    {
                        sec_dropDown_value="";
                    }
                    else
                    {
                        sec_dropDown_value=spinner2.getSelectedItem().toString();
                    }

                    Context mContext = null;

                    //to check both edit text should not be the empty
                    if ((atvPlaces.getText().toString().length() == 0 || city.equals(" ") || atvPlaces.getText().toString() == null) ||
                            (beer.length() == 0 || beer.equals(" "))||atvPlaces.getText().toString().replaceAll("[^a-zA-Z0-9]", "").length()==0) {
                        showAlertDialog(Homescreen.this, "Alert",
                                "Please enter locality.", false);

                    } else
                    {
                        // atvPlaces.requestFocus();
                        GPSTracker gpsTracker = new GPSTracker(Homescreen.this);
                        ArrayList latlng = new ArrayList();
                        //latlng = gpsTracker.getLatLOng(city);
                        JSONObject jsonObject= GPSTracker.getLocationInfo(city +" "+spinner2.getSelectedItem().toString());
                        Double latitude=null;// = (Double) latlng.get(0);
                        Double longitude=null;// (Double) latlng.get(1);
                        if (jsonObject.length()!=0)
                        {
                            for (int m = 0; m < jsonObject.length(); m++)
                            {
                                try
                                {
                                    String status =jsonObject.getString("status");
                                    latitude = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                    longitude = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                } catch (JSONException ex) {

                                }

                            }
                        }
                            else
                            {
                                Toast.makeText(Homescreen.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                            }



                        if (jsonObject.length()!=0)
                        {

                            try {
                                //Calling NearBybar from api in from the entered city place lat and long which returns the JSON
                                JSONArray nearByBar = NearByBar.getNearByBar(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue());
                                if (nearByBar != null)
                                {
                                    //Calling liquor  api in for the entered city place lat and long and for particular brand  which returns the JSON
                                    nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), beer);
                                    int avg_price = 0;
                                    String checkLiquor = null;
                                    if (nearByBar != null) {
                                        try {
                                            for (int j = 0; j < nearByBar.length(); j++) {
//                                                avg_price = nearByBar.getJSONObject(j).getInt("avg_price");
                                                checkLiquor = nearByBar.getJSONObject(j).getString("liquor_type");

                                            }

                                            if (checkLiquor.equalsIgnoreCase("beer"))
                                            {
                                                if (isInternetPresent)
                                                {

                                                    final ProgressDialog progressDialog = ProgressDialog.show(Homescreen.this, "Please Wait", "Loading...");
                                                    new Thread() {

                                                        public void run() {
                                                            try {

                                                                sleep(1500);
                                                    Intent barList = new Intent(Homescreen.this, NewExpandaList.class);
                                                    barList.putExtra("brand", beer);
                                                    barList.putExtra("place", city + " " + sec_dropDown_value);
                                                    barList.putExtra("sublocality",city);
                                                    barList.putExtra("locality", sec_dropDown_value);
                                                    barList.putExtra("subBrandPosition",subBrandPosition);
                                                    barList.putExtra("mainbrand",spinner1.getSelectedItem().toString());
                                                    barList.putExtra("spinner1_pos",spinner1.getSelectedItemPosition());
                                                    barList.putExtra("spinner2_pos",spinner2.getSelectedItemPosition());
                                                    barList.putExtra("spinner3_pos",spinner3.getSelectedItemPosition());
                                                    barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(barList);
                                                    // overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                                                    oneClick = 0;

                                                            } catch (Exception e) {

                                                                Log.e("tag", e.getMessage());

                                                            }

                                                            progressDialog.dismiss();

                                                        }

                                                    }.start();
                                                }
                                                else
                                                {
                                                    Toast.makeText(Homescreen.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                            else if(checkLiquor.equalsIgnoreCase("Cocktails"))
                                            {

                                                if (isInternetPresent)
                                                {
                                                    final ProgressDialog progressDialog = ProgressDialog.show(Homescreen.this, "Please Wait", "Loading...");
                                                    new Thread() {

                                                        public void run() {
                                                            try {

                                                                sleep(1500);
                                                    Intent barList = new Intent(Homescreen.this, CocktailsExpandaList.class);
                                                    barList.putExtra("brand", beer);
                                                    barList.putExtra("place", city + " " + sec_dropDown_value);
                                                    barList.putExtra("locality", sec_dropDown_value);
                                                    barList.putExtra("mainbrand",spinner1.getSelectedItem().toString());
                                                    barList.putExtra("subBrandPosition",subBrandPosition);
                                                    barList.putExtra("sublocality",city);
                                                                barList.putExtra("spinner1_pos",spinner1.getSelectedItemPosition());
                                                                barList.putExtra("spinner2_pos",spinner2.getSelectedItemPosition());
                                                                barList.putExtra("spinner3_pos",spinner3.getSelectedItemPosition());
                                                    barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(barList);
                                                            } catch (Exception e) {

                                                                Log.e("tag", e.getMessage());

                                                            }

                                                            progressDialog.dismiss();

                                                        }

                                                    }.start();
                                                }

                                                else

                                                {
                                                        Toast.makeText(Homescreen.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                            else
                                            {
                                                if (isInternetPresent)
                                                {
                                                    final ProgressDialog progressDialog = ProgressDialog.show(Homescreen.this, "Please Wait", "Loading...");
                                                    new Thread() {

                                                        public void run()
                                                        {
                                                            try {

                                                                sleep(1500);
                                                    Intent barList = new Intent(Homescreen.this, NonBeerExpandList.class);
                                                    barList.putExtra("brand", beer);
                                                    barList.putExtra("place", city + " " + sec_dropDown_value);
                                                                barList.putExtra("locality", sec_dropDown_value);
                                                    barList.putExtra("mainbrand",spinner1.getSelectedItem().toString());
                                                    barList.putExtra("subBrandPosition",subBrandPosition);
                                                    barList.putExtra("sublocality",city);
                                                                barList.putExtra("spinner1_pos",spinner1.getSelectedItemPosition());
                                                                barList.putExtra("spinner2_pos",spinner2.getSelectedItemPosition());
                                                                barList.putExtra("spinner3_pos",spinner3.getSelectedItemPosition());
                                                    barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(barList);
                                                            }
                                                            catch (Exception e)
                                                            {

                                                                Log.e("tag", e.getMessage());

                                                            }

                                                            progressDialog.dismiss();

                                                        }

                                                    }.start();
                                                }

                                                else

                                                {
                                                    Toast.makeText(Homescreen.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                        } catch (JSONException j) {
                                            j.printStackTrace();

                                        }

                                    } else {
                                        showAlertDialog(Homescreen.this, "Alert",
                                                "No data found please try different liquor.", false);

                                    }
                                }
                                else
                                {
                                    showAlertDialog(Homescreen.this, "Alert",
                                            "No data found please try different place.", false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            showAlertDialog(Homescreen.this,"Alert","You seem to have entered invalid address. Please check",false );
                        }

                    }



                        Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();


                        // Build and send an Event.
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory(city+" "+sec_dropDown_value)
                                .setAction(beer)
                                .setLabel(city+" "+sec_dropDown_value)
                                .build());

                }
                else
                {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(Homescreen.this, "No Internet Connection",
                            "Please try again later", false);
                }

                }
            });


        Log.i("bottoms","locality in place activity "+location);

        //button to find the nearest bar and restaurent from the current location using lat and long of GPS lat and long
        winebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {

                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent)
                {


                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();

                    // Build and send an Event.
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Wine and beer shop ")
                            .setAction("Wine screen")
                            .setLabel("Wine Button Pressed")
                            .build());


                    city = atvPlaces.getText().toString();
                    if (city.equalsIgnoreCase("Current Location"))
                    {
                        if (add==null)
                        {
                            Toast.makeText(Homescreen.this,"Unable to fetch your current location",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            city = add;
                        }
                    }
                    else
                    {
                        city = atvPlaces.getText().toString();
                    }

                    if (city.length() != 0)
                    {
                        //InternetConnectionDetector connectionDetector = newlayout InternetConnectionDetector(getApplicationContext());
                        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                        ArrayList latlng = new ArrayList();
                        //calling getLatLong method to get the lat and long of the entered city
                        latlng = gpsTracker.getLatLOng(city);
                        // Double latitude1 = (Double) latlng.get(0);
                        //     Double longitude1 = (Double) latlng.get(1);

                        String status=null;
                        JSONObject jsonObject = GPSTracker.getLocationInfo(city);
                        try {
                            status=  jsonObject.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status.equalsIgnoreCase("ZERO_RESULTS")|| status==null)
                        {
                            showAlertDialog(Homescreen.this, "Alert", "No data found for your location", false);


                        } else
                        {

                            Double latitude1 = null;// = (Double) latlng.get(0);
                            Double longitude1 = null;// (Double) latlng.get(1);
                            for (int m = 0; m < jsonObject.length(); m++) {
                                try {
                                    latitude1 = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                    longitude1 = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                } catch (JSONException ex) {
                                    ex.printStackTrace();

                                }

                            }


                            JSONArray nearByBar = null;
                            //calling the API to liquoar for beer or other bratry {nd for entered city in city textbox
                            nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude1.doubleValue(), longitude1.doubleValue());
                            // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                            if (nearByBar != null) {

                                Intent shopIntent = new Intent(Homescreen.this, Wineclass.class);
                                shopIntent.putExtra("city", location + " " + city);
                                startActivity(shopIntent);
                                overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                            } else {
                                showAlertDialog(Homescreen.this, "Alert", "No Data Found for Current Location", false);
                            }
                        }
                    }
                }
                else
                {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(Homescreen.this, "No Internet Connection",
                            "Please try again later", false);
                }
            }

        });



        GoogleAnalyticsApplication application = (GoogleAnalyticsApplication) getApplicationContext();
        mTracker = application.getDefaultTracker();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Home screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        Spinner spinner = (Spinner)parent;
        Spinner spinner2 = (Spinner)parent;
        Spinner spinner4=(Spinner)parent;

        if(spinner.getId() == R.id.spinner1)
        {

            AsyncCallWS asyncCallWS = new AsyncCallWS();
            hideKeyboard();
            hideKeyboard(view);
            isInternetPresent = cd.isConnectingToInternet();
            //Toast.makeText(this, "Your choose :" + list.get(position), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Brandlist Cleared");
            String liquor = list.get(position);
            if (isInternetPresent) {
                asyncCallWS.execute(liquor);
            }
            else
            {
              showAlertDialog(Homescreen.this,"No Internet Connection","Please Try again.", false);
            }
          //  AsyncCallWS task = new AsyncCallWS();
          //  task.execute(liquor);
           /* try
            {

                JSONObject liquorsJson = getLiquors(Homescreen.this, liquor);

                int j=liquorsJson.length();
                for (int i = 0; i < j-1; i++)
                {
                    JSONArray jsonArray =liquorsJson.getJSONArray("result");
                    for (int k =0; k<jsonArray.length();k++) {
                        String brand_name =jsonArray.getJSONObject(k).getString("brand_name");
                        brands.add(brand_name.toLowerCase());
                    }
                }
              //  aAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, brands);
              //  aAdapter.notifyDataSetChanged();
*//*
                ArrayAdapter<String> brandadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,brands);
                brandadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(brandadapter);*//*
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }*/
         //   ArrayAdapter<String> brandadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,brands);

           // brandadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          //  spinner3.setAdapter(brandadapter);

        }
        if(spinner2.getId() == R.id.spinner2)
        {
            hideKeyboard();
            hideKeyboard(view);
           // Toast.makeText(this, "Your choose :" + listplace.get(position),Toast.LENGTH_SHORT).show();
            location=spinner2.getSelectedItem().toString();
        }


        }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {


    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideKeyboard() {
        try {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = findViewById(android.R.id.content);
            in.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Throwable e)
        {
            System.out.println();
            e.printStackTrace();
            // handle any un-expected UI issue if happened
        }

    }


  /*  public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }*/

    // method declaration to get the address from the latitude and longitude
    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                if (addresses.size()>0) {
                    Address returnedAddress = addresses.get(0);

                    StringBuilder strReturnedAddress = new StringBuilder("");


                    locality = addresses.get(0).getLocality();
                    String subLocality = addresses.get(0).getSubLocality();
                    if (subLocality != null) {
                        add = subLocality + " " + locality;
                    } else {
                        add = locality;
                    }
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
                    }
                    strAdd = strReturnedAddress.toString();


                    Log.w(TAG, "" + strReturnedAddress.toString());
                }
            } else
            {
                Log.w(TAG, "No Address returned!");

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG,"Canont get Address!");
        }
        return add;
    }


//current location lat 13.043115,77.570393

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Edownloading url", e.toString());
        }finally{
            if (iStream!=null) {
                iStream.close();
            }
            if (urlConnection!=null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    String s[]=null;
    class getJson extends AsyncTask<String,Void,String>
    {

        String [] s = new String[10000];
        ArrayList<String> strings = new ArrayList<String>();

        @Override
        protected String doInBackground(String... key)
        {

            String newText = key[0];
            newText = newText.trim();
            newText = newText.replace(" ", "+");
            HttpClient hClient=null;
            HttpGet hGet=null;
            try
            {
                hClient= new DefaultHttpClient();
                if (newText.length()!=0)
                {
                    hGet = new HttpGet("http://demos.dignitasdigital.com/bottomzup/radmin/liquors.php?find=" + newText);//url to fetch the liquor
                    ResponseHandler<String> rHandler = new BasicResponseHandler();
                    data = hClient.execute(hGet, rHandler);
                    suggest = new ArrayList<String>();
                    JSONArray jArray = new JSONArray(data);
                    // JSONObject mJsonObject = newlayout JSONObject();
                    for (int i = 0; i < jArray.length(); i++)
                    {
                        String liquor = jArray.getJSONObject(i).getString("liquors");
                        //  s[i]=liquor;
                        // strings.add(liquor);
                        s = new String[jArray.length()];
                        s[i] = jArray.getJSONObject(i).getString("liquors");
                        suggest.add(liquor);
                    }
                }
                else
                {
                    suggest.clear();
                }
                //   Log.i("liquors", "list of liquors " +strings);
            }
            catch(Exception e)
            {
                Log.w("Error", e.getMessage());
            }
            finally
            {
                if (hClient!=null)
                {
                    if(newText.length()<=0||newText.length()<1)
                    {
                        suggest.clear();
                    }

                    hClient.getConnectionManager().shutdown();
                }
                if (hGet!=null)
                {
                    // hGet.abort();
                }

            }
            return null;
        }
        public void onPostExecute(String result)
        {
            String[] data = suggest.toArray(new String[suggest.size()]);

            //String[] data1=s;
            Log.i("liquors","the data is " +data);
            // SimpleAdapter simpleAdapter = newlayout SimpleAdapter(getApplicationContext(), result, android.R.layout.simple_list_item_1,data);
            //newlayout SimpleAdapter(getApplicationContext(),)
            aAdapter = new ArrayAdapter<String>(Homescreen.this,android.R.layout.simple_list_item_1,android.R.id.text1,data); //simple_spinner_item to make very smaller text // simple_list_item_1 for medium
          //  autoComplete.setAdapter(aAdapter);
            data=null;
            suggest.clear();

            aAdapter.notifyDataSetChanged();

        }
    }


    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>
    {


        @Override
        protected String doInBackground(String... place)
        {
            // For storing data from web service
            String data = "";
           /* if(location.equalsIgnoreCase("Select City"))
            {
             //   showAlertDialog(Homescreen.this,"Alert","Please Select City",false);
                Toast.makeText(Homescreen.this,"Please select city",Toast.LENGTH_LONG).show();
            }
            else {
*/
                // Obtain browser key from https://code.google.com/apis/console
                // String key = "key=AIzaSyARPbMtArJO5ej-RtpX72RRDp6jMsG2gUw ";
                //String key = "key=AIzaSyARPbMtArJO5ej-RtpX72RRDp6jMsG2gUw ";
                String key = "key=AIzaSyC45IqTyfdeO5SzyLDGAVWiwADSSv70S6g";
                //AIzaSyCpN3yDXh0SJAl5ZGRinBmupJ6R0vamIkg

                String input = "";

                  //  input =place[0].replaceAll(" ","+");

                try {

                    input = URLEncoder.encode(place[0], "utf-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                // place type to be searched
                String types = "types=geocode";

                // Sensor enabled
                String sensor = "sensor=false";


                // Building the parameters to the web service
                String parameters = input + "&" + types + "&" + sensor + "&" + key + "&types=(regions)&components=country:IN";

                // Output format
                String output = "json";


                // Building the url to the web service
                //  String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
                String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyC45IqTyfdeO5SzyLDGAVWiwADSSv70S6g&input=" + "{" + location + "}" + input + "&types=(regions)&components=country:IN";
                Log.i("place ", "url " + url);

                try {
                    // Fetching the data from we service
                    data = downloadUrl(url);
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
          /*  }*/
            return data;

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }





    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result)
        {
            //Log.i(TAG,"place result "+result);

            String[] from = new String[] { "description"};

            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from,to);
            // Setting the adapter
            //adapter.setDropDownViewResource(R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            atvPlaces.setAdapter(adapter);
            if (adapter!=null) {
                adapter.notifyDataSetChanged();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

                public void showSettingsAlert() {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Homescreen.this);
                    alertDialog.setTitle("SETTINGS");
                    alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
                    alertDialog.setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Homescreen.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    // method  to display the alerts (positive or negative)
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();

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




    public static JSONObject getLiquors(Context context,String brand)
    {
        HttpURLConnection connection=null;

        //"http://10.0.2.2:51382/RestServiceImpl.svc/jsons/?Location="+items+"&GROUP="+items1+"&asondate="+dates;
        String brandUrl=brand.replaceAll(" ","%20");
        Log.i("bottoms", "brandUrl " + brandUrl);
        // String uuu="http://demos.dignitasdigital.com/bottomzup/searchresult.php?lat="+lattitude+"&long="+longitude+"&km="+km+"&records=20&query="+brandUrl;
        String uuu="http://demos.dignitasdigital.com/bottomzup/get_brandmaster_for_category.php?category="+brandUrl;
        try {;
            //String encodedurl = URLEncoder.encode(uuu, "UTF-8");
            URI uri = new URI(uuu.replace(" ", "%20"));
            URLEncoder.encode(uuu, "UTF-8");
            URL url = new URL(uuu);
            Log.i("bottoms" ,"Search URL "+uuu);

            connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("x-api-key","11111");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            JSONObject data = new JSONObject(json.toString());

            return data;
        }
        catch(Exception e)
        {
            return null;
        }


        finally {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }



    public static JSONObject getLocationInfo(String address)
    {
        String s=address;
        //	s=address;
        StringBuilder stringBuilder = new StringBuilder();
        HttpPost httppost=null;
        HttpClient client=null;
        s = s.replaceAll(" ","%20");
        try
        {

            httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + s + "&sensor=false");
            client= new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        }
        catch (ClientProtocolException e)
        {

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(client!=null)
            {
                client.getConnectionManager().shutdown();
            }
            if (httppost!=null)
            {
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }




    private class AsyncCallWS extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            try {
                progressDialog = ProgressDialog.show(Homescreen.this, "Please wait", "Loading...", true);
            } catch (final Throwable th) {
                //TODO
            }
        }


        @Override
        protected void onProgressUpdate(String... progress) {
            //do something
            super.onProgressUpdate(progress);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String s=params[0];
            brands.clear();

            try
            {

           /* Log.i(TAG, "doInBackground");
                String s1="http://demos.dignitasdigital.com/bottomzup/get_brandmaster_for_category.php?category="+s;

                HttpParams httpParameters = new BasicHttpParams();
              //  int timeout1 = 1000*10;
             //   int timeout2 = 1000*10;
              //  HttpConnectionParams.setConnectionTimeout(httpParameters);
              //  HttpConnectionParams.setSoTimeout(httpParameters);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);

                String jsonString = "";
                HttpGet request;
                try {
                    request = new HttpGet(new URI(s1));
                    request.addHeader("User-Agent", "Android");
                    HttpResponse response = httpclient.execute(request);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        jsonString = out.toString();
                    }

                }
                catch(ConnectException e)
                {
                    // handle your exception here, maybe something like
                   // Toast.makeText(Homescreen.this,"Error!",Toast.LENGTH_SHORT).show();
                    finish(); // if your are within activity class, otherwise call finish on your activity

                } catch (URISyntaxException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    //Toast.makeText(Homescreen.this,"Connection time out please try again",Toast.LENGTH_SHORT).show();
                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(new StandardExceptionParser(Homescreen.this, null).getDescription(Thread.currentThread().getName(), e1))
                            .setFatal(false)
                            .build());
                } catch (ClientProtocolException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                   // Toast.makeText(Homescreen.this,"Connection time out please try again",Toast.LENGTH_SHORT).show();
                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(new StandardExceptionParser(Homescreen.this, null).getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build());
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                   // Toast.makeText(Homescreen.this,"Connection time out please try again",Toast.LENGTH_SHORT).show();
                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(new StandardExceptionParser(Homescreen.this, null).getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build());
                } catch (Exception e) {
                    e.printStackTrace();
                   // Toast.makeText(Homescreen.this,"Connection time out please try again",Toast.LENGTH_SHORT).show();
                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(new StandardExceptionParser(Homescreen.this, null).getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build());
                }*/
               // JSONObject myJsonObject = new JSONObject(jsonString);

                JSONObject myJsonObject = getLiquors(Homescreen.this, s);

            int j=myJsonObject.length();
            for (int i = 0; i < j-1; i++)
            {
                JSONArray jsonArray =myJsonObject.getJSONArray("result");
                for (int k =0; k<jsonArray.length();k++)
                {
                    String brand_name =jsonArray.getJSONObject(k).getString("brand_name");

                    brands.add(brand_name.substring(0,1).toUpperCase()+brand_name.substring(1));
                }
            }






        }
            catch (JSONException e)
            {
                e.printStackTrace();

                Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                t.send(new HitBuilders.ExceptionBuilder()
                        .setDescription(new StandardExceptionParser(Homescreen.this, null).getDescription(Thread.currentThread().getName(), e))
                        .setFatal(false)
                        .build());
            }



            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {

            if (brands.size()>0) {
                ArrayAdapter<String> brandadapter = new ArrayAdapter<String>(Homescreen.this, android.R.layout.simple_spinner_item, brands);
                brandadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spinner3.setAdapter(brandadapter);
                Bundle bundle = getIntent().getExtras();
                if (bundle != null)
                {
                    if (spinner3_pos!=0)
                    {
                        brandflag = false;
                        spinner3.setSelection(spinner3_pos);
                        spinner3_pos=0;
                    } else
                    {
                        spinner3.setSelection(0);
                    }
                    Log.i(TAG, "onPostExecute");
                    progressDialog.dismiss();
                }
                else
                {
                    if (brandflag)
                    {
                        brandflag = false;
                        spinner3.setSelection(0);
                    } else {
                        spinner3.setSelection(0);
                    }
                    Log.i(TAG, "onPostExecute");
                    progressDialog.dismiss();
                }
            }
          //  Toast.makeText(MainActivity.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }

    }



    private class LatLong extends AsyncTask<String, String, String>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            try {
                progressDialog = ProgressDialog.show(Homescreen.this, "Please wait", "Loading...", true);
            } catch (final Throwable th) {
                //TODO
            }
        }


        @Override
        protected void onProgressUpdate(String... progress) {
            //do something
            super.onProgressUpdate(progress);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String s=params[0];
            brands.clear();


                JSONObject myJsonObject = getLocationInfo(s);
                myJsonObject.toString();


            return myJsonObject.toString();
            }
        @Override
        protected void onPostExecute(String result)
        {

            try
            {
                JSONObject myJsonObject = new JSONObject(result);
                for (int m = 0; m < myJsonObject.length(); m++) {
                    try {
                        String status = myJsonObject.getString("status");
                        lat = myJsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        lng = myJsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    } catch (JSONException ex) {

                    }

                }


                Log.i(TAG, "onPostExecute");
                progressDialog.dismiss();
            }
            catch (JSONException j)
            {

            }
        }




        }




}

package com.example.appsriv_02.loginwithfacebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
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

public class duplicateClass extends Activity implements  AdapterView.OnItemSelectedListener
{
    //second autocompletetextview
    public String data;
    public List<String> suggest;
    public AutoCompleteTextView autoComplete;
    public ArrayAdapter<String> aAdapter;
    static boolean flag = false;

    private Filter filter;
    //end sec
    private ImageButton serachButton;
    ImageButton loc;
    ImageButton winebutton;
    ImageView back;
    static String switchStateOn="off";
    int oneClick=0;
    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;
    private Button btnSubmit;

    private static final String TAG="bottoms";

    InternetConnectionDetector cd;
    Boolean isInternetPresent = false;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    String city=null;
    GPSTracker gps;
    boolean selectedText = false;
    private static final int ADDRESS_TRESHOLD = 10;
    List<String> list = new ArrayList<String>();
    List<String> listplace = new ArrayList<String>();
    List<String> brands=new ArrayList<String>();
    //List<String>

    String beer[]  = new String[1000];
    //String beer[]={"basu","navi","koti"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        beer[0]="";
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


        spinner1 = (Spinner) findViewById(R.id.spinner1);

        list.add("Beer");
        list.add("Whiskey");
        list.add("Wine");
        list.add("Rum");
        list.add("scotch");

        spinner2 = (Spinner) findViewById(R.id.spinner2);

        listplace.add("Delhi");
        listplace.add("Bangalore");
        listplace.add("Pune");
        listplace.add("Mumbai");
        listplace.add("Hydrabad");

        spinner3=(Spinner)findViewById(R.id.spinner3);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);

        ArrayAdapter<String> placeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,listplace);

        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        spinner2.setAdapter(placeAdapter);
        spinner1.setAdapter(dataAdapter);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);

        // Spinner item selection Listener
       // addListenerOnSpinnerItemSelection();

        // Button click Listener
       // addListenerOnButton();



        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        Point pointSize = new Point();

        getWindowManager().getDefaultDisplay().getSize(pointSize);


        gps = new GPSTracker(duplicateClass.this);

        //  hideSoftKeyboard(this);
        hideKeyboard();

        // check if GPS enabled
        if (gps.canGetLocation())
        {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //Calling the getCompleteAddress method using GPS lat and Long

            String add = getCompleteAddressString(latitude, longitude);
            atvPlaces.setText(add);
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

        // this is to set the drop down after three character insted of first 3 if replaced with 1 means it will show suggestion from first char

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
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
        autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        // autoComplete.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // autoComplete.setDropDownBackgroundResource(R.color.autocompletet_background_color);
        autoComplete.setDropDownBackgroundResource(R.drawable.my_shape1);
        atvPlaces.setDropDownBackgroundResource(R.drawable.my_shape1);
        autoComplete.setTextColor(Color.BLACK);
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


        });*/




        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                        (beer.length() == 0 || beer.equals(" ")) || autoComplete.getText().toString() == null) {
                    showAlertDialog(duplicateClass.this, "Alert",
                            "Please enter city & liquor.", false);

                } else {
                    // atvPlaces.requestFocus();

                    InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(mContext);
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


                    if (jsonObject!=null)
                    {
                   // Double latitude = (Double) latlng.get(0);
                 //   Double longitude = (Double) latlng.get(1);
                    // check if GPS enabled
                    if (gps.canGetLocation())
                    {

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
                                            final ProgressDialog progressDialog = ProgressDialog.show(duplicateClass.this, "Please Wait ", "Loading...");
                                            new Thread() {

                                                public void run() {

                                                    try {

                                                        sleep(1500);
                                                        Intent barList = new Intent(duplicateClass.this, NewExpandaList.class);
                                                        barList.putExtra("brand", beer);
                                                        barList.putExtra("place", city);
                                                        barList.putExtra("switchstate", switchStateOn);
                                                        barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        barList.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        startActivity(barList);
                                                        overridePendingTransition(R.anim.in_animation, R.anim.out_animation);

                                                    } catch (Exception e) {

                                                        Log.e("tag", e.getMessage());

                                                    }

// dismiss the progress dialog

                                                    progressDialog.dismiss();

                                                }

                                            }.start();


                                        } else

                                        {
                                            final ProgressDialog progressDialog = ProgressDialog.show(duplicateClass.this, "Please Wait", "Loading...");
                                            new Thread() {

                                                public void run() {
                                                    try {

                                                        sleep(1500);
                                                        //ProgressDialog.show(mContext, "please wait","Loading");
                                                        Intent barList = new Intent(duplicateClass.this, NonBeerExpandList.class);
                                                        barList.putExtra("brand", beer);
                                                        barList.putExtra("place", city);
                                                        barList.putExtra("switchstate", switchStateOn);
                                                        barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        barList.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        startActivity(barList);
                                                        overridePendingTransition(R.anim.in_animation, R.anim.out_animation);

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
                                    showAlertDialog(duplicateClass.this, "Alert",
                                            "No data found please try different liquor.", false);

                                }
                            } else {
                                showAlertDialog(duplicateClass.this, "Alert",
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
                        showAlertDialog(duplicateClass.this,"Alert","You seem to have entered invalid address. Please check" ,false);
                    }
                }
            }
        });



        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener()
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
                    showAlertDialog(duplicateClass.this, "Alert",
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

                   // new testThread(city);

                 /*   for (int m =0; m<jsonObject.length();m++)
                    {
                        try {
                            latitude= jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            longitude=jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        }
                        catch (JSONException ex)
                        {

                        }

                    }
*/
                    /*if (latlng.size()>0) {*/

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
                                                final ProgressDialog progressDialog = ProgressDialog.show(duplicateClass.this, "Please Wait ", "Loading...");
                                                new Thread() {

                                                    public void run() {

                                                        try {

                                                            sleep(1500);
                                                            Intent barList = new Intent(duplicateClass.this, NewExpandaList.class);
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
                                                final ProgressDialog progressDialog = ProgressDialog.show(duplicateClass.this, "Please Wait", "Loading...");
                                                new Thread() {

                                                    public void run() {
                                                        try {

                                                            sleep(1500);
                                                            //ProgressDialog.show(mContext, "please wait","Loading");
                                                            Intent barList = new Intent(duplicateClass.this, NonBeerExpandList.class);
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
                                        showAlertDialog(duplicateClass.this, "Alert",
                                                "No data found please try different liquor.", false);

                                    }
                                } else {
                                    showAlertDialog(duplicateClass.this, "Alert",
                                            "No data found please try different place.", false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            gps.showSettingsAlert();
                        }
                   /* }
                    else
                    {
                        showAlertDialog(Homescreen.this,"Alert","You seem to have entered invalid address. Please check",false);
                    }*/
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

        atvPlaces.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //sec ACTV end

        // to get the value of place and what to have and calling api
        if(isInternetPresent)
        {
            //on search button clicked in second page
            serachButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    //  slideToLeft(serachButton);
                    city = atvPlaces.getText().toString();
                    final String beer = autoComplete.getText().toString();
                    Context mContext = null;

                    //to check both edit text should not be the empty
                    if ((atvPlaces.getText().toString().length() == 0 || city.equals(" ") || atvPlaces.getText().toString() == null) ||
                            (beer.length() == 0 || beer.equals(" ")) || autoComplete.getText().toString() == null) {
                        showAlertDialog(duplicateClass.this, "Alert",
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
                                                avg_price = nearByBar.getJSONObject(j).getInt("avg_price");
                                                checkLiquor = nearByBar.getJSONObject(j).getString("liquor_type");

                                            }

                                            if (checkLiquor.equalsIgnoreCase("beer")) {
                                                final ProgressDialog progressDialog = ProgressDialog.show(duplicateClass.this, "", "Loading...");

                                                new Thread() {

                                                    public void run() {

                                                        try {

                                                            sleep(5000);
                                                            Intent barList = new Intent(duplicateClass.this, NewExpandaList.class);
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


                                                final ProgressDialog progressDialog = ProgressDialog.show(duplicateClass.this, "Please Wait", "Loading...");

                                                new Thread() {

                                                    public void run() {

                                                        try {

                                                            sleep(1500);
                                                            Intent barList = new Intent(duplicateClass.this, NonBeerExpandList.class);
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
                                        showAlertDialog(duplicateClass.this, "Alert",
                                                "No data found please try different liquor.", false);

                                    }


                                } else {
                                    showAlertDialog(duplicateClass.this, "Alert",
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
                            showAlertDialog(duplicateClass.this,"Alert","You seem to have entered invalid address. Please check",false );
                        }


                    }

                }
            });


        }
        else
        {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(duplicateClass.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }


        //button to find the nearest bar and restaurent from the current location using lat and long of GPS lat and long
        winebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                double latitude = 0;
                double longitude = 0;
                // check if GPS enabled
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                } else {
                    gps.showSettingsAlert();
                }
                city = atvPlaces.getText().toString();
                if (city.length() != 0) {
                    //InternetConnectionDetector connectionDetector = newlayout InternetConnectionDetector(getApplicationContext());
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    //calling getLatLong method to get the lat and long of the entered city
                    latlng = gpsTracker.getLatLOng(city);
                    // Double latitude1 = (Double) latlng.get(0);
                    //     Double longitude1 = (Double) latlng.get(1);

                    JSONObject jsonObject = GPSTracker.getLocationInfo(city);
                    Double latitude1 = null;// = (Double) latlng.get(0);
                    Double longitude1 = null;// (Double) latlng.get(1);
                    for (int m = 0; m < jsonObject.length(); m++) {
                        try {
                            latitude1 = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            longitude1 = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        } catch (JSONException ex) {

                        }

                    }


                    JSONArray nearByBar = null;
                    //calling the API to liquoar for beer or other brand for entered city in city textbox
                    nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude1.doubleValue(), longitude1.doubleValue());
                    // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                    if (nearByBar != null) {

                        Intent shopIntent = new Intent(duplicateClass.this, Wineclass.class);
                        shopIntent.putExtra("city", city);
                        startActivity(shopIntent);
                        overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                    } else {
                        showAlertDialog(duplicateClass.this, "Alert", "No Data Found for Current Location", false);
                    }
                } else {
                    JSONArray nearByBar = null;


                    //calling the API to liquoar for beer or other brand for current location when user doesn't enter the city instead just he touch the wine and beer shop

                    nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude, longitude);
                    // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                    if (nearByBar != null) {

                        Intent shopIntent = new Intent(duplicateClass.this, Wineclass.class);
                        // shopIntent.putExtra("city",city);
                        startActivity(shopIntent);
                    } else {
                        showAlertDialog(duplicateClass.this, "Alert", "No Data Found for Current Location", false);
                    }
                }
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        Spinner spinner = (Spinner)parent;
        Spinner spinner2 = (Spinner)parent;
        if(spinner.getId() == R.id.spinner1)
        {
            brands.clear();
            Toast.makeText(this, "Your choose :" + list.get(position), Toast.LENGTH_SHORT).show();
            Log.i(TAG,"Brandlist Cleared");
            String liquor = list.get(position);
            try
            {

                JSONObject liquorsJson = getLiquors(duplicateClass.this, liquor);
                int j=liquorsJson.length();
                for (int i = 0; i < j-1; i++)
                {
                    JSONArray jsonArray =liquorsJson.getJSONArray("result");
                    for (int k =0; k<jsonArray.length();k++) {
                        String brand_name =jsonArray.getJSONObject(k).getString("brand_name");
                        brands.add(brand_name.toLowerCase());
                    }
                }
                aAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, brands);
                //  aAdapter = new ArrayAdapter<String>(Homescreen.this,android.R.layout.simple_list_item_1,android.R.id.text1,beer); //simple_spinner_item to make very smaller text // simple_list_item_1 for medium
                autoComplete.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();

                ArrayAdapter<String> brandadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,brands);

                brandadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(brandadapter);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

        }
        if(spinner2.getId() == R.id.spinner2)
        {
            Toast.makeText(this, "Your choose :" + listplace.get(position),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
                }
                strAdd = strReturnedAddress.toString();
                String add[]= strAdd.split(",");

                Log.w(TAG, "" + strReturnedAddress.toString());
            } else
            {
                Log.w(TAG, "No Address returned!");

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG,"Canont get Address!");
        }
        return strAdd;
    }



    // Add spinner data

   /* public void addListenerOnSpinnerItemSelection()
    {

        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    //get the selected dropdown list value

    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        //spinner1.setBackgroundResource(R.drawable.arrowdown);


    }
*/
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
                    hGet = new HttpGet("http://demos.dignitasdigital.com/bottomzup/liquors.php?find=" + newText);//url to fetch the liquor
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
            aAdapter = new ArrayAdapter<String>(duplicateClass.this,android.R.layout.simple_list_item_1,android.R.id.text1,data); //simple_spinner_item to make very smaller text // simple_list_item_1 for medium
            autoComplete.setAdapter(aAdapter);
            data=null;
            suggest.clear();

            aAdapter.notifyDataSetChanged();

        }
    }


    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            // String key = "key=AIzaSyARPbMtArJO5ej-RtpX72RRDp6jMsG2gUw ";
            //String key = "key=AIzaSyARPbMtArJO5ej-RtpX72RRDp6jMsG2gUw ";
            String key ="key=AIzaSyC45IqTyfdeO5SzyLDGAVWiwADSSv70S6g";
            //AIzaSyCpN3yDXh0SJAl5ZGRinBmupJ6R0vamIkg

            String input="";

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
            String parameters = input + "&" + types + "&" + sensor + "&" + key+"&types=(regions)&components=country:IN";

            // Output format
            String output = "json";


            // Building the url to the web service
            //  String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyC45IqTyfdeO5SzyLDGAVWiwADSSv70S6g&input=" + input + "&types=(regions)&components=country:IN";
            Log.i("place ", "url " + url);

            try {
                // Fetching the data from we service
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

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
            Log.i(TAG,"place result "+result);

            String[] from = new String[] { "description"};

            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from,to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public  void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(duplicateClass.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        duplicateClass.this.startActivity(intent);
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
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    class GetLiquor extends AsyncTask<String,Void,String>
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
                    hGet = new HttpGet("http://demos.dignitasdigital.com/bottomzup/liquors.php?find=" + newText);//url to fetch the liquor
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
            aAdapter = new ArrayAdapter<String>(duplicateClass.this,android.R.layout.simple_list_item_1,android.R.id.text1,data); //simple_spinner_item to make very smaller text // simple_list_item_1 for medium
            autoComplete.setAdapter(aAdapter);
            data=null;
            suggest.clear();

            aAdapter.notifyDataSetChanged();

        }
    }







    public static JSONObject getLiquors(Context context,String brand)
    {
        HttpURLConnection connection=null;

        //"http://10.0.2.2:51382/RestServiceImpl.svc/jsons/?Location="+items+"&GROUP="+items1+"&asondate="+dates;
        String brandUrl=brand.replaceAll(" ","%20");
        Log.i("bottoms" ,"brandUrl " +brandUrl);
        // String uuu="http://demos.dignitasdigital.com/bottomzup/searchresult.php?lat="+lattitude+"&long="+longitude+"&km="+km+"&records=20&query="+brandUrl;
        String uuu="http://demos.dignitasdigital.com/bottomzup/radmin/get_brandmaster_for_category.php?category="+brandUrl;
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
            while((tmp=reader.readLine())!=null) json.append(tmp).append("\n");
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





  /*  public ArrayList<Double> getLatLongFromPlace(String place)
    {
        ArrayList<Double> list = new ArrayList<>();
        try {
            Geocoder selected_place_geocoder = new Geocoder(getBaseContext());
            List<Address> address;

            address = selected_place_geocoder.getFromLocationName(place, 5);

            if (address == null) {
              //  d.dismiss();
            } else
            {
                Address location = address.get(0);
                double lat= location.getLatitude();
                double lng = location.getLongitude();

                list.add(lat);
                list.add(lng);


            }

        } catch (Exception e) {
            e.printStackTrace();
            fetchLatLongFromService fetch_latlng_from_service_abc = new fetchLatLongFromService(place.replaceAll("\\s+", ""));
            fetch_latlng_from_service_abc.execute();

        }
        return list;

    }


//Sometimes happens that device gives location = null

    public class fetchLatLongFromService extends
            AsyncTask<Void, Void, StringBuilder> {
        String place;


        public fetchLatLongFromService(String place) {
            super();
            this.place = place;

        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            this.cancel(true);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
                        + this.place + "&sensor=false";

                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(
                        conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                String a = "";
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");

                // Extract the Place descriptions from the results
                // resultList = new ArrayList<String>(resultJsonArray.length());

                JSONObject before_geometry_jsonObj = resultJsonArray
                        .getJSONObject(0);

                JSONObject geometry_jsonObj = before_geometry_jsonObj
                        .getJSONObject("geometry");

                JSONObject location_jsonObj = geometry_jsonObj
                        .getJSONObject("location");

                String lat_helper = location_jsonObj.getString("lat");
                double lat = Double.valueOf(lat_helper);


                String lng_helper = location_jsonObj.getString("lng");
                double lng = Double.valueOf(lng_helper);


                LatLng point = new LatLng(lat, lng);


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }

*/

}

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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cityautocomplete.PlaceJSONParser;

public class MainPage extends Activity
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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
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
        ImageButton deals_near_you= (ImageButton)findViewById(R.id.getdeal);
        deals_near_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainPage.this,DealsNearYou.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
            }
        });
        //autocomple text view starts
       // LinearLayout autoCompleteTextViewlayout = (LinearLayout)findViewById(R.id.autoCompleteTextViewlayout);
       // autoCompleteTextViewlayout.getWidth();



        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        Point pointSize = new Point();

        getWindowManager().getDefaultDisplay().getSize(pointSize);


        gps = new GPSTracker(MainPage.this);

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

            autoComplete.addTextChangedListener(new TextWatcher()
            {

                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    String newText = s.toString();
                   new getJson().execute(newText);

                    selectedText=false;
                }

                public void afterTextChanged(Editable editable)
                {

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }



            });




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
                    showAlertDialog(MainPage.this, "Alert",
                            "Please enter city & liquor.", false);

                } else {
                    // atvPlaces.requestFocus();

                    InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(mContext);
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    latlng = gpsTracker.getLatLOng(city);
                    Double latitude = (Double) latlng.get(0);
                    Double longitude = (Double) latlng.get(1);
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
                                            final ProgressDialog progressDialog = ProgressDialog.show(MainPage.this, "Please Wait ", "Loading...");
                                            new Thread() {

                                                public void run() {

                                                    try {

                                                        sleep(1500);
                                                        Intent barList = new Intent(MainPage.this, NewExpandaList.class);
                                                        barList.putExtra("brand", beer);
                                                        barList.putExtra("place", city);
                                                        barList.putExtra("switchstate", switchStateOn);
                                                        barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                                            final ProgressDialog progressDialog = ProgressDialog.show(MainPage.this, "Please Wait", "Loading...");
                                            new Thread() {

                                                public void run() {
                                                    try {

                                                        sleep(1500);
                                                        //ProgressDialog.show(mContext, "please wait","Loading");
                                                        Intent barList = new Intent(MainPage.this, NonBeerExpandList.class);
                                                        barList.putExtra("brand", beer);
                                                        barList.putExtra("place", city);
                                                        barList.putExtra("switchstate", switchStateOn);
                                                        barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                                    showAlertDialog(MainPage.this, "Alert",
                                            "No data found please try different liquor.", false);

                                }
                            } else {
                                showAlertDialog(MainPage.this, "Alert",
                                        "No data found please try different place.", false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        gps.showSettingsAlert();
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
                    showAlertDialog(MainPage.this, "Alert",
                            "Please enter city & liquor.", false);

                } else
                {
                    // atvPlaces.requestFocus();

                    InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(mContext);
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    latlng = gpsTracker.getLatLOng(city);
                    Double latitude = (Double) latlng.get(0);
                    Double longitude = (Double) latlng.get(1);
                    // check if GPS enabled
                    if (gps.canGetLocation()) {

                        try {

                            JSONArray nearByBar = NearByBar.getNearByBar(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue());

                            if (nearByBar != null)
                            {
                                //Calling liquor  api in for the entered city place lat and long and for particular brand  which returns the JSON
                                nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), beer);
                                int avg_price = 0;
                                String checkLiquor=null;
                                if (nearByBar != null)
                                {
                                    try {
                                        for (int j = 0; j < nearByBar.length(); j++) {
                                            avg_price = nearByBar.getJSONObject(j).getInt("avg_price");
                                            checkLiquor=nearByBar.getJSONObject(j).getString("liquor_type");
                                        }

                                        if (checkLiquor.equalsIgnoreCase("beer")) {
//                                          final ProgressDialog progressDialog = ProgressDialog.show(MainPage.this, "", "Loading...");
                                            final ProgressDialog progressDialog = ProgressDialog.show(MainPage.this, "Please Wait ", "Loading...");
                                            new Thread() {

                                                public void run()
                                                {

                                                    try
                                                    {

                                                        sleep(1500);
                                                        Intent barList = new Intent(MainPage.this, NewExpandaList.class);
                                                        barList.putExtra("brand", beer);
                                                        barList.putExtra("place", city);
                                                        barList.putExtra("switchstate",switchStateOn);
                                                        barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                                            final ProgressDialog progressDialog = ProgressDialog.show(MainPage.this, "Please Wait", "Loading...");
                                            new Thread() {

                                                public void run()
                                                {
                                                    try
                                                    {

                                                        sleep(1500);
                                                        //ProgressDialog.show(mContext, "please wait","Loading");
                                                        Intent barList = new Intent(MainPage.this, NonBeerExpandList.class);
                                                        barList.putExtra("brand", beer);
                                                        barList.putExtra("place", city);
                                                        barList.putExtra("switchstate",switchStateOn);
                                                        barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                                }
                                else
                                {
                                    showAlertDialog(MainPage.this, "Alert",
                                            "No data found please try different liquor.", false);

                                }
                            }
                            else
                            {
                                showAlertDialog(MainPage.this, "Alert",
                                        "No data found please try different place.", false);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        gps.showSettingsAlert();
                    }
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
                            showAlertDialog(MainPage.this, "Alert",
                                    "Please enter city & liquor.", false);

                        } else {
                            // atvPlaces.requestFocus();
                            GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                            ArrayList latlng = new ArrayList();
                            latlng = gpsTracker.getLatLOng(city);
                            Double latitude = (Double) latlng.get(0);
                            Double longitude = (Double) latlng.get(1);

                            // check if GPS enabled
                            if (gps.canGetLocation()) {
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
                                                    final ProgressDialog progressDialog = ProgressDialog.show(MainPage.this, "", "Loading...");

                                                    new Thread() {

                                                        public void run() {

                                                            try {

                                                                sleep(1000);
                                                                Intent barList = new Intent(MainPage.this, NewExpandaList.class);
                                                                barList.putExtra("brand", beer);
                                                                barList.putExtra("place", city);
                                                                barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(barList);
                                                                overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                                                                oneClick=0;

                                                            } catch (Exception e) {

                                                                Log.e("tag", e.getMessage());

                                                            }

// dismiss the progress dialog

                                                            progressDialog.dismiss();

                                                        }

                                                    }.start();


                                                } else {


                                                    final ProgressDialog progressDialog = ProgressDialog.show(MainPage.this, "Please Wait", "Loading...");

                                                    new Thread() {

                                                        public void run() {

                                                            try {

                                                                sleep(1000);
                                                                Intent barList = new Intent(MainPage.this, NonBeerExpandList.class);
                                                                barList.putExtra("brand", beer);
                                                                barList.putExtra("place", city);
                                                                barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(barList);
                                                                overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
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
                                            showAlertDialog(MainPage.this, "Alert",
                                                    "No data found please try different liquor.", false);

                                        }


                                    } else {
                                        showAlertDialog(MainPage.this, "Alert",
                                                "No data found please try different place.", false);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                gps.showSettingsAlert();
                            }


                        }

                }
            });


        }
        else
        {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(MainPage.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }
            //to get the current location address
           /* loc.setOnClickListener(newlayout View.OnClickListener() {
                @Override
                public
                void onClick(View arg0)
                {
                    gps = newlayout GPSTracker(MainPage.this);
                    if (gps.canGetLocation())
                    {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        //Calling the getCompleteAddress method using GPS lat and Long
                     //   28.63875,77.07380
                        String add=getCompleteAddressString(latitude, longitude);
                       // String add=getCompleteAddressString(28.63875,77.07380);
                        atvPlaces.setText(add);
                    }
                    else
                    {
                        gps.showSettingsAlert();
                    }
                }
            });*/




        //button to find the nearest bar and restaurent from the current location using lat and long of GPS lat and long
        winebutton.setOnClickListener(new View.OnClickListener()
        {
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
                    Double latitude1 = (Double) latlng.get(0);
                    Double longitude1 = (Double) latlng.get(1);

                    JSONArray nearByBar = null;
                    //calling the API to liquoar for beer or other brand for entered city in city textbox
                    nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude1.doubleValue(), longitude1.doubleValue());
                    // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                    if (nearByBar != null) {

                        Intent shopIntent = new Intent(MainPage.this, Wineclass.class);
                        shopIntent.putExtra("city", city);
                        startActivity(shopIntent);
                        overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                    } else {
                        showAlertDialog(MainPage.this, "Alert", "No Data Found for Current Location", false);
                    }
                } else {
                    JSONArray nearByBar = null;


                    //calling the API to liquoar for beer or other brand for current location when user doesn't enter the city instead just he touch the wine and beer shop

                    nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude, longitude);
                    // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                    if (nearByBar != null) {

                        Intent shopIntent = new Intent(MainPage.this, Wineclass.class);
                        // shopIntent.putExtra("city",city);
                        startActivity(shopIntent);
                    } else {
                        showAlertDialog(MainPage.this, "Alert", "No Data Found for Current Location", false);
                    }
                }
            }
        });

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
            aAdapter = new ArrayAdapter<String>(MainPage.this,android.R.layout.simple_list_item_1,android.R.id.text1,data); //simple_spinner_item to make very smaller text // simple_list_item_1 for medium
            autoComplete.setAdapter(aAdapter);
            data=null;
            suggest.clear();

            aAdapter.notifyDataSetChanged();

        }
    }



    public void slideToLeft(View view){
        TranslateAnimation animate = new TranslateAnimation(0,-view.getWidth(),0,0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

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

/*
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            //tvAddress.setText(locationAddress);
        }
    }*/

    // method to show alerts for gps enabling

    public  void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainPage.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainPage.this.startActivity(intent);
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


}

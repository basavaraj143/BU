package com.example.appsriv_02.loginwithfacebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cityautocomplete.PlaceJSONParser;

public class ResultActivity extends Activity  implements  AdapterView.OnItemSelectedListener
{
    AutoCompleteTextView brandAutocomplete;
    boolean selectedText = false;
    public ArrayAdapter<String> aAdapter;
    public String data;
    public List<String> suggest;
    String place;
    static TextView atv_places;
    GPSTracker gps;
    String city;
    String locality;
    Boolean isInternetPresent = false;
    InternetConnectionDetector cd;
    static Spinner spinner1;
    static Spinner spinner2;
    static Spinner spinner3;
    List<String> listplace = new ArrayList<String>();
    List<String> list = new ArrayList<String>();
    String location;
    String brand;
    List<String> brands=new ArrayList<String>();
    ParserTask parserTask;
    AutoCompleteTextView atvPlaces;
    private Filter filter;
    private static final int ADDRESS_TRESHOLD = 10;
    PlacesTask placesTask;
    String sec_dropDown_value;
    int subBrandPosition;
    String mainbrand;
    boolean brandflag=true ;
    String sublocality;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        atv_places = (TextView)findViewById(R.id.atv_places);
        LinearLayout parent = (LinearLayout)findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ResultActivity.this.overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
            }
        });
        cd = new InternetConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            if (Place_Activity.city!=null)
            {
                atv_places.setText(Place_Activity.city);
                locality=bundle.getString("locality");
                brand=bundle.getString("brand");
                subBrandPosition=bundle.getInt("subBrandPosition");
                mainbrand=bundle.getString("mainbrand");
                sublocality=bundle.getString("sublocality");
            }
            else
            {
                place = bundle.getString("city");
                locality=bundle.getString("locality");
                brand=bundle.getString("brand");
                subBrandPosition=bundle.getInt("subBrandPosition");
                mainbrand=bundle.getString("mainbrand");
                sublocality=bundle.getString("sublocality");
                atv_places.setText(place);

            }
        }
       /* atv_places.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isInternetPresent) {
                Intent intent = new Intent(ResultActivity.this,Place_Activity.class);
                intent.putExtra("locality",locality);
                startActivity(intent);
                }
                else
                {
                    Toast.makeText(ResultActivity.this,"Please check your internet connectivity ",Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        Log.i("bottoms", "locality in ResultActivity activity " + locality);
        LinearLayout top =(LinearLayout)findViewById(R.id.city);
        /*top.setOnClickListener(new View.OnClickListener()
        {


            @Override
            public void onClick(View v)
            {
                if (isInternetPresent) {
                    Intent intent = new Intent(ResultActivity.this, Place_Activity.class);
                    intent.putExtra("locality", locality);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(ResultActivity.this,"Please check your internet connectivity ",Toast.LENGTH_SHORT).show();
                }
            }
        });*/



        atvPlaces = (AutoCompleteTextView) findViewById(R.id.place);
        atvPlaces.setText(sublocality);
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
        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                placesTask = new PlacesTask();
                /*if (location.equalsIgnoreCase("Change city"))
                {
                    Toast.makeText(ResultActivity.this,"Please select city",Toast.LENGTH_SHORT).show();
                }
                else {*/
                    placesTask.execute(s.toString());
                //}
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


        atvPlaces.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


     //   brandAutocomplete = (AutoCompleteTextView)findViewById(R.id.brandAutocomplete);
      //  brandAutocomplete.setDropDownBackgroundResource(R.drawable.my_shape1);

       /* brandAutocomplete.addTextChangedListener(new TextWatcher()
        {

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String newText = s.toString();

                if (isInternetPresent) {
                    new GetBrand().execute(newText);
                    selectedText = false;
                }
                else
                {
                    Toast.makeText(ResultActivity.this,"Please check your internet connectivity ",Toast.LENGTH_LONG).show();
                }

            }
            public void afterTextChanged(Editable editable) {


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


        });
*/

        spinner1 = (Spinner) findViewById(R.id.spinner1);

        if (mainbrand!=null) {
            if (mainbrand.equalsIgnoreCase("beer")) {
                list.add("Beer");
                list.add("Whiskey");
                list.add("Rum");
                list.add("Vodka");
                list.add("Cocktail");
            } else if (mainbrand.equalsIgnoreCase("Whiskey")) {
                list.add("Whiskey");
                list.add("Beer");
                list.add("Rum");
                list.add("Vodka");
                list.add("Cocktail");
            } else if (mainbrand.equalsIgnoreCase("Rum")) {
                list.add("Rum");
                list.add("Vodka");
                list.add("Beer");
                list.add("Whiskey");
                list.add("Cocktail");
            } else if (mainbrand.equalsIgnoreCase("vodka")) {
                list.add("Vodka");
                list.add("Beer");
                list.add("Whiskey");
                list.add("Rum");
                list.add("Cocktail");
            }
            else if (mainbrand.equalsIgnoreCase("Cocktail")) {
                list.add("Cocktail");
                list.add("Vodka");
                list.add("Beer");
                list.add("Whiskey");
                list.add("Rum");

            }
            else
            {
                list.add("Beer");
                list.add("Whiskey");
                list.add("Rum");
                list.add("Vodka");
                list.add("Cocktail");
            }
        }
        else
        {
            list.add("Beer");
            list.add("Whiskey");
            list.add("Rum");
            list.add("Vodka");
        }

        spinner2 = (Spinner) findViewById(R.id.spinner2);
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
            } else
            {
               // listplace.add(locality);
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,list);

        ArrayAdapter<String> placeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,listplace);

       // placeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
       // dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);



        spinner2.setAdapter(placeAdapter);
        spinner1.setAdapter(dataAdapter);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);





/*
        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                selectedText = true;
                String foundItem = arg0.getItemAtPosition(arg2).toString();
                Log.i("bottoms", foundItem);


                hideKeyboard();


                //  slideToLeft(serachButton);

                city = atvPlaces.getText().toString();
                 String beer = spinner3.getSelectedItem().toString();
                if (city.equalsIgnoreCase("Current Location"))
                {
                    city=" ";
                }
                else
                {
                    city=atvPlaces.getText().toString();
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
                        (beer.length() == 0 || beer.equals(" "))) {
                    showAlertDialog(ResultActivity.this, "Alert",
                            "Please enter city & liquor.", false);

                } else
                {
                    // atvPlaces.requestFocus();
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    //latlng = gpsTracker.getLatLOng(city);
                    JSONObject jsonObject= GPSTracker.getLocationInfo(city);
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
                        Toast.makeText(ResultActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                    }



                    if (jsonObject.length()!=0)
                    {
                        //  Double latitude = (Double) latlng.get(0);
                        //   Double longitude = (Double) latlng.get(1);


                        // check if GPS enabled
                       */
/* if (gps.canGetLocation())
                        {*//*

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
                                            if (isInternetPresent) {
                                                Intent barList = new Intent(ResultActivity.this, NewExpandaList.class);
                                                barList.putExtra("brand", beer);
                                                barList.putExtra("place", city + " " + sec_dropDown_value);
                                                barList.putExtra("locality", sec_dropDown_value);
                                                barList.putExtra("subBrandPosition",subBrandPosition);
                                                barList.putExtra("mainbrand",spinner1.getSelectedItem().toString());
                                                barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(barList);
                                                // overridePendingTransition(R.anim.in_animation, R.anim.out_animation);

                                            }
                                            else
                                            {
                                                Toast.makeText(ResultActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                        else
                                        {

                                            if (isInternetPresent)
                                            {
                                                Intent barList = new Intent(ResultActivity.this, NonBeerExpandList.class);
                                                barList.putExtra("brand", beer);
                                                barList.putExtra("place", city + " " + sec_dropDown_value);
                                                barList.putExtra("subBrandPosition",spinner3.getSelectedItemPosition());
                                                barList.putExtra("mainbrand",spinner1.getSelectedItem().toString());
                                                barList.putExtra("locality", sec_dropDown_value);
                                                barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(barList);
                                            }

                                            else

                                            {
                                                Toast.makeText(ResultActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                    } catch (JSONException j) {
                                        j.printStackTrace();

                                    }

                                } else {
                                    showAlertDialog(ResultActivity.this, "Alert",
                                            "No data found please try different liquor.", false);

                                }
                            }
                            else
                            {
                                showAlertDialog(ResultActivity.this, "Alert",
                                        "No data found please try different place.", false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        showAlertDialog(ResultActivity.this,"Alert","You seem to have entered invalid address. Please check",false );
                    }
                }
            }
        });
*/

        Button searchLiquor=(Button)findViewById(R.id.searchbutton);

        searchLiquor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isInternetPresent = cd.isConnectingToInternet();
                Toast.makeText(ResultActivity.this,"Please wait Loading",Toast.LENGTH_LONG).show();
                city = atvPlaces.getText().toString();
                city.replaceAll("[^a-zA-Z0-9]", "");
                String beer = spinner3.getSelectedItem().toString();
                if (city.equalsIgnoreCase("Current Location"))
                {
                    city=" ";
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
                    showAlertDialog(ResultActivity.this, "Alert",
                            "Please enter locality.", false);

                } else
                {
                    // atvPlaces.requestFocus();
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    //latlng = gpsTracker.getLatLOng(city);
                    JSONObject jsonObject= GPSTracker.getLocationInfo(city+" "+spinner2.getSelectedItem().toString());
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
                        Toast.makeText(ResultActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                    }



                    if (jsonObject.length()!=0)
                    {
                        //  Double latitude = (Double) latlng.get(0);
                        //   Double longitude = (Double) latlng.get(1);


                        // check if GPS enabled
                       /* if (gps.canGetLocation())
                        {*/
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
                                            if (isInternetPresent) {
                                                Intent barList = new Intent(ResultActivity.this, NewExpandaList.class);
                                                barList.putExtra("brand", beer);
                                                barList.putExtra("place", city + " " + sec_dropDown_value);
                                                barList.putExtra("locality", sec_dropDown_value);
                                                barList.putExtra("subBrandPosition",spinner3.getSelectedItemPosition());
                                                barList.putExtra("sublocality",atvPlaces.getText().toString());
                                                barList.putExtra("mainbrand",spinner1.getSelectedItem().toString());
                                                barList.putExtra("spinner1_pos",spinner1.getSelectedItemPosition());
                                                barList.putExtra("spinner2_pos",spinner2.getSelectedItemPosition());
                                                barList.putExtra("spinner3_pos",spinner3.getSelectedItemPosition());
                                                barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(barList);
                                                // overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                                            }
                                            else
                                            {
                                                Toast.makeText(ResultActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if(checkLiquor.equalsIgnoreCase("Cocktails"))
                                        {

                                            if (isInternetPresent)
                                            {
                                                Intent barList = new Intent(ResultActivity.this, CocktailsExpandaList.class);
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

                                            else

                                            {
                                                Toast.makeText(ResultActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                        else
                                        {

                                            if (isInternetPresent)
                                            {
                                                Intent barList = new Intent(ResultActivity.this, NonBeerExpandList.class);
                                                barList.putExtra("brand", beer);
                                                barList.putExtra("place", city + " " + sec_dropDown_value);
                                                barList.putExtra("subBrandPosition",spinner3.getSelectedItemPosition());
                                                barList.putExtra("mainbrand",spinner1.getSelectedItem().toString());
                                                barList.putExtra("locality", sec_dropDown_value);
                                                barList.putExtra("sublocality",atvPlaces.getText().toString());
                                                barList.putExtra("spinner1_pos",spinner1.getSelectedItemPosition());
                                                barList.putExtra("spinner2_pos",spinner2.getSelectedItemPosition());
                                                barList.putExtra("spinner3_pos",spinner3.getSelectedItemPosition());
                                                barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(barList);
                                            }

                                            else

                                            {
                                                Toast.makeText(ResultActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                    } catch (JSONException j) {
                                        j.printStackTrace();

                                    }

                                } else {
                                    showAlertDialog(ResultActivity.this, "Alert",
                                            "No data found please try different liquor.", false);

                                }
                            }
                            else
                            {
                                showAlertDialog(ResultActivity.this, "Alert",
                                        "No data found please try different place.", false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        showAlertDialog(ResultActivity.this,"Alert","You seem to have entered invalid address. Please check",false );
                    }
                }
            }
        });


        /*brandAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedText = true;
                String foundItem = arg0.getItemAtPosition(arg2).toString();
                // Log.i(TAG, foundItem);


                hideKeyboard();

                city = atv_places.getText().toString()*//*.substring(13,atv_places.getText().toString().length()-1)*//*;
                final String beer = brandAutocomplete.getText().toString();
                Context mContext = null;

                //to check both edit text should not be the empty
                if ((atv_places.getText().toString().length() == 0 || city.equals(" ") || atv_places.getText().toString() == null) ||
                        (beer.length() == 0 || beer.equals(" ")) || brandAutocomplete.getText().toString() == null) {
                    showAlertDialog(ResultActivity.this, "Alert",
                            "Please enter city & liquor.", false);

                } else {
                    // atvPlaces.requestFocus();

                    InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(mContext);
                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    latlng = gpsTracker.getLatLOng(city);
                    JSONObject jsonObject = GPSTracker.getLocationInfo(city);
                    Double latitude = null;// (Double) latlng.get(0);
                    Double longitude = null;// (Double) latlng.get(1);

                    for (int m = 0; m < jsonObject.length(); m++) {
                        try {
                            latitude = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            longitude = jsonObject.getJSONArray("results").getJSONObject(m).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        } catch (JSONException ex) {

                        }

                    }
                    *//*if (latlng.size()>0) {*//*

                    // check if GPS enabled

                    try {

                        JSONArray nearByBar = NearByBar.getNearByBar(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue());

                        if (nearByBar != null) {
                            //Calling liquor  api in for the entered city place lat and long and for particular brand  which returns the JSON
                            nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), beer);
                            String checkLiquor = null;
                            if (nearByBar != null) {
                                try {
                                    for (int j = 0; j < nearByBar.length(); j++) {
                                        checkLiquor = nearByBar.getJSONObject(j).getString("liquor_type");
                                    }

                                    if (checkLiquor.equalsIgnoreCase("beer")) {
                                                    Intent barList = new Intent(ResultActivity.this, NewExpandaList.class);
                                                    barList.putExtra("brand", beer);
                                                    barList.putExtra("place", city);
                                                    barList.putExtra("locality", locality);
                                                    //  barList.putExtra("switchstate", switchStateOn);
                                                    barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(barList);
                                                    overridePendingTransition(R.anim.in_animation, R.anim.out_animation);



                                    } else

                                    {
                                                    Intent barList = new Intent(ResultActivity.this, NonBeerExpandList.class);
                                                    barList.putExtra("brand", beer);
                                                    barList.putExtra("place", city);
                                                    barList.putExtra("locality", locality);
                                                    // barList.putExtra("switchstate", switchStateOn);
                                                    barList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(barList);
                                                    overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                                    }

                                } catch (JSONException j) {
                                    j.printStackTrace();

                                }
                            } else {
                                showAlertDialog(ResultActivity.this, "Alert",
                                        "No data found please try different liquor.", false);

                            }
                        } else {
                            showAlertDialog(ResultActivity.this, "Alert",
                                    "No data found please try different place.", false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                   *//* }
                    else
                    {
                        showAlertDialog(Homescreen.this,"Alert","You seem to have entered invalid address. Please check",false);
                    }*//*
                }
            }
        });


        brandAutocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
*/


       /* final ImageView close = (ImageView)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                ResultActivity.this.overridePendingTransition(R.anim.in_animation,R.anim.out_animation);

            }

        });
*/
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        Spinner spinner = (Spinner)parent;
        Spinner spinner2 = (Spinner)parent;
        if(spinner.getId() == R.id.spinner1)
        {

            AsyncCallWS asyncCallWS = new AsyncCallWS();


            //Toast.makeText(this, "Your choose :" + list.get(position), Toast.LENGTH_SHORT).show();
            Log.i("ResultActivity", "Brandlist Cleared");
            String liquor = list.get(position);
            if (isInternetPresent)
            {

                asyncCallWS.execute(liquor);
            }
            else
            {
                showAlertDialog(ResultActivity.this,"No Internet Connection","Please Try again.", false);
            }

        }
        if(spinner2.getId() == R.id.spinner2)
        {
            // Toast.makeText(this, "Your choose :" + listplace.get(position),Toast.LENGTH_SHORT).show();
            location=spinner2.getSelectedItem().toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class GetBrand extends AsyncTask<String,String,String>
    {

        String liquor;
        String[] s = new String[10000];
        ArrayList<String> strings = new ArrayList<String>();

        @Override
        protected String doInBackground(String... key) {

            String newText = key[0];
            newText = newText.trim();
            newText = newText.replace(" ", "+");
            HttpClient hClient = null;
            HttpGet hGet = null;
            try {
                hClient = new DefaultHttpClient();
                if (newText.length() != 0)
                {
                    hGet = new HttpGet("http://demos.dignitasdigital.com/bottomzup/liquors.php?find=" + newText);//url to fetch the liquor
                    ResponseHandler<String> rHandler = new BasicResponseHandler();
                    data = hClient.execute(hGet, rHandler);
                    suggest = new ArrayList<String>();
                    JSONArray jArray = new JSONArray(data);
                    // JSONObject mJsonObject = newlayout JSONObject();
                    for (int i = 0; i < jArray.length(); i++) {
                        liquor = jArray.getJSONObject(i).getString("liquors");
                        s = new String[jArray.length()];
                        s[i] = jArray.getJSONObject(i).getString("liquors");
                        suggest.add(liquor);

                    }
                } else
                {
                    suggest.clear();
                }
            } catch (Exception e) {
                Log.w("Error", e.getMessage());
            } finally {
                if (hClient != null) {
                    if (newText.length() <= 0 || newText.length() < 1) {
//                        suggest.clear();
                    }

                    hClient.getConnectionManager().shutdown();
                }


            }
            return null;

        }

        public void onPostExecute(String result) {
            String[] data = suggest.toArray(new String[suggest.size()]);
            //String[] data1=s;
           // Log.i("liquors", "the data is " + data);
            // SimpleAdapter simpleAdapter = newlayout SimpleAdapter(getApplicationContext(), result, android.R.layout.simple_list_item_1,data);

            aAdapter = new ArrayAdapter<String>(ResultActivity.this, android.R.layout.simple_dropdown_item_1line, data);
            // autoComplete.setAdapter(aAdapter);
            brandAutocomplete.setAdapter(aAdapter);
            suggest.clear();
            aAdapter.notifyDataSetChanged();
            /*  if(this.getStatus().equals(AsyncTask.Status.RUNNING))
            {
                this.cancel(true);
            }*/
            //  parserTask = newlayout ParserTask();
            /*parserTaskliquor = newlayout ParserTaskFOrLiquor();

            // Starting Parsing the JSON string returned by Web Service
           // parserTask.execute(result); =newlayout

            // Starting Parsing the JSON string returned by Web Service
                parserTaskliquor.execute(result);*/
        }
    }

    private class AsyncCallWS extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Log.i("in ResultActivity", "onPreExecute");
            try {
                progressDialog = ProgressDialog.show(ResultActivity.this, "Please wait", "Loading...", true);
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

                Log.i("DUmmy", "doInBackground");
                String s1="http://demos.dignitasdigital.com/bottomzup/get_brandmaster_for_category.php?category="+s;

                HttpParams httpParameters = new BasicHttpParams();
                int timeout1 = 1000*10;
                int timeout2 = 1000*10;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeout1);
                HttpConnectionParams.setSoTimeout(httpParameters, timeout2);
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
                    Toast.makeText(ResultActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                    finish(); // if your are within activity class, otherwise call finish on your activity

                } catch (URISyntaxException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    Toast.makeText(ResultActivity.this,"Connection time out please try again",Toast.LENGTH_SHORT).show();
                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(new StandardExceptionParser(ResultActivity.this, null).getDescription(Thread.currentThread().getName(), e1))
                            .setFatal(false)
                            .build());
                } catch (ClientProtocolException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(ResultActivity.this,"Connection time out please try again",Toast.LENGTH_SHORT).show();
                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(new StandardExceptionParser(ResultActivity.this, null).getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build());
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(ResultActivity.this,"Connection time out please try again",Toast.LENGTH_SHORT).show();
                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(new StandardExceptionParser(ResultActivity.this, null).getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ResultActivity.this,"Connection time out please try again",Toast.LENGTH_SHORT).show();
                    Tracker t = ((GoogleAnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(new StandardExceptionParser(ResultActivity.this, null).getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build());
                }
                JSONObject myJsonObject = new JSONObject(jsonString);



                // JSONObject myJsonObject = getLiquors(Homescreen.this, s);

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
                        .setDescription(new StandardExceptionParser(ResultActivity.this, null).getDescription(Thread.currentThread().getName(), e))
                        .setFatal(false)
                        .build());
            }



            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            ArrayAdapter<String> brandadapter = new ArrayAdapter<String>(ResultActivity.this, android.R.layout.simple_dropdown_item_1line,brands);
            brandadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

            spinner3.setAdapter(brandadapter);
            if (brandflag)
            {
                brandflag=false;
                spinner3.setSelection(subBrandPosition);
            }
            else
            {
                spinner3.setSelection(0);
            }
            Log.i("in ResultActivity", "onPostExecute");
            progressDialog.dismiss();
            //  Toast.makeText(MainActivity.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }

    }

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
            Log.i("bottoms","place result "+result);

            String[] from = new String[] { "description"};

            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_dropdown_item_1line, from,to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }
}

//UA-73017734-1
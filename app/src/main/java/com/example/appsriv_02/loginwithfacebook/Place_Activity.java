package com.example.appsriv_02.loginwithfacebook;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cityautocomplete.PlaceJSONParser;

public class Place_Activity extends Activity {

    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    private Filter filter;
    boolean selectedText = false;
    public static String city;
    private static final int ADDRESS_TRESHOLD = 10;
    String locality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_);


        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            locality=bundle.getString("locality");
        }


        final ImageView close = (ImageView)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();


            }

        });


        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        atvPlaces.setDropDownBackgroundResource(R.drawable.my_shape1);
        atvPlaces.setThreshold(1);


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

        Log.i("bottoms","locality in place activity  "+locality);


        atvPlaces.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                selectedText = true;
                String foundItem = arg0.getItemAtPosition(arg2).toString();
               // ResultActivity.atv_places.setText(foundItem);
                city= atvPlaces.getText().toString();
              //  ResultActivity.atv_places.setText(city);
                hideKeyboard(arg1);
                Intent intent = new Intent(Place_Activity.this,ResultActivity.class);
                intent.putExtra("place", foundItem);
                intent.putExtra("locality",locality);
                startActivity(intent);
                finish();
            }
        });


    }




    public void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
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
           // Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>{


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
                input=input.replaceAll(" ","%20");
                input = URLEncoder.encode(place[0],"utf-8");
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
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyC45IqTyfdeO5SzyLDGAVWiwADSSv70S6g&input="+ "{" + locality + "}"  + input + "&types=(regions)&components=country:IN";
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
                Log.d("Exception",e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_spinner_dropdown_item, from, to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


}
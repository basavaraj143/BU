package com.example.appsriv_02.loginwithfacebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NonBeerExpandList extends Activity
{

    private ExpandListAdapterForNonBeer ExpAdapter;
    static  ArrayList<Group> ExpListItems;
    public static ExpandableListView ExpandList;

    //group and child
    public static String nonbeer="";
    static String activeColumn="";
   // String switchStateOnFOrNonBeer="off";
   String locality;


    ArrayList<Group> list = new ArrayList<Group>();

    double   latitude_dist=0;
    double longitude_dist=0;

    String reversed;

    Boolean isInternetPresent = false;
    InternetConnectionDetector cd;

    ArrayList<Child> ch_list=null;
    private static final String TAG="bottoms";
    public static ArrayList<String> res_lat = new ArrayList<String>();
    public static ArrayList<String> res_long= new ArrayList<>();
    ArrayList<String> distance= new ArrayList<>();
    ArrayList<String> res_name= new ArrayList<>();
    ArrayList<String> brand_name= new ArrayList<>();
    ArrayList<String> maxPrice= new ArrayList<>();
    ArrayList<String> min_price= new ArrayList<>();
    ArrayList<String> res_namelf= new ArrayList<>();
    ArrayList<String> min_pricelf= new ArrayList<>();
    public static int count = 0;


    static ArrayList<String> happypyHourStart = new ArrayList<>();
    static ArrayList<String> happypyHourEnd = new ArrayList<>();
    static ArrayList<String> isHappyHour = new ArrayList<>();


    boolean selectedText = false;
    public static boolean flag=false;
    public static boolean lkflag=false;
   // PlacesTask placesTask;
   // ParserTask parserTask;
    //AutoCompleteTextView atvPlaces;
    //public AutoCompleteTextView autoComplete;
    public ArrayAdapter<String> aAdapter;
    public String data;
    public List<String> suggest;
    String city=null;
    private static final int ADDRESS_TRESHOLD = 10;
    Filter filter;

    ToggleButton aswich;
    ArrayList<Double> reslat= new ArrayList<>();
    ArrayList<Double> reslong= new ArrayList<>();
    static ArrayList<Float> dist_fromFormula= new ArrayList<>();

    private String brand;
    private String place;
    TextView pint;
    TextView dist,resultplace;
    private ImageButton lookfurther;

    Double latitude = null;
    Double longitude = null;

    public static String radius="two";

    GPSTracker gps;

    ArrayList<String> placeList = new ArrayList<>();

    static Tracker mTracker;
    private String name = "Non Beer Result screen";
    private static final String TAGG = NewExpandaList.class.getSimpleName();
    String mainbrand;
    int subBrandPosition;
    String sublocality;
    ImageView shadow;


    int spinner1_pos;
    int spinner2_pos;
    int spinner3_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.non_beer_expandablelistlayout);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build());

        ExpandList = (ExpandableListView) findViewById(R.id.exp_list1);
        resultplace =(TextView)findViewById(R.id.place);
        shadow =(ImageView)findViewById(R.id.shadow);
        cd = new InternetConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //to fetch the current location
        gps = new GPSTracker(NonBeerExpandList.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude_dist = gps.getLatitude();
            longitude_dist = gps.getLongitude();


        } else {

            gps.showSettingsAlert();
        }
        GoogleAnalyticsApplication application = (GoogleAnalyticsApplication) getApplicationContext();
        mTracker = application.getDefaultTracker();
        lookfurther = (ImageButton) findViewById(R.id.lookfurther);
      //  ImageButton delas_near_you = (ImageButton) findViewById(R.id.deals);
        dist = (TextView) findViewById(R.id.button3);
        ImageButton winebutton = (ImageButton) findViewById(R.id.winebutton);
        Group g = new Group();

        if (isInternetPresent) {

            AsyncCallWS asyncCallWS = new AsyncCallWS();
            asyncCallWS.execute();
        }
        else
        {
            Toast.makeText(NonBeerExpandList.this,"Please Check your Internet Connectivity",Toast.LENGTH_SHORT).show();
        }
        pint = (TextView) findViewById(R.id.button);
        // Typeface tf =Typeface.createFromAsset(getAssets(),"fonts/halvica.ttf");
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO_REGULAR.OTF");
        pint.setTypeface(tf, Typeface.NORMAL);
        dist.setTypeface(tf, Typeface.NORMAL);
       // atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        aswich = (ToggleButton) findViewById(R.id.toggleButton1);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            brand = bundle.getString("brand");
            place = bundle.getString("place");
            locality=bundle.getString("locality");
            mainbrand=bundle.getString("mainbrand");
            subBrandPosition=bundle.getInt("subBrandPosition");
            sublocality=bundle.getString("sublocality");
            spinner1_pos=bundle.getInt("spinner1_pos");
            spinner2_pos=bundle.getInt("spinner2_pos");
            spinner3_pos=bundle.getInt("spinner3_pos");

        }
        Log.i("bottoms", "locality in NonBeerExpandList activity " + locality);
        final TextView search = (TextView) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v)
            {
                if (isInternetPresent)
                {
                Intent intent = new Intent(NonBeerExpandList.this, ResultActivity.class);
                // overridePendingTransition(R.anim.in_animation,R.anim.out_animation);
                intent.putExtra("city", place);
                intent.putExtra("locality",locality);
                intent.putExtra("brand",brand);
                intent.putExtra("mainbrand",mainbrand);
                intent.putExtra("sublocality",sublocality);
                intent.putExtra("subBrandPosition",subBrandPosition);
                startActivity(intent);
                overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                }
                else
                {
                    Toast.makeText(NonBeerExpandList.this,"Please Check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                }

            }
        });


        if (NewExpandaList.switchStateOn.equalsIgnoreCase("on")) {
            aswich.setChecked(true);

        } else {
            aswich.setChecked(false);
        }


        winebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {

                if (isInternetPresent)
                {
                    // city = atvPlaces.getText().toString();
                    if (place.length() != 0)
                    {
                        //InternetConnectionDetector connectionDetector = newlayout InternetConnectionDetector(getApplicationContext());
                        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                        ArrayList latlng = new ArrayList();
                        //calling getLatLong method to get the lat and long of the entered city
                        latlng = gpsTracker.getLatLOng(place);
                        //   Double latitude1 = (Double) latlng.get(0);
                        //   Double longitude1 = (Double) latlng.get(1);

                        JSONObject jsonObject = GPSTracker.getLocationInfo(place);
                        if (jsonObject.length() != 0) {
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
                            //calling the API to liquoar for beer or other brand for entered city in city textbox
                            nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude1.doubleValue(), longitude1.doubleValue());

                            JSONArray jarr = NearByBar.BeerAndWineShop(getApplicationContext(), latitude1.doubleValue(), longitude1.doubleValue(), 2);

                            if (jarr != null) {

                                Intent shopIntent = new Intent(NonBeerExpandList.this, Wineclass.class);
                                shopIntent.putExtra("city", place);
                                startActivity(shopIntent);
                            } else {
                                showAlertDialog(NonBeerExpandList.this, "Alert", "No Data Found for Current Location", false);
                            }
                        } else {
                            showAlertDialog(NonBeerExpandList.this, "Alert", "No data found for your location", false);
                        }
                    } else {
                        JSONArray nearByBar = null;


                        //calling the API to liquoar for beer or other brand for current location when user doesn't enter the city instead just he touch the wine and beer shop

                        nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude, longitude);
                        // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                        if (nearByBar != null) {

                            Intent shopIntent = new Intent(NonBeerExpandList.this, Wineclass.class);
                            // shopIntent.putExtra("city",city);
                            startActivity(shopIntent);
                        } else {
                            showAlertDialog(NonBeerExpandList.this, "Alert", "No Data Found for Current Location", false);
                        }
                    }
                }
            else
            {
                Toast.makeText(NonBeerExpandList.this,"Please Check your Internet Connectivity",Toast.LENGTH_SHORT).show();
            }

            }
            });

            aswich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

            {
                public void onCheckedChanged (CompoundButton buttonView,boolean isChecked){
                if (isChecked) {
                    NewExpandaList.switchStateOn = "on";
                    for (int i = ExpListItems.size() - 1; i >= 0; i--) {
                        if (ExpListItems.get(i).getIsHappyHours().equalsIgnoreCase("no")) {
                            ExpListItems.remove(i);
                        } else {
                            // ExpListItems.add(list.get(i));
                        }
                    }
                    ExpAdapter.notifyDataSetChanged();
                } else
                {
                    if (isInternetPresent)
                    {
                        new HappyHourOff().execute();
                    }
                    else
                    {
                        Toast.makeText(NonBeerExpandList.this,"Please Check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                    }


                }
            }
            }

            );


            MainPage mainPage = new MainPage();

            //  hideSoftKeyboard(this);
            mainPage.hideKeyboard();


            pint.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                nonbeer = "exceptBeer";
                Group g = new Group();
                list = g.bottleSort(list);
                ExpAdapter.notifyDataSetChanged();
                for (int i = 0; i < list.size(); i++) {
                    ExpandList.collapseGroup(i);
                    ;
                    pint.setBackgroundResource(R.drawable.yellowtab);

                }
            }
            }

            );


            gps=new

            GPSTracker(NonBeerExpandList.this);
            // check if GPS enabled
            if(gps.canGetLocation())//if gps is enabled get the lat and long

            {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }

            else

            {
                gps.showSettingsAlert();
            }

            StringBuilder stringBuilder = new StringBuilder(brand);
            reversed=stringBuilder.reverse().

            toString();

            Log.i(TAG,"reversed string "+reversed);
            if(reversed.length()>6)

            {
                if (reversed.charAt(5) == '6') {

                    activeColumn = "bottle";
                } else {
                    activeColumn = "pint";
                }
            }

            else

            {
                activeColumn = "pint";
            }


            resultplace.setText(brand.substring(0,1).toUpperCase()+brand.substring(1)+" Near "+place);

            resultplace.setOnClickListener(new View.OnClickListener()

            {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick (View v)
                {

                    if (isInternetPresent) {
                        Intent intent = new Intent(NonBeerExpandList.this, ResultActivity.class);
                        // overridePendingTransition(R.anim.in_animation,R.anim.out_animation);
                        intent.putExtra("city", place);
                        intent.putExtra("locality", locality);
                        intent.putExtra("brand",brand);
                        intent.putExtra("mainbrand",mainbrand);
                        intent.putExtra("sublocality",sublocality);
                        intent.putExtra("subBrandPosition",subBrandPosition);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                    }
                    else
                    {
                        Toast.makeText(NonBeerExpandList.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                    }


            }
        });


    }

    protected void onResume() {
        super.onResume();
        Log.i(TAGG, "Setting screen name: " + name);
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void look_further_popup(final Context context)
    {

        LayoutInflater layoutInflater = (LayoutInflater) NonBeerExpandList.this.getSystemService(context.LAYOUT_INFLATER_SERVICE);


        final View popupView = layoutInflater.inflate(R.layout.look_further_popup, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);


        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.isOutsideTouchable();


        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        ArrayList latlng = new ArrayList();
        latlng = gpsTracker.getLatLOng(place);
        //final Double latitude = (Double) latlng.get(0);
      //  final Double longitude = (Double) latlng.get(1);
        Log.i(TAG, "lattitude and longitude" + latlng + "  " + latitude + " " + longitude);

        JSONObject jsonObject= GPSTracker.getLocationInfo(place);
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



        ImageView sevenkm = (ImageView) popupView.findViewById(R.id.sevenkm);
        ImageView fivekm = (ImageView) popupView.findViewById(R.id.fivekm);
        ImageView twokm = (ImageView) popupView.findViewById(R.id.twokm);

        if (radius.equalsIgnoreCase("two"))
        {
            twokm.setVisibility(View.GONE);
        }
        else if (radius.equalsIgnoreCase("five"))
        {
            fivekm.setVisibility(View.GONE);
        }
        else if (radius.equalsIgnoreCase("seven"))
        {
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




        sevenkm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NonBeerExpandList.this, "7 km restaurants are showing", Toast.LENGTH_SHORT).show();
                radius = "seven";

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray nearByBar = null;
                        lookfurther.setBackgroundResource(R.drawable.radius_sevenkm_press);
                        try {
                            double km = 7;
                            nearByBar = NearByBar.getNearByBarnewLookFurther(getApplicationContext(), latitude, longitude, km, brand);
                            if (nearByBar != null)
                            {
                                if (NewExpandaList.switchStateOn.equalsIgnoreCase("off")) {

                                    for (int j = 0; j < nearByBar.length(); j++) {
                                        happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                        happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                        isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));


                                        if (!res_name.contains(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"))) {
                                            Group gru1 = new Group();
                                            flag = true;
                                            lkflag = true;
                                            gru1.setCheckRestaurants(true);
                                            res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            res_namelf.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            gru1.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                            gru1.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                            gru1.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                            gru1.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                            gru1.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                            gru1.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                            res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                            gru1.setPrice(/*"₹" +*/ nearByBar.getJSONObject(j).getString("alcohol_min_price"));
                                            reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                            reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                            gru1.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                            gru1.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                            gru1.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                            gru1.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                            gru1.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                            gru1.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                            dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                            gru1.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                            Child ch = null;
                                            ch_list = new ArrayList<Child>();
                                            JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                            for (int i = 0; i < jArray.length(); i++) {
                                                ch = new Child();
                                                ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                ch.setPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                gru1.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                gru1.setPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                ch_list.add(ch);
                                            }

                                            gru1.setItems(ch_list);
                                            list.add(gru1);
                                            Group g = new Group();
                                            g.bottleSort(list);
                                        }
                                    }
                                } else if (NewExpandaList.switchStateOn.equalsIgnoreCase("on")) {
                                    for (int j = 0; j < nearByBar.length(); j++) {
                                        happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                        happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                        isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));

                                        if (nearByBar.getJSONObject(j).getString("is_happy_hour").equalsIgnoreCase("yes")) {

                                            if (!res_name.contains(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"))) {
                                                Group gru1 = new Group();
                                                flag = true;
                                                lkflag = true;
                                                gru1.setCheckRestaurants(true);
                                                res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                res_namelf.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                gru1.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                                gru1.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                                gru1.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                                gru1.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                                gru1.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                gru1.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                                gru1.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                                gru1.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                                gru1.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                                gru1.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                                res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                gru1.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                gru1.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                gru1.setPrice(/*"₹" +*/ nearByBar.getJSONObject(j).getString("alcohol_min_price"));
                                                reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                                reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                                dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                                gru1.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                                Child ch = null;
                                                ch_list = new ArrayList<Child>();
                                                JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                                for (int i = 0; i < jArray.length(); i++) {
                                                    ch = new Child();
                                                    ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                    ch.setPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                    ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                    gru1.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                    gru1.setPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                    ch_list.add(ch);
                                                }

                                                gru1.setItems(ch_list);
                                                list.add(gru1);
                                                Group g = new Group();
                                                g.bottleSort(list);
                                            }
                                        }
                                    }
                                }
                            } else {
                                showAlertDialog(NonBeerExpandList.this, "Alert", "No Data Found within 7 km", false);
                            }
                        } catch (Exception e) {

                        }

                        ExpAdapter.notifyDataSetChanged();
                    }
                });
                if (lkflag)
                {
                    lkflag=false;
                }
                else
                {
                    showAlertDialog(NonBeerExpandList.this,"Alert","No Restaurants found in 7 Km",false);

                }
                ExpAdapter.notifyDataSetChanged();

                popupWindow.dismiss();
            }
        });

        fivekm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NonBeerExpandList.this, "5 km restaurants are showing", Toast.LENGTH_SHORT).show();
                lookfurther.setBackgroundResource(R.drawable.radius_fivekm_press);
                radius = "five";


                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray nearByBar = null;
                        lookfurther.setBackgroundResource(R.drawable.radius_fivekm_press);
                        try {
                            double km = 5;
                            nearByBar = NearByBar.getNearByBarnewLookFurther(getApplicationContext(), latitude, longitude, km, brand);
                            if (nearByBar != null) {

                                if (NewExpandaList.switchStateOn.equalsIgnoreCase("off")) {
                                    for (int j = 0; j < nearByBar.length(); j++) {

                                        //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));

                                                   /* happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                                    happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                                    isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));*/


                                        if (!res_name.contains(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"))) {

                                            flag = true;
                                            lkflag = true;
                                            Group gru1 = new Group();
                                            res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            res_namelf.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            gru1.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                            gru1.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                            gru1.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                            gru1.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                            // gru1.setName(res_namelf.get(j));
                                            gru1.setCheckRestaurants(true);
                                            gru1.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            gru1.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                            gru1.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                            gru1.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                            gru1.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                            gru1.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                            res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                            res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                            gru1.setPrice(/*"₹" +*/ nearByBar.getJSONObject(j).getString("alcohol_min_price"));
                                            reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                            reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                            gru1.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                            gru1.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                            dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                            gru1.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                            Child ch = null;
                                            ch_list = new ArrayList<Child>();
                                            JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                            for (int i = 0; i < jArray.length(); i++) {
                                                ch = new Child();
                                                ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                ch.setPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                gru1.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                gru1.setPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                ch_list.add(ch);
                                            }

                                            gru1.setItems(ch_list);
                                            list.add(gru1);
                                            Group g = new Group();
                                            g.bottleSort(list);
                                        }
                                    }
                                } else if (NewExpandaList.switchStateOn.equalsIgnoreCase("on")) {
                                    for (int j = 0; j < nearByBar.length(); j++) {

                                        if (!res_name.contains(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"))) {
                                            if (nearByBar.getJSONObject(j).getString("is_happy_hour").equalsIgnoreCase("yes")) {
                                                flag = true;
                                                lkflag = true;
                                                Group gru1 = new Group();
                                                res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                res_namelf.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                gru1.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                                gru1.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                                gru1.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                                gru1.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                                gru1.setCheckRestaurants(true);
                                                gru1.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                gru1.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                                gru1.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                                gru1.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                                gru1.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                                gru1.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                                res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                gru1.setPrice(/*"₹" +*/ nearByBar.getJSONObject(j).getString("alcohol_min_price"));
                                                reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                                reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                                gru1.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                gru1.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                                gru1.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                                Child ch = null;
                                                ch_list = new ArrayList<Child>();
                                                JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                                for (int i = 0; i < jArray.length(); i++) {
                                                    ch = new Child();
                                                    ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                    ch.setPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                    ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                    gru1.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                    gru1.setPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                    ch_list.add(ch);
                                                }

                                                gru1.setItems(ch_list);
                                                list.add(gru1);
                                                Group g = new Group();
                                                g.bottleSort(list);
                                            }
                                        }
                                    }
                                }
                            } else {
                                showAlertDialog(NonBeerExpandList.this, "Alert", "No Data Found within 8km", false);
                            }
                        } catch (Exception e) {

                        }

                        ExpAdapter.notifyDataSetChanged();
                    }
                });
               /* if (lkflag)
                {
                    lkflag=false;
                }
                else
                {
                    showAlertDialog(NonBeerExpandList.this,"Alert","No Restaurants found in 5 Km",false);

                }
*/

                popupWindow.dismiss();
            }
        });

        twokm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NonBeerExpandList.this, "2 km restaurants are showing", Toast.LENGTH_SHORT).show();

                radius = "two";

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray nearByBar = null;

                        try {
                            lookfurther.setBackgroundResource(R.drawable.radius_twokm_press);
                            double km = 2;
                            nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), brand);
                            if (nearByBar != null) {
                                if (NewExpandaList.switchStateOn.equalsIgnoreCase("off")) {


                                    res_name.clear();
                                    ExpListItems.clear();
                                    for (int j = 0; j < nearByBar.length(); j++) {
                                        Group gru = new Group();

                                        happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                        happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                        isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                        //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                        gru.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                        gru.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                        gru.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                        gru.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                        res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                        gru.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                        gru.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                        gru.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                        gru.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                        gru.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                        gru.setName(res_name.get(j));
                                        res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                        res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                        reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                        reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                        gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                        gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));

                                        dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                        distance.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("distance").substring(0, 3));
                                        gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                        gru.setMinPrice(nearByBar.getJSONObject(j).getString("alcohol_min_price"));

                                        Child ch = null;
                                        ch_list = new ArrayList<Child>();
                                        JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                        for (int i = 0; i < jArray.length(); i++) {
                                            ch = new Child();
                                            ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                            maxPrice.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                            ch.setPrice(maxPrice.get(i));
                                            min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                            ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                            min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                            gru.setPrice(min_price.get(i));
                                            ch_list.add(ch);
                                        }
                                        gru.setItems(ch_list);
                                        list.add(gru);
                                        Group g = new Group();
                                        g.bottleSort(list);

                                    }
                                } else if (NewExpandaList.switchStateOn.equalsIgnoreCase("on")) {
                                    res_name.clear();
                                    ExpListItems.clear();

                                    for (int j = 0; j < nearByBar.length(); j++) {
                                        if (nearByBar.getJSONObject(j).getString("is_happy_hour").equalsIgnoreCase("yes")) {


                                            Group gru = new Group();

                                            happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                            happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                            isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                            //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            gru.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                            gru.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                            gru.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                            gru.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                            res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            gru.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                            gru.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                            gru.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                            gru.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                            gru.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                            gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                            res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                            res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                            reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                            reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                            gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                            gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));

                                            dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                            distance.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("distance").substring(0, 3));
                                            gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));
                                            gru.setMinPrice(nearByBar.getJSONObject(j).getString("alcohol_min_price"));
                                            Child ch = null;
                                            ch_list = new ArrayList<Child>();
                                            JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                            for (int i = 0; i < jArray.length(); i++) {
                                                ch = new Child();
                                                ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                maxPrice.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                ch.setPrice(maxPrice.get(i));
                                                min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                                gru.setPrice(min_price.get(i));
                                                ch_list.add(ch);
                                            }
                                            gru.setItems(ch_list);
                                            list.add(gru);
                                            Group g = new Group();
                                            g.bottleSort(list);

                                        }
                                    }
                                }
                            } else {
                                showAlertDialog(NonBeerExpandList.this, "Alert", "No Data Found within 7km", false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        ExpAdapter.notifyDataSetChanged();
                    }
                });


                popupWindow.dismiss();
            }
        });


        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);

        if (radius.equalsIgnoreCase("look"))
        {
            popupWindow.showAsDropDown(lookfurther, 0, -lookfurther.getHeight() - lookfurther.getLayoutParams().height - lookfurther.getHeight(), Gravity.TOP);
        }
        else
        {
            popupWindow.showAsDropDown(lookfurther, 0, -lookfurther.getHeight() - lookfurther.getLayoutParams().height - lookfurther.getHeight(), Gravity.TOP);
        }

    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public ArrayList<Group> SetStandardGroups()
    {
        list = new ArrayList<Group>();
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {

            brand = bundle.getString("brand");
            place = bundle.getString("place");
           // switchStateOnFOrNonBeer=bundle.getString("switchstate");
            Log.i("bottoms","searching place " +place);
            Log.i("bottoms", "brand " + brand);
          //  Log.i(TAG,"log in setStandard() in nonbeerLIst  "+switchStateOnFOrNonBeer);
        }


        JSONArray nearByBar =null;

        try {

            JSONArray jsonArray = InternetConnectionDetector.getSearchBeer(getApplicationContext(), brand);
            if (jsonArray != null)
            {
                for (int m = 0; m < jsonArray.length(); m++) {
                    Child child = new Child();
                    child.setBrand(jsonArray.getJSONObject(m).getString("liquors"));
                    placeList.add(jsonArray.getJSONObject(m).getString("liquors"));

            }
                Log.i("bottoms", "Searched API Value " + placeList);
                try {

                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    latlng = gpsTracker.getLatLOng(place);
                   // final Double latitude = (Double) latlng.get(0);
                   // final Double longitude = (Double) latlng.get(1);
                    JSONObject jsonObject= GPSTracker.getLocationInfo(place);
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
                  //  Log.i(TAG, "lattitude and longitude" + latlng + "  " + latitude + " " + longitude);

                   // nearByBar= NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), brand);

                    nearByBar=InternetConnectionDetector.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), brand);

                    if (nearByBar!=null)
                    {

                        if (NewExpandaList.switchStateOn.equalsIgnoreCase("off"))
                        {

                        for (int j = 0; j < nearByBar.length(); j++)
                        {
                        Group gru = new Group();

                        happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                        happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                        isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                        gru.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                        gru.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                        gru.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                        gru.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                        res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                            gru.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                            gru.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                            gru.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                            gru.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                            gru.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                        gru.setName(res_name.get(j));
                         res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                        res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                        reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                        reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                            gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                            gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));

                        dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                        distance.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("distance").substring(0, 3));
                        gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                        gru.setMinPrice(nearByBar.getJSONObject(j).getString("alcohol_min_price"));

                        Child ch = null;
                        ch_list = new ArrayList<Child>();
                        JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                        for (int i = 0; i < jArray.length(); i++) {
                            ch = new Child();
                            ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                            maxPrice.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                            ch.setPrice(maxPrice.get(i));
                            min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                            ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                            min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                            gru.setPrice(min_price.get(i));
                            ch_list.add(ch);
                        }
                        gru.setItems(ch_list);
                        list.add(gru);

                    }

           }
                        else if (NewExpandaList.switchStateOn.equalsIgnoreCase("on"))
                        {

                            for (int j = 0; j < nearByBar.length(); j++) {
                                Group gru = new Group();

                                happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                if (nearByBar.getJSONObject(j).getString("is_happy_hour").equalsIgnoreCase("yes"))
                                {

                                    gru.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                    gru.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                    gru.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                    gru.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                    res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                    gru.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                    gru.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                    gru.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                    gru.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                    gru.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                    gru.setName(res_name.get(j));
                                    res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                    res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                    reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                    reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                    gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                    gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));

                                    dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                    distance.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("distance").substring(0, 3));
                                    gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                    gru.setMinPrice(nearByBar.getJSONObject(j).getString("alcohol_min_price"));

                                    Child ch = null;
                                    ch_list = new ArrayList<Child>();
                                    JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                    for (int i = 0; i < jArray.length(); i++) {
                                        ch = new Child();
                                        ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                        maxPrice.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                        ch.setPrice(maxPrice.get(i));
                                        min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                        ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                        min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                        gru.setPrice(min_price.get(i));
                                        ch_list.add(ch);
                                    }
                                    gru.setItems(ch_list);
                                    list.add(gru);

                                }
                            }
                        }


                        lookfurther.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                isInternetPresent = cd.isConnectingToInternet();
                                if (isInternetPresent) {
                                    look_further_popup(NonBeerExpandList.this);
                                }
                                else
                                {
                                    Toast.makeText(NonBeerExpandList.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                                }


                            }

                        });

                    }
                    else
                    {
                        showAlertDialog(NonBeerExpandList.this,"Alert","No Data Found Please Try other place",false);
                    }

                }
                catch (JSONException j)
                {
                    j.printStackTrace();
                }

            }

            }

            catch(JSONException j)
            {
                j.printStackTrace();
            }


        return list;
    }



    public ArrayList<Group> liquorSearch() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            brand = bundle.getString("brand");
            place = bundle.getString("place");
            //switchStateOnFOrNonBeer = bundle.getString("switchstate");
            Log.i("bottoms", "searching place " + place);
            Log.i("bottoms", "brand " + brand);
        }

        JSONArray nearByBar = null;

        try {

            JSONArray jsonArray = InternetConnectionDetector.getSearchBeer(getApplicationContext(), brand);
            if (jsonArray != null)
            {
                for (int m = 0; m < jsonArray.length(); m++) {
                    Child child = new Child();
                    child.setBrand(jsonArray.getJSONObject(m).getString("liquors"));
                    placeList.add(jsonArray.getJSONObject(m).getString("liquors"));

                }
                Log.i("bottoms", "Searched API Value " + placeList);
                try {

                    GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                    ArrayList latlng = new ArrayList();
                    latlng = gpsTracker.getLatLOng(place);

                    //final Double latitude = (Double) latlng.get(0);
                //    final Double longitude = (Double) latlng.get(1);


                    JSONObject jsonObject= GPSTracker.getLocationInfo(place);
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
                    Log.i(TAG, "lattitude and longitude" + latlng + "  " + latitude + " " + longitude);

                    nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), brand);

                    if (nearByBar != null) {

                        double latitude_dist = 0;
                        double longitude_dist = 0;

                        gps = new GPSTracker(NonBeerExpandList.this);
                        // check if GPS enabled
                        if (gps.canGetLocation()) {
                            latitude_dist = gps.getLatitude();
                            longitude_dist = gps.getLongitude();


                        } else {

                            gps.showSettingsAlert();

                        }

                            for (int j = 0; j < nearByBar.length(); j++) {
                                Group gru = new Group();

                                happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                gru.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                gru.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                gru.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                gru.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                gru.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                gru.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                gru.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                gru.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                gru.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                gru.setName(res_name.get(j));
                                res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                distance.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("distance").substring(0, 3));
                                gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));
                                gru.setMinPrice(nearByBar.getJSONObject(j).getString("alcohol_min_price"));
                                Child ch = null;
                                ch_list = new ArrayList<Child>();
                                JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                for (int i = 0; i < jArray.length(); i++) {
                                    ch = new Child();
                                    ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                    maxPrice.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                    ch.setPrice(maxPrice.get(i));
                                    min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                    ch.setMinPrice(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                    min_price.add(jArray.getJSONObject(i).getString("res_liq_brand_price"));
                                    gru.setPrice(min_price.get(i));
                                    ch_list.add(ch);
                                }
                                gru.setItems(ch_list);
                                list.add(gru);

                            }

                    }
                    else
                    {
                        showAlertDialog(NonBeerExpandList.this,"Alert","No Data Found",false);

                    }
                }
                catch (JSONException j)
                {

                }
            }
            else
            {
                showAlertDialog(NonBeerExpandList.this,"Alert","No Data Found Please Try other place",false);
            }
        }
        catch (JSONException j)
        {

        }

        return list;

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
    public void onBackPressed()
    {
        Intent intent = new Intent(NonBeerExpandList.this,Homescreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("spinner1_pos", spinner1_pos);
        intent.putExtra("spinner2_pos" ,spinner2_pos);
        intent.putExtra("spinner3_pos", spinner3_pos);
        startActivity(intent);


    }
    private class AsyncCallWS extends AsyncTask<String, String, String>
    {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            Log.i(TAG, "onPreExecute in non beer");
            try {
                progressDialog = ProgressDialog.show(NonBeerExpandList.this, "Please wait", "Loading...", true);
            } catch (final Throwable th)
            {
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
            NonBeerExpandList.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ExpListItems = Group.bottleSort(SetStandardGroups());
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (ExpListItems.size()>0)
            {
                ExpAdapter = new ExpandListAdapterForNonBeer(NonBeerExpandList.this, ExpListItems);
                ExpandList.setAdapter(ExpAdapter);
                Log.i(TAG, "onPostExecute in nonbeer");
                if (ExpListItems.size()>5)
                {
                    shadow.setVisibility(View.VISIBLE);
                }
                else
                {
                    shadow.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }
        }

    }
    private class HappyHourOff extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            try {
                progressDialog = ProgressDialog.show(NonBeerExpandList.this, "Please wait", "Loading...", true);
            } catch (final Throwable th)
            {
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

            NewExpandaList.switchStateOn = "off";
            ExpListItems.clear();
            NewExpandaList.switchStateOn = "off";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ExpListItems = Group.bottleSort(liquorSearch());
                    NewExpandaList.switchStateOn = "off";
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            ExpAdapter.notifyDataSetChanged();
            Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            //  Toast.makeText(MainActivity.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }

    }

}


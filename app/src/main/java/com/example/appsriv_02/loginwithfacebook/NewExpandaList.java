package com.example.appsriv_02.loginwithfacebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.widget.Button;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewExpandaList extends Activity

{
    private ExpandListAdapterForBeerItems ExpAdapter;
    static ArrayList<Group> ExpListItems;
    static ExpandableListView ExpandList;
    CallbackManager callbackManager;
    Context context;
    ShareDialog shareDialog;
    boolean selectedText = false;
   // PlacesTask placesTask;
   // ParserTask parserTask;
    //AutoCompleteTextView atvPlaces;
    //public AutoCompleteTextView autoComplete;
    public ArrayAdapter<String> aAdapter;
    public String data;
    public List<String> suggest;
    String city = null;
    String reversed;
    static String activeColumn = "";
    public static String switchStateOn = "off";
    public static String radius="two";

    public static boolean flag = false;
    public static boolean lkflag = false;
    String locality;
    Boolean isInternetPresent = false;
    InternetConnectionDetector cd;

    int spinner1_pos;
    int spinner2_pos;
    int spinner3_pos;
    //group and child
    static ArrayList<Group> list = new ArrayList<Group>();


    Double latitude=null;// = (Double) latlng.get(0);
    Double longitude=null;;

    ArrayList<Child> ch_list = null;
    private static final String TAG = "bottoms";
    public static ArrayList<String> res_lat = new ArrayList<>();
    public static ArrayList<String> res_long = new ArrayList<>();

    ArrayList<String> res_name = new ArrayList<>();
    ArrayList<String> brand_name = new ArrayList<>();
    public static int count = 0;


    ArrayList<Double> reslat = new ArrayList<>();
    ArrayList<Double> reslong = new ArrayList<>();
    static ArrayList<Float> dist_fromFormula = new ArrayList<>();

    static ArrayList<String> happypyHourStart = new ArrayList<>();
    static ArrayList<String> happypyHourEnd = new ArrayList<>();
    static ArrayList<String> isHappyHour = new ArrayList<>();


    ArrayList<String> res_namelf = new ArrayList<>();
    private String brand;
    private String place;
    ImageButton lookfurther;
    static Button pint, bottle;
    TextView dist,resultplace;

    double latitude_dist = 0;
    double longitude_dist = 0;

    private static final int ADDRESS_TRESHOLD = 10;
    static LinearLayout pintlayout;
    static LinearLayout bottle_layout;


    ToggleButton aswich;
    GPSTracker gps;
    Filter filter;

    ArrayList<String> placeList = new ArrayList<>();
    private static final String TAGG = NewExpandaList.class.getSimpleName();
    static Tracker mTracker;
    private String name = "Beer Result screen";
    int subBrandPosition;
    String mainbrand;
    String sublocality;
    ImageView shadow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beer_expandablelistlayout);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build());
        Configuration config = getResources().getConfiguration();
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(NewExpandaList.this);
        callbackManager = CallbackManager.Factory.create();
        ExpandList = (ExpandableListView) findViewById(R.id.exp_list);
        resultplace =(TextView)findViewById(R.id.place);





        GoogleAnalyticsApplication application = (GoogleAnalyticsApplication) getApplicationContext();
        mTracker = application.getDefaultTracker();



        lookfurther = (ImageButton) findViewById(R.id.lookfurther);
      //  final ImageButton delas_near_you = (ImageButton) findViewById(R.id.deals);

        pintlayout = (LinearLayout) findViewById(R.id.pintlayout);
        bottle_layout = (LinearLayout) findViewById(R.id.bottlelayout);
     //   ExpListItems = Group.bottleSort(SetStandardGroups());
        Log.i(TAG, "Explist Items " + ExpListItems);
       /* ExpAdapter = new ExpandListAdapterForBeerItems(NewExpandaList.this, ExpListItems, ExpandList);
        ExpandList.setAdapter(ExpAdapter);*/
        cd = new InternetConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            AsyncCallWS asyncCallWS = new AsyncCallWS();
            asyncCallWS.execute();
        }
        else
        {
            Toast.makeText(NewExpandaList.this,"please try again",Toast.LENGTH_LONG).show();
        }
        if (ExpAdapter != null)
        {
            ExpAdapter.notifyDataSetChanged();
        }

        pint = (Button) findViewById(R.id.button);
        bottle = (Button) findViewById(R.id.button2);
        TextView happyHoursText = (TextView) findViewById(R.id.apyhourstext);
        dist = (TextView) findViewById(R.id.button3);
        //atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        aswich = (ToggleButton) findViewById(R.id.toggleButton1);
         shadow =(ImageView)findViewById(R.id.shadow);

        //autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        //autoComplete.setDropDownBackgroundResource(R.drawable.my_shape1);
       // atvPlaces.setDropDownBackgroundResource(R.drawable.my_shape1);
        ImageButton winebutton=(ImageButton)findViewById(R.id.winebutton);

        gps = new GPSTracker(NewExpandaList.this);
        // check if GPS enabled
        if (gps.canGetLocation())
        {
            latitude_dist = gps.getLatitude();
            longitude_dist = gps.getLongitude();


        }
        else {

            gps.showSettingsAlert();
        }


        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {

            brand = bundle.getString("brand");
            place = bundle.getString("place");
            locality=bundle.getString("locality");
            subBrandPosition=bundle.getInt("subBrandPosition");
            mainbrand=bundle.getString("mainbrand");
            sublocality=bundle.getString("sublocality");
            spinner1_pos=bundle.getInt("spinner1_pos");
            spinner2_pos=bundle.getInt("spinner2_pos");
            spinner3_pos=bundle.getInt("spinner3_pos");
            // switchStateOn=bundle.getString("switchstate");
            //Log.i("bottoms", "searching place " + place);
            //Log.i("bottoms", "brand " + brand);
            //Log.i("bottoms", "oncreat() in newexpandlist  " + switchStateOn);
        }
        if (switchStateOn.equalsIgnoreCase("on"))
        {
            aswich.setChecked(true);

        } else if (switchStateOn.equalsIgnoreCase("off")) {
            aswich.setChecked(false);
        }


        final TextView search = (TextView) findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v)
            {
                if (isInternetPresent) {
                    Intent intent = new Intent(NewExpandaList.this, ResultActivity.class);
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
                else {
                    Toast.makeText(NewExpandaList.this, "Please try again ", Toast.LENGTH_SHORT).show();
                }

            }
        });



        aswich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    switchStateOn = "on";


                    for (int i = ExpListItems.size() - 1; i >= 0; i--) {
                        if (ExpListItems.get(i).getIsHappyHours().equalsIgnoreCase("no")) {
                            ExpListItems.remove(i);
                        }
                    }
                    Log.i(TAG, "oncreat() in newexpandlist  ischecked" + switchStateOn);
                    ExpAdapter.notifyDataSetChanged();
                } else
                {
                   /* switchStateOn = "off";
                    // Log.i(TAG, "oncreat() in newexpandlist  isUnChecked" +switchStateOn );
                    //  ExpListItems.clear();

                    if (activeColumn.equalsIgnoreCase("bottle"))
                    {

                        if (isInternetPresent)
                        {
                            switchStateOn = "off";
                            ExpListItems.clear();
                            ExpListItems = Group.bubblesrt(liquorSearch());
                            switchStateOn = "off";
                        }
                        else
                        {
                            Toast.makeText(NewExpandaList.this,"please try again ",Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        if (isInternetPresent) {
                            ExpListItems.clear();
                            switchStateOn = "off";
                            Group group = new Group();
                            ExpListItems = group.bottleSort(liquorSearch());
                            switchStateOn = "off";
                        }
                        else {
                            Toast.makeText(NewExpandaList.this,"Please try again ",Toast.LENGTH_SHORT).show();
                        }

                    }
                    ExpAdapter.notifyDataSetChanged();
                    //  Toast.makeText(getApplicationContext(),"switch button 0ff",Toast.LENGTH_SHORT).show();*/
                    new HappyHourOff().execute();
                }
            }
        });


        winebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if (isInternetPresent) {
                    if (place.length() != 0) {
                        //InternetConnectionDetector connectionDetector = newlayout InternetConnectionDetector(getApplicationContext());
                        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                        ArrayList latlng = new ArrayList();
                        //calling getLatLong method to get the lat and long of the entered city
                        latlng = gpsTracker.getLatLOng(place);
                        // Double latitude1 = (Double) latlng.get(0);
                        //  Double longitude1 = (Double) latlng.get(1);
                        JSONObject jsonObject = GPSTracker.getLocationInfo(place);
                        Double latitude1 = null;// = (Double) latlng.get(0);
                        Double longitude1 = null;// (Double) latlng.get(1);
                        if (jsonObject.length() != 0) {
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
                            JSONArray jarr = NearByBar.BeerAndWineShop(getApplicationContext(), latitude1.doubleValue(), longitude1.doubleValue(), 2);
                            // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                            if (jarr != null) {

                                Intent shopIntent = new Intent(NewExpandaList.this, Wineclass.class);
                                shopIntent.putExtra("city", place);
                                startActivity(shopIntent);
                            } else {
                                showAlertDialog(NewExpandaList.this, "Alert", "No Data Found for Current Location", false);
                            }
                        } else {
                            showAlertDialog(NewExpandaList.this, "Alert", "No data found for your location", false);
                        }
                    } else {
                        JSONArray nearByBar = null;


                        //calling the API to liquoar for beer or other brand for current location when user doesn't enter the city instead just he touch the wine and beer shop

                        nearByBar = NearByBar.findLiquor(getApplicationContext(), latitude, longitude);
                        // nearByBar=NearByBar.findLiquor(getApplicationContext(), latitude,longitude);
                        if (nearByBar != null) {

                            Intent shopIntent = new Intent(NewExpandaList.this, Wineclass.class);
                            // shopIntent.putExtra("city",city);
                            startActivity(shopIntent);
                        } else {
                            showAlertDialog(NewExpandaList.this, "Alert", "No Data Found for Current Location", false);
                        }
                    }
                }
            else {
            Toast.makeText(NewExpandaList.this,"Please try again ",Toast.LENGTH_SHORT).show();
        }

            }

        });

      /*  delas_near_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //delas_near_you.setBackgroundResource(R.drawable.open);
                Intent dealsIntent = new Intent(NewExpandaList.this, DealsNearYou.class);
                dealsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dealsIntent);

            }
        });

*/
        pint.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                activeColumn = "pint";
                Group g = new Group();
                list = g.bottleSort(list);
                ExpListItems = list;
                for (int i = 0; i < list.size(); i++) {
                    ExpandList.collapseGroup(i);
                    /*if (p != null) {
                        showPopup(NewExpandaList.this, p);
                    }*/
                }

                ExpandListAdapterForBeerItems.minPrice.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO_REGULAR.OTF"), Typeface.BOLD);
                NewExpandaList.pint.setBackgroundResource(R.drawable.yellowtab);
                NewExpandaList.bottle.setBackgroundResource(R.drawable.whitetab);
                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO_REGULAR.OTF");
                pint.setTypeface(tf, Typeface.BOLD);
                pint.setTextColor(Color.BLACK);
                bottle.setTypeface(tf, Typeface.NORMAL);
                int defaultTextColor = pint.getTextColors().getDefaultColor();
                bottle.setTextColor(defaultTextColor);


                ExpAdapter.notifyDataSetChanged();
                //pintflag=true;

            }
        });

        bottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                list = Group.bubblesrt(list);
                activeColumn = "bottle";
                ExpListItems = list;
                for (int i = 0; i < list.size(); i++) {
                    ExpandList.collapseGroup(i);

                }
                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO_REGULAR.OTF");
                ExpandListAdapterForBeerItems.price.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/MYRIADPRO_REGULAR.OTF"));
                bottle.setTypeface(tf, Typeface.BOLD);
                bottle.setTextColor(Color.BLACK);
                pint.setTypeface(tf, Typeface.NORMAL);
                int defaultTextColor = pint.getTextColors().getDefaultColor();
                pint.setTextColor(defaultTextColor);
                NewExpandaList.pint.setBackgroundResource(R.drawable.whitetab);
                NewExpandaList.bottle.setBackgroundResource(R.drawable.yellowtab);

                ExpAdapter.notifyDataSetChanged();
            }
        });




        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO_REGULAR.OTF");
        pint.setTypeface(tf);
        bottle.setTypeface(tf);
        dist.setTypeface(tf);
        happyHoursText.setTypeface(tf);

        gps = new GPSTracker(NewExpandaList.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {

            gps.showSettingsAlert();
        }

        //atvPlaces.setText(place);
       // autoComplete.setText(brand);
        StringBuilder stringBuilder = new StringBuilder(brand);
        reversed = stringBuilder.reverse().toString();
        Log.i(TAG, "reversed string " + reversed);
        if (reversed.length() > 6) {
            if (reversed.charAt(5) == '6') {

                activeColumn = "bottle";
            } else {
                activeColumn = "pint";
            }
        } else {
            activeColumn = "pint";
        }
        resultplace.setText(brand.substring(0,1).toUpperCase() + brand.substring(1)+" Near "+place);
       // search.setText(brand);

        resultplace.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (isInternetPresent)
                {
                Intent intent = new Intent(NewExpandaList.this, ResultActivity.class);
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
                else {
                    Toast.makeText(NewExpandaList.this,"Please try again ",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAGG, "Setting screen name: " + name);
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void look_further_popup(final Context context)
    {

        LayoutInflater layoutInflater = (LayoutInflater)NewExpandaList.this.getSystemService(context.LAYOUT_INFLATER_SERVICE);


        final View popupView = layoutInflater.inflate(R.layout.look_further_popup, null);

        final PopupWindow popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);


        popupWindow .setTouchable(true);
        popupWindow .setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.isOutsideTouchable();


        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        ArrayList latlng = new ArrayList();
        latlng = gpsTracker.getLatLOng(place);
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


     /*   if (latlng.size()>0) {*/
         //   final Double latitude = (Double) latlng.get(0);
            //final Double longitude = (Double) latlng.get(1);
            Log.i(TAG, "lattitude and longitude" + latlng + "  " + latitude + " " + longitude);

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


            sevenkm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(NewExpandaList.this, "7 km restaurants are showing", Toast.LENGTH_SHORT).show();

                    radius = "seven";

                    runOnUiThread(new Runnable() {
                        public void run() {
                            JSONArray nearByBar = null;
                            lookfurther.setBackgroundResource(R.drawable.radius_sevenkm_press);

                            try {
                                double km = 7;
                                nearByBar = NearByBar.getNearByBarnewLookFurther(getApplicationContext(), latitude, longitude, km, brand);
                                int a =nearByBar.length();
                                if (nearByBar != null) {

                                    if (switchStateOn.equalsIgnoreCase("off")) {

                                        for (int j = 0; j < nearByBar.length(); j++) {
                                            //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));

                                            happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                            happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                            isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));


                                            if (!res_name.contains(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"))) {
                                                Group gru1 = new Group();
                                                flag = true;
                                                lkflag = true;
                                                res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                res_namelf.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                gru1.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                                gru1.setCheckRestaurants(true);
                                                gru1.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                                gru1.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                                gru1.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                                gru1.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                gru1.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                                gru1.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                                gru1.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                                gru1.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                                gru1.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                                            /*List<String> sourceList = newlayout ArrayList<String>(res_name);
                                                            List<String> destinationList = newlayout ArrayList<String>(res_namelf);
                                                            destinationList.removeAll( res_name );
*/

                                                gru1.setPrice(nearByBar.getJSONObject(j).getString("bottle_min_price"));
                                                gru1.setMinPrice(nearByBar.getJSONObject(j).getString("pint_min_price"));
                                                res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                gru1.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                gru1.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                                reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                                dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                                gru1.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                                Child ch = null;

                                                ch_list = new ArrayList<>();
                                                JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                                for (int i = 0; i < jArray.length(); i++) {
                                                    ch = new Child();
                                                    ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                    ch.setPrice(jArray.getJSONObject(i).getString("pint_price"));
                                                    ch.setMinPrice(jArray.getJSONObject(i).getString("bottle_price"));
                                                    ch_list.add(ch);
                                                }


                                                gru1.setItems(ch_list);
                                                list.add(gru1);
                                                Group g = new Group();
                                                // list=g.bottleSort(list);


                                                if (activeColumn.equalsIgnoreCase("pint")) {
                                                    list = Group.bottleSort(list);
                                                } else {
                                                    list = Group.bubblesrt(list);
                                                }
                                            }
                                        }
                                    } else if (switchStateOn.equalsIgnoreCase("on")) {
                                        // Toast.makeText(NewExpandaList.this,"happy hour on 8 km",Toast.LENGTH_SHORT).show();
                                        for (int j = 0; j < nearByBar.length(); j++) {
                                            happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                            happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                            isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));

                                            if (!res_name.contains(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"))) {
                                                if (nearByBar.getJSONObject(j).getString("is_happy_hour").equalsIgnoreCase("yes")) {
                                                    Group gru1 = new Group();
                                                    flag = true;
                                                    lkflag = true;
                                                    res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                    res_namelf.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                    gru1.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                                    gru1.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                                    gru1.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                                    gru1.setCheckRestaurants(true);
                                                    gru1.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                                    gru1.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                    gru1.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                                    gru1.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                                    gru1.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                                    gru1.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                                    gru1.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                                    gru1.setPrice(nearByBar.getJSONObject(j).getString("bottle_min_price"));
                                                    gru1.setMinPrice(nearByBar.getJSONObject(j).getString("pint_min_price"));
                                                    res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                    res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                    reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                                    reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                                    gru1.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                    gru1.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                    dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                                    gru1.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                                    Child ch = null;

                                                    ch_list = new ArrayList<>();
                                                    JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                                    for (int i = 0; i < jArray.length(); i++) {
                                                        ch = new Child();
                                                        ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                        ch.setPrice(jArray.getJSONObject(i).getString("pint_price"));
                                                        ch.setMinPrice(jArray.getJSONObject(i).getString("bottle_price"));
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
                                    showAlertDialog(NewExpandaList.this, "Alert", "No Data Found within " + km + "km", false);
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

            fivekm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(NewExpandaList.this, "5 km restaurants are showing", Toast.LENGTH_SHORT).show();

                    radius = "five";

                    runOnUiThread(new Runnable() {
                        public void run() {
                            JSONArray nearByBar = null;
                            lookfurther.setBackgroundResource(R.drawable.radius_fivekm_press);

                            try {
                                gps = new GPSTracker(NewExpandaList.this);
                                // check if GPS enabled
                                if (gps.canGetLocation()) {
                                    latitude_dist = gps.getLatitude();
                                    longitude_dist = gps.getLongitude();


                                } else {

                                    gps.showSettingsAlert();
                                }

                                double km = 5;
                                nearByBar = NearByBar.getNearByBarnewLookFurther(getApplicationContext(), latitude, longitude, km, brand);
                                if (nearByBar != null) {
                                    if (switchStateOn.equalsIgnoreCase("off")) {

                                        for (int j = 0; j < nearByBar.length(); j++) {
                                            happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                            happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                            isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));

                                            if (!res_name.contains(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"))) {
                                                Group gru1 = new Group();
                                                flag = true;
                                                lkflag = true;
                                                res_namelf.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                gru1.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                                gru1.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                                gru1.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                                gru1.setCheckRestaurants(true);
                                                gru1.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                                gru1.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                gru1.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                                gru1.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                                gru1.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                                gru1.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                                gru1.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                                gru1.setPrice(nearByBar.getJSONObject(j).getString("bottle_min_price"));
                                                gru1.setMinPrice(nearByBar.getJSONObject(j).getString("pint_min_price"));
                                                res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                                reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                                gru1.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                gru1.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                                gru1.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                                Child ch = null;

                                                ch_list = new ArrayList<>();
                                                JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                                for (int i = 0; i < jArray.length(); i++) {
                                                    ch = new Child();
                                                    ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                    ch.setPrice(jArray.getJSONObject(i).getString("pint_price"));
                                                    ch.setMinPrice(jArray.getJSONObject(i).getString("bottle_price"));
                                                    ch_list.add(ch);
                                                }

                                                gru1.setItems(ch_list);
                                                list.add(gru1);
                                                Group g = new Group();
                                                g.bottleSort(list);


                                            }


                                        }
                                    } else if (switchStateOn.equalsIgnoreCase("on")) {
                                        //  Toast.makeText(NewExpandaList.this,"happy hour on 8 km",Toast.LENGTH_SHORT).show();
                                        for (int j = 0; j < nearByBar.length(); j++) {
                                                            /*happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                                            happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                                            isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));*/

                                            if (!res_name.contains(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"))) {
                                                if (nearByBar.getJSONObject(j).getString("is_happy_hour").equalsIgnoreCase("yes")) {
                                                    Group gru1 = new Group();
                                                    flag = true;
                                                    lkflag = true;
                                                    res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                    res_namelf.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                    gru1.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                                                    gru1.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                                                    gru1.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                                                    gru1.setCheckRestaurants(true);
                                                    gru1.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                                                    gru1.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                                                    gru1.setPlaceName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_address"));
                                                    gru1.setRes_locality(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_locality"));
                                                    gru1.setRes_offers(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_offers"));
                                                    gru1.setRes_phone1(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone1"));
                                                    gru1.setRes_phone2(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_phone2"));
                                                    gru1.setPrice(nearByBar.getJSONObject(j).getString("bottle_min_price"));
                                                    gru1.setMinPrice(nearByBar.getJSONObject(j).getString("pint_min_price"));
                                                    res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                    res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                    reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                                    reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                                    gru1.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                    gru1.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                    dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));
                                                    gru1.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                                    Child ch = null;

                                                    ch_list = new ArrayList<>();
                                                    JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                                    for (int i = 0; i < jArray.length(); i++) {
                                                        ch = new Child();
                                                        ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                        ch.setPrice(jArray.getJSONObject(i).getString("pint_price"));
                                                        ch.setMinPrice(jArray.getJSONObject(i).getString("bottle_price"));
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
                                    showAlertDialog(NewExpandaList.this, "Alert", "No Data Found within " + km + "km", false);
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

            twokm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(NewExpandaList.this, "2 km restaurants are showing", Toast.LENGTH_SHORT).show();

                    radius = "two";


                    runOnUiThread(new Runnable() {
                        public void run() {
                            JSONArray nearByBar = null;
                            lookfurther.setBackgroundResource(R.drawable.radius_twokm_press);
                            try {

                                double km = 2;
                                res_name.clear();
                                nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude, longitude, brand);
                                if (nearByBar != null) {

                                    if (switchStateOn.equalsIgnoreCase("off")) {
                                        res_name.clear();
                                        ExpListItems.clear();

                                        for (int j = 0; j < nearByBar.length(); j++) {
                                            Group gru = new Group();
                                            //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
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
                                            gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                            gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                            reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                            reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                            dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));

                                            gru.setPrice(/*"₹" +*/ nearByBar.getJSONObject(j).getString("bottle_min_price"));
                                            gru.setMinPrice(/*"₹" + */nearByBar.getJSONObject(j).getString("pint_min_price"));
                                            gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                            Child ch = null;
                                            ch_list = new ArrayList<Child>();
                                            JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                            for (int i = 0; i < jArray.length(); i++) {
                                                ch = new Child();
                                                ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                ch.setPrice(jArray.getJSONObject(i).getString("pint_price"));
                                                ch.setMinPrice(jArray.getJSONObject(i).getString("bottle_price"));
                                                ch_list.add(ch);
                                            }
                                            gru.setItems(ch_list);
                                            list.add(gru);
                                            Group g = new Group();
                                            g.bottleSort(list);

                                        }
                                    } else if (switchStateOn.equalsIgnoreCase("on")) {
                                        ExpListItems.clear();
                                        res_name.clear();
                                        for (int j = 0; j < nearByBar.length(); j++) {
                                            if (nearByBar.getJSONObject(j).getString("is_happy_hour").equalsIgnoreCase("yes")) {
                                                Group gru = new Group();
                                                //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
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
                                                gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));

                                                res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));

                                                reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                                reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                                gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                                gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));
                                                dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));

                                                gru.setPrice(/*"₹" +*/ nearByBar.getJSONObject(j).getString("bottle_min_price"));
                                                gru.setMinPrice(/*"₹" + */nearByBar.getJSONObject(j).getString("pint_min_price"));
                                                gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                                Child ch = null;
                                                ch_list = new ArrayList<Child>();
                                                JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                                for (int i = 0; i < jArray.length(); i++) {
                                                    ch = new Child();
                                                    ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                                    ch.setPrice("₹" + jArray.getJSONObject(i).getString("pint_price"));
                                                    ch.setMinPrice("₹" + jArray.getJSONObject(i).getString("bottle_price"));
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
                                    showAlertDialog(NewExpandaList.this, "Alert", "No Data Found within " + km + "km", false);
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
            //  popupWindow.showAsDropDown(lookfurther, 20, 3, Gravity.TOP);

       // lookfurther.getDrawable().getBounds().height();
       // lookfurther.getDrawable().getBounds().width();
        int height = lookfurther.getHeight();

        if (radius.equalsIgnoreCase("look"))
        {
            popupWindow.showAsDropDown(lookfurther, 5, -lookfurther.getHeight()-lookfurther.getLayoutParams().height-lookfurther.getHeight(), Gravity.TOP);
        }
        else
        {
            popupWindow.showAsDropDown(lookfurther, 5, -lookfurther.getHeight()-lookfurther.getLayoutParams().height-lookfurther.getHeight(), Gravity.TOP);
        }

    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    //method to set the value to expandablelistview
    public ArrayList<Group> SetStandardGroups()
    {
        list = new ArrayList<Group>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            brand = bundle.getString("brand");
            place = bundle.getString("place");

        }


        JSONArray nearByBar = null;
        try {


            JSONArray jsonArray = InternetConnectionDetector.getSearchBeer(getApplicationContext(), brand);
            if (jsonArray != null) {
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
                  //  final Double longitude = (Double) latlng.get(1);
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

                 //   nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), brand);
                    nearByBar=InternetConnectionDetector.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), brand);

                    if (nearByBar != null) {
                        if (switchStateOn.equalsIgnoreCase("off")) {

                            for (int j = 0; j < nearByBar.length(); j++) {
                                Group gru = new Group();
                                //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
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

                                gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));

                                reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));

                                gru.setPrice(nearByBar.getJSONObject(j).getString("bottle_min_price"));
                                gru.setMinPrice(nearByBar.getJSONObject(j).getString("pint_min_price"));
                                gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                Child ch = null;
                                ch_list = new ArrayList<Child>();
                                JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                for (int i = 0; i < jArray.length(); i++) {
                                    ch = new Child();
                                    ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                    ch.setPrice(jArray.getJSONObject(i).getString("pint_price"));
                                    ch.setMinPrice(jArray.getJSONObject(i).getString("bottle_price"));
                                    ch_list.add(ch);
                                }
                                gru.setItems(ch_list);
                                list.add(gru);
                            }
                        } else if (switchStateOn.equalsIgnoreCase("on"))
                        {

                            for (int j = 0; j < nearByBar.length(); j++) {
                                if (nearByBar.getJSONObject(j).getString("is_happy_hour").equalsIgnoreCase("yes"))
                                {
                                    Group gru = new Group();
                                    //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
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

                                    gru.setRes_lat(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                                    gru.setRes_lng(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));

                                    reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                                    reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                                    dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));

                                    gru.setPrice(/*"₹" +*/ nearByBar.getJSONObject(j).getString("bottle_min_price"));
                                    gru.setMinPrice(/*"₹" + */nearByBar.getJSONObject(j).getString("pint_min_price"));
                                    gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                                    Child ch = null;
                                    ch_list = new ArrayList<Child>();
                                    JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                                    for (int i = 0; i < jArray.length(); i++) {
                                        ch = new Child();
                                        ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                        ch.setPrice(jArray.getJSONObject(i).getString("pint_price"));
                                        ch.setMinPrice(jArray.getJSONObject(i).getString("bottle_price"));
                                        ch_list.add(ch);
                                    }
                                    gru.setItems(ch_list);
                                    list.add(gru);
                                }
                            }
                        }


                     /* */

                        //on lookfurther button is clicked
                        lookfurther.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                isInternetPresent = cd.isConnectingToInternet();
                                if (isInternetPresent)
                                {
                                look_further_popup(NewExpandaList.this);
                                }
                                else {
                                    Toast.makeText(NewExpandaList.this,"Please try again ",Toast.LENGTH_SHORT).show();
                                }

                            }

                        });
                    } else {
                        showAlertDialog(NewExpandaList.this, "Alert", "No Data Found Please Try other place", false);
                        ;
                    }

                } catch (JSONException j) {
                    j.printStackTrace();
                }

            }

        } catch (JSONException j) {
            j.printStackTrace();
        }

        if (activeColumn.equalsIgnoreCase("pint")) {

            list = Group.bubblesrt(list);
        } else if (activeColumn.equalsIgnoreCase("bottle")) {
            list = Group.bottleSort(list);
        }


        return list;
    }


    public ArrayList<Group> liquorSearch()
    {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            brand = bundle.getString("brand");
            place = bundle.getString("place");
            switchStateOn = bundle.getString("switchstate");
            Log.i("bottoms", "searching place " + place);
            Log.i("bottoms", "brand " + brand);

        }
        JSONArray nearByBar = null;
        try {

            JSONArray jsonArray = InternetConnectionDetector.getSearchBeer(getApplicationContext(), brand);
            if (jsonArray != null) {
                for (int m = 0; m < jsonArray.length(); m++) {
                    Child child = new Child();
                    child.setBrand(jsonArray.getJSONObject(m).getString("liquors"));
                    placeList.add(jsonArray.getJSONObject(m).getString("liquors"));

                }
                Log.i("bottoms", "Searched API Value " + placeList);
                try {

                  //  GPSTracker gpsTracker = new GPSTracker(NewExpandaList.this);
                    ArrayList latlng = new ArrayList();
                 //   latlng = gpsTracker.getLatLOng(place);
                 //   final Double latitude = (Double) latlng.get(0);
                    //final Double longitude = (Double) latlng.get(1);
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


                   // Log.i(TAG, "lattitude and longitude" + latlng + "  " + latitude + " " + longitude);

                    nearByBar = NearByBar.getNearByBarnew(getApplicationContext(), latitude.doubleValue(), longitude.doubleValue(), brand);

                    if (nearByBar != null) {

                        for (int j = 0; j < nearByBar.length(); j++)
                        {
                            Group gru = new Group();
                            //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
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

                            gru.setPrice(/*"₹" +*/ nearByBar.getJSONObject(j).getString("bottle_min_price"));
                            gru.setMinPrice(/*"₹" + */nearByBar.getJSONObject(j).getString("pint_min_price"));
                            gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                            Child ch = null;
                            ch_list = new ArrayList<Child>();
                            JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                            for (int i = 0; i < jArray.length(); i++) {
                                ch = new Child();
                                ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                                ch.setPrice(jArray.getJSONObject(i).getString("pint_price"));
                                ch.setMinPrice(jArray.getJSONObject(i).getString("bottle_price"));
                                ch_list.add(ch);
                            }
                            gru.setItems(ch_list);
                            list.add(gru);
                        }
                    } else {
                        showAlertDialog(NewExpandaList.this, "Alert", "No Data Found Please Try other place", false);
                    }
                } catch (JSONException j) {

                }
            } else {

            }
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return list;

    }


    // alert message
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



    public ArrayList<Group> setGroupAsPerLiquor() {
        JSONArray nearByBar = null;


        try {
            double km = 2;
            res_name.clear();
            nearByBar = NearByBar.getNearByBarnewLookFurther(getApplicationContext(), latitude, longitude, km, brand);
            if (nearByBar != null) {
                double latitude_dist = 0;
                double longitude_dist = 0;

                gps = new GPSTracker(NewExpandaList.this);
                // check if GPS enabled
                if (gps.canGetLocation()) {
                    latitude_dist = gps.getLatitude();
                    longitude_dist = gps.getLongitude();


                } else {

                    gps.showSettingsAlert();
                }
                // res_name.clear();

                for (int j = 0; j < nearByBar.length(); j++) {
                    Group gru = new Group();
                    //  gru.setName(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));
                    happypyHourStart.add(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                    happypyHourEnd.add(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                    isHappyHour.add(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                    gru.setIsHappyHours(nearByBar.getJSONObject(j).getString("is_happy_hour"));
                    gru.setHappyHourStart(nearByBar.getJSONObject(j).getString("happy_hour_start"));
                    gru.setHappyHourEnds(nearByBar.getJSONObject(j).getString("happy_hour_end"));
                    gru.setRest_offers_happy_hour(nearByBar.getJSONObject(j).getString("rest_offers_happy_hour"));
                    res_name.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_name"));

                    gru.setName(res_name.get(j));

                    res_lat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_lat"));
                    res_long.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getString("res_long"));

                    reslat.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_lat"));
                    reslong.add(nearByBar.getJSONObject(j).getJSONObject("resInfo").getDouble("res_long"));
                    dist_fromFormula.add(Wineclass.distFrom(latitude_dist, longitude_dist, reslat.get(j).doubleValue(), reslong.get(j).doubleValue()));

                    gru.setPrice( nearByBar.getJSONObject(j).getString("bottle_min_price"));
                    gru.setMinPrice(nearByBar.getJSONObject(j).getString("pint_min_price"));
                    gru.setDist(dist_fromFormula.get(j).toString().substring(0, 4));

                    Child ch = null;
                    ch_list = new ArrayList<Child>();
                    JSONArray jArray = nearByBar.getJSONObject(j).getJSONArray("resLiqInfo");
                    for (int i = 0; i < jArray.length(); i++) {
                        ch = new Child();
                        ch.setBrand(jArray.getJSONObject(i).getString("liq_brand_name"));
                        ch.setPrice("₹" + jArray.getJSONObject(i).getString("pint_price"));
                        ch.setMinPrice("₹" + jArray.getJSONObject(i).getString("bottle_price"));
                        ch_list.add(ch);
                    }
                    gru.setItems(ch_list);
                    list.add(gru);
                    Group g = new Group();
                    g.bottleSort(list);

                }
            } else {
                showAlertDialog(NewExpandaList.this, "Alert", "No Data Found within " + km + "km", false);
            }
            //  showAlertDialog(NewExpandaList.this, "Alert", "Do u want to go back to 2 km radius", true);
        } catch (Exception e) {
            e.printStackTrace();

        }

        ExpAdapter.notifyDataSetChanged();
        return list;
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(NewExpandaList.this,Homescreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("spinner1_pos", spinner1_pos);
        intent.putExtra("spinner2_pos" ,spinner2_pos);
        intent.putExtra("spinner3_pos" ,spinner3_pos);
        startActivity(intent);

    }



    private class AsyncCallWS extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            try {
                progressDialog = ProgressDialog.show(NewExpandaList.this, "Please wait", "Loading...", true);
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


            NewExpandaList.this.runOnUiThread(new Runnable() {
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
            if (ExpListItems.size()>0) {
                ExpAdapter = new ExpandListAdapterForBeerItems(NewExpandaList.this, ExpListItems, ExpandList);
                ExpandList.setAdapter(ExpAdapter);
                Log.i(TAG, "onPostExecute");
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
            //  Toast.makeText(MainActivity.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }

    }





    private class HappyHourOff extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            try {
                progressDialog = ProgressDialog.show(NewExpandaList.this, "Please wait", "Loading...", true);
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


            // Log.i(TAG, "oncreat() in newexpandlist  isUnChecked" +switchStateOn );
            //  ExpListItems.clear();

            if (activeColumn.equalsIgnoreCase("bottle"))
            {

                if (isInternetPresent)
                {
                    switchStateOn = "off";
                    ExpListItems.clear();
                    ExpListItems = Group.bubblesrt(liquorSearch());
                    switchStateOn = "off";
                }
                else
                {
                    Toast.makeText(NewExpandaList.this,"please try again ",Toast.LENGTH_SHORT).show();
                }

            } else {

                if (isInternetPresent) {
                    ExpListItems.clear();
                    switchStateOn = "off";
                    ExpListItems = Group.bottleSort(liquorSearch());
                    switchStateOn = "off";
                }
                else {
                    Toast.makeText(NewExpandaList.this,"Please try again ",Toast.LENGTH_SHORT).show();
                }

            }

            //  Toast.makeText(getApplicationContext(),"switch button 0ff",Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
           /* ExpAdapter = new ExpandListAdapterForBeerItems(NewExpandaList.this, ExpListItems, ExpandList);*/
           /* ExpandList.setAdapter(ExpAdapter);*/
            ExpAdapter.notifyDataSetChanged();
            Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            //  Toast.makeText(MainActivity.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }

    }


}

   /* public void addressDialog(String title, final int value, final Context activity)
    {

        ArrayList<Group> g =NewExpandaList.ExpListItems;
        Log.i("newlayout ", "value " + g);

        // create a dialog class object
        final Dialog myDialog = new Dialog(activity);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setting the xml file As setcontentview
        myDialog.setContentView(R.layout.address_customalert);
        //set title





        //myDialog.setTitle(title);
        // myDialog.getWindow().setBackgroundDrawable(newlayout ColorDrawable(Color.BLACK));
        myDialog.setCancelable(false);
        //cancel the alert when user clicks outside the alert message
        myDialog.setCanceledOnTouchOutside(true);

        // myDialog.getWindow().setBackgroundDrawableResource(R.drawable.addresspopback);

        TextView address=(TextView)myDialog.findViewById(R.id.address);
        TextView findOnGoogle=(TextView)myDialog.findViewById(R.id.findongoogle);
        ImageView close =(ImageView)myDialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
        //myDialog.getWindow().setLayout(600, 400);

    }


    / method for custom alert message when avail discount button is clicked
    public void messageDialog(String title, final String message, final Context activity) {

        ArrayList<Group> g = ExpListItems;
        Log.i("newlayout ", "value " + g);

        // create a dialog class object
        final Dialog myDialog = new Dialog(activity);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setting the xml file As setcontentview
        myDialog.setContentView(R.layout.custom_revel_offer_alert);
        //set title

        //myDialog.setTitle(title);
        // myDialog.getWindow().setBackgroundDrawable(newlayout ColorDrawable(Color.BLACK));
        myDialog.getWindow().setBackgroundDrawableResource(R.drawable.popupback);
        myDialog.setCancelable(false);
        //cancel the alert when user clicks outside the alert message
        myDialog.setCanceledOnTouchOutside(true);

        TextView textView = (TextView) myDialog.findViewById(R.id.text);
        textView.setText("\"I just got a 10% discount at " + message + " through BottomzUp app \" ");
        ImageView login = (ImageView) myDialog.findViewById(R.id.create_account);

        ImageView close = (ImageView) myDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });


        // when avail offer butten is clicked
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {

                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("BottomzUp")
                            .setContentDescription("I just got a 10% discount at " + message + " thought Bottomz Up")
                            .setContentUrl(Uri.parse("www.BottomzUp.com"))
                            .build();
                    ShareDialog.show((Activity) activity, linkContent);
                }
                myDialog.dismiss();

            }
        });

        Window window = myDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        myDialog.show();

    }


*/
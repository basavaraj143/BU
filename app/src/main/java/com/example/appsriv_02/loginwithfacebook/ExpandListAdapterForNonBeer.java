package com.example.appsriv_02.loginwithfacebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;

public class ExpandListAdapterForNonBeer extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Group> groups;
    GPSTracker gps;
    TextView dist;
    ImageButton button;
    TextView botte;
    Typeface tf;

    Point p;
    int linewidth;
    static int group_position;
    public static LinearLayout bottleLayout[] = new LinearLayout[10000];
     LinearLayout setBackground[] = new LinearLayout[10000];
    LinearLayout top_layout[] = new LinearLayout[10000];
    LinearLayout locationLayout[] = new LinearLayout[10000];
    //RelativeLayout relative_layout[] = newlayout RelativeLayout[10000];
    View belowline[] = new View[10000];
    LinearLayout groupnamelayout[]= new LinearLayout[10000];
  //  ImageView[] offerribben= new ImageView[10000];
    public ArrayList<Child> CustomListViewValuesArr = new ArrayList<Child>();

    ImageView[] direction = new ImageView[10000];
    ImageView[] phone = new ImageView[10000];



    public ExpandListAdapterForNonBeer(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }
    @Override
    public int getChildrenCount(int groupPosition)
    {
        ArrayList<Child> chList = groups.get(groupPosition).getItems();
        //return chList.size();
        CustomListViewValuesArr=chList;
        return 1;
    }

    public Object getChild(int groupPosition)
    {
        ArrayList<Child> chList = groups.get(groupPosition).getItems();
        return chList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Child> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }
int g;
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {
        g=groupPosition;
        Child child = (Child) getChild(groupPosition, childPosition);
        Group group = (Group) getGroup(groupPosition);
        ArrayList<Child> children =(ArrayList<Child>)getChild(groupPosition);
        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.non_beer_child, null);
        }

       // ImageButton location = (ImageButton)convertView.findViewById(R.id.direction);
        ListView list=(ListView)convertView.findViewById(R.id.listViewwine);
        list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT ,(children.size()*80)));
        list.requestLayout();


        final ImageView arrowup = (ImageView)convertView.findViewById(R.id.arrowup);
         direction[groupPosition] =(ImageView)convertView.findViewById(R.id.direction);
         phone[groupPosition] =(ImageView)convertView.findViewById(R.id.phone);


        TextView group_name= (TextView) convertView.findViewById(R.id.group_name);
        TextView place_name=(TextView)convertView.findViewById(R.id.place_name);
        TextView time=(TextView)convertView.findViewById(R.id.time);
        TextView timeText=(TextView)convertView.findViewById(R.id.timetext);

        group_name.setTypeface(tf);
        place_name.setTypeface(tf);
        timeText.setTypeface(tf);
        time.setTypeface(tf);
        group_name.setText(group.getName());
        place_name.setText(group.getPlaceName());
        timeText.setText("Happy Hours");
        time.setText(NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5)
                + "-" + NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5));


        if (NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5).equalsIgnoreCase("00:00")
                &&NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5).equalsIgnoreCase("00:00"))
        {
            time.setVisibility(View.INVISIBLE);
            timeText.setVisibility(View.INVISIBLE);
        }
        else
        {
            time.setVisibility(View.VISIBLE);
            timeText.setVisibility(View.VISIBLE);
        }
        ImageView collapse = (ImageView)convertView.findViewById(R.id.collapse);
        arrowup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewExpandaList.ExpandList.collapseGroup(groupPosition);
                //Toast.makeText(context, "collapsed", Toast.LENGTH_SHORT).show();
            }
        });


        arrowup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NonBeerExpandList.ExpandList.collapseGroup(groupPosition);
             //   Toast.makeText(context, "collapsed", Toast.LENGTH_SHORT).show();
            }
        });


        direction[groupPosition].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addressPopUp(context, groupPosition);
                NonBeerExpandList.mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("address" )
                        .setAction("Submit")
                        .build());
                //  addressDialog("Bottomz Up", groupPosition, context);
               /* NewExpandaList newExpandaList = newlayout NewExpandaList();
                 newExpandaList.showPopup((Activity)context);*/

            }
        });

        phone[groupPosition].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  phoneDialog("Bottomz Up", groupPosition, context);
                phonePopUp(context, groupPosition);
                NonBeerExpandList.mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Phone number")
                        .setAction("Submit")
                        .build());

            }
        });


        NonBeerCustomAdapter adapter= new NonBeerCustomAdapter((Activity)context, children,groups);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();


       // ImageView discount = (ImageView)convertView.findViewById(R.id.discount);
       /* discount.setOnClickListener(new View.OnClickListener()
        {
            NewExpandaList newExpandaList = new NewExpandaList();

            @Override
            public void onClick(View v) {

           //     newExpandaList.messageDialog("Bottomz Up", NonBeerExpandList.ExpListItems.get(groupPosition).getName(), context);
                popForPressRevel(context,NonBeerExpandList.ExpListItems.get(groupPosition).getName());
                //popForPressRevel(context, NewExpandaList.ExpListItems.get(groupPosition).getName());
            }
        });*/
      //  discount.setFocusable(false);

        if (childPosition==0)
        {

           /* location.setVisibility(View.VISIBLE);
            location.setOnClickListener(newlayout View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(context, "button clicked", Toast.LENGTH_SHORT).show();
                    double latitude = 0;
                    double longitude = 0;
                    gps = newlayout GPSTracker(context);
                    // check if GPS enabled
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();


                    } else {

                        gps.showSettingsAlert();
                    }
//28.63875,77.07380
                    //bangalore 12.9667° N, 77.5667°


                    Intent intent = newlayout Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + NonBeerExpandList.res_lat.get(g) +
                            "," + NonBeerExpandList.res_long.get(g) + ""));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    context.startActivity(intent);


                }
            });*/
        }
        else
        {
           // location.setVisibility(View.GONE);
        }



        //TextView brand_name = (TextView) convertView.findViewById(R.id.brand_name);
        //TextView price =(TextView)convertView.findViewById(R.id.name);
       // TextView maxprice=(TextView)convertView.findViewById(R.id.maxPrice);

        //brand_name.setText(child.getBrand().toString());
       // price.setText("₹"+child.getPrice().toString());
       //maxprice.setText(child.getMinPrice().toString());

        timeText.setTextColor(Color.parseColor("#C4614A"));
        return convertView;
    }



    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


  @Override
    public void onGroupExpanded(int groupPosition)
    {
        NonBeerExpandList.mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(NonBeerExpandList.ExpListItems.get(groupPosition).getName())
                .setAction("Restaurant")
                .setLabel(NonBeerExpandList.ExpListItems.get(groupPosition).getName())
                .build());
    }

/*    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        dist.setVisibility(View.INVISIBLE);
        minPrice.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        Toast.makeText(context, "collapsed " + groupPosition, Toast.LENGTH_SHORT).show();

    }*/



    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        Group group = (Group) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.nonbeergrouplayout, null);

        }
        tf=Typeface.createFromAsset(context.getAssets(),"fonts/MYRIADPRO_REGULAR.OTF");
        bottleLayout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.minprice);
      //  relative_layout[groupPosition]=(RelativeLayout)convertView.findViewById(R.id.relative_layout);
        top_layout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.top_layout);
        TextView tv = (TextView) convertView.findViewById(R.id.group_name);
        TextView place_name = (TextView)convertView.findViewById(R.id.place_name);
       // TextView price=(TextView)convertView.findViewById(R.id.name);
 //       offerribben[groupPosition] =(ImageView)convertView.findViewById(R.id.offerribben);
       // offerribben[groupPosition].bringToFront();
        LinearLayout grpnamelayout=(LinearLayout)convertView.findViewById(R.id.grpnamelayout);
        belowline[groupPosition]=(View)convertView.findViewById(R.id.belowline);
        tv.setTypeface(tf);
        dist=(TextView)convertView.findViewById(R.id.dist);
        dist.setTypeface(tf);
        botte=(TextView)convertView.findViewById(R.id.botte);
        botte.setTypeface(tf);
        TextView time=(TextView)convertView.findViewById(R.id.time);
        TextView timetext=(TextView)convertView.findViewById(R.id.timetext);
        timetext.setTypeface(tf);
        timetext.setText("Happy Hour ");
        timetext.setTextColor(Color.BLACK);
        ImageView arrow = (ImageView)convertView.findViewById(R.id.arrowdown);
        LinearLayout vodkalayout=(LinearLayout)convertView.findViewById(R.id.vodkalayout);
        locationLayout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.locationlayout);
        time.setTypeface(tf);
        time.setText(NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5)+
                "-"+NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5));

     //   ImageView discount=(ImageView)convertView.findViewById(R.id.discount);
        groupnamelayout[groupPosition]= (LinearLayout)convertView.findViewById(R.id.grpnamelayout);
        setBackground[groupPosition]= (LinearLayout)convertView.findViewById(R.id.hh);


        ImageView shadow= (ImageView) convertView.findViewById(R.id.shadow);
        /*setBackground[groupPosition].post(newlayout Runnable() {
            public void run() {
                linewidth = setBackground[groupPosition].getWidth();
            }
        });
*/
        if (groupPosition==0)
        {

        }
        if (NonBeerExpandList.ExpListItems.get(groupPosition).isCheckRestaurants())
        {
            //setBackground[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
           // locationLayout[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
        }
        else
        {
          //  setBackground[groupPosition].setBackgroundColor(Color.WHITE);
            //locationLayout[groupPosition].setBackgroundColor(Color.WHITE);
        }

        /*if (NonBeerExpandList.nonbeer.equalsIgnoreCase("exceptBeer"))
        {
            bottleLayout[groupPosition].setBackgroundResource(R.drawable.pintstripe);
        }
*/
        if (group.isCheckRestaurants())
        {
            grpnamelayout.setBackgroundColor(Color.parseColor("#ccd9ff"));
           /* group_name.setTextColor(Color.RED);
            price.setTextColor(Color.RED);
            minPrice.setTextColor(Color.RED);
            dist.setTextColor(Color.RED);*/
        }
        else
        {
           // grpnamelayout.setBackgroundColor(Color.WHITE);
            /*group_name.setTextColor(Color.BLACK);
            price.setTextColor(Color.BLACK);
            minPrice.setTextColor(Color.BLACK);
            dist.setTextColor(Color.BLACK);*/
        }

        place_name.setTypeface(tf);
        String PRICE =checkZeroValue(group.getMinPrice());
        botte.setText(PRICE);
        dist.setText(group.getDist() + "Km");
        tv.setText(group.getName());
        place_name.setText(group.getRes_locality());



        if (isExpanded)
        {
           // price.setVisibility(View.INVISIBLE);

          //  offerribben[groupPosition].setVisibility(View.GONE);
            top_layout[groupPosition].setVisibility(View.GONE);
            locationLayout[groupPosition].setBackgroundColor(Color.WHITE);
           // relative_layout[groupPosition].setVisibility(View.GONE);
            //belowline[groupPosition].setVisibility(View.GONE);
            locationLayout[groupPosition].setBackgroundColor(Color.WHITE);
            groupnamelayout[groupPosition].setBackgroundColor(Color.WHITE);
            bottleLayout[groupPosition].setBackgroundColor(Color.WHITE);


            botte.setVisibility(View.INVISIBLE);
            dist.setVisibility(View.INVISIBLE);
            //time.setVisibility(View.GONE);
          //  discount.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
            //tv.setBackgroundResource(R.drawable.rectangletab);
          //  groupnamelayout[groupPosition].setBackgroundResource(R.drawable.threesiderectangle);


            if (NonBeerExpandList.ExpListItems.get(groupPosition).getRest_offers_happy_hour().equalsIgnoreCase("no"))
            {
                vodkalayout.setVisibility(View.INVISIBLE);
            }
            else
              {
                    vodkalayout.setVisibility(View.VISIBLE);
                    if (NonBeerExpandList.ExpListItems.get(groupPosition).getIsHappyHours().equalsIgnoreCase("no"))
                    {
                        time.setTextColor(Color.BLACK);
                    } else
                    {
                      //  vodkalayout.setVisibility(View.VISIBLE);
                        time.setTextColor(Color.parseColor("#46A13A"));
                    }
                    vodkalayout.setBackgroundResource(0);
                }
           // tv.setGravity(1);
            shadow.setVisibility(View.GONE);

        }
        else
        {


            if (groups.size()<=5)
            {
                if (groupPosition==groups.size()-1)
                {
                    shadow.bringToFront();
                    shadow.setVisibility(View.VISIBLE);
                }
                else
                {
                    shadow.setVisibility(View.GONE);
                }

            }
            else
            {
                shadow.setVisibility(View.GONE);
            }
//            offerribben[groupPosition].setVisibility(View.VISIBLE);
            top_layout[groupPosition].setVisibility(View.VISIBLE);
            setBackground[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            locationLayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
           // relative_layout[groupPosition].setVisibility(View.VISIBLE);
            belowline[groupPosition].setVisibility(View.VISIBLE);
            locationLayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            groupnamelayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            bottleLayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));


            if (NonBeerExpandList.ExpListItems.get(groupPosition).getRest_offers_happy_hour().equalsIgnoreCase("no"))
            {
                vodkalayout.setVisibility(View.INVISIBLE);
            }
            else
            {
                if (NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0, 5).equalsIgnoreCase("00:00")
                        && NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0, 5).equalsIgnoreCase("00:00"))
                {
                    vodkalayout.setVisibility(View.INVISIBLE);
                }
                else
                {

                    vodkalayout.setVisibility(View.VISIBLE);

                    if (NonBeerExpandList.ExpListItems.get(groupPosition).getIsHappyHours().equalsIgnoreCase("no")) {
                        time.setTextColor(Color.BLACK);
                    } else {

                        time.setTextColor(Color.parseColor("#46A13A"));
                    }
                }
            }
            botte.setVisibility(View.VISIBLE);
            dist.setVisibility(View.VISIBLE);
           /* time.setVisibility(View.VISIBLE);
            time.setBackgroundResource(R.drawable.graybackground);*/
//            discount.setVisibility(View.VISIBLE);
            tv.setBackgroundResource(0);
            arrow.setVisibility(View.VISIBLE);
           // linearLayout.setBackgroundResource(0);
        }

        timetext.setTextColor(Color.parseColor("#C4614A"));


        return convertView;
    }

    private void popForPressRevel(final Context context,final String message ) {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.custom_revel_offer_alert, null);

        final PopupWindow popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);

        popupWindow .setTouchable(true);
        popupWindow .setFocusable(true);
        popupWindow.isOutsideTouchable();
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(650);
        // popupWindow .showAtLocation(popupView, Gravity.AXIS_SPECIFIED, 600,800);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        //popupWindow.showAsDropDown(discount, x, y, Gravity.CENTER_VERTICAL);

        TextView textView = (TextView) popupView.findViewById(R.id.text);
        textView.setText("\"I just got a 10% discount at " + message + " through BottomzUp app \" ");
        ImageView login = (ImageView) popupView.findViewById(R.id.create_account);
        // when avail offer butten is clicked
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class))
                {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("BottomzUp")
                            .setContentDescription("I just got a 10% discount at " + message + " thought Bottomz Up")
                            .setContentUrl(Uri.parse("www.BottomzUp.com"))
                            .build();
                    ShareDialog.show((Activity) context, linkContent);
                }

            }
        });

        ImageView close = (ImageView)popupView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                popupWindow.dismiss();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void addressPopUp(final Context context, final int value)
    {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        final View popupView = layoutInflater.inflate(R.layout.address_customalert, null);

        final PopupWindow popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        popupWindow .setFocusable(false);
        popupWindow.isOutsideTouchable();
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        DisplayMetrics display = context.getResources().getDisplayMetrics();

        int width = display.widthPixels;
        int height = display.heightPixels;
        popupWindow.setWidth(width/2);
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
       /* popupWindow.setTouchInterceptor(newlayout View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });*/

        //popupWindow.showAsDropDown(discount,x,y,Gravity.CENTER_VERTICAL);
        //popupWindow .showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(direction[value],-20,3,Gravity.RIGHT);
        // popupView.setBackgroundResource(R.drawable.phonebackground);
        //  popupWindow.showAtLocation(popupView, Gravity.START, x+30 , y+30);
        TextView address=(TextView)popupView.findViewById(R.id.address);
        TextView findOnGoogle=(TextView)popupView.findViewById(R.id.findongoogle);
        ImageView close =(ImageView)popupView.findViewById(R.id.close);
        String text = "<a h" +
                "" +
                "ref='http://maps.google.com/maps'> find on google map </a>";
        findOnGoogle.setText(Html.fromHtml(text));
        findOnGoogle.setTextColor(Color.BLACK);

        address.setText(NonBeerExpandList.ExpListItems.get(value).getPlaceName());

        findOnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "button clicked", Toast.LENGTH_SHORT).show();
                double latitude = 0;
                double longitude = 0;
                gps = new GPSTracker(context);
                // check if GPS enabled
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();


                } else {

                    gps.showSettingsAlert();
                }
//28.63875,77.07380
                //bangalore 12.9667° N, 77.5667°


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + NonBeerExpandList.ExpListItems.get(value).getRes_lat() +
                        "," + NonBeerExpandList.ExpListItems.get(value).getRes_lng() + ""));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void phonePopUp(final Context context, int value)
    {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.phone_customalert, null);

        final PopupWindow popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);

        popupWindow .setTouchable(true);
        popupWindow .setFocusable(false);
        popupWindow.isOutsideTouchable();
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        DisplayMetrics display = context.getResources().getDisplayMetrics();

        int width = display.widthPixels;
        int height = display.heightPixels;
        popupWindow.setWidth(width/2);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //popupWindow.dismiss();
                return false;
            }
        });


        popupWindow.showAsDropDown(phone[value],-50,2,Gravity.RIGHT);
        final TextView phoneone=(TextView)popupView.findViewById(R.id.phoneone);
        final TextView phonetwo=(TextView)popupView.findViewById(R.id.phonetwo);
        LinearLayout linearLayout =(LinearLayout)popupView.findViewById(R.id.top);
        linearLayout.bringToFront();
        ImageView close = (ImageView)popupView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(context, "closed", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });


        final String ph1 = phoneone.getText().toString();
       /* phoneone.setText(Html.fromHtml(text));*/
        phoneone.setTextColor(Color.BLACK);

        String mystring=new String(ph1);
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        phoneone.setText(content);


        phoneone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phone = ph1;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);


            }
        });
        phoneone.setText(groups.get(group_position).getRes_phone1());
        phonetwo.setText(groups.get(group_position).getRes_phone2());
        //String text1 = "<a href='http://maps.google.com/maps'>"+" 9731276143"+ "</a>";
        final  String ph2 = phonetwo.getText().toString();
        //phonetwo.setText(Html.fromHtml(text1));
        String mystring1=ph2;
        SpannableString content1 = new SpannableString(mystring1);
        content1.setSpan(new UnderlineSpan(), 0, mystring1.length(), 0);
        phonetwo.setText(content1);
        phonetwo.setTextColor(Color.BLACK);

        phonetwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phone = ph2;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);

            }
        });

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

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
 /*try {
                    //String string1 = "20:11:13";
                    String string1 = NewExpandaList.happypyHourStart.get(groupPosition);
                    Date time1 = newlayout SimpleDateFormat("HH:mm:ss").parse(string1);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(time1);

                    //String string2 = "14:49:00";
                    String string2 = NewExpandaList.happypyHourEnd.get(groupPosition);
                    Date time2 = newlayout SimpleDateFormat("HH:mm:ss").parse(string2);
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(time2);
                    calendar2.add(Calendar.DATE, 1);


                    long tim=System.currentTimeMillis();
                    SimpleDateFormat df = newlayout SimpleDateFormat("hh:mm:ss");
                    String curTime =df.format(tim);
                    System.out.println("Time : " + curTime);

                    String someRandomTime = "01:00:00";
                    Date d = newlayout SimpleDateFormat("HH:mm:ss").parse(curTime);
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(d);
                    calendar3.add(Calendar.DATE, 1);
                    Date x = calendar3.getTime();

                    if (x.after(calendar1.getTime()) && x.before(calendar2.getTime()))
                    {
                        //checkes whether the current time is between 14:49:00 and 20:11:13.
                        System.out.println(true);
                       *//* time.setVisibility(View.VISIBLE);
                        time.setBackgroundResource(R.drawable.graybackground);*//*
                        time.setTextColor(Color.GREEN);
                    }
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }


*/


/*


    public void addressDialog(String title, final int value, final Context activity)
    {

        ArrayList<Group> g =NewExpandaList.ExpListItems;
        Log.i("newlayout ", "value " + g);

        // create a dialog class object
        final Dialog myDialog = newlayout Dialog(activity);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setting the xml file As setcontentview
        myDialog.setContentView(R.layout.address_customalert);
        //set title
        //cancel the alert when user clicks outside the alert message
        myDialog.setCanceledOnTouchOutside(true);

        // myDialog.getWindow().setBackgroundDrawableResource(R.drawable.addresspopback);

        TextView address=(TextView)myDialog.findViewById(R.id.address);
        TextView findOnGoogle=(TextView)myDialog.findViewById(R.id.findongoogle);
        ImageView close =(ImageView)myDialog.findViewById(R.id.close);
        String text = "<a href='http://maps.google.com/maps'> find on google map </a>";
        findOnGoogle.setText(Html.fromHtml(text));
        findOnGoogle.setTextColor(Color.BLACK);

        address.setText(NonBeerExpandList.ExpListItems.get(value).getPlaceName());

        findOnGoogle.setOnClickListener(newlayout View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "button clicked", Toast.LENGTH_SHORT).show();
                double latitude = 0;
                double longitude = 0;
                gps = newlayout GPSTracker(context);
                // check if GPS enabled
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();


                } else {

                    gps.showSettingsAlert();
                }
//28.63875,77.07380
                //bangalore 12.9667° N, 77.5667°


                Intent intent = newlayout Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + NonBeerExpandList.res_lat.get(value) +
                        "," + NonBeerExpandList.res_long.get(value) + ""));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);

            }
        });

        close.setOnClickListener(newlayout View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
        //myDialog.getWindow().setLayout(600, 400);

    }


    public void phoneDialog(String title, final int value, final Context activity)
    {

        ArrayList<Group> g =NewExpandaList.ExpListItems;
        Log.i("newlayout ", "value " + g);

        // create a dialog class object
        final Dialog myDialog = newlayout Dialog(activity);

        final PopupWindow popup = newlayout PopupWindow(context);

        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setting the xml file As setcontentview
        myDialog.setContentView(R.layout.phone_customalert);
        //set title

        // myDialog.getWindow().setBackgroundDrawableResource(R.drawable.addresspopback);
        //myDialog.setTitle(title);
        // myDialog.getWindow().setBackgroundDrawable(newlayout ColorDrawable(Color.BLACK));

        */
/*String phone = "+34666777888";
Intent intent = newlayout Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
startActivity(intent);*//*


        final TextView phoneone=(TextView)myDialog.findViewById(R.id.phoneone);
        final TextView phonetwo=(TextView)myDialog.findViewById(R.id.phonetwo);
        ImageView close =(ImageView)myDialog.findViewById(R.id.close);
        myDialog.setCancelable(false);
        //cancel the alert when user clicks outside the alert message
        myDialog.setCanceledOnTouchOutside(true);

        */
/*String text = "<a href='http://maps.google.com/maps'> 123456789 </a>";*//*

        final String ph1 = phoneone.getText().toString();
       */
/* phoneone.setText(Html.fromHtml(text));*//*

        phoneone.setTextColor(Color.BLACK);

        String mystring=newlayout String(ph1);
        SpannableString content = newlayout SpannableString(mystring);
        content.setSpan(newlayout UnderlineSpan(), 0, mystring.length(), 0);
        phoneone.setText(content);


        phoneone.setOnClickListener(newlayout View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phone = ph1;
                Intent intent = newlayout Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);


            }
        });

        //String text1 = "<a href='http://maps.google.com/maps'>"+" 9731276143"+ "</a>";
        final  String ph2 = phonetwo.getText().toString();
        //phonetwo.setText(Html.fromHtml(text1));
        String mystring1=ph2;
        SpannableString content1 = newlayout SpannableString(mystring1);
        content1.setSpan(newlayout UnderlineSpan(), 0, mystring1.length(), 0);
        phonetwo.setText(content1);
        phonetwo.setTextColor(Color.BLACK);

        phonetwo.setOnClickListener(newlayout View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phone = ph2;
                Intent intent = newlayout Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);

            }
        });

        close.setOnClickListener(newlayout View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
        myDialog.getWindow().setLayout(600,200);


    }

*/

package com.example.appsriv_02.loginwithfacebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import java.util.ArrayList;

/**
 * Created by appsriv-02 on 24/12/15.
 */
public class ExpandableAdapterForDealsPage extends BaseExpandableListAdapter
{

    private Context context;
    private ArrayList<Group> groups;
    GPSTracker gps;
    ImageButton button;
    TextView botte;
    Typeface tf;

    Point p;
    int linewidth;
    static int group_position;
    public static LinearLayout bottleLayout[] = new LinearLayout[10000];
    LinearLayout setBackground[] = new LinearLayout[10000];
    LinearLayout top_layout[] = new LinearLayout[10000];
    View belowline[] = new View[10000];
    LinearLayout groupnamelayout[]= new LinearLayout[10000];
    ImageView phone[] = new ImageView[10000];
    public ArrayList<Child> CustomListViewValuesArr = new ArrayList<Child>();

    public ExpandableAdapterForDealsPage(Context context, ArrayList<Group> groups)
    {
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        Group group = (Group) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.deals_grp_layout, null);

        }
        tf=Typeface.createFromAsset(context.getAssets(),"fonts/MYRIADPRO_REGULAR.OTF");
        bottleLayout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.minprice);
        //  relative_layout[groupPosition]=(RelativeLayout)convertView.findViewById(R.id.relative_layout);
        top_layout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.top_layout);
        TextView tv = (TextView) convertView.findViewById(R.id.group_name);
        TextView place_name = (TextView)convertView.findViewById(R.id.place_name);
        // TextView price=(TextView)convertView.findViewById(R.id.name);
        LinearLayout grpnamelayout=(LinearLayout)convertView.findViewById(R.id.grpnamelayout);
        belowline[groupPosition]=(View)convertView.findViewById(R.id.belowline);
        tv.setTypeface(tf);
      //  dist=(TextView)convertView.findViewById(R.id.dist);
//        dist.setTypeface(tf);
        botte=(TextView)convertView.findViewById(R.id.botte);
        botte.setTypeface(tf);
      //  TextView time=(TextView)convertView.findViewById(R.id.time);
      //  TextView timetext=(TextView)convertView.findViewById(R.id.timetext);
       // timetext.setTypeface(tf);
      //  timetext.setText("Happy Hour");
       // timetext.setTextColor(Color.BLACK);
        ImageView arrow = (ImageView)convertView.findViewById(R.id.arrowdown);
      //  LinearLayout vodkalayout=(LinearLayout)convertView.findViewById(R.id.vodkalayout);
        //locationLayout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.locationlayout);
     //   time.setTypeface(tf);
//        time.setText(NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourStart()+"-"+NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourEnds());

        //   ImageView discount=(ImageView)convertView.findViewById(R.id.discount);
        groupnamelayout[groupPosition]= (LinearLayout)convertView.findViewById(R.id.grpnamelayout);
        setBackground[groupPosition]= (LinearLayout)convertView.findViewById(R.id.hh);



        /*setBackground[groupPosition].post(newlayout Runnable() {
            public void run() {
                linewidth = setBackground[groupPosition].getWidth();
            }
        });
*/
        if (groupPosition==0)
        {

        }
       /* if (NonBeerExpandList.ExpListItems.get(groupPosition).isCheckRestaurants())
        {
            //setBackground[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
            // locationLayout[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
        }
        else
        {
            //  setBackground[groupPosition].setBackgroundColor(Color.WHITE);
            //locationLayout[groupPosition].setBackgroundColor(Color.WHITE);
        }

        if (NonBeerExpandList.nonbeer.equalsIgnoreCase("exceptBeer"))
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
       // dist.setText(group.getDist() + "Km");
        tv.setText(group.getName());
        place_name.setText(group.getPlaceName());



        if (isExpanded)
        {
            // price.setVisibility(View.INVISIBLE);

            top_layout[groupPosition].setVisibility(View.GONE);
          //  locationLayout[groupPosition].setBackgroundColor(Color.WHITE);
            // relative_layout[groupPosition].setVisibility(View.GONE);
            belowline[groupPosition].setVisibility(View.GONE);
           // locationLayout[groupPosition].setBackgroundColor(Color.WHITE);
            groupnamelayout[groupPosition].setBackgroundColor(Color.WHITE);
            bottleLayout[groupPosition].setBackgroundColor(Color.WHITE);


            botte.setVisibility(View.INVISIBLE);
            //dist.setVisibility(View.INVISIBLE);
            //time.setVisibility(View.GONE);
            //  discount.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
            //tv.setBackgroundResource(R.drawable.rectangletab);
            //  groupnamelayout[groupPosition].setBackgroundResource(R.drawable.threesiderectangle);


            /*if (NonBeerExpandList.ExpListItems.get(groupPosition).getRest_offers_happy_hour().equalsIgnoreCase("no"))
            {
                vodkalayout.setVisibility(View.GONE);
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
            }*/
        }
        else
        {
            top_layout[groupPosition].setVisibility(View.VISIBLE);
            setBackground[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
        //    locationLayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            // relative_layout[groupPosition].setVisibility(View.VISIBLE);
            belowline[groupPosition].setVisibility(View.VISIBLE);
         //   locationLayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            groupnamelayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            bottleLayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));


            /*if (NonBeerExpandList.ExpListItems.get(groupPosition).getRest_offers_happy_hour().equalsIgnoreCase("no"))
            {
                vodkalayout.setVisibility(View.GONE);
            }
            else
            {

                vodkalayout.setVisibility(View.VISIBLE);

                if (NonBeerExpandList.ExpListItems.get(groupPosition).getIsHappyHours().equalsIgnoreCase("no"))
                {
                    //vodkalayout.setVisibility(View.VISIBLE);
                    // vodkalayout.setBackgroundResource(R.drawable.graybackground);
                    time.setTextColor(Color.BLACK);
                }
                else
                {
                    //vodkalayout.setBackgroundResource(R.drawable.graybackground);
                *//**//*//**//*//*vodkalayout.setVisibility(View.VISIBLE);
                //vodkalayout.setBackgroundResource(R.drawable.graybackground);
                if(NonBeerExpandList.ExpListItems.get(groupPosition).getIsHappyHours().equalsIgnoreCase("no"))
                {
                    time.setTextColor(Color.BLACK);
                }
                else*//**//*
               *//**//* {
                    time.setTextColor(Color.parseColor("#46A13A"));
                }*//**//*
                    time.setTextColor(Color.parseColor("#46A13A"));
                *//**//*time.setVisibility(View.VISIBLE);
                time.setBackgroundResource(R.drawable.graybackground);*//**//*
                }
            }*/
            botte.setVisibility(View.VISIBLE);
          //  dist.setVisibility(View.VISIBLE);
           /* time.setVisibility(View.VISIBLE);
            time.setBackgroundResource(R.drawable.graybackground);*/
//            discount.setVisibility(View.VISIBLE);
            //tv.setBackgroundResource(0);
            arrow.setVisibility(View.VISIBLE);
            // linearLayout.setBackgroundResource(0);
        }



        return convertView;
    }
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Child child = (Child) getChild(groupPosition, childPosition);
        Group group = (Group) getGroup(groupPosition);
        ArrayList<Child> children =(ArrayList<Child>)getChild(groupPosition);
        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.deals_list, null);
        }
        ListView list=(ListView)convertView.findViewById(R.id.listViewwine);
        list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT ,children.size()*50));
        list.requestLayout();


        final ImageView arrowup = (ImageView)convertView.findViewById(R.id.arrowup);
        ImageView direction =(ImageView)convertView.findViewById(R.id.direction);
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
        timeText.setTextColor(Color.BLACK);
//        time.setText(NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourStart() + "-" + NonBeerExpandList.ExpListItems.get(groupPosition).getHappyHourEnds());


        // linewidth = setBackground[groupPosition].getWidth();
        //View line = (View)convertView.findViewById(R.id.line);
        //line.getLayoutParams().width=linewidth-19;


        arrowup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DealsNearYou.ExpandList.collapseGroup(groupPosition);
                //   Toast.makeText(context, "collapsed", Toast.LENGTH_SHORT).show();
            }
        });


        direction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                Toast.makeText(context,"You have Clicked direction button",Toast.LENGTH_SHORT).show();
            }
        });

        phone[groupPosition].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  phoneDialog("Bottomz Up", groupPosition, context);
                phone[groupPosition].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //phoneDialog("Bottomz Up", groupPosition, context);
                        phonePopUp(context, groupPosition);

                    }
                });

            }
        });


        DealsCustomAdapter adapter= new DealsCustomAdapter((Activity)context, children,groups);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        ImageView discount = (ImageView)convertView.findViewById(R.id.discount);
        discount.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Toast.makeText(context,"please wait for next release to get offers",Toast.LENGTH_SHORT).show();
            }
        });
        discount.setFocusable(false);

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

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void phonePopUp(final Context context,int value)
    {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.phone_customalert, null);

        final PopupWindow popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);

        popupWindow .setTouchable(true);
        popupWindow .setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(300);
        //popupWindow.showAsDropDown(discount,x,y,Gravity.CENTER_VERTICAL);
        //popupWindow .showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(phone[value],-50,2, Gravity.RIGHT);
        // popupView.setBackgroundResource(R.drawable.phonebackground);
        //  popupWindow.showAtLocation(popupView, Gravity.START, x+30 , y+30);
        final TextView phoneone=(TextView)popupView.findViewById(R.id.phoneone);
        final TextView phonetwo=(TextView)popupView.findViewById(R.id.phonetwo);
        LinearLayout linearLayout =(LinearLayout)popupView.findViewById(R.id.top);
        linearLayout.bringToFront();
        ImageView close = (ImageView)popupView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Toast.makeText(context,"closed",Toast.LENGTH_SHORT).show();
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

}

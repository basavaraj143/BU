package com.example.appsriv_02.loginwithfacebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;


public class ExpandListAdapterForBeerItems extends BaseExpandableListAdapter implements SectionIndexer, AbsListView.OnScrollListener
{
    private Context context;
    private ArrayList<Group> groups;
    GPSTracker gps;
    static TextView price,dist,minPrice;
    ImageButton button;
    static int group_position;
    TextView group_name;
    static LinearLayout budwieserlayout;

    private boolean manualScroll;
    static  LinearLayout minpricelayout[] = new LinearLayout [10000];
    static LinearLayout bottlelayout[] = new LinearLayout [10000];
    static LinearLayout locationlayout[] = new LinearLayout[10000];
    LinearLayout[] resBackgrount=new LinearLayout[10000];
  //  RelativeLayout[] relative_layout=newlayout RelativeLayout[10000];
    LinearLayout[] toplayout=new LinearLayout[10000];
    View[] belowline;
   // ImageView[] offerribben = new ImageView[10000];
    ExpandableListView expandableListView;
    public ArrayList<Child> CustomListViewValuesArr = new ArrayList<Child>();
    ImageView direction[] = new ImageView[1000];
    Typeface tf ;


    int x;
    int y;
    ImageView phone[] = new ImageView[10000];
    ImageView discount;

    public ExpandListAdapterForBeerItems(Context context, ArrayList<Group> groups,ExpandableListView expandableListView)
    {
        this.context = context;
        this.groups = groups;
        this.expandableListView=expandableListView;
        this.expandableListView.setOnScrollListener(this);

    }


    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.manualScroll = scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
    }

    @Override
    public void onScroll(AbsListView view,
                         int firstVisibleItem,
                         int visibleItemCount,
                         int totalItemCount) {}

    @Override
    public int getPositionForSection(int section) {
        if (manualScroll) {
            return section;
        } else {
            return expandableListView.getFlatListPosition(
                    ExpandableListView.getPackedPositionForGroup(section));
        }
    }

    // Gets called when scrolling the list manually
    @Override
    public int getSectionForPosition(int position) {
        return ExpandableListView.getPackedPositionGroup(
                expandableListView.getExpandableListPosition(position));
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
    public int getChildType(int groupPosition, int childPosition)
    {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        ArrayList<Child> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {

        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent)
    {


        ///parent.getLayoutParams().width
      Child child = (Child) getChild(groupPosition, childPosition);
        Group group = (Group) getGroup(groupPosition);
        ArrayList<Child> children =(ArrayList<Child>)getChild(groupPosition);
        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.beer_child, null);
        }
       /* resBackgrount[groupPosition].post(newlayout Runnable() {
            public void run() {
                linewidth = resBackgrount[groupPosition].getWidth();
            }
        });*/
      //  linewidth = resBackgrount[groupPosition].getWidth();
  //      line=(View)convertView.findViewById(R.id.line);
//        line.getLayoutParams().width=linewidth-13;


       //LinearLayout linearLayout=  (LinearLayout) convertView.findViewById(R.id.childlayout);
        //View v =(View)convertView.findViewById(R.id.view1);
       // linearLayout.setBackgroundResource(R.drawable.my_shape);
       // ImageButton location = (ImageButton)convertView.findViewById(R.id.direction);
        ListView list=(ListView)convertView.findViewById(R.id.listViewwine);

        list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (children.size()*55)));
        //list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        list.requestLayout();


        // discount = (ImageView)convertView.findViewById(R.id.discount);
        final ImageView arrowup = (ImageView)convertView.findViewById(R.id.arrowup);
        direction[groupPosition]=(ImageView)convertView.findViewById(R.id.direction);
        phone[groupPosition] =(ImageView)convertView.findViewById(R.id.phone);


        group_name= (TextView) convertView.findViewById(R.id.group_name);
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
       // timeText.setTextColor(Color.BLACK);
        time.setText(NewExpandaList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5)
                + "-" + NewExpandaList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5));


        //childdist=(TextView)convertView.findViewById(R.id.childdist);
//        childdist.setTypeface(tf);
       // childdist.setText(group.getDist() + "km");

        if (NewExpandaList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5).equalsIgnoreCase("00:00")
                &&NewExpandaList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5).equalsIgnoreCase("00:00"))
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
        arrowup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NewExpandaList.ExpandList.collapseGroup(groupPosition);
                //Toast.makeText(context, "collapsed", Toast.LENGTH_SHORT).show();
            }
        });

        collapse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NewExpandaList.ExpandList.collapseGroup(groupPosition);
                //Toast.makeText(context, "collapsed", Toast.LENGTH_SHORT).show();
            }
        });
        /*discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int[] pos = new int[2];
                discount.getLocationInWindow(pos);
                x = pos[0];
                y = pos[1] + discount.getHeight();
                // newExpandaList.messageDialog("Bottomz Up", NewExpandaList.ExpListItems.get(groupPosition).getName(), context);
                // callPopup(context);
                popForPressRevel(context, NewExpandaList.ExpListItems.get(groupPosition).getName());
            }
        });
        discount.setFocusable(false);*/

        direction[groupPosition].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //addressDialog("Bottomz Up", groupPosition, context);

                NewExpandaList.mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("direction")
                        .setAction("Submit")
                        .build());
                int[] pos = new int[2];
                // direction.getLocationInWindow(pos);
                direction[groupPosition].getLocationOnScreen(pos);
                x = pos[0];
                y = pos[1] + direction[groupPosition].getHeight();
                addressPopUp(context, groupPosition);


                //callPopup(context);
            }
        });

       /* direction.getViewTreeObserver().addOnGlobalLayoutListener(newlayout ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                int height = direction.getHeight();
                int width = direction.getWidth();
                int a = direction.getLeft();
                int b = direction.getTop();

                //don't forget to remove the listener to prevent being called again by future layout events:
                direction.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
*/
        phone[groupPosition].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //phoneDialog("Bottomz Up", groupPosition, context);

                NewExpandaList.mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Phone number")
                        .setAction("Submit")
                        .build());

                phonePopUp(context, groupPosition);

            }
        });


        BeerCustomAdapter adapter= new BeerCustomAdapter((Activity)context, children,groups);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

       /* if (childPosition==0)
        {

            location.setVisibility(View.VISIBLE);
           // v.setVisibility(View.VISIBLE);
            location.setOnClickListener(newlayout View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {


                }
            });
        }
        else
        {
            location.setVisibility(View.INVISIBLE);
          //  v.setVisibility(View.INVISIBLE);
        }*/

       /* TextView brand_name = (TextView) convertView.findViewById(R.id.brand_name);
        TextView price=(TextView)convertView.findViewById(R.id.name);
        TextView maxprice=(TextView)convertView.findViewById(R.id.maxPrice);
        TextView time = (TextView)convertView.findViewById(R.id.time);


        brand_name.setText(child.getBrand());
        price.setText(child.getPrice());
        maxprice.setText(child.getMinPrice());*/
       // convertView.callOnClick();
        timeText.setTextColor(Color.parseColor("#C4614A"));
        return convertView;
    }
    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        super.onGroupCollapsed(groupPosition);

    }

    @Override
    public void onGroupExpanded(int groupPosition)
    {
        super.onGroupExpanded(groupPosition);
        NewExpandaList.mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(NewExpandaList.ExpListItems.get(groupPosition).getName())
                .setAction("Restaurant")
                .setLabel(NewExpandaList.ExpListItems.get(groupPosition).getName())
                .build());
    }



    @Override
    public Object getGroup(int groupPosition)
    {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }



    @Override
    public int getChildTypeCount() {
        return super.getChildTypeCount();
    }



    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {

        belowline=new View[10000];
        tf=Typeface.createFromAsset(context.getAssets(),"fonts/MYRIADPRO_REGULAR.OTF");
        Group group = (Group) getGroup(groupPosition);
        if (convertView == null)
        {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.beergroup, null);
        }

        minpricelayout[groupPosition] =(LinearLayout)convertView.findViewById(R.id.minpricelayout);
        bottlelayout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.bottlelayout);
        locationlayout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.locationlayout);
        budwieserlayout=(LinearLayout)convertView.findViewById(R.id.bottlelayout);
        belowline[groupPosition]=(View)convertView.findViewById(R.id.belowline);
        TextView place_name=(TextView)convertView.findViewById(R.id.place_name);
        ImageView shadow= (ImageView) convertView.findViewById(R.id.shadow);

        //offerribben[groupPosition]=(ImageView)convertView.findViewById(R.id.offerribben);
       // offerribben[groupPosition].bringToFront();

       // relative_layout[groupPosition]=(RelativeLayout)convertView.findViewById(R.id.relative_layout);



        LinearLayout grpnamelayout=(LinearLayout)convertView.findViewById(R.id.grpnamelayout);


        group_name= (TextView) convertView.findViewById(R.id.group_name);
        price=(TextView)convertView.findViewById(R.id.name);
        dist=(TextView)convertView.findViewById(R.id.dist);
        minPrice=(TextView)convertView.findViewById(R.id.minPrice);
       // View view = (View)convertView.findViewById(R.id.view);

        group_name.setTypeface(tf);
        price.setTypeface(tf);
        dist.setTypeface(tf);
        minPrice.setTypeface(tf);
        place_name.setTypeface(tf);
       //ImageView discount=(ImageView)convertView.findViewById(R.id.discount);
       TextView time=(TextView)convertView.findViewById(R.id.time);
        TextView timeText=(TextView)convertView.findViewById(R.id.timetext);
        ImageView arrow = (ImageView)convertView.findViewById(R.id.arrowdown);
        arrow.bringToFront();
        resBackgrount[groupPosition]=(LinearLayout)convertView.findViewById(R.id.hh);
        LinearLayout timalayout = (LinearLayout) convertView.findViewById(R.id.timalayout);
        toplayout[groupPosition]=(LinearLayout)convertView.findViewById(R.id.toplayout);
       //linewidth=resBackgrount[groupPosition].getLayoutParams().width;
       // linewidth = widthlayout.getWidth();

        /*resBackgrount[groupPosition].post(newlayout Runnable() {
            public void run() {
                linewidth = resBackgrount[groupPosition].getWidth();
            }
        });
*/
       // View line =(View)convertView.findViewById(R.id.line);
        time.setTypeface(tf);
        timeText.setTypeface(tf);
        timeText.setText("Happy Hours");
        timeText.setTextColor(Color.BLACK);
        time.setText(NewExpandaList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5)
                + "-" + NewExpandaList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5));
        if (NewExpandaList.activeColumn.equalsIgnoreCase("pint"))
        {
             Typeface tfb=Typeface.createFromAsset(context.getAssets(),"fonts/MYRIADPRO_REGULAR.OTF");
           /* minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffd200"));

            bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffffff"));

            NewExpandaList.bottle.setBackgroundResource(R.drawable.whitetab);
            NewExpandaList.pint.setBackgroundResource(R.drawable.yellowtab);

*/
            int defaultTextColor = minPrice.getTextColors().getDefaultColor();
            minPrice.setTypeface(tfb,Typeface.BOLD);
            minPrice.setTextColor(Color.BLACK);
            price.setTextColor(defaultTextColor);
            NewExpandaList.pint.setTypeface(tfb,Typeface.BOLD);


        }
        else if (NewExpandaList.activeColumn.equalsIgnoreCase("bottle"))
        {
             Typeface tfb=Typeface.createFromAsset(context.getAssets(),"fonts/MYRIADPRO_REGULAR.OTF");
           /* minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffffff"));

            bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffd200"));
            NewExpandaList.bottle.setBackgroundResource(R.drawable.yellowtab);
            NewExpandaList.pint.setBackgroundResource(R.drawable.whitetab);*/
                price.setTypeface(tfb,Typeface.BOLD);
                price.setTextColor(Color.BLACK);
            int defaultTextColor = minPrice.getTextColors().getDefaultColor();
            minPrice.setTextColor(defaultTextColor);
                NewExpandaList.bottle.setTypeface(tfb,Typeface.BOLD);


        }
        group_position=groupPosition;
        if (group.isCheckRestaurants())
        {

           resBackgrount[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
            toplayout[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
            locationlayout[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
            minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
            bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));
            /*if (NewExpandaList.activeColumn.equalsIgnoreCase("pint"))
            {
                minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffd200"));

                bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));

                NewExpandaList.bottle.setBackgroundResource(R.drawable.whitetab);
                NewExpandaList.pint.setBackgroundResource(R.drawable.yellowtab);



            }
            else if (NewExpandaList.activeColumn.equalsIgnoreCase("bottle"))
            {
                minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#ccd9ff"));

                bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffd200"));
                NewExpandaList.bottle.setBackgroundResource(R.drawable.yellowtab);
                NewExpandaList.pint.setBackgroundResource(R.drawable.whitetab);


            }*/
        }
        else
        {
            toplayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            resBackgrount[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            locationlayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
        }


       // button .setFocusable(false);
        String MAX_PRICE=checkZeroValue(group.getPrice());
        String MIN_PRICE=checkZeroValue(group.getMinPrice());
        price.setText(MAX_PRICE);
        minPrice.setText(MIN_PRICE);
        dist.setText(group.getDist() + "Km");
        place_name.setText(group.getRes_locality());
       // childdist.setText(group.getDist());


        group_name.setText(group.getName());
        context = convertView.getContext();

       /* if (NewExpandaList.flag)
        {
            group_name.setTextColor(Color.YELLOW);
        }*/

        //when group is expanded
        if (isExpanded)
        {
            //belowline[groupPosition].setVisibility(View.GONE);
            //group_name.setSingleLine(false);
            //line.setVisibility(View.VISIBLE);
            resBackgrount[groupPosition].setVisibility(View.GONE);
            price.setVisibility(View.GONE);
            minPrice.setVisibility(View.GONE);
            dist.setVisibility(View.GONE);
           // offerribben[groupPosition].setVisibility(View.GONE);
            // time.setVisibility(View.GONE);
           // discount.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
           // belowline[groupPosition].setVisibility(View.GONE);
           // relative_layout[groupPosition].setVisibility(View.GONE);

//            line.getLayoutParams().width=linewidth-12;


            //colouring row section when expanded
            resBackgrount[groupPosition].setBackgroundColor(Color.WHITE);
            locationlayout[groupPosition].setBackgroundColor(Color.WHITE);
            toplayout[groupPosition].setBackgroundColor(Color.WHITE);
            minpricelayout[groupPosition].setBackgroundColor(Color.WHITE);
            bottlelayout[groupPosition].setBackgroundColor(Color.WHITE);
            /*if (NewExpandaList.activeColumn.equalsIgnoreCase("pint"))
            {
                minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffd200"));
                bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffffff"));

                NewExpandaList.bottle.setBackgroundResource(R.drawable.whitetab);
                NewExpandaList.pint.setBackgroundResource(R.drawable.yellowtab);

            }
            else if (NewExpandaList.activeColumn.equalsIgnoreCase("bottle"))
            {
                minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffffff"));
                bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#ffd200"));

                NewExpandaList.bottle.setBackgroundResource(R.drawable.yellowtab);
                NewExpandaList.pint.setBackgroundResource(R.drawable.whitetab);
            }
*/
            //group_name.setBackgroundResource(R.drawable.rectangletab);
          //  grpnamelayout.setBackgroundResource(R.drawable.threesiderectangle);
            grpnamelayout.setBackgroundColor(Color.parseColor("#E0CA95"));
            group_name.setGravity(1);


            //to check wether restaurants has offer or not
            if (NewExpandaList.ExpListItems.get(groupPosition).getRest_offers_happy_hour().equalsIgnoreCase("no"))
            {
                timalayout.setVisibility(View.INVISIBLE);
                if (NewExpandaList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5).equalsIgnoreCase("00:00")
                        &&NewExpandaList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5).equalsIgnoreCase("00:00"))
                {
                    timalayout.setVisibility(View.INVISIBLE);
                }
              //  offerribben[groupPosition].setVisibility(View.GONE);
            }
            else
            {
                if (NewExpandaList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5).equalsIgnoreCase("00:00")
                        &&NewExpandaList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5).equalsIgnoreCase("00:00"))
                {
                    timalayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    timalayout.setBackgroundResource(0);
                    //offerribben[groupPosition].setVisibility(View.VISIBLE);

                    // to check wether the current time is happy hour r not
                    if (NewExpandaList.ExpListItems.get(groupPosition).getIsHappyHours().equalsIgnoreCase("no"))
                    {
                        time.setTextColor(Color.BLACK);
                    } else
                    {
                        timalayout.setVisibility(View.VISIBLE);
                        time.setTextColor(Color.parseColor("#46A13A"));
                    }

                }

            }
            shadow.setVisibility(View.GONE);
            group_name.setVisibility(View.VISIBLE);
        }
        //when group collapsed
        else
        {
          //  resBackgrount.setBackgroundColor(Color.parseColor("#ccd9ff"));
          //  line.setVisibility(View.GONE);
           // offerribben[groupPosition].setVisibility(View.VISIBLE);
            group_name.setVisibility(View.VISIBLE);


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
            resBackgrount[groupPosition].setVisibility(View.VISIBLE);
            belowline[groupPosition].setVisibility(View.VISIBLE);
            //relative_layout[groupPosition].setVisibility(View.VISIBLE);
            toplayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            resBackgrount[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            locationlayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            minpricelayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
            bottlelayout[groupPosition].setBackgroundColor(Color.parseColor("#f9e5bd"));
                price.setVisibility(View.VISIBLE);
                minPrice.setVisibility(View.VISIBLE);
                dist.setVisibility(View.VISIBLE);

            if (NewExpandaList.ExpListItems.get(groupPosition).getRest_offers_happy_hour().equalsIgnoreCase("no"))
            {
                timalayout.setVisibility(View.INVISIBLE);
            }
            else
            {

                timalayout.setVisibility(View.VISIBLE);
                //timalayout.setBackgroundResource(R.drawable.graybackground);
                if(NewExpandaList.ExpListItems.get(groupPosition).getIsHappyHours().equalsIgnoreCase("no"))
                {
                    time.setTextColor(Color.BLACK);
                }
                else
                {
                    time.setTextColor(Color.parseColor("#46A13A"));
                }
                if (NewExpandaList.ExpListItems.get(groupPosition).getHappyHourStart().substring(0,5).equalsIgnoreCase("00:00")
                        &&NewExpandaList.ExpListItems.get(groupPosition).getHappyHourEnds().substring(0,5).equalsIgnoreCase("00:00"))
                {
                    timalayout.setVisibility(View.INVISIBLE);
                }


                /*
                time.setVisibility(View.VISIBLE);
                time.setBackgroundResource(R.drawable.graybackground);*/
            }
            //discount.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.VISIBLE);
           // group_name.setBackgroundResource(0);
            grpnamelayout.setBackgroundResource(0);


        }

        timeText.setTextColor(Color.parseColor("#C4614A"));
        return convertView;
    }



    private void popForPressRevel(final Context context,final String message )
    {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.custom_revel_offer_alert, null);

      final   PopupWindow  popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,
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
        login.setOnClickListener(new View.OnClickListener()
        {
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
            public void onClick(View v) {
             //   Toast.makeText(context,"closed",Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
    }




    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void addressPopUp(final Context context,final int value)
    {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        final View popupView = layoutInflater.inflate(R.layout.address_customalert, null);

       final PopupWindow popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);


        popupWindow .setTouchable(true);
        popupWindow .setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.isOutsideTouchable();
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //popupWindow.dismiss();
                return false;
            }
        });

        /*popupWindow.setTouchInterceptor(newlayout View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });*/
        DisplayMetrics display = context.getResources().getDisplayMetrics();

        int width = display.widthPixels;
        int height = display.heightPixels;

        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(width/2);
       // popupWindow.showAsDropDown(direction, 10, 10);
        popupWindow.showAsDropDown(direction[value],-20,3,Gravity.RIGHT);
        //popupWindow.sho
        //popupWindow .showAtLocation(popupView, Gravity.CENTER, x-30, y-30);
        // popupView.setBackgroundResource(R.drawable.phonebackground);
        //  popupWindow.showAtLocation(popupView, Gravity.START, x+30 , y+30);
        TextView address=(TextView)popupView.findViewById(R.id.address);
        TextView findOnGoogle=(TextView)popupView.findViewById(R.id.findongoogle);
        ImageView close =(ImageView)popupView.findViewById(R.id.close);
        String text = "<a href='http://maps.google.com/maps'> find on google map </a>";
        findOnGoogle.setText(Html.fromHtml(text));
        findOnGoogle.setTextColor(Color.BLACK);

        address.setText(NewExpandaList.ExpListItems.get(value).getPlaceName());

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


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + NewExpandaList.ExpListItems.get(value).getRes_lat() +
                        "," + NewExpandaList.ExpListItems.get(value).getRes_lng() + ""));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);

            }
        });

        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });

    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void phonePopUp(final Context context,int value)
    {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.phone_customalert, null);

      final   PopupWindow  popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);

        popupWindow .setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        DisplayMetrics display = context.getResources().getDisplayMetrics();

        int width = display.widthPixels;
        int height = display.heightPixels;
        popupWindow.setWidth(width/2);

        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //popupWindow.dismiss();
                return false;
            }
        });

        //popupWindow.showAsDropDown(discount,x,y,Gravity.CENTER_VERTICAL);
         //popupWindow .showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(phone[value],-50,2,Gravity.RIGHT);
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

        phoneone.setText(groups.get(group_position).getRes_phone1());
        phonetwo.setText(groups.get(group_position).getRes_phone2());

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





    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }










    // method to replace zero with '--'
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




}

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
       */
/* DisplayMetrics metrics = myDialog.getContext().getResources()
                .getDisplayMetrics();
        final int width = metrics.widthPixels;
        AlertDialog alertDialog = (AlertDialog) myDialog;
        View view = alertDialog.getWindow().getDecorView()
                .findViewById(android.R.id.content);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = 4 * width / 5; // 80% of screen
        layoutParams.gravity = Gravity.CENTER;
        view.setLayoutParams(layoutParams);
        alertDialog.getWindow().setBackgroundDrawable(newlayout ColorDrawable(Color.TRANSPARENT));*//*





        //myDialog.setTitle(title);
        // myDialog.getWindow().setBackgroundDrawable(newlayout ColorDrawable(Color.BLACK));
        myDialog.setCancelable(false);
        //cancel the alert when user clicks outside the alert message
        myDialog.setCanceledOnTouchOutside(true);

       // myDialog.getWindow().setBackgroundDrawableResource(R.drawable.addresspopback);

        TextView address=(TextView)myDialog.findViewById(R.id.address);
        TextView findOnGoogle=(TextView)myDialog.findViewById(R.id.findongoogle);
        ImageView close =(ImageView)myDialog.findViewById(R.id.close);
        String text = "<a href='http://maps.google.com/maps'> find on google map </a>";
        findOnGoogle.setText(Html.fromHtml(text));
        findOnGoogle.setTextColor(Color.BLACK);

        address.setText(NewExpandaList.ExpListItems.get(value).getPlaceName());

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


                Intent intent = newlayout Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + NewExpandaList.res_lat.get(value) +
                        "," + NewExpandaList.res_long.get(value) + ""));
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


 /*
            try {
                //String string1 = "20:11:13";
               // String string1 = NewExpandaList.happypyHourStart.get(groupPosition);
                String string1 = group.getHappyHourStart().substring(0,5);
                Date time1 = newlayout SimpleDateFormat("HH:mm:ss").parse(string1);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(time1);

                //String string2 = "14:49:00";
                //String string2 = NewExpandaList.happypyHourEnd.get(groupPosition);
                String string2 = group.getHappyHourEnds().substring(0,5);
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
                        *//*time.setVisibility(View.VISIBLE);
                        time.setBackgroundResource(R.drawable.graybackground);*//*
                    time.setTextColor(Color.GREEN);
                    System.out.println("time start "+string1);
                    System.out.println("time ends "+string2);
                    System.out.println("current time "+curTime);
                }
                else
                {
                    time.setTextColor(Color.BLACK);
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }*/


       /* button.setOnClickListener(newlayout View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = newlayout Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + NewExpandaList.res_lat.get(groupPosition) +
                        "," + NewExpandaList.res_long.get(groupPosition) + ""));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);
            }
        });*/

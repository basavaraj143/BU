package com.example.appsriv_02.loginwithfacebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

    public class CustomAdapterWine extends BaseAdapter {
        Context context;
        List<RowItem> rowItems;
    GPSTracker gps;

        ImageButton location;
        public CustomAdapterWine(Context context, List<RowItem> items) {
            this.context = context;
            this.rowItems = items;
        }

        /*private view holder class*/
        private class ViewHolder
        {

            TextView distance;
            TextView place;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.winechild, null);
                holder = new ViewHolder();
                holder.place = (TextView) convertView.findViewById(R.id.wine1);
                holder.distance = (TextView) convertView.findViewById(R.id.wine2);
                location=(ImageButton)convertView.findViewById(R.id.location);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            RowItem rowItem = (RowItem) getItem(position);

            holder.place.setText(Wineclass.dist.get(position).toString().substring(0,4)+"km");
            holder.distance.setText(rowItem.getTitle());


            location.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
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
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + Wineclass.reslat.get(position) +
                            "," + Wineclass.reslong.get(position) + ""));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    context.startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return rowItems.indexOf(getItem(position));
        }

}
/*
color codes
Yellow – #ffd200

Grey – #e9e9e9

Red buttons - #bd1004
*/

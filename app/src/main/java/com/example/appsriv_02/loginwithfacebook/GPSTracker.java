package com.example.appsriv_02.loginwithfacebook;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}

	//method declaration for GPS status
	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 *
	 * @return boolean
	 */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location)
	{

	}

	@Override
	public void onProviderDisabled(String provider)
	{

	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	//Function to find the latitude and longitude from the Address

	public ArrayList getLatLOng(String loc)
	{

		ArrayList list = new ArrayList();
		if (Geocoder.isPresent()) {
			try {
				String location = loc;
				Geocoder gc = new Geocoder(mContext);
				List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

				List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
				for (Address a : addresses) {
					if (a.hasLatitude() && a.hasLongitude()) {
						ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
						LatLng latLng=new LatLng(a.getLatitude(),a.getLongitude());
						double latitude=latLng.latitude;
						double longitude=latLng.longitude;
						list.add(latitude);
						list.add(longitude);
					}
				}
			} catch (IOException e)
			{
				// handle the exception
				e.printStackTrace();
//				showSettingsAlert();
			}
		}
		return list;
	}






	public static JSONObject getLocationInfo(String address)
	{
		String s=address;
	//	s=address;
		StringBuilder stringBuilder = new StringBuilder();
		HttpPost httppost=null;
		HttpClient client=null;
		s = s.replaceAll(" ","%20");
		try
		{

			httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + s + "&sensor=false");
			client= new DefaultHttpClient();
			HttpResponse response;
			stringBuilder = new StringBuilder();


			response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		}
		catch (ClientProtocolException e)
		{

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(client!=null)
			{
				client.getConnectionManager().shutdown();
			}
			if (httppost!=null)
			{
			}
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}




	public static ArrayList getLatitudeLongitude(String address)
	{
		String s=address;
		//	s=address;
		StringBuilder stringBuilder = new StringBuilder();
		HttpPost httppost=null;
		HttpClient client=null;
		s = s.replaceAll(" ","%20");
		ArrayList list = new ArrayList();
		try
		{

			httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + s + "&sensor=false");
			client= new DefaultHttpClient();
			HttpResponse response;
			stringBuilder = new StringBuilder();


			response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		}
		catch (ClientProtocolException e)
		{

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(client!=null)
			{
				client.getConnectionManager().shutdown();
			}
			if (httppost!=null)
			{
			}
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//JSONObject jsonObj = new JSONObject(result.toString());
			JSONArray resultJsonArray = jsonObject.getJSONArray("results");

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
			list.add(lat);
			list.add(lng);


			//LatLng point = new LatLng(lat, lng);


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return list;
	}



}





package com.example.appsriv_02.loginwithfacebook;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@SuppressWarnings({"all"})
public class InternetConnectionDetector
{
	
	private Context _context;

	public InternetConnectionDetector(Context context){
		this._context = context;
	}

	// method to get the status of the internet

	public boolean isConnectingToInternet()
	{
		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  if (connectivity != null) 
		  {
			  NetworkInfo[] info = connectivity.getAllNetworkInfo();
			  if (info != null) 
				  for (int i = 0; i < info.length; i++) 
					  if (info[i].getState() == NetworkInfo.State.CONNECTED)
					  {
						  return true;
					  }
		  }
		  return false;
	}

	//this is the method declaration for the Login with Email
	private static final String OPEN_LOGIN_API = "http://demos.dignitasdigital.com/bottomzup/login.php?emailid=%s&password=%s1";
	public static JSONObject getJSON(Context context, String email,String password){

		HttpURLConnection connection=null;

		try {
			URL url = new URL(String.format(OPEN_LOGIN_API, email,password));

			connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("x-api-key","11111");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null) json.append(tmp).append("\n");
			reader.close();
			JSONObject data = new JSONObject(json.toString());

			return data;
		}
		catch(Exception e)
		{
			return null;
		}
		finally {
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	//this is the method declaration for the Login with G+
private static final String OPEN_GOOGLE_LOGIN_API =
		"http://demos.dignitasdigital.com/bottomzup/googlelogin.php?emailid=%s&googleid=%s1";
	public static JSONObject getGooglePlusJSON(Context context, String email,String password){

		HttpURLConnection connection=null;

		try {
			URL url = new URL(String.format(OPEN_GOOGLE_LOGIN_API, email,password));
			Log.i("bottoms","facebooid " +url.toString());

			connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("x-api-key","11111");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null) json.append(tmp).append("\n");
			reader.close();
			JSONObject data = new JSONObject(json.toString());

			return data;
		}
		catch(Exception e)
		{
			return null;
		}
		finally {
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	//this is the method declaration for the Login with Facebook
	private static final String OPEN_FACEBOOK_LOGIN_API =
			" http://demos.dignitasdigital.com/bottomzup/facebooklogin.php?emailid=%s&facebookid=%s1";
	public static JSONObject getFacebookJSON(Context context, String email,String password){

		HttpURLConnection connection=null;

		try {
			URL url = new URL(String.format(OPEN_FACEBOOK_LOGIN_API, email,password));

			connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("x-api-key","11111");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null) json.append(tmp).append("\n");
			reader.close();
			JSONObject data = new JSONObject(json.toString());

			return data;
		}
		catch(Exception e)
		{
			return null;
		}
		finally {
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	//this is the method declaration to find the distance from API
	private static final String OPEN_DISTANCE_LOGIN_API =
			"http://demos.dignitasdigital.com/bottomzup/distancecalc.php?lat=%s&long=%s&km=5&records=4";
	public static JSONArray getDistanceJSON(Context context, double lattitude,double longitude)
	{

		HttpURLConnection connection=null;

		try
		{
			URL url = new URL(String.format(OPEN_DISTANCE_LOGIN_API, lattitude,longitude));

			connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("x-api-key","11111");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null) json.append(tmp).append("\n");
			reader.close();
			JSONArray data = new JSONArray(json.toString());

			return data;
		}
		catch(Exception e)
		{
			return null;
		}
		finally {
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}


	//this is the method declaration to Search the liquors
	private static final String PLACE_API = "http://demos.dignitasdigital.com/bottomzup/liquors.php?find=%s";
	public static JSONArray getSearchBeer(Context context, String beer)
	{

		HttpURLConnection connection=null;

		try {
			URL url = new URL(String.format(PLACE_API, beer));

			connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("x-api-key","11111");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null) json.append(tmp).append("\n");
			reader.close();
			JSONArray data = new JSONArray(json.toString());

			return data;
		}
		catch(Exception e)
		{
			return null;
		}
		finally {
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}



	//this is the method declaration to find the Latitude and Longitude from the address
	private static final String LOC_LAT_LONG_API =
			"http://maps.google.com/maps/api/geocode/json?address=%s&sensor=false";
	public static JSONObject latLong(Context context, String address){

		HttpURLConnection connection=null;

		try {
			URL url = new URL(String.format(LOC_LAT_LONG_API, address));

			connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("x-api-key","11111");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null) json.append(tmp).append("\n");
			reader.close();
			JSONObject data = new JSONObject(json.toString());

			return data;
		}
		catch(Exception e)
		{
			return null;
		}
		finally {
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}


	public static JSONArray getNearByBarnew(Context context, double lattitude,double longitude,String brand)
	{
		HttpURLConnection connection=null;
		JSONArray myJsonObject=null;

		String brandUrl=brand.replaceAll(" ","%20");

		String NEAR_BY_BAR_LF="http://demos.dignitasdigital.com/bottomzup/searchresultV2.php?lat="+lattitude+"&long="+longitude+"&km=2&records=500&query="+brandUrl;
		//String s1="http://demos.dignitasdigital.com/bottomzup/radmin/get_brandmaster_for_category.php?category=beer";

		Log.i("bottoms","search url "+NEAR_BY_BAR_LF);
		HttpParams httpParameters = new BasicHttpParams();
		int timeout1 = 1000*10;
		int timeout2 = 1000*10;
		//HttpConnectionParams.setConnectionTimeout(httpParameters, timeout1);
		//HttpConnectionParams.setSoTimeout(httpParameters, timeout2);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		String jsonString = "";
		HttpGet request;
		try {
			request = new HttpGet(new URI(NEAR_BY_BAR_LF));
			request.addHeader("User-Agent", "Android");

			HttpResponse response = httpclient.execute(request);

			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				jsonString = out.toString();
			}
			myJsonObject = new JSONArray(jsonString);


			return myJsonObject;

		}

		catch(ConnectException e)
		{
			// handle your exception here, maybe something like
			//Toast.makeText(Homescreen.this,"Error!",Toast.LENGTH_SHORT).show();
			Log.i("bottoms", " Error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//finish(); // if your are within activity class, otherwise call finish on your activity
		}


		catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context,"server taking too much time ",Toast.LENGTH_SHORT).show();


		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

//this is the method declaration to find the distance between two places manually
	/*public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
		double earthRadius = 6371000; //meters
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
						Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		float dist = (float) (earthRadius * c);

		return dist;
	}
*/

}

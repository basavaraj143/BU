package com.example.appsriv_02.loginwithfacebook;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;


public class NearByBar
{
    //this method is to call the neraBy restaurant API
    private static String NEAR_BY_BAR="http://demos.dignitasdigital.com/bottomzup/nearbyrestaurant.php?lat=%s&long=%s1&km=10&records=20";
   // private static String NEAR_BY_BAR="http://demos.dignitasdigital.com/bottomzup/nearby.php?lat=%s&long=%s1&km=5&records=4";
    //http://demos.dignitasdigital.com/bottomzup/nearby.php?lat=28.6376279&long=77.0696547&km=5&records=4
    public static JSONArray getNearByBar(Context context, double lattitude,double longitude)
    {
        HttpURLConnection connection=null;

        try {
            URL url = new URL(String.format(NEAR_BY_BAR, lattitude,longitude));

            Log.i("bottoms" , "near by bar url "+url.toString());
         //   Log.i("bottoms" ,"Search URL "+NEAR_BY_BAR);
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

    // this method declaration for fetching the API for oarticular brand

    public static JSONArray getNearByBarnew(Context context, double lattitude,double longitude,String brand)
    {
        HttpURLConnection connection=null;

        //"http://10.0.2.2:51382/RestServiceImpl.svc/jsons/?Location="+items+"&GROUP="+items1+"&asondate="+dates;
        String brandUrl=brand.replaceAll(" ","%20");


        //Log.i("bottoms" ,"brandUrl " +brandUrl);
        //String NEAR_BY_BAR_LF="http://demos.dignitasdigital.com/bottomzup/searchresult.php?lat="+lattitude+"&long="+longitude+"&km=5&records=15&query="+brandUrl;
        //http://demos.dignitasdigital.com/bottomzup/searchresultV2.php?lat=28.6345345&long=77.0749759&km=50&records=10&query=Beer
        String NEAR_BY_BAR_LF="http://demos.dignitasdigital.com/bottomzup/searchresultV2.php?lat="+lattitude+"&long="+longitude+"&km=2&records=20&query="+brandUrl;
        try {
            //String encodedurl = URLEncoder.encode(uuu, "UTF-8");
            URI uri = new URI(NEAR_BY_BAR_LF.replace(" ", "%20"));
           // URLEncoder.encode(NEAR_BY_BAR_LF, "UTF-8");
            URL url = new URL(NEAR_BY_BAR_LF);
            Log.i("bottoms" ,"Near by bar new url  "+NEAR_BY_BAR_LF);

            System.out.println(NEAR_BY_BAR_LF);
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



    private static String NEAR_BY_BARLOOK_FURTHER="http://demos.dignitasdigital.com/bottomzup/nearby.php?lat=%s&long=%s1&km=5&records=20";
    //http://demos.dignitasdigital.com/bottomzup/nearby.php?lat=28.6376279&long=77.0696547&km=5&records=4
    public static JSONArray getNearByBarLookFurther(Context context, double lattitude,double longitude)
    {
        HttpURLConnection connection=null;

        try {
            URL url = new URL(String.format(NEAR_BY_BARLOOK_FURTHER, lattitude,longitude));

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

    public static JSONArray getNearByBarnewLookFurther(Context context, double lattitude,double longitude,double km,String brand)
    {
        HttpURLConnection connection=null;

        //"http://10.0.2.2:51382/RestServiceImpl.svc/jsons/?Location="+items+"&GROUP="+items1+"&asondate="+dates;
        String brandUrl=brand.replaceAll(" ","%20");
        Log.i("bottoms" ,"brandUrl " +brandUrl);
       // String uuu="http://demos.dignitasdigital.com/bottomzup/searchresult.php?lat="+lattitude+"&long="+longitude+"&km="+km+"&records=20&query="+brandUrl;
        String uuu="http://demos.dignitasdigital.com/bottomzup/searchresultV2.php?lat="+lattitude+"&long="+longitude+"&km="+km+"&records=20&query="+brandUrl;
        try {;
            //String encodedurl = URLEncoder.encode(uuu, "UTF-8");
            URI uri = new URI(uuu.replace(" ", "%20"));
            URLEncoder.encode(uuu, "UTF-8");
            URL url = new URL(uuu);
            Log.i("bottoms" ,"getNearByBarnewLookFurther "+uuu);

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



   // private static String NEAR_BY_BAR="http://demos.dignitasdigital.com/bottomzup/nearbyrestaurant.php?lat=%s&long=%s1&km=5&records=4";
    // private static String NEAR_BY_BAR="http://demos.dignitasdigital.com/bottomzup/nearby.php?lat=%s&long=%s1&km=5&records=4";
    //http://demos.dignitasdigital.com/bottomzup/nearby.php?lat=28.6376279&long=77.0696547&km=5&records=4

    private static String FINDLIQUOR="http://demos.dignitasdigital.com/bottomzup/searchwb.php?lat=%s&long=%s1&km=15&records=20";
    public static JSONArray findLiquor(Context context, double lattitude,double longitude)
    {
        HttpURLConnection connection=null;

        try {
            URL url = new URL(String.format(FINDLIQUOR, lattitude,longitude));

            Log.i("bottoms" , "findLiquor "+url.toString());
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

    private static String WINEANDBEER="http://demos.dignitasdigital.com/bottomzup/searchwb.php?lat=%s&long=%s1&km=%s&records=20";
    public static JSONArray BeerAndWineShop(Context context, double lattitude,double longitude ,int km)
    {
        HttpURLConnection connection=null;

        try {
            URL url = new URL(String.format(WINEANDBEER, lattitude,longitude,km));

            Log.i("bottoms" , "BeerAndWineShop "+url.toString());
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

}


//http://demos.dignitasdigital.com/bottomzup/radmin/get_brandmaster_for_category.php?category=Beer
//near by bar url http://demos.dignitasdigital.com/bottomzup/radmin/nearbyrestaurant.php?lat=28.6314512&long=77.21666721&km=10&records=20
//Near by bar url with brand  http://demos.dignitasdigital.com/bottomzup/radmin/searchresultV2.php?lat=28.6314512&long=77.2166672&km=2&records=20&query=Beer
//getNearByBarnewLookFurther http://demos.dignitasdigital.com/bottomzup/radmin/searchresultV2.php?lat=28.6314512&long=77.2166672&km=7.0&records=20&query=Beer
// findLiquor http://demos.dignitasdigital.com/bottomzup/radmin/searchwb.php?lat=28.6314512&long=77.21666721&km=15&records=5
// BeerAndWineShop http://demos.dignitasdigital.com/bottomzup/radmin/searchwb.php?lat=28.6314512&long=77.21666721&km=2&records=5
//to search liquor char by char dropdown http://demos.dignitasdigital.com/bottomzup/radmin/liquors.php?find=b
package com.example.appsriv_02.loginwithfacebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainClass extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener
{
    ImageButton login,login_facebook;
    InternetConnectionDetector cd;
    Boolean isInternetPresent = false;
    CallbackManager callbackManager;
    Context context;
    boolean loggedIn;
    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "bottoms";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress,mSignInClicked;

    private ConnectionResult mConnectionResult;

    private Tracker mTracker;
//https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyC45IqTyfdeO5SzyLDGAVWiwADSSv70S6g&input=connaugh&types=(regions)&components=country:IN





    /*  private SignInButton btnSignIn;*/
    private ImageButton google;
    ShareDialog shareDialog;
    // Profile pic image size in pixels
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build());


        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(MainClass.this);
        callbackManager = CallbackManager.Factory.create();

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.appsriv_02.loginwithfacebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        cd = new InternetConnectionDetector(getApplicationContext());
        login = (ImageButton) findViewById(R.id.login);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO_SEMIBOLD.OTF");

        TextView legalage1 =(TextView)findViewById(R.id.legalage1);
        TextView legalage2 =(TextView)findViewById(R.id.legalage2);
        legalage1.setTypeface(tf);
        legalage2.setTypeface(tf);
        //login.startAnimation(animation);
        login_facebook = (ImageButton) findViewById(R.id.login_facebook);
        google = (ImageButton) findViewById(R.id.google);
        isInternetPresent = cd.isConnectingToInternet();

        //signin with google button is clicked
        google.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signInWithGplus();
            }
        });


        //on let's go button clicked
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {

                isInternetPresent = cd.isConnectingToInternet();
                arg0.clearAnimation();
                if (isInternetPresent) // to chack the internet connectivity
                {
                    //if internet is connected moving to next page
                    Intent shopIntent = new Intent(MainClass.this, Homescreen.class);
                    shopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(shopIntent);
                    overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
                } else
                {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    // display the alert message
                    showAlertDialog(MainClass.this, "No Internet Connection", "Please Try again.", false);
                }

            }


        });

        //facebook login code
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {


//...

            //...


            @Override
            public void onSuccess(LoginResult loginResult)
            {
                loggedIn = isFacebookLoggedIn();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                JSONObject jsonObject = response.getJSONObject();
                                try {
                                    String id = jsonObject.getString("id");
                                    String emailText = jsonObject.getString("email");


                                    //sending the user and email to API for login with facebook API
                                    JSONObject object1 = InternetConnectionDetector.getFacebookJSON(getApplicationContext(), emailText, id);
                                    String status = object1.getString("status");
                                    //Log.i(TAG, "FacebookStatus " + status);

                                    Log.d("Success", "Login");
                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                Log.i(TAG, response.toString());
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
                Intent shopIntent = new Intent(MainClass.this, Homescreen.class);
                startActivity(shopIntent);
                if (loggedIn)
                {
                    startActivity(shopIntent);
                }
                loggedIn = isFacebookLoggedIn();


            }

            public boolean isFacebookLoggedIn()
            {
                return AccessToken.getCurrentAccessToken() != null;
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainClass.this, "Login Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainClass.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });

        //on facebook button clicked
        login_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //calling the facebook login with required permission
                LoginManager.getInstance().logInWithReadPermissions(MainClass.this, Arrays.asList("public_profile", "user_friends"));

            }
        });

        GoogleAnalyticsApplication application = (GoogleAnalyticsApplication) getApplicationContext();
        mTracker = application.getDefaultTracker();


    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Login Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e)
            {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress)
        {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }


    }

    private void facebookPost() {
        //check login
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Log.d(TAG, ">>>" + "Signed Out");
        } else {
            Log.d(TAG, ">>>" + "Signed In");
        }
    }



    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
       getProfileInformation();

        Intent showTasks = new Intent(MainClass.this, Homescreen.class);
        startActivity(showTasks);

    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
          //  btnSignIn.setVisibility(View.GONE);
          ////  btnSignOut.setVisibility(View.VISIBLE);
//            btnRevokeAccess.setVisibility(View.VISIBLE);
            // llProfileLayout.setVisibility(View.VISIBLE);
        } else {
           // btnSignIn.setVisibility(View.VISIBLE);
          //  btnSignOut.setVisibility(View.GONE);
           // btnRevokeAccess.setVisibility(View.GONE);
            // llProfileLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation()

    {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
             String   emailString = Plus.AccountApi.getAccountName(mGoogleApiClient);
              String  id =currentPerson.getId().toString();

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + emailString
                        + ", Image: " + personPhotoUrl +", googleId " +id);
                JSONObject object = InternetConnectionDetector.getGooglePlusJSON(getApplicationContext(), emailString, id);
                String gPlusStatus= object.getString("status");
                Log.i(TAG,"GooglePlusStatus "+gPlusStatus );
                //  txtName.setText(personName);
                //   txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                //      newlayout LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }


    private void signInWithGplus()
    {
        if (!mGoogleApiClient.isConnecting())
        {
            mSignInClicked = true;
            resolveSignInError();

        }

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting())
            {
                mGoogleApiClient.connect();
            }
        }
    }



    // method to show alert message
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
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
}

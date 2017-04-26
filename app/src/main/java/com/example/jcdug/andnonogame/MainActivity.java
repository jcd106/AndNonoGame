package com.example.jcdug.andnonogame;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amazonaws.regions.Region;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumedRoleUser;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Activity for the home screen of the application
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static PuzzleDatabase db;
    private static final String TAG = "SignInMainActivity";
    private static final int RC_SIGN_IN_MAIN = 1;

    private GoogleApiClient mGoogleApiClient;

    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonDynamoDBClient ddbClient;
    private static DynamoDBMapper mapper;
    Map<String, String> logins = new HashMap<String, String>();

    private static boolean isSignedIn = false;

    /**
     * Creates the view for the activity
     * and initializes the PuzzleDatabase
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new PuzzleDatabase(this);


        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic profile.
        // ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("639961835043-mjg2pqa2ko8gkvcn69qbni7g4v21d4i1.apps.googleusercontent.com")
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // get the context for the current activity
                //"749534254956", // your AWS Account id
                "us-west-2:2c3043a2-bcad-42d9-9c0f-89ccaf7c8c7c", // your identity pool id
                //"arn:aws:iam::749534254956:role/Cognito_AndNonoGameUnauth_Role", // an unauthenticated role ARN
                //"arn:aws:iam::749534254956:role/Cognito_AndNonoGameAuth_Role",// an authenticated role ARN
                Regions.US_WEST_2 //Region
        );

        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        ddbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        mapper = new DynamoDBMapper(ddbClient);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.
            // Cross-device single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_MAIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String authCode = acct.getServerAuthCode();
            String token = acct.getIdToken();

            logins.put("accounts.google.com", token);
            credentialsProvider.setLogins(logins);
            new Thread(new Runnable() {
                public void run() {
                    credentialsProvider.refresh();
                }
            }).start();

            isSignedIn = true;
            //Possiblly set textview with acct.getDisplayName();
            updateUI(isSignedIn);
        } else {
            isSignedIn = false;
            updateUI(isSignedIn);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_MAIN);

        //updateUI(true);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        isSignedIn = false;
                        updateUI(isSignedIn);
                        //logins.clear();
                        //credentialsProvider.setLogins(logins);
                        credentialsProvider.clearCredentials();

                        new Thread(new Runnable() {
                            public void run() {
                                credentialsProvider.refresh();
                            }
                        }).start();
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(isSignedIn);
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.button_upload).setEnabled(true);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.button_upload).setEnabled(false);
        }
    }


    /**
     * Starts the correct activity depending on which button is clicked
     *
     * @param view The view that was clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_button_main:
                Intent i1 = new Intent(this, MenuActivity.class);
                startActivity(i1);
                break;
            case R.id.tutorial_button:
                Intent i2 = new Intent(this, TutorialActivity.class);
                startActivity(i2);
                break;
            case R.id.settings_button_main:
                Intent i3 = new Intent(this, SettingsActivity.class);
                startActivity(i3);
                break;
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.button_upload:
                int id = 1;
                /*
                int[] s = {5, 5};
                int[][] sol = {{0, 0, 1, 0, 0},
                        {0, 1, 1, 1, 0},
                        {1, 1, 1, 1, 1},
                        {1, 1, 0, 1, 1},
                        {1, 1, 1, 1, 1}};
                int[][] r = {{0, 1}, {0, 3}, {0, 5}, {2, 2}, {0, 5}};
                int[][] c = {{0, 0, 3, 0, 0},
                        {3, 4, 1, 4, 3}};
                */
                ArrayList<Integer> size = new ArrayList<>(Arrays.asList(5, 5));
                ArrayList<List<Integer>> solution = new ArrayList<List<Integer>>(size.get(1));
                solution.add(new ArrayList<Integer>(Arrays.asList(0, 0, 1, 0, 0)));
                solution.add(new ArrayList<Integer>(Arrays.asList(0, 1, 1, 1, 0)));
                solution.add(new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 1)));
                solution.add(new ArrayList<Integer>(Arrays.asList(1, 1, 0, 1, 1)));
                solution.add(new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 1)));
                ArrayList<List<Integer>> rows = new ArrayList<List<Integer>>(size.get(1));
                rows.add(new ArrayList<Integer>(Arrays.asList(0, 1)));
                rows.add(new ArrayList<Integer>(Arrays.asList(0, 3)));
                rows.add(new ArrayList<Integer>(Arrays.asList(0, 5)));
                rows.add(new ArrayList<Integer>(Arrays.asList(2, 2)));
                rows.add(new ArrayList<Integer>(Arrays.asList(0, 5)));
                ArrayList<List<Integer>> cols = new ArrayList<List<Integer>>(2);
                cols.add(new ArrayList<Integer>(Arrays.asList(0, 0, 3, 0, 0)));
                cols.add(new ArrayList<Integer>(Arrays.asList(3, 4, 1, 4, 3)));

                int completed = 0;
                final PuzzleUpload pu = new PuzzleUpload(id, size, solution, rows, cols, completed);
                new Thread(new Runnable() {
                    public void run() {
                        //ddbClient.listTables();
                        mapper.save(pu);
                    }
                }).start();
                break;
        }
    }

    /**
     * Returns the PuzzleDatabase
     *
     * @return The PuzzleDatabase
     */
    public static PuzzleDatabase getDB() {
        return db;
    }

    /**
     * Returns the mapper
     * @return the mapper
     */
    public static DynamoDBMapper getMapper() { return mapper; }

    /**
     * Returns sign in status
     * @return the status
     */
    public static boolean getSignInStatus() { return isSignedIn; }
}

package com.exaple.googlewithlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by hp on 6/20/2016.
 */
public class GoogleSignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleSignIn";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1001;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(GoogleSignInActivity.this)
                        .enableAutoManage(GoogleSignInActivity.this, GoogleSignInActivity.this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                MarshMallowPermission marshMallowPermission = new MarshMallowPermission(GoogleSignInActivity.this);

                if (!marshMallowPermission.checkPermissionForGetAccounts()) {
                    marshMallowPermission.requestPermissionForGetAccount();
                } else {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });
    }

    protected void onStart() {
        super.onStart();

    }


    protected void onStop() {
        super.onStop();

    }

    @Override

    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed: ");

    }

//    @Override
//    public void onConnected(Bundle arg0) {
//        GoogleApiClient.ConnectionCallbacks,
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        Log.e(TAG, "onConnectionSuspended: ");
//        //mGoogleApiClient.connect();
//    }
//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            finish();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                /*Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override

                            public void onResult(Status status) {
                                Log.e(TAG, "onResult: sign out" );
                                // ...
                            }
                        });
*/
                Log.e(TAG, "handleSignInResult: display name " + acct.getDisplayName());
                Log.e(TAG, "handleSignInResult: email " + acct.getEmail());
                Log.e(TAG, "handleSignInResult: photo url " + acct.getPhotoUrl());
                Log.e(TAG, "handleSignInResult: ID " +
                        "" + acct.getId());


            } else {
                Log.e(TAG, "handleSignInResult: account get null");
            }

        } else {
            // Signed out, show unauthenticated UI.
            Log.e(TAG, "handleSignInResult: something went wrong !");
        }
        progressDialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed: ");
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MarshMallowPermission.GET_ACCOUNTS_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    finish();
                }
                break;

        }
    }

}

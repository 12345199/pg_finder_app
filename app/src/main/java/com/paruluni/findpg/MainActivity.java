package com.paruluni.findpg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";

    Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    //To check if the internet is connected
    boolean isInternetConnected = false;

    TextView navName, navEmail;
    CoordinatorLayout coordinatorLayout;

    GoogleApiClient mGoogleApiClient;
    Button signOut;
    static long noOfChildren;

    SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        pDialog = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.PROGRESS_TYPE);

        user = firebaseAuth.getCurrentUser();

        if (firebaseAuth.getCurrentUser() != null) {
            Log.d(TAG, "onCreate: USER " + user.getEmail());
            Log.d(TAG, "onCreate: USER " + user.getUid());
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        navName = (TextView) header.findViewById(R.id.textview_username);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinate_layout);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(MainActivity.this, "Google play services error..", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Setting the name in navigation drawer
        if (user != null) {
            navName.setText(user.getDisplayName().toString());
//            navEmail.setText(user.getEmail().toString());

            Snackbar.make(coordinatorLayout, "Welcome  " + user.getDisplayName().toString() + "!", Snackbar.LENGTH_LONG).show();

        }
    }


    //This function opens the register pg activity
    public void openRegisterPgActivity(View view) {

        if (firebaseAuth.getCurrentUser() == null) {

            // NEW ALERT DIALOG
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Not Signed In!")
                    .setContentText("Please SignIn Before Registering!")
                    .setCancelText("CANCEL")
                    .setConfirmText("SIGN IN")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            startActivity(new Intent(getApplicationContext(), AuthorisationActivity.class));
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .show();
        } else {

            Intent i = new Intent(this, RegisterPGPageOne.class);
            startActivity(i);
        }
    }


    //This function opens the find pg activity
    public void openFindPgActivity(View view) {
        Intent i = new Intent(this, FindPGActivity.class);
        i.putExtra("source", "MainActivity");  //Adding source so that in FindPgActivity we can check whether to display all cards or filtered cards
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {

            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(getApplicationContext(), AuthorisationActivity.class));
            } else
                startActivity(new Intent(getApplicationContext(), MyAccountPage.class));

        } else if (id == R.id.nav_pg) {


            //startActivity(new Intent(this,RegisterPGPageOne.class));
            //open comment later on
//// ********************************Counting the number of Pgs first in the firebase ***************************************
//
//            //Checking if the internet is available
           // isNetworkConnected();
//            if (isInternetConnected) {
//                if (user == null) {
//                    Toast.makeText(this, "Please Sign In First!", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    final SweetAlertDialog mdialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//                    mdialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                    mdialog.setTitleText("Please Wait");
//                    mdialog.setCancelable(false);
//                    mdialog.show();
//                    Firebase.setAndroidContext(this);
//
//                    RegisterPG.firebaseRef = new Firebase("https://pgfinder-86c19.firebaseio.com/");
//
//                    // ********************************Counting the number of Pgs first in the firebase ************************
//                    RegisterPG.firebaseRef.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
//                                Log.d(TAG, "onChildAdded: NUMBER OF CHILDREN " + dataSnapshot.getChildrenCount());
//                                noOfChildren = dataSnapshot.getChildrenCount();
//                                mdialog.dismiss();
//                                startActivity(new Intent(getApplicationContext(), MyRegisteredPGInfo.class));
//                            }
//                        }
//
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//
//                        }
//
//                    });
//                }
//            }

        } else if (id == R.id.nav_editPg) {

            //Checking if the internet is available
            isNetworkConnected();
            if (isInternetConnected) {

                if (user == null) {
                    Toast.makeText(this, "Please Sign In First!", Toast.LENGTH_SHORT).show();
                } else {

                    final SweetAlertDialog mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                    mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    mDialog.setTitleText("Please Wait");
                    mDialog.setCancelable(false);
                    mDialog.show();

                    Firebase.setAndroidContext(this);
//
//                // ********************************Counting the number of Pgs first in the firebase **************************
                    RegisterPG.databaseref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                Log.d(TAG, "onChildAdded: NUMBER OF CHILDREN " + dataSnapshot.getChildrenCount());
                                noOfChildren = dataSnapshot.getChildrenCount();

//                            //Starting the Multiple Pg Edit Activity which will further allow user to choose a particular PG
                                finish();
                                mDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MultiplePGEdit.class));
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });
                }
            }
        } else if (id == R.id.nav_home) {


        }else if (id == R.id.nav_signout){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Sign Out!")
                    .setContentText("Are you sure you want to Sign out?")
                    .setCancelText("CANCEL")
                    .setConfirmText("YES")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            if (firebaseAuth.getCurrentUser() != null) {
                                firebaseAuth.signOut();
                                user = null;

                                Toast.makeText(MainActivity.this, "You are logged out!", Toast.LENGTH_SHORT).show();

                                if (LoginFrag.t == 1) {
                                    LoginManager.getInstance().logOut();
                                    LoginFrag.t = 0;
                                    Toast.makeText(MainActivity.this, "You are logged out!", Toast.LENGTH_SHORT).show();
                                }
                            } else
                                Toast.makeText(MainActivity.this, "Please SignIn First", Toast.LENGTH_SHORT).show();
                            navName.setText("User");
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .show();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This function checks if the internet is connected
     * and shows an error dialog if not connected to the internet
     */
    private void isNetworkConnected() {
        if (isNetworkAvailable()) {
            isInternetConnected = true;
        } else {
            isInternetConnected = false;
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No Internet")
                    .setContentText("Please Check Your Internet Connection!")
                    .show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

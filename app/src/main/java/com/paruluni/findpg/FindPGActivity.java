package com.paruluni.findpg;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paruluni.findpg.adpter.PgDetailsAdapter;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Class to Find PGs
 */

public class FindPGActivity extends AppCompatActivity {

    public static final String TAG = "FindPGActivity";

    RecyclerView mrecyclerView;
    RecyclerView.Adapter madapter;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<PgDetails_POJO.PgDetails> cardDetails;
    Button filterButton;
    Intent filterActivityIntent;

    Toolbar toolbar;

    boolean isInternetConnected = false;

    Intent checkActivityCallerIntent;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_pg);

        isNetworkConnected();

        filterActivityIntent = new Intent(this, FilterActivity.class);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        filterButton = (Button) findViewById(R.id.filter);
        progress_bar = (ProgressBar)findViewById(R.id.progress_bar);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(filterActivityIntent);
            }
        });

        mrecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        cardDetails = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        mrecyclerView.setLayoutManager(layoutManager);
        mrecyclerView.setHasFixedSize(true);
        madapter = new PgDetailsAdapter(cardDetails, this);
        mrecyclerView.setAdapter(madapter);

        if (isInternetConnected) {
            progress_bar.setVisibility(View.VISIBLE);
            DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();
            databaseref.child(Constants.PGDetails).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    checkActivityCallerIntent = getIntent();
                    Bundle b = checkActivityCallerIntent.getExtras();
                    final String intentSource = (String) b.get("source");
                    Log.d(TAG, intentSource);
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        if (intentSource.equals("FilterActivity")) {
                            if (b != null) {

                                String localityCheckCode = (String) b.get("localityCheckCode");
                                String collegeCheckCode = (String) b.get("collegeCheckCode");
                                String rentCheckCode = (String) b.get("rentCheckCode");

                                String finalCheckCode = "";

                                if (localityCheckCode != null)
                                    finalCheckCode = localityCheckCode;

                                if (collegeCheckCode != null)
                                    finalCheckCode = finalCheckCode + collegeCheckCode;

                                if (rentCheckCode != null)
                                    finalCheckCode = finalCheckCode + rentCheckCode;


                                Log.d(TAG, finalCheckCode);


                                if (finalCheckCode.equals("012")) {
                                    ArrayList<String> filteredLocalityList = new ArrayList<String>();
                                    filteredLocalityList = checkActivityCallerIntent.getStringArrayListExtra("filteredLocalityList");

                                    ArrayList<PgDetails_POJO.PgDetails> filteredObjects = new ArrayList<PgDetails_POJO.PgDetails>();

                                    for (int i = 0; i < filteredLocalityList.size(); i++) {
                                        if (dataSnapshot.child("locality").getValue().equals(filteredLocalityList.get(i))) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);

                                            filteredObjects.add(model);
                                            progress_bar.setVisibility(View.GONE);
                                        }
                                    }


                                    ArrayList<String> filteredCollegeList = new ArrayList<String>();
                                    filteredCollegeList = checkActivityCallerIntent.getStringArrayListExtra("filteredCollegesList");

                                    for (int i = 0; i < filteredCollegeList.size(); i++) {
                                        if (dataSnapshot.child("nearbyInstitute").getValue().equals(filteredCollegeList.get(i))) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);

                                            filteredObjects.add(model);
                                            progress_bar.setVisibility(View.GONE);
                                        }
                                    }


                                    ArrayList<String> filteredRentList = new ArrayList<String>();
                                    filteredRentList = checkActivityCallerIntent.getStringArrayListExtra("filteredRentList");

                                    for (int i = 0; i < filteredRentList.size(); i++) {

                                        String rentSelected = filteredRentList.get(i);

                                        if (rentSelected.equals("Below 5000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 5000);

                                        } else if (rentSelected.equals("5000-10000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 10000);
                                        } else if (rentSelected.equals("10000-15000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 15000);
                                        } else if (rentSelected.equals("Above 15000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 15001);
                                        }
                                    }
                                } else if (finalCheckCode.equals("01")) {
                                    ArrayList<String> filteredLocalityList = new ArrayList<String>();
                                    filteredLocalityList = checkActivityCallerIntent.getStringArrayListExtra("filteredLocalityList");

                                    for (int i = 0; i < filteredLocalityList.size(); i++) {
                                        if (dataSnapshot.child("locality").getValue().equals(filteredLocalityList.get(i))) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);
                                            cardDetails.add(model);
                                            madapter.notifyDataSetChanged();
                                            progress_bar.setVisibility(View.GONE);
                                        }

                                        ArrayList<String> filteredCollegeList = new ArrayList<String>();
                                        filteredCollegeList = checkActivityCallerIntent.getStringArrayListExtra("filteredCollegesList");

                                        for (i = 0; i < filteredCollegeList.size(); i++) {
                                            if (dataSnapshot.child("nearbyInstitute").getValue().equals(filteredCollegeList.get(i))) {
                                                PgDetails_POJO.PgDetails model = dataSnapshot
                                                        .getValue(PgDetails_POJO.PgDetails.class);

                                                cardDetails.add(model);
                                                madapter.notifyDataSetChanged();
                                               progress_bar.setVisibility(View.GONE);
                                            }
                                        }

                                    }
                                } else if (finalCheckCode.equals("02")) {
                                    ArrayList<String> filteredLocalityList = new ArrayList<String>();
                                    filteredLocalityList = checkActivityCallerIntent.getStringArrayListExtra("filteredLocalityList");

                                    ArrayList<PgDetails_POJO.PgDetails> filteredObjects = new ArrayList<PgDetails_POJO.PgDetails>();

                                    for (int i = 0; i < filteredLocalityList.size(); i++) {
                                        if (dataSnapshot.child("locality").getValue().equals(filteredLocalityList.get(i))) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);

                                            filteredObjects.add(model);
                                           progress_bar.setVisibility(View.GONE);
                                        }
                                    }

                                    ArrayList<String> filteredRentList = new ArrayList<String>();
                                    filteredRentList = checkActivityCallerIntent.getStringArrayListExtra("filteredRentList");

                                    for (int i = 0; i < filteredRentList.size(); i++) {

                                        String rentSelected = filteredRentList.get(i);

                                        if (rentSelected.equals("Below 5000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 5000);

                                        } else if (rentSelected.equals("5000-10000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 10000);
                                        } else if (rentSelected.equals("10000-15000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 15000);
                                        } else if (rentSelected.equals("Above 15000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 15001);
                                        }
                                    }
                                } else if (finalCheckCode.equals("12")) {
                                    ArrayList<PgDetails_POJO.PgDetails> filteredObjects = new ArrayList<PgDetails_POJO.PgDetails>();

                                    ArrayList<String> filteredCollegeList = new ArrayList<String>();
                                    filteredCollegeList = checkActivityCallerIntent.getStringArrayListExtra("filteredCollegesList");

                                    for (int i = 0; i < filteredCollegeList.size(); i++) {
                                        if (dataSnapshot.child("nearbyInstitute").getValue().equals(filteredCollegeList.get(i))) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);

                                            filteredObjects.add(model);
                                            progress_bar.setVisibility(View.GONE);
                                        }
                                    }


                                    ArrayList<String> filteredRentList = new ArrayList<String>();
                                    filteredRentList = checkActivityCallerIntent.getStringArrayListExtra("filteredRentList");

                                    for (int i = 0; i < filteredRentList.size(); i++) {

                                        String rentSelected = filteredRentList.get(i);

                                        if (rentSelected.equals("Below 5000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 5000);

                                        } else if (rentSelected.equals("5000-10000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 10000);
                                        } else if (rentSelected.equals("10000-15000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 15000);
                                        } else if (rentSelected.equals("Above 15000")) {
                                            filteringForRentWithCollegesOrLocality(filteredObjects, 15001);
                                        }
                                    }
                                } else if (finalCheckCode.equals("0")) {
                                    ArrayList<String> filteredLocalityList = new ArrayList<String>();
                                    filteredLocalityList = checkActivityCallerIntent.getStringArrayListExtra("filteredLocalityList");

                                    ArrayList<PgDetails_POJO.PgDetails> filteredObjects = new ArrayList<PgDetails_POJO.PgDetails>();

                                    for (int i = 0; i < filteredLocalityList.size(); i++) {
                                        if (dataSnapshot.child("locality").getValue().equals(filteredLocalityList.get(i))) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);


                                            cardDetails.add(model);
                                            madapter.notifyDataSetChanged();
                                            progress_bar.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (finalCheckCode.equals("1")) {
                                    ArrayList<String> filteredCollegeList = new ArrayList<String>();
                                    filteredCollegeList = checkActivityCallerIntent.getStringArrayListExtra("filteredCollegesList");

                                    for (int i = 0; i < filteredCollegeList.size(); i++) {
                                        if (dataSnapshot.child("nearbyInstitute").getValue().equals(filteredCollegeList.get(i))) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);
                                            cardDetails.add(model);
                                            madapter.notifyDataSetChanged();
                                            progress_bar.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (finalCheckCode.equals("2")) {
                                    ArrayList<String> filteredRentList = new ArrayList<String>();
                                    filteredRentList = checkActivityCallerIntent.getStringArrayListExtra("filteredRentList");


                                    for (int i = 0; i < filteredRentList.size(); i++) {

                                        String rentSelected = filteredRentList.get(i);

                                        if (rentSelected.equals("Below 5000")) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);
                                            if (model.getRent() <= 5000) {
                                                cardDetails.add(model);
                                                madapter.notifyDataSetChanged();
                                                progress_bar.setVisibility(View.GONE);
                                            }
                                        } else if (rentSelected.equals("5000-10000")) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);
                                            if (model.getRent() > 5000 && model.getRent() <= 10000) {
                                                cardDetails.add(model);
                                                madapter.notifyDataSetChanged();
                                                progress_bar.setVisibility(View.GONE);
                                            }
                                        } else if (rentSelected.equals("10000-15000")) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);
                                            if (model.getRent() > 10000 && model.getRent() <= 15000) {
                                                cardDetails.add(model);
                                                madapter.notifyDataSetChanged();
                                                progress_bar.setVisibility(View.GONE);
                                            }
                                        } else if (rentSelected.equals("Above 15000")) {
                                            PgDetails_POJO.PgDetails model = dataSnapshot
                                                    .getValue(PgDetails_POJO.PgDetails.class);
                                            if (model.getRent() > 15000) {
                                                cardDetails.add(model);
                                                madapter.notifyDataSetChanged();
                                                progress_bar.setVisibility(View.GONE);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                        else if (intentSource.equals("MainActivity")) {
                            Log.d(TAG, "onChildAdded: " + dataSnapshot.child(Constants.PGDetails).getValue());
                            Log.d(TAG, "onChildAdded: KEY VALUE : " + (dataSnapshot.child("city").getValue().equals("delhi")));
                            PgDetails_POJO.PgDetails model = dataSnapshot
                                    .getValue(PgDetails_POJO.PgDetails.class);
                            cardDetails.add(model);
                            madapter.notifyDataSetChanged();
                            progress_bar.setVisibility(View.GONE);
                        }
                        progress_bar.setVisibility(View.GONE);
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


    private void filteringForRentWithCollegesOrLocality(ArrayList<PgDetails_POJO.PgDetails> filteredObjects, int rentSelected) {
        for (int i = 0; i < filteredObjects.size(); i++) {
            progress_bar.setVisibility(View.VISIBLE);


            if (rentSelected == 15001) {
                if (filteredObjects.get(i).getRent() > 15000) {
                    cardDetails.add(filteredObjects.get(i));
                    madapter.notifyDataSetChanged();
                   progress_bar.setVisibility(View.GONE);
                }
            } else if (rentSelected == 5000) {
                if (filteredObjects.get(i).getRent() <= 5000) {
                    cardDetails.add(filteredObjects.get(i));
                    madapter.notifyDataSetChanged();
                   progress_bar.setVisibility(View.GONE);
                }
            } else if (rentSelected == 10000) {
                if (filteredObjects.get(i).getRent() > 5000 && filteredObjects.get(i).getRent() <= 10000) {
                    cardDetails.add(filteredObjects.get(i));
                    madapter.notifyDataSetChanged();
                   progress_bar.setVisibility(View.GONE);
                }
            } else if (rentSelected == 15000) {
                if (filteredObjects.get(i).getRent() > 10000 && filteredObjects.get(i).getRent() <= 15000) {
                    cardDetails.add(filteredObjects.get(i));
                    madapter.notifyDataSetChanged();
                   progress_bar.setVisibility(View.GONE);
                }
            }
           progress_bar.setVisibility(View.GONE);
        }


    }

}



package com.paruluni.findpg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.paruluni.findpg.adpter.PgDetailsAdapter;

import java.util.ArrayList;

/**
 * Thi activity is called when the user selects MY PG from the navigation drawer
 * It directs the user to the fragment and shows his/her PG's information
 */
//TODO Check if the pg exists for the user

public class MyRegisteredPGInfo extends AppCompatActivity {

    public static final String TAG = "FindPGActivity";

    RecyclerView mrecyclerView;
    RecyclerView.Adapter madapter;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<PgDetails_POJO.PgDetails> cardDetails;
    Button filterButton;
    Intent filterActivityIntent;

    Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    long count = 0;
    int countFind = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_pg);

        filterActivityIntent = new Intent(this, FilterActivity.class);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        filterButton = (Button) findViewById(R.id.filter);

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

        //Adding progress dialogue while the cards are loading
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.show();

        Firebase.setAndroidContext(this);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user != null) {
            RegisterPG.databaseref.child(Constants.PGDetails).addChildEventListener(new ChildEventListener() {


                @Override
                public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        Log.d(TAG, "onChildAdded: " + dataSnapshot.child(Constants.PGDetails).getValue());

                        //Getting the PGs of corresponding to the User's UID
                        if (dataSnapshot.child("userUID").getValue().equals(user.getUid())) {
                            PgDetails_POJO.PgDetails model = dataSnapshot
                                    .getValue(PgDetails_POJO.PgDetails.class);
                            cardDetails.add(model);
                            madapter.notifyDataSetChanged();

                            pd.dismiss();
                        } else {
                            countFind++;
                            Log.d(TAG, "onChildAdded: COUNTFIND " + countFind);

                            //Checking if we have reached the end of the database and didn't find any PG
                            if (countFind == MainActivity.noOfChildren) {
                                pd.dismiss();
                                Log.d(TAG, "onChildAdded: COUNT " + count);
                                Toast.makeText(getApplicationContext(), "No Pg Found!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }
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
        else    {
            Toast.makeText(this, "Please Login First!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }


}

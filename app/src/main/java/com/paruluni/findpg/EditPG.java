package com.paruluni.findpg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;

/**
 * This class allows the user to edit his/her registered PG
 * First we count the number of PGs in the MainActivity navDrawer using a static int(MainActivity.noOfChildren)
 * Then in the second firebase query we compare the values and if we reach at the end of the list we display "No Pg Found"
 */

public class EditPG extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String pgKey;
    public static final int INITIAL_FLAG = 9001;
    public static final int FINAL_FLAG = 8001;
    int flag = INITIAL_FLAG;
    long count = 0;
    int countFind = 0;
    ProgressDialog dialog;

    public static final String TAG = "EditPG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pg);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");
        dialog.show();

/**
 * If intent is coming from the MyAccount page
 * We need to search the PG by its unique key not UID
 */
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b.getString("source").equals("MultiplePGEditApapter")) {
            final String pgId = b.getString("PG ID");
            Log.d(TAG, "onClick: ID FROM INTENT " + b.getString("PG ID"));
            RegisterPG.databaseref.child(Constants.PGDetails).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        if (dataSnapshot.child("id").getValue().equals(pgId)) {
                            Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                            pgKey = dataSnapshot.getKey();
                            flag = FINAL_FLAG;
                            dialog.dismiss();
                            Intent i = new Intent(getApplicationContext(), RegisterPGPageOne.class);
                            i.putExtra("flag", flag);
                            i.putExtra("key", pgKey);
                            i.putExtra("PgId", pgId);
                            i.putExtra("source", "editPG");
                            finish();
                            startActivity(i);
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
    }

}

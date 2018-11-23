package coatcheck.apps.shanperera.coatsadmin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Venue[] venueList;
    int nVenues;
    public static Venue currentVenue = new Venue();
    private String uid;

    private FirebaseDatabase db;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();
        mDatabase = db.getReference();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser u = mAuth.getCurrentUser();
        uid = u.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                uid = user.getUid();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent myIntent = new Intent(MainActivity.this, SignInActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    MainActivity.this.startActivity(myIntent);
                }
                // ...
            }
        };

        getVenueDb();
    }

    public void ccButton(View btn){
        Intent myIntent = new Intent(MainActivity.this, CheckActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MainActivity.this.startActivity(myIntent);
    }

    public void rcButton(View btn){
        Intent myIntent = new Intent(MainActivity.this, ReturnActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MainActivity.this.startActivity(myIntent);
    }

    public void getVenueDb(){
        mDatabase.child("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get the number of children to set the number of elements in the User array
                long index = dataSnapshot.getChildrenCount();
                nVenues = (int) index;
                venueList = new Venue[nVenues];


                //Counter for the for each loop
                int count = 0;
                //Iterate over every child in the data snapshot to get each User checked
                //into the current venue
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Log.e(TAG, child.toString());
                    venueList[count] = child.getValue(Venue.class);
                    count++;
                }


                //Verification purposes
                for(int i = 0; i < nVenues; i++){
                    Log.e(TAG, "VENUE " + i + ": "+ venueList[i].getAddress() + ", " + venueList[i].getName() + ", " +
                            venueList[i].getUid());
                }

                getCurrentVenue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getCurrentVenue(){
        for(int i = 0; i < nVenues; i++){
            if(venueList[i].getUid().equals(uid)){
                currentVenue = venueList[i];
            }
        }
        Log.e(TAG, "CURRENT VENUE " + currentVenue.getName() + " " + currentVenue.getUid());
    }
}

package coatcheck.apps.shanperera.coatsadmin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static coatcheck.apps.shanperera.coatsadmin.MainActivity.currentVenue;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.UUID;

public class CheckActivity extends AppCompatActivity {

    public static final String TAG = "CheckActivity";
    public static String qrCode;
    private FirebaseDatabase db;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Coats coats; //coats database
    User[] user;
    private int nUsers; //number of users to be determined after user database is retrieved
    private int newHanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        db = FirebaseDatabase.getInstance();
        mDatabase = db.getReference();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        getCoatDb();
        getUserDb();
    }

    public void getHanger(){
        String s = coats.getOrderedHangers();
        int index = s.indexOf(",");
        if(index == -1){
            if(s.equalsIgnoreCase("null")){
                newHanger = coats.getHangerIndex();
                coats.setHangerIndex(newHanger+1);
            }
            else {
                newHanger = Integer.parseInt(s);
                coats.setOrderedHangers("null");
            }
        }
        else{
            String n = s.substring(0, index);
            newHanger = Integer.parseInt(n);
            String o = s.substring(index+1);
            coats.setOrderedHangers(o);
        }
    }

    public void cPhone (View btn) {
        EditText phone = (EditText) findViewById(R.id.etPhone);
        String phoneNum = phone.getText().toString();

        User nu = new User("Anon", phoneNum, "null", Integer.toString(newHanger));
        updateCoatDb(nu);
    }

    // Scan a QR code on a User's phone using the Admin tablet's camera
    public void cCode (View btn){
        IntentIntegrator integrator = new IntentIntegrator(CheckActivity.this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        User newCoat = new User();

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            qrCode = scanResult.getContents();
            if(qrCode != null) {

                String[] parts = qrCode.split(",");

                newCoat.setFullName(parts[0]);
                newCoat.setPhoneNumber(parts[1]);
                newCoat.setEmail(parts[2]);
                newCoat.setCoat(Integer.toString(newHanger));

                updateCoatDb(newCoat);

            }
            else{
            }
        }
    }

    public void updateCoatDb(User coat){
        String fn, em, pn;

        fn = coat.getFullName();
        em = coat.getEmail();
        pn = coat.getPhoneNumber();

        getUserDb();

        //String checks
        if(fn.length() <= 70){
            if(em.length() <= 254){
                if(pn.length() <= 15){

                    coats.setAvailableHangers(coats.getAvailableHangers() - 1);
                    coats.setUsedHangers(coats.usedHangers+1);

                    mDatabase.child("Coats").child("Venue1").setValue(coats);

                    final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                    User nu = new User(fn, pn, em, Integer.toString(newHanger), uuid);

                    String newUser = uuid;
                    mDatabase.child("Users").child("Venue1").child(newUser).setValue(nu);
                }
            }
        }

        Intent myIntent = new Intent(CheckActivity.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CheckActivity.this.startActivity(myIntent);
    }

    public void getCoatDb(){
        mDatabase.child("Coats").child(currentVenue.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                coats = dataSnapshot.getValue(Coats.class);
                Log.e(TAG, "COATS: " + coats.getAvailableHangers() + ", " + coats.getHangerIndex() + ", " +
                        coats.getOrderedHangers() + ", " + coats.getUsedHangers());
                getHanger(); //FINDS AVAILABLE HANGER IN ASCENDING ORDER
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getUserDb(){
        mDatabase.child("Users").child("Venue1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get the number of children to set the number of elements in the User array
                long index = dataSnapshot.getChildrenCount();
                nUsers = (int) index;
                user = new User[nUsers];

                //Counter for the for each loop
                int count = 0;
                //Iterate over every child in the data snapshot to get each User checked
                //into the current venue
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Log.e(TAG, child.toString());
                    user[count] = child.getValue(User.class);
                    count++;
                }

                //Verification purposes
                for(int i = 0; i < nUsers; i++){
                    Log.e(TAG, "USER " + i + ": "+ user[i].getFullName() + ", " + user[i].getEmail() + ", " +
                    user[i].getPhoneNumber() + ", " + user[i].getCoat());
                }

                //findNextHanger();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

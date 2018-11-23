package coatcheck.apps.shanperera.coatsadmin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ReturnActivity extends AppCompatActivity {

    private static final String TAG = "ReturnActivity";
    public static String qrCode;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Coats coats; //coats database
    User[] user;
    private int nUsers; //number of users to be determined after user database is retrieved
    private int coatHanger = -1;
    private String ui = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        getUserDb();
        getCoatDb();

    }

    public void phoneCheck(View button){
        EditText et = (EditText) findViewById(R.id.etPhoneReturn);
        String phoneNumber = et.getText().toString();

        searchNumbers(phoneNumber);
        returnCoat();
    }

    public void searchNumbers(String pn){
        for(int i = 0; i < user.length; i++){
            if(user[i].getPhoneNumber().equals(pn)){
                coatHanger = Integer.parseInt(user[i].getCoat());
                ui = user[i].getId();
                break;
            }
        }
        if(coatHanger == -1){
            Log.e(TAG,"ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR not recognizing coat");
            //too bad.
        }
    }

    public void returnCoat(){
        coats.setUsedHangers(coats.getUsedHangers() - 1);
        coats.setAvailableHangers(coats.getAvailableHangers() + 1);
        String s = coats.getOrderedHangers();
        if(s.equals("null")) {
            if (coatHanger < coats.getHangerIndex() - 1) {
                coats.setOrderedHangers(Integer.toString(coatHanger));
            } else{
                coats.setHangerIndex(coatHanger);
            }
        }
        else{
            String newOrder = "";
            int index = s.indexOf(',');
            //Only 1 element in orderedHangers
            if(index == -1){
                int oHanger = Integer.parseInt(s);
                if(oHanger < coatHanger){
                    newOrder = Integer.toString(oHanger) + Integer.toString(coatHanger);
                }
                else{
                    newOrder = Integer.toString(coatHanger) + Integer.toString(oHanger);
                }
            }
            else{
                String[] parts = s.split(",");
                int[] iParts = new int[parts.length];
                for(int i = 0; i < iParts.length; i++){
                    iParts[i] = Integer.parseInt(parts[i]);
                }
                boolean found = false;
                for(int i = 0; i < iParts.length; i++){
                    if(iParts[i] < coatHanger && found == false){
                        newOrder+= parts[i] + ",";
                    }
                    else if(iParts[i] > coatHanger && found == false){
                        newOrder+= Integer.toString(coatHanger) + "," + parts[i];
                        found = true;
                    }
                    else{
                        newOrder+= "," + parts[i];
                    }
                }
            }
            coats.setOrderedHangers(newOrder);
        }
        mDatabase.child("Coats").child("Venue1").setValue(coats);
        String del = ui;
        mDatabase.child("Users").child("Venue1").child(del).removeValue();

        Intent myIntent = new Intent(ReturnActivity.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ReturnActivity.this.startActivity(myIntent);
    }

    public void qrCheck(View button){
        IntentIntegrator integrator = new IntentIntegrator(ReturnActivity.this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            qrCode = scanResult.getContents();

            if(qrCode != null) {
                String[] parts = qrCode.split(",");

                User newUser = new User();
                newUser.setFullName(parts[0]);
                newUser.setPhoneNumber(parts[1]);
                newUser.setEmail(parts[2]);
                searchNumbers(newUser.getPhoneNumber());
                returnCoat();
            }
        }
    }

    public void getCoatDb(){
        mDatabase.child("Coats").child("Venue1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                coats = dataSnapshot.getValue(Coats.class);
                Log.e(TAG, "COATS: " + coats.getAvailableHangers() + ", " + coats.getHangerIndex() + ", " +
                        coats.getOrderedHangers() + ", " + coats.getUsedHangers());

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

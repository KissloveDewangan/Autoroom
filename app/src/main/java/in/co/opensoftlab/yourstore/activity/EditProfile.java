package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.Users;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 09-02-2017.
 */

public class EditProfile extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabase;
    CircleImageView profilePic;
    TextView name;
    TextView emailId;
    TextView userLocation;
    ImageView back;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(getUid());
        initUI();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profilePic = (CircleImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.et_name);
        emailId = (TextView) findViewById(R.id.et_email_address);
        userLocation = (TextView) findViewById(R.id.et_mobno);
        back = (ImageView) findViewById(R.id.iv_back);

        userLocation.setText(PrefUtils.getFromPrefs(getApplicationContext(), "address", "Location unknown"));

        back.setOnClickListener(this);

//        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
//                    InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm2.hideSoftInputFromWindow(name.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//                    name.setFocusable(false);
//                    name.setInputType(InputType.TYPE_NULL);
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        emailId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
//                    InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm2.hideSoftInputFromWindow(emailId.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//                    emailId.setFocusable(false);
//                    emailId.setInputType(InputType.TYPE_NULL);
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        userLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
//                    InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm2.hideSoftInputFromWindow(userLocation.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//                    userLocation.setFocusable(false);
//                    userLocation.setInputType(InputType.TYPE_NULL);
//                    return true;
//                }
//                return false;
//            }
//        });

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Users user = dataSnapshot.getValue(Users.class);
                // [START_EXCLUDE]
                if (user != null) {
                    Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(profilePic);
                    name.setText(user.getName());

                    if (user.getEmailId().contentEquals("null")) {
                        emailId.setText("");
                    } else {
                        emailId.setText(user.getEmailId());
                    }
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(valueEventListener);
        }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
                return;
        }
    }
}

package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.CareModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 06-02-2017.
 */
public class ContactUs extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    ImageView back;
    TextView purpose;
    EditText msg;
    EditText email;
    Button submit;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        mDatabase = FirebaseDatabase.getInstance().getReference("customerCare");

        initUI();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        submit = (Button) findViewById(R.id.b_submit);
        back = (ImageView) findViewById(R.id.iv_back);
        purpose = (TextView) findViewById(R.id.tv_purpose);
        msg = (EditText) findViewById(R.id.tv_msg);
        email = (EditText) findViewById(R.id.tv_email);

        submit.setOnClickListener(this);
        back.setOnClickListener(this);
        purpose.setOnClickListener(this);
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
            case R.id.tv_purpose:
                showPopup(view);
                break;
            case R.id.b_submit:
                if (purpose.getText().toString().contentEquals("") || msg.getText().toString().contentEquals("") ||
                        email.getText().toString().contentEquals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill all the details.", Toast.LENGTH_SHORT).show();
                } else if (!email.getText().toString().contains("@")) {
                    Toast.makeText(getApplicationContext(), "Email entered is incorrect.", Toast.LENGTH_SHORT).show();
                } else {
                    CareModel careModel = new CareModel(email.getText().toString(),
                            purpose.getText().toString(), msg.getText().toString());
                    mDatabase.push().setValue(careModel);
                    Toast.makeText(getApplicationContext(), "Message has been sent. We will reply you soon.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                return;
        }
    }

    private void showPopup(View view) {
        Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.PopupMenu);
        final PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.purpose_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.select_purpose).setTitle(Html.fromHtml("<font color='#000000'><b>Select Purpose"));
        popup.getMenu().findItem(R.id.get_help).setTitle(Html.fromHtml("<font color='#000000'>Get Help"));
        popup.getMenu().findItem(R.id.report).setTitle(Html.fromHtml("<font color='#000000'>Report a Problem"));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.get_help) {
                    purpose.setText("Get Help");
                    purpose.setTextColor(Color.parseColor("#424242"));
                } else if (item.getItemId() == R.id.report) {
                    purpose.setText("Report a Problem");
                    purpose.setTextColor(Color.parseColor("#424242"));
                } else {
                    purpose.setText("Select Purpose");
                    purpose.setTextColor(Color.parseColor("#BDBDBD"));
                }
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }
}

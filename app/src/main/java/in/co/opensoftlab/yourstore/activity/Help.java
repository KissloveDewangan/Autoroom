package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import in.co.opensoftlab.yourstore.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 06-02-2017.
 */

public class Help extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    ImageView back;
    RelativeLayout contactUs;
    RelativeLayout terms;
    RelativeLayout aboutUs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_help);
        initUI();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.iv_back);
        contactUs = (RelativeLayout) findViewById(R.id.rl_contact_us);
        terms = (RelativeLayout) findViewById(R.id.rl_terms);
        aboutUs = (RelativeLayout) findViewById(R.id.rl_about);

        back.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        terms.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.rl_contact_us:
                startActivity(new Intent(Help.this, ContactUs.class));
                break;
            case R.id.rl_terms:
                Uri uri = Uri.parse("http://www.autoroom.in"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.rl_about:
                startActivity(new Intent(Help.this, AboutUs.class));
                break;
            default:
                return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}

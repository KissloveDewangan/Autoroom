package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.Intent;
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

public class Settings extends AppCompatActivity implements View.OnClickListener {
    ImageView back;
    RelativeLayout blockedCustomer;
    RelativeLayout blockedSeller;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUI();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.iv_back);
        blockedCustomer = (RelativeLayout) findViewById(R.id.rl_blocked_customers);
        blockedSeller = (RelativeLayout) findViewById(R.id.rl_blocked_seller);

        blockedCustomer.setOnClickListener(this);
        blockedSeller.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.rl_blocked_customers:
                Bundle bundle = new Bundle();
                bundle.putString("mode", "seller");
                Intent intent = new Intent(Settings.this, BlockedCustomers.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.rl_blocked_seller:
                Bundle bundle2 = new Bundle();
                bundle2.putString("mode", "buyer");
                Intent intent2 = new Intent(Settings.this, BlockedCustomers.class);
                intent2.putExtra("bundle",bundle2);
                startActivity(intent2);
                finish();
                break;
            default:
                return;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}

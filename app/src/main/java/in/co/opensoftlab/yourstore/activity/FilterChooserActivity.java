package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.ChooserListAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 07-03-2017.
 */

public class FilterChooserActivity extends BaseActivity {

    TextView headline;
    RecyclerView recyclerView;
    List<String> listNames = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    ChooserListAdapter chooserListAdapter;
    ImageView close;

    private DatabaseReference mDatabase;
    private ValueEventListener valueEventListener;

    private Bundle bundle;
    String[] fuelTypes;
    String[] bodyTypes;
    String[] sortTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_chooser);
        initUI();

        bundle = getIntent().getExtras().getBundle("chooser");

        if (bundle.getString("what").contentEquals("Car")) {
            if ((bundle.getString("fuelType") == null || bundle.getString("bodyType") == null) && bundle.getString("brand") != null) {
                if ((bundle.getString("model") == null)) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("ourDB").child("offerings").child("car").child("brands");
                    headline.setText("Select a Brand");
                } else if (bundle.getString("model") != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("ourDB").child("offerings").child("car").child("models")
                            .child(bundle.getString("brand"));
                    headline.setText("Select a Car Models");
                }
                showProgressDialog();
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        listNames.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.getKey();
                            listNames.add(name);
                        }
                        hideProgressDialog();
                        chooserListAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.d("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                        // ...
                        hideProgressDialog();
                    }
                };
                mDatabase.addListenerForSingleValueEvent(valueEventListener);
            } else if (bundle.getString("fuelType") != null) {
                headline.setText("Select Fuel Type");
                fuelTypes = getResources().getStringArray(R.array.fuel_types);
                listNames.clear();
                for (int i = 0; i < fuelTypes.length; i++) {
                    listNames.add(fuelTypes[i]);
                }
                chooserListAdapter.notifyDataSetChanged();
            } else if (bundle.getString("bodyType") != null) {
                headline.setText("Select Body Type");
                bodyTypes = getResources().getStringArray(R.array.body_types);
                listNames.clear();
                for (int i = 0; i < bodyTypes.length; i++) {
                    listNames.add(bodyTypes[i]);
                }
                chooserListAdapter.notifyDataSetChanged();
            } else if (bundle.getString("sortBy") != null) {
                headline.setText("Sort by");
                sortTypes = getResources().getStringArray(R.array.sort_types);
                listNames.clear();
                for (int i = 0; i < sortTypes.length; i++) {
                    listNames.add(sortTypes[i]);
                }
                chooserListAdapter.notifyDataSetChanged();
            }
        } else {
            if (bundle.getString("brand") != null) {
                if ((bundle.getString("model") == null)) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("ourDB").child("offerings").child("bike").child("brands");
                    headline.setText("Select a Brand");
                } else if (bundle.getString("model") != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("ourDB").child("offerings").child("bike").child("models")
                            .child(bundle.getString("brand"));
                    headline.setText("Select a Bike Models");
                }
                showProgressDialog();
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        listNames.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.getKey();
                            listNames.add(name);
                        }
                        hideProgressDialog();
                        chooserListAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.d("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                        // ...
                        hideProgressDialog();
                    }
                };
                mDatabase.addListenerForSingleValueEvent(valueEventListener);
            } else if (bundle.getString("sortBy") != null) {
                headline.setText("Sort by");
                sortTypes = getResources().getStringArray(R.array.sort_types);
                listNames.clear();
                for (int i = 0; i < sortTypes.length; i++) {
                    listNames.add(sortTypes[i]);
                }
                chooserListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initUI() {
        headline = (TextView) findViewById(R.id.tv_headline);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        close = (ImageView) findViewById(R.id.iv_close);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        chooserListAdapter = new ChooserListAdapter(getApplicationContext(), listNames);
        recyclerView.setAdapter(chooserListAdapter);

        chooserListAdapter.setOnItemClickListener(new ChooserListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", listNames.get(position));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}

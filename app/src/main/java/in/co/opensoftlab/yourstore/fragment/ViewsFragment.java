package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.NoViewListAdapter;
import in.co.opensoftlab.yourstore.model.ProductViewModel;
import in.co.opensoftlab.yourstore.utils.PrefUtils;


/**
 * Created by dewangankisslove on 30-01-2017.
 */
public class ViewsFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    NoViewListAdapter noViewListAdapter;

    private List<String> productNames = new ArrayList<>();
    private List<Integer> noViews = new ArrayList<>();

    private DatabaseReference mDatabase;
    ValueEventListener valueEventListener;
    private FirebaseAuth mAuth;
    Query query;
    TextView filterType;
    TextView filterDate;
    String month;
    String date;
    String year;

    ImageView filter;
    TextView noSales;
    CardView cvfilter;
    ImageView closeFilter;

    RelativeLayout rlMsg;
    TextView myListings;

    public interface goToListings {
        public void emptyStatsEvent();
    }

    goToListings emptySatsEvent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            emptySatsEvent = (goToListings) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement emptyStatsEventListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("stats").child("views").child(getUserUID());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        month = formattedDate.substring(0, 7);
        date = formattedDate;
        year = formattedDate.substring(0, 4);
        query = mDatabase.child("month").child(month);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_views, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                productNames.clear();
                noViews.clear();
                noViewListAdapter.notifyDataSetChanged();
                int i = 0;
                noSales.setText("");
                if (dataSnapshot != null)
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        ProductViewModel viewModel = postSnapshot.getValue(ProductViewModel.class);
                        i = i + viewModel.getNoViews();
                        if (productNames.contains(viewModel.getProductName())) {
                            noViews.set(productNames.indexOf(viewModel.getProductName()), (viewModel.getNoViews() + noViews.get(productNames.indexOf(viewModel.getProductName()))));
                        } else {
                            productNames.add(viewModel.getProductName());
                            noViews.add(viewModel.getNoViews());
                        }
                    }
                noSales.setText("" + i);
                noViewListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                // ...
            }
        };
        query.addValueEventListener(valueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getActivity().getFragmentManager().findFragmentByTag("Datepickerdialog");

        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    private void initUI(View view) {
        rlMsg = (RelativeLayout) view.findViewById(R.id.rl_msg);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        filterType = (TextView) view.findViewById(R.id.tv_filter_type);
        filterDate = (TextView) view.findViewById(R.id.tv_date);
        filter = (ImageView) view.findViewById(R.id.iv_filter);
        noSales = (TextView) view.findViewById(R.id.tv_total_sales);
        cvfilter = (CardView) view.findViewById(R.id.cv_filter);
        closeFilter = (ImageView) view.findViewById(R.id.iv_close);
        myListings = (TextView) view.findViewById(R.id.tv_my_listings);

        if (PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "showStats", "no").contentEquals("no")) {
            rlMsg.setVisibility(View.VISIBLE);
        } else {
            rlMsg.setVisibility(View.GONE);
        }
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        noViewListAdapter = new NoViewListAdapter(getActivity().getApplicationContext(), productNames, noViews);
        recyclerView.setAdapter(noViewListAdapter);

        noSales.setText("");
        cvfilter.setVisibility(View.GONE);


        filterType.setText("Month");
        filterDate.setText(month);
        filterType.setOnClickListener(this);
        filterDate.setOnClickListener(this);
        filter.setOnClickListener(this);
        closeFilter.setOnClickListener(this);
        myListings.setOnClickListener(this);
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }

    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(400);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(300);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        String tempDay;
        String tempMonth;
        String tempDate;
        filterDate.setText(date);
        if (filterType.getText().toString().contentEquals("Day")) {
            if (String.valueOf(dayOfMonth).length() == 1) {
                tempDay = "0" + dayOfMonth;
            } else {
                tempDay = "" + dayOfMonth;
            }
            if (String.valueOf(monthOfYear).length() == 1) {
                tempMonth = "0" + (monthOfYear + 1);
            } else {
                tempMonth = "" + (monthOfYear + 1);
            }
            tempDate = year + "-" + tempMonth + "-" + tempDay;
            query = mDatabase.child("day").child(tempDate);
            query.addListenerForSingleValueEvent(valueEventListener);
        } else if (filterType.getText().toString().contentEquals("Month")) {
            if (String.valueOf(monthOfYear).length() == 1) {
                tempMonth = "0" + (monthOfYear + 1);
            } else {
                tempMonth = "" + (monthOfYear + 1);
            }
            tempDate = year + "-" + tempMonth;
            Log.d("tempMonth", tempMonth);
            query = mDatabase.child("month").child(tempDate);
            query.addListenerForSingleValueEvent(valueEventListener);
        } else if (filterType.getText().toString().contentEquals("Year")) {
            tempDate = "" + year;
            query = mDatabase.child("year").child(tempDate);
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_my_listings:
                emptySatsEvent.emptyStatsEvent();
                break;
            case R.id.iv_filter:
                cvfilter.setVisibility(View.VISIBLE);
                cvfilter.startAnimation(inFromRightAnimation());
                break;
            case R.id.iv_close:
                cvfilter.startAnimation(outToRightAnimation());
                cvfilter.setVisibility(View.GONE);
                break;
            case R.id.tv_filter_type:
                showPopup(view);
                break;
            case R.id.tv_date:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ViewsFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.setAccentColor(getResources().getColor(R.color.highlightColor));
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            default:
                return;
        }
    }

    private void showPopup(View view) {
        Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.PopupMenu);
        final PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.stats_filter_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.day).setTitle(Html.fromHtml("<font color='#000000'>Day"));
        popup.getMenu().findItem(R.id.month).setTitle(Html.fromHtml("<font color='#000000'>Month"));
        popup.getMenu().findItem(R.id.year).setTitle(Html.fromHtml("<font color='#000000'>Year"));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.day) {
                    filterType.setText("Day");
                    filterDate.setText(date);
                } else if (item.getItemId() == R.id.month) {
                    filterType.setText("Month");
                    filterDate.setText(month);
                } else if (item.getItemId() == R.id.year) {
                    filterType.setText("Year");
                    filterDate.setText(year);
                }
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }
}

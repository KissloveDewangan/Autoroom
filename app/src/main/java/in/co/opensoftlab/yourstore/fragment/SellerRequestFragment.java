package in.co.opensoftlab.yourstore.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.OrderListAdapterSeller;
import in.co.opensoftlab.yourstore.model.NotificationHistory;
import in.co.opensoftlab.yourstore.model.NotificationModel;
import in.co.opensoftlab.yourstore.model.NotifyItemSeller;
import in.co.opensoftlab.yourstore.utils.PrefUtils;

/**
 * Created by dewangankisslove on 27-01-2017.
 */

public class SellerRequestFragment extends Fragment {
    LinearLayout parentLayout;
    RecyclerView recyclerView;
    List<NotifyItemSeller> orderItems = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    List<Integer> boolDates = new ArrayList<>();
    List<NotificationModel> orderModels = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    OrderListAdapterSeller orderListAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ValueEventListener valueEventListener;

    private DatabaseReference mOrderHistory;
    private DatabaseReference orderBuyer;
    private DatabaseReference mToken;

    AVLoadingIndicatorView loadingIndicatorView;
    LinearLayout msg;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("notification").child("seller").child(getUserUID());
        orderBuyer = FirebaseDatabase.getInstance().getReference("notification").child("buyer");
        mOrderHistory = FirebaseDatabase.getInstance().getReference("notificationHistory").child(getUserUID());
        mToken = FirebaseDatabase.getInstance().getReference("notificationToken");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
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
                orderItems.clear();
                orderModels.clear();
                dates.clear();
                boolDates.clear();
                orderListAdapter.notifyDataSetChanged();
                if (dataSnapshot != null)
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        NotificationModel orderModel = postSnapshot.getValue(NotificationModel.class);
                        NotifyItemSeller orderItem = null;

                        if (!dates.contains(orderModel.getRequestDate().split(" ")[0])) {
                            dates.add(orderModel.getRequestDate().split(" ")[0]);
                            boolDates.add(1);
                        } else {
                            boolDates.add(0);
                        }

                        if (orderModel.getRequestType().contentEquals("deal")) {
                            orderItem = new NotifyItemSeller(postSnapshot.getKey(), orderModel.getRequestDate(),
                                    orderModel.getPersonName(),
                                    orderModel.getPersonId(), orderModel.getRequestType(), orderModel.getPersonUrl(),
                                    "Hi " + orderModel.getMsg().split("::")[0] + ", I would like to buy your " +
                                            orderModel.getMsg().split("::")[1] + " at Rs " + orderModel.getMsg().split("::")[2] + ".",
                                    orderModel.getStatus());
                        } else {
                            orderItem = new NotifyItemSeller(postSnapshot.getKey(), orderModel.getRequestDate(),
                                    orderModel.getPersonName(),
                                    orderModel.getPersonId(), orderModel.getRequestType(), orderModel.getPersonUrl(),
                                    "Hi " + orderModel.getMsg().split("::")[0] + ", I'm interested to buy your " +
                                            orderModel.getMsg().split("::")[1] +
                                            ". I need your mobile number to talk more about your " +
                                            orderModel.getMsg().split("::")[2] + ".",
                                    orderModel.getStatus());
                        }
                        orderItems.add(orderItem);
                        orderModels.add(orderModel);
                    }
                orderListAdapter.notifyDataSetChanged();
                loadingIndicatorView.hide();
                recyclerView.setVisibility(View.VISIBLE);
                if (orderItems.isEmpty())
                    msg.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                loadingIndicatorView.hide();
                recyclerView.setVisibility(View.VISIBLE);
                // ...
            }
        };
    }

    private void initUI(final View view) {
        parentLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        msg = (LinearLayout) view.findViewById(R.id.ll_msg);
        loadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);

        recyclerView.setVisibility(View.GONE);
        loadingIndicatorView.smoothToShow();
        msg.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        orderListAdapter = new OrderListAdapterSeller(getActivity().getApplicationContext(), orderItems, boolDates);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderListAdapter);

        orderListAdapter.setOnItemClickListener(new OrderListAdapterSeller.MyClickListener() {
            @Override
            public void onItemClick(final int position, View v) {
                switch (v.getId()) {
                    case R.id.ll_reply:
                        if (isNetworkConnected()) {
                            showMobDialog(orderItems.get(position).getBuyerId(), orderModels.get(position).getPersonName(),
                                    orderModels.get(position).getMsg().split("::")[1]);
                        } else {
                            showConnectionFailed(parentLayout);
                        }
                        break;
                    case R.id.ll_accept:
                        if (isNetworkConnected()) {
                            final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(getContext());
                            alertDiallogBuilder.setTitle(Html.fromHtml("<font color='#212121'><b>Alert"));
                            alertDiallogBuilder.setMessage(Html.fromHtml("<font color=\"#424242\">Are you sure want to accept this deal.</font>"));
                            alertDiallogBuilder.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Yes</b></font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    mDatabase.child(orderItems.get(position).getRequestId()).child("status").setValue("accepted");
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd hh:mm aaa");
                                    String formattedDate = df.format(c.getTime());
                                    String key = mDatabase.push().getKey();
                                    String orderKey = mOrderHistory.push().getKey();
                                    NotificationModel model = null;
                                    if (PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "currentCity", "Location unavailable.").contentEquals("Location unavailable.")) {
                                        model = new NotificationModel("deal", formattedDate, getUserName(), getUserUID(), getUserUri(),
                                                orderModels.get(position).getPersonName() + "::" +
                                                        orderModels.get(position).getMsg().split("::")[1],
                                                "accepted", "Location unavailable.");
                                    } else {
                                        model = new NotificationModel("deal", formattedDate, getUserName(), getUserUID(), getUserUri(),
                                                orderModels.get(position).getPersonName() + "::" +
                                                        orderModels.get(position).getMsg().split("::")[1],
                                                "accepted",
                                                "Lives in " +
                                                        PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "currentCity", "Location unavailable."));
                                    }
                                    NotificationHistory orderHistory = new NotificationHistory(key,
                                            orderModels.get(position).getMsg().split("::")[1], formattedDate);
                                    orderBuyer.child(orderItems.get(position).getBuyerId()).child(key).setValue(model);
                                    mOrderHistory.child("seller").child(orderKey).setValue(orderHistory);
                                    mToken.child(orderModels.get(position).getPersonId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null)
                                                sendMsg(dataSnapshot.getValue().toString(), "deal",
                                                        "Autoroom", getUserName().split(" ")[0] + "::has::accepted::your::deal.", "empty");

                                            Toast.makeText(getActivity().getApplicationContext(), "Notification sent successfully!", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Notification sent successfully!", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                            alertDiallogBuilder.setNegativeButton(Html.fromHtml("<font color=\"#212121\"><b>No</b></font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            final AlertDialog alertDialog = alertDiallogBuilder.create();
                            alertDialog.show();
                        } else {
                            showConnectionFailed(parentLayout);
                        }
                        break;
                    case R.id.ll_reject:
                        if (isNetworkConnected()) {
                            final AlertDialog.Builder alertDiallogBuilder2 = new AlertDialog.Builder(getContext());
                            alertDiallogBuilder2.setTitle(Html.fromHtml("<font color='#212121'><b>Alert"));
                            alertDiallogBuilder2.setMessage(Html.fromHtml("<font color=\"#424242\">Are you sure want to reject this deal.</font>"));
                            alertDiallogBuilder2.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Yes</b></font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    mDatabase.child(orderItems.get(position).getRequestId()).child("status").setValue("rejected");
                                    Calendar c1 = Calendar.getInstance();
                                    SimpleDateFormat df1 = new SimpleDateFormat("yy/MM/dd hh:mm aaa");
                                    String formattedDate1 = df1.format(c1.getTime());
                                    String key1 = mDatabase.push().getKey();
                                    String orderKey1 = mOrderHistory.push().getKey();
                                    NotificationModel model1 = null;
                                    if (PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "currentCity", "Location unavailable.").contentEquals("Location unavailable.")) {
                                        model1 = new NotificationModel("deal", formattedDate1, getUserName(), getUserUID(), getUserUri(),
                                                orderModels.get(position).getPersonName() + "::" +
                                                        orderModels.get(position).getMsg().split("::")[1] + "::" +
                                                        orderModels.get(position).getMsg().split("::")[2],
                                                "rejected", "Location unavailable.");
                                    } else {
                                        model1 = new NotificationModel("deal", formattedDate1, getUserName(), getUserUID(), getUserUri(),
                                                orderModels.get(position).getPersonName() + "::" +
                                                        orderModels.get(position).getMsg().split("::")[1] + "::" +
                                                        orderModels.get(position).getMsg().split("::")[2],
                                                "rejected",
                                                "Lives in " +
                                                        PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "currentCity", "Location unavailable."));
                                    }
                                    NotificationHistory orderHistory1 = new NotificationHistory(key1,
                                            orderModels.get(position).getMsg().split("::")[1], formattedDate1);
                                    orderBuyer.child(orderItems.get(position).getBuyerId()).child(key1).setValue(model1);
                                    mOrderHistory.child("seller").child(orderKey1).setValue(orderHistory1);
                                    mToken.child(orderModels.get(position).getPersonId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null)
                                                sendMsg(dataSnapshot.getValue().toString(), "deal",
                                                        "Autoroom", getUserName().split(" ")[0] + "::has::rejected::your::deal.", "empty");

                                            Toast.makeText(getActivity().getApplicationContext(), "Notification sent successfully!", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Notification sent successfully!", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                            alertDiallogBuilder2.setNegativeButton(Html.fromHtml("<font color=\"#212121\"><b>No</b></font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            final AlertDialog alertDialog2 = alertDiallogBuilder2.create();
                            alertDialog2.show();
                        } else {
                            showConnectionFailed(parentLayout);
                        }
                        break;
                    default:
                        return;
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDatabase != null)
            mDatabase.removeEventListener(valueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabase.addValueEventListener(valueEventListener);
    }

    public void showConnectionFailed(LinearLayout linearLayout) {
        Snackbar snackbar = Snackbar.make(linearLayout, "No internet connection!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void sendMsg(final String id, final String type, final String title, final String msg, final String extra) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                InputStream inputStream = null;
                HttpURLConnection urlConnection = null;
                Integer result = 0;
                try {
                    String url = "http://139.59.18.133/api/message?";
                    url += "to=" + id;
                    url += "&type=" + type + "&title=" + title + "&msg=" + msg + "&extra=" + extra;
                    Log.d("url", url);
                    URL newurl = new URL(url);
                    urlConnection = (HttpURLConnection) newurl.openConnection();
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    int requestcode = urlConnection.getResponseCode();
                    Log.d("url", requestcode + "");
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (ProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();
    }

    public void showMobDialog(String id, String buyerName, String productName) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = AddMobDialogFragment.NewInstance(id, buyerName, productName);
        dialog.show(getActivity().getSupportFragmentManager(), "MobDialogFragment");
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }

    public String getUserName() {
        return mAuth.getCurrentUser().getDisplayName();
    }

    public String getUserUri() {
        return mAuth.getCurrentUser().getPhotoUrl().toString();
    }
}

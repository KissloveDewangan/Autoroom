package in.co.opensoftlab.yourstore.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.NotificationHistory;
import in.co.opensoftlab.yourstore.model.NotificationModel;
import in.co.opensoftlab.yourstore.utils.PrefUtils;

/**
 * Created by dewangankisslove on 27-12-2016.
 */

public class AddMobDialogFragment extends DialogFragment {
    EditText mobNo;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mOrderHistory;
    private DatabaseReference orderBuyer;
//    private DatabaseReference mToken;

    String id;
    String buyerName;
    String productName;

    public static AddMobDialogFragment NewInstance(String id, String buyerName, String productName) {
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("buyerName", buyerName);
        args.putString("productName", productName);
        AddMobDialogFragment fragment = new AddMobDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        id = args.getString("id");
        buyerName = args.getString("buyerName");
        productName = args.getString("productName");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("notification").child("seller").child(getUserUID());
        orderBuyer = FirebaseDatabase.getInstance().getReference("notification").child("buyer");
        mOrderHistory = FirebaseDatabase.getInstance().getReference("notificationHistory").child(getUserUID());
//        mToken = FirebaseDatabase.getInstance().getReference("notificationToken");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_mob_dialog, null);
        mobNo = (EditText) view.findViewById(R.id.et_mob_no);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setCancelable(false)
                .setTitle(Html.fromHtml("<font color='#212121'><b>Reply"))
                // Add action buttons
                .setPositiveButton(Html.fromHtml("<font color='black'>Send</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        if (mobNo.getText().toString().isEmpty() || mobNo.getText().toString().length() != 10) {
                            Toast.makeText(getActivity().getApplicationContext(), "Fill mobile number correctly.", Toast.LENGTH_SHORT).show();
                        } else {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd hh:mm aaa");
                            String formattedDate = df.format(c.getTime());
                            String key = mDatabase.push().getKey();
                            String orderKey = mOrderHistory.push().getKey();
                            NotificationModel model = null;
                            if (PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "currentCity", "Location unavailable.").contentEquals("Location unavailable.")) {
                                model = new NotificationModel("reqMob", formattedDate, getUserName(), getUserUID(), getUserUri(),
                                        buyerName + "::" + productName + "::" + mobNo.getText().toString(),
                                        "accepted", "Location unavailable.");
                            } else {
                                model = new NotificationModel("reqMob", formattedDate, getUserName(), getUserUID(), getUserUri(),
                                        buyerName + "::" + productName + "::" + mobNo.getText().toString(),
                                        "accepted",
                                        "Lives in " +
                                                PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "currentCity", "Location unavailable."));
                            }
                            NotificationHistory orderHistory = new NotificationHistory(key, productName, formattedDate);
                            orderBuyer.child(id).child(key).setValue(model);
                            mOrderHistory.child("seller").child(orderKey).setValue(orderHistory);
//                            mToken.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.getValue() != null)
//                                        sendMsg(dataSnapshot.getValue().toString(), "reqMob",
//                                                "Autoroom", getUserName().split(" ")[0] + "::has::sent::his::mobile::no.", "empty");
//
//                                    sendMsg(PrefUtils.getFromPrefs(getActivity().getApplicationContext(),
//                                            "token", "none"), "reqMob",
//                                            "Autoroom", getUserName().split(" ")[0] + "::has::sent::his::mobile::no.", "empty");
//
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    sendMsg(PrefUtils.getFromPrefs(getActivity().getApplicationContext(),
//                                            "token", "none"), "reqMob",
//                                            "Autoroom", getUserName().split(" ")[0] + "::has::sent::his::mobile::no.", "empty");
//                                    Toast.makeText(getContext(), "Notification sent successfully!", Toast.LENGTH_SHORT).show();
//                                    AddMobDialogFragment.this.getDialog().dismiss();
//                                }
//                            });
                            Toast.makeText(getActivity().getApplicationContext(), "Notification sent successfully!", Toast.LENGTH_SHORT).show();
                            AddMobDialogFragment.this.getDialog().dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddMobDialogFragment.this.getDialog().dismiss();
                    }
                });
        return builder.create();
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

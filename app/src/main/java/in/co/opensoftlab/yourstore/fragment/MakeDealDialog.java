package in.co.opensoftlab.yourstore.fragment;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Date;
import java.util.Locale;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.ChatModel;
import in.co.opensoftlab.yourstore.model.MsgItem;
import in.co.opensoftlab.yourstore.model.NotificationHistory;
import in.co.opensoftlab.yourstore.model.NotificationModel;
import in.co.opensoftlab.yourstore.utils.PrefUtils;

/**
 * Created by dewangankisslove on 14-03-2017.
 */

public class MakeDealDialog extends DialogFragment {
    EditText price;
    TextView makeDeal;
    TextView type;
    Button cancel;

    private FirebaseAuth mAuth;
    private DatabaseReference mNotificationHistory;
    private DatabaseReference mToken;
    private DatabaseReference mDatabase;

    String sellerId;
    String sellerName;
    String sellerLocation;
    String productId;
    String productPrice;
    String productName;
    String productType;

    public static MakeDealDialog NewInstance(String id, String name, String productId,
                                             String price, String productName,
                                             String sellerLocation, String productType) {
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("name", name);
        args.putString("productId", productId);
        args.putString("productPrice", price);
        args.putString("productName", productName);
        args.putString("productType", productType);
        args.putString("sellerLocation", sellerLocation);
        MakeDealDialog fragment = new MakeDealDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        sellerId = args.getString("id");
        sellerName = args.getString("name");
        productId = args.getString("productId");
        productPrice = args.getString("productPrice");
        productName = args.getString("productName");
        productType = args.getString("productType");
        sellerLocation = args.getString("sellerLocation");
        mAuth = FirebaseAuth.getInstance();
        mNotificationHistory = FirebaseDatabase.getInstance().getReference("notificationHistory").child(getUserUID()).child("buyer");
        mDatabase = FirebaseDatabase.getInstance().getReference("notification");
        mToken = FirebaseDatabase.getInstance().getReference("notificationToken");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.make_deal_dialog, null);
        price = (EditText) view.findViewById(R.id.tv_product_price);
        makeDeal = (TextView) view.findViewById(R.id.b_send);
        type = (TextView) view.findViewById(R.id.tv_type);
        cancel = (Button) view.findViewById(R.id.b_cancel);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        type.setText("I would like to buy this " + productType + " for");

        price.setText(productPrice);
        price.setSelection(productPrice.length());
        makeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (price.getText().toString().isEmpty() || price.getText().toString().contentEquals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter amount.", Toast.LENGTH_SHORT).show();
                } else {
                    createDeal();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeDealDialog.this.getDialog().cancel();
            }
        });

        builder.setView(view)
                .setTitle(Html.fromHtml("<font color='#212121'><b>Make a deal"))
                .setCancelable(false);
        // Add action buttons
        return builder.create();
    }

    private void createDeal() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd hh:mm aaa");
        String formattedDate = df.format(c.getTime());
        String key = mDatabase.push().getKey();
        String orderKey = mNotificationHistory.push().getKey();
        NotificationModel model = new NotificationModel("deal", formattedDate, getUserName(), getUserUID(),
                getUserUri(), sellerName + "::" + productName + "::" + price.getText().toString(), "null", sellerLocation);

        NotificationHistory orderHistory = new NotificationHistory(key, productId, formattedDate);
        mDatabase.child("seller").child(sellerId).child(key).setValue(model);
        mNotificationHistory.child(orderKey).setValue(orderHistory);
        mToken.child(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    sendMsg(dataSnapshot.getValue().toString(), "deal",
                            "Autoroom", getUserName().split(" ")[0] + "::wants::to::make::a::deal::with::you.", "empty");

                Toast.makeText(getActivity().getApplicationContext(), "Notification sent successfully!", Toast.LENGTH_LONG).show();
                MakeDealDialog.this.getDialog().dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "Notification sent successfully!", Toast.LENGTH_LONG).show();
                MakeDealDialog.this.getDialog().dismiss();
            }
        });
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

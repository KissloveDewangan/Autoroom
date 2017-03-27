package in.co.opensoftlab.yourstore.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.ChatModel;
import in.co.opensoftlab.yourstore.model.MsgItem;
import in.co.opensoftlab.yourstore.utils.PrefUtils;

/**
 * Created by dewangankisslove on 14-03-2017.
 */

public class SendMessageDialog extends DialogFragment {
    EditText msg;
    TextView sendMsg;
    Button cancel;

    private FirebaseAuth mAuth;
    private DatabaseReference mMessage;
    private DatabaseReference mDatabase;

    String sellerId;
    String sellerName;
    String sellerUrl;
    String sellerLocation;

    public static SendMessageDialog NewInstance(String id, String name, String url, String sellerLocation) {
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("name", name);
        args.putString("url", url);
        args.putString("sellerLocation", sellerLocation);
        SendMessageDialog fragment = new SendMessageDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        sellerId = args.getString("id");
        sellerName = args.getString("name");
        sellerUrl = args.getString("url");
        sellerLocation = args.getString("sellerLocation");
        mAuth = FirebaseAuth.getInstance();
        mMessage = FirebaseDatabase.getInstance().getReference("messages");
        mDatabase = FirebaseDatabase.getInstance().getReference("chats");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.send_msg_dialog, null);
        msg = (EditText) view.findViewById(R.id.et_msg);
        sendMsg = (TextView) view.findViewById(R.id.b_send);
        cancel = (Button) view.findViewById(R.id.b_cancel);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msg.getText().toString().isEmpty() || msg.getText().toString().contentEquals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Write a message.", Toast.LENGTH_SHORT).show();
                } else {
                    String time = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
                    String key = getUserUID() + "K17D" + sellerId;
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    final String formattedDate = df.format(c.getTime());

                    mDatabase.child("buyer").child(getUserUID()).child(key).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            ChatModel p = mutableData.getValue(ChatModel.class);
                            if (p == null) {
                                ChatModel model = new ChatModel(sellerName, sellerUrl, "@" + sellerLocation, formattedDate, 0, "false", "false");
                                mutableData.setValue(model);
                                return Transaction.success(mutableData);
                            }

                            if (p.getTitle().contentEquals("Location unavailable.")) {
                                p.setTitle("@" + sellerLocation);
                            }
                            // Set value and report transaction success
                            mutableData.setValue(p);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });

                    mDatabase.child("seller").child(sellerId).child(key).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            ChatModel p = mutableData.getValue(ChatModel.class);
                            if (p == null) {
                                ChatModel model = null;
                                if (PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "currentCity", "Location unavailable.").contentEquals("Location unavailable.")) {
                                    model = new ChatModel(getUserName(), getUserUri(),
                                            "Location unavailable.",
                                            formattedDate, 1, "false", "false");
                                } else {
                                    model = new ChatModel(getUserName(), getUserUri(),
                                            "Lives in " +
                                                    PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "currentCity", "Location unavailable."),
                                            formattedDate, 1, "false", "false");
                                }
                                mutableData.setValue(model);
                                return Transaction.success(mutableData);
                            }
                            // Set value and report transaction success
                            p.setUnreadMsg(1);
                            mutableData.setValue(p);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });

                    MsgItem msgItem = new MsgItem(getUserUID(), msg.getText().toString(), time, formattedDate);
                    mMessage.child(key).push().setValue(msgItem);
                    Toast.makeText(getActivity().getApplicationContext(), "Message sent successfully!. Check your chat box.", Toast.LENGTH_LONG).show();
                    SendMessageDialog.this.getDialog().dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessageDialog.this.getDialog().cancel();
            }
        });

        builder.setView(view)
                .setCancelable(false);
        // Add action buttons
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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

package in.co.opensoftlab.yourstore.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.OrderListAdapter;
import in.co.opensoftlab.yourstore.model.NotifyItemBuyer;
import in.co.opensoftlab.yourstore.model.NotificationModel;

/**
 * Created by dewangankisslove on 27-01-2017.
 */

public class BuyerRequestFragment extends Fragment {
    RecyclerView recyclerView;
    List<NotifyItemBuyer> orderItems = new ArrayList<>();
    List<NotificationModel> notifModels = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    List<Integer> boolDates = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    OrderListAdapter orderListAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ValueEventListener valueEventListener;

    AVLoadingIndicatorView loadingIndicatorView;
    LinearLayout msg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("notification").child("buyer").child(getUserUID());
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
                notifModels.clear();
                dates.clear();
                boolDates.clear();
                orderListAdapter.notifyDataSetChanged();
                if (dataSnapshot != null)
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        NotificationModel orderModel = postSnapshot.getValue(NotificationModel.class);
                        NotifyItemBuyer orderItem = null;

                        if (!dates.contains(orderModel.getRequestDate().split(" ")[0])) {
                            dates.add(orderModel.getRequestDate().split(" ")[0]);
                            boolDates.add(1);
                        } else {
                            boolDates.add(0);
                        }

                        if (orderModel.getRequestType().contentEquals("deal")) {
                            if (orderModel.getStatus().contentEquals("accepted")) {
                                orderItem = new NotifyItemBuyer(postSnapshot.getKey(), orderModel.getRequestDate(),
                                        orderModel.getPersonName(),
                                        orderModel.getPersonId(), orderModel.getRequestType(), orderModel.getPersonUrl()
                                        , "Hi " + orderModel.getMsg().split("::")[0] + ", I humbly accept the deal regarding " +
                                        orderModel.getMsg().split("::")[1] + ". Text me to finalize the deal.",
                                        orderModel.getStatus());
                            } else {
                                orderItem = new NotifyItemBuyer(postSnapshot.getKey(), orderModel.getRequestDate(),
                                        orderModel.getPersonName(),
                                        orderModel.getPersonId(), orderModel.getRequestType(), orderModel.getPersonUrl()
                                        , "Hi " + orderModel.getMsg().split("::")[0] + ", Sorry to say but I can't offer " +
                                        orderModel.getMsg().split("::")[1] + " at Rs " + orderModel.getMsg().split("::")[2] +
                                        ". I am open for bargaining through chat if you are still interested to buy " +
                                        orderModel.getMsg().split("::")[1] + ".",
                                        orderModel.getStatus());
                            }
                        } else {
                            orderItem = new NotifyItemBuyer(postSnapshot.getKey(), orderModel.getRequestDate(),
                                    orderModel.getPersonName(),
                                    orderModel.getPersonId(), orderModel.getRequestType(), orderModel.getPersonUrl()
                                    , "Hi " + orderModel.getMsg().split("::")[0] + ", I appreciate for showing your interest in " +
                                    orderModel.getMsg().split("::")[1] + ". For any queries call me at " + orderModel.getMsg().split("::")[2] + ".",
                                    orderModel.getStatus());
                        }
                        orderItems.add(orderItem);
                        notifModels.add(orderModel);
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        msg = (LinearLayout) view.findViewById(R.id.ll_msg);
        loadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);

        recyclerView.setVisibility(View.GONE);
        loadingIndicatorView.smoothToShow();
        msg.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        orderListAdapter = new OrderListAdapter(getActivity().getApplicationContext(), orderItems, boolDates);
        recyclerView.setAdapter(orderListAdapter);

        orderListAdapter.setOnItemClickListener(new OrderListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (v.getId()) {
                    case R.id.iv_send_msg:
                        showMobDialog(orderItems.get(position).getSellerId(), orderItems.get(position).getSellerName(),
                                orderItems.get(position).getSellerUrl(),
                                notifModels.get(position).getLocation());
                        break;
                    case R.id.iv_call:
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:0" + notifModels.get(position).getMsg().split("::")[2]));
                        startActivity(callIntent);
                        break;
                    default:
                        return;
                }
            }
        });

    }

    public void showMobDialog(String id, String name, String url, String location) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = SendMessageDialog.NewInstance(id, name, url, location);
        dialog.show(getActivity().getSupportFragmentManager(), "SendMsgDialogFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabase.removeEventListener(valueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabase.addValueEventListener(valueEventListener);
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }
}

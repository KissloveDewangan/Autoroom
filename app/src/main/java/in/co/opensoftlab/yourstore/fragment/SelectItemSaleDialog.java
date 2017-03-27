package in.co.opensoftlab.yourstore.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.activity.AddBikeInfo;
import in.co.opensoftlab.yourstore.activity.AddProductInfo;
import in.co.opensoftlab.yourstore.model.NotificationHistory;
import in.co.opensoftlab.yourstore.model.NotificationModel;

/**
 * Created by dewangankisslove on 14-03-2017.
 */

public class SelectItemSaleDialog extends DialogFragment {
    RelativeLayout car;
    RelativeLayout bike;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.list_product_dialog, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        car = (RelativeLayout) view.findViewById(R.id.rl_car);
        bike = (RelativeLayout) view.findViewById(R.id.rl_bike);

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddingProduct();
                SelectItemSaleDialog.this.getDialog().dismiss();
            }
        });

        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddingBike();
                SelectItemSaleDialog.this.getDialog().dismiss();
            }
        });

        builder.setView(view);
        builder.setTitle(Html.fromHtml("<font color='#212121'><b>Which item you want to list for sale?"));
        // Add action buttons
        return builder.create();
    }

    private void startAddingProduct() {
        startActivity(new Intent(getActivity(), AddProductInfo.class));
    }

    private void startAddingBike() {
        startActivity(new Intent(getActivity(), AddBikeInfo.class));
    }
}

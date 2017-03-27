package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
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
import android.widget.RelativeLayout;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.activity.AddBikeInfo;
import in.co.opensoftlab.yourstore.activity.AddProductInfo;

/**
 * Created by dewangankisslove on 14-03-2017.
 */

public class SelectCategoryDialog extends DialogFragment {
    RelativeLayout car;
    RelativeLayout bike;
    RelativeLayout all;

    public interface onSelectCategory {
        public void selectCategoryEvent(String category);
    }

    onSelectCategory selectCategoryEvent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            selectCategoryEvent = (onSelectCategory) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement selectCategoryEventListener");
        }
    }


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
        View view = inflater.inflate(R.layout.select_category_dialog, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        car = (RelativeLayout) view.findViewById(R.id.rl_car);
        bike = (RelativeLayout) view.findViewById(R.id.rl_bike);
        all = (RelativeLayout) view.findViewById(R.id.rl_all);

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCategoryEvent.selectCategoryEvent("Car");
                SelectCategoryDialog.this.getDialog().dismiss();
            }
        });

        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCategoryEvent.selectCategoryEvent("Bike");
                SelectCategoryDialog.this.getDialog().dismiss();
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCategoryEvent.selectCategoryEvent("All");
                SelectCategoryDialog.this.getDialog().dismiss();
            }
        });

        builder.setView(view);
        builder.setTitle(Html.fromHtml("<font color='#212121'><b>Select"));
        // Add action buttons
        return builder.create();
    }
}

package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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

/**
 * Created by dewangankisslove on 14-03-2017.
 */

public class SelectImagesOption extends DialogFragment {
    String[] resource = {"Take a Snapshot", "Upload from Gallery"};
//    RelativeLayout snap;
//    RelativeLayout upload;

    public interface onSelectImgOption {
        public void selectImgOption(String option);
    }

    onSelectImgOption selectImgOption;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            selectImgOption = (onSelectImgOption) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement selectImgOptionListener");
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
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.img_option_dialog, null);
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        snap = (RelativeLayout) view.findViewById(R.id.rl_snap);
//        upload = (RelativeLayout) view.findViewById(R.id.rl_upload);
//
//        snap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectImgOption.selectImgOption("snap");
//                SelectImagesOption.this.getDialog().dismiss();
//            }
//        });
//
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                SelectImagesOption.this.getDialog().dismiss();
//            }
//        });

//        builder.setView(view);
        builder.setTitle(Html.fromHtml("<font color='#212121'><b>Select"));
        builder.setItems(resource, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                // The 'which' argument contains the index position
                // of the selected item
                selectImgOption.selectImgOption(resource[position]);
            }
        });
        // Add action buttons
        return builder.create();
    }
}

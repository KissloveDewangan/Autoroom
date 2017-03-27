package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;

/**
 * Created by dewangankisslove on 26-12-2016.
 */

public class OptionPickerDialogFragment extends DialogFragment {

    String title;
    String[] resource;

    public static OptionPickerDialogFragment NewInstance(String title, String[] resource) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArray("resource", resource);
        OptionPickerDialogFragment fragment = new OptionPickerDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        title = args.getString("title");
        resource = args.getStringArray("resource");
    }

    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface CategoryDialogListener {
        public void onDialogClick(DialogFragment dialog, int position, String title);
    }

    // Use this instance of the interface to deliver action events
    CategoryDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CategoryDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(Html.fromHtml("<font color='#212121'><b>" + title))
                .setItems(resource, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        mListener.onDialogClick(OptionPickerDialogFragment.this, position, title);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

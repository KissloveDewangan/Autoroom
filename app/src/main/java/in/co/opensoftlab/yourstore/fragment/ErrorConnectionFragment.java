package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.co.opensoftlab.yourstore.R;

/**
 * Created by dewangankisslove on 20-12-2016.
 */

public class ErrorConnectionFragment extends Fragment implements View.OnClickListener {
    TextView retry;

    String tabName;

    public interface onRetry {
        public void retryOpenTabEvent(String tabName);
    }

    onRetry retryOpenTabEvent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            retryOpenTabEvent = (onRetry) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement retryEventListener");
        }
    }


    public static ErrorConnectionFragment NewInstance(String tabName) {
        Bundle args = new Bundle();
        args.putString("tabName", tabName);
        ErrorConnectionFragment fragment = new ErrorConnectionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        tabName = args.getString("tabName");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error_connection, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        retry = (TextView) view.findViewById(R.id.tv_retry);

        retry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_retry:
                retryOpenTabEvent.retryOpenTabEvent(tabName);
                break;
            default:
                return;
        }
    }
}

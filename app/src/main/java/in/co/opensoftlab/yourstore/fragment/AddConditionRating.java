package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.helper.DatabaseHandler;

/**
 * Created by dewangankisslove on 19-03-2017.
 */

public class AddConditionRating extends Fragment implements View.OnClickListener {
    int fragNo = 0;
    int maxFrag = 1;
    String productId;
    String productType;

    ContentLoadingProgressBar progressBar;
    Button next;
    Button back;
    RatingBar ratingBar;
    TextView conditionName;
    TextView question;
    TextView queNo;

    DatabaseHandler db;
    String[] condName;
    String[] ratingName;

    public interface conditionCompleted {
        public void conditionCompleteEvent();
    }

    conditionCompleted completedCondition;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            completedCondition = (conditionCompleted) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement condCompletedEventListener");
        }
    }


    public static AddConditionRating newInstance(String productType, int maxFrag, String productId) {
        AddConditionRating fragment = new AddConditionRating();
        Bundle b = new Bundle();
        b.putInt("maxFrag", maxFrag);
        b.putString("productId", productId);
        b.putString("productType", productType);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getActivity());
        maxFrag = getArguments().getInt("maxFrag");
        productId = getArguments().getString("productId");
        productType = getArguments().getString("productType");

        ratingName = getActivity().getResources().getStringArray(R.array.ratings);

        if (maxFrag == 8) {
            condName = getActivity().getResources().getStringArray(R.array.car_condition_name);
        } else {
            condName = getActivity().getResources().getStringArray(R.array.bike_condition_name);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ratings, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initButtons();
    }

    private void initButtons() {
        back.setEnabled(fragNo != 0);
        next.setEnabled(true);
    }

    private void initViews() {
        int actualQueNo = fragNo + 1;
        if (fragNo == maxFrag) {
            progressBar.setProgress(100);
        } else {
            int progress = 100 / (maxFrag + 1);
            progressBar.setProgress(progress * actualQueNo);
        }

        queNo.setText("Question " + actualQueNo);
        if (maxFrag == 8 && fragNo == 1) {
            StringBuilder sb = new StringBuilder(condName[fragNo]);
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            sb.setCharAt(1, Character.toLowerCase(sb.charAt(1)));
            question.setText("How would you rate your " + productType + " " + sb.toString() + " condition?");
        } else {
            StringBuilder sb = new StringBuilder(condName[fragNo]);
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            question.setText("How would you rate your " + productType + " " + sb.toString() + " condition?");
        }


        String ratingVal = db.getFeatureVal(productId, condName[fragNo]);
        Log.d("ratingVal", ratingVal);
        if (ratingVal == null || ratingVal.contentEquals("") || ratingVal.contentEquals("N/A")) {
            conditionName.setText("Not Rated");
            ratingBar.setRating(0.0f);
        } else {
            float rate = Float.parseFloat(ratingVal);
            int rateV = (int) rate;
            Log.d("ratingName", ratingName[0]);
            conditionName.setText(ratingName[rateV - 1]);
            ratingBar.setRating(rate);
        }
    }


    private void initUI(View view) {
        progressBar = (ContentLoadingProgressBar) view.findViewById(R.id.progress_bar);
        next = (Button) view.findViewById(R.id.b_next);
        back = (Button) view.findViewById(R.id.b_back);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        conditionName = (TextView) view.findViewById(R.id.tv_condition);
        question = (TextView) view.findViewById(R.id.tv_que);
        queNo = (TextView) view.findViewById(R.id.tv_que_no);

        ratingBar.setStepSize(1.0f);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v != 0.0f)
                    conditionName.setText(ratingName[(int) (v - 1)]);
            }
        });

        next.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_next:
                if (fragNo >= maxFrag) {
                    if (!db.checkFeature(productId, condName[fragNo])) {
                        if (ratingBar.getRating() == 0.0f)
                            db.addFeature(productId, condName[fragNo], "N/A");
                        else
                            db.addFeature(productId, condName[fragNo], String.valueOf(ratingBar.getRating()));
                    } else {
                        db.removeFeatures(productId, condName[fragNo]);
                        if (ratingBar.getRating() == 0.0f)
                            db.addFeature(productId, condName[fragNo], "N/A");
                        else
                            db.addFeature(productId, condName[fragNo], String.valueOf(ratingBar.getRating()));
                    }
                    completedCondition.conditionCompleteEvent();
                } else {
                    if (!db.checkFeature(productId, condName[fragNo])) {
                        if (ratingBar.getRating() == 0.0f)
                            db.addFeature(productId, condName[fragNo], "N/A");
                        else
                            db.addFeature(productId, condName[fragNo], String.valueOf(ratingBar.getRating()));
                    } else {
                        db.removeFeatures(productId, condName[fragNo]);
                        if (ratingBar.getRating() == 0.0f)
                            db.addFeature(productId, condName[fragNo], "N/A");
                        else
                            db.addFeature(productId, condName[fragNo], String.valueOf(ratingBar.getRating()));
                    }
                    fragNo++;
                    initViews();
                    initButtons();
                }
                break;
            case R.id.b_back:
                fragNo--;
                initViews();
                initButtons();
                break;
            default:
                return;
        }
    }
}

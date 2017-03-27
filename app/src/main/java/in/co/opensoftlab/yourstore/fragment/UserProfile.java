package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.activity.EditProfile;
import in.co.opensoftlab.yourstore.activity.Feedback;
import in.co.opensoftlab.yourstore.activity.Help;
import in.co.opensoftlab.yourstore.activity.Settings;
import in.co.opensoftlab.yourstore.model.Users;
import in.co.opensoftlab.yourstore.utils.PrefUtils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by dewangankisslove on 08-12-2016.
 */

public class UserProfile extends Fragment implements View.OnClickListener{
    CircleImageView profilePic;
    TextView userName;
    RelativeLayout invite;
    RelativeLayout help;
    RelativeLayout settings;
    RelativeLayout feedback;
    TextView editProfile;

    RelativeLayout switchMode;
    TextView userSwitchField;

    String TAG = "error";
    private static final int REQUEST_INVITE = 0;

    private DatabaseReference mDatabase;

    CoordinatorLayout coordinatorLayout;
    private BottomSheetBehavior<View> behavior;
    private BottomSheetDialog dialog;

    public interface onSwitchMode {
        public void switchEvent(String s);
    }

    onSwitchMode switchEvent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            switchEvent = (onSwitchMode) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSwitchEventListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(getUid());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_profile, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (PrefUtils.getFromPrefs(getApplicationContext(), "SwitchMode", "buyer").contentEquals("buyer")) {
            userSwitchField.setText("Switch to Seller");
        } else {
            userSwitchField.setText("Switch to Buyer");
        }

        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefUtils.getFromPrefs(getApplicationContext(), "SwitchMode", "buyer").contentEquals("buyer")) {
                    PrefUtils.saveToPrefs(getApplicationContext(), "SwitchMode", "seller");
                    switchEvent.switchEvent("seller");
                } else {
                    PrefUtils.saveToPrefs(getApplicationContext(), "SwitchMode", "buyer");
                    switchEvent.switchEvent("buyer");
                }

            }
        });

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Users user = dataSnapshot.getValue(Users.class);
                // [START_EXCLUDE]
                if (user != null) {
                    Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(profilePic);
                    userName.setText(user.getName().split(" ")[0]);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        };
        mDatabase.addValueEventListener(valueEventListener);
    }

    private void initUI(View view) {
        profilePic = (CircleImageView) view.findViewById(R.id.image);
        userName = (TextView) view.findViewById(R.id.user_name);
        invite = (RelativeLayout) view.findViewById(R.id.rl_invite_friends);
        help = (RelativeLayout) view.findViewById(R.id.rl_help);
        settings = (RelativeLayout) view.findViewById(R.id.rl_settings);
        editProfile = (TextView) view.findViewById(R.id.user_edit_profile);
        switchMode = (RelativeLayout) view.findViewById(R.id.rl_user_switch);
        feedback = (RelativeLayout) view.findViewById(R.id.rl_feedback);
        userSwitchField = (TextView) view.findViewById(R.id.tv_user_switch);


//        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
//        View bottomSheet = coordinatorLayout.view.findViewById(R.id.bottom_sheet);
//        behavior = BottomSheetBehavior.from(bottomSheet);
//        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                // React to state change
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                // React to dragging events
//            }
//        });

        invite.setOnClickListener(this);
        help.setOnClickListener(this);
        settings.setOnClickListener(this);
        feedback.setOnClickListener(this);
        editProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_edit_profile:
                startActivity(new Intent(getActivity().getApplicationContext(), EditProfile.class));
                break;
            case R.id.rl_invite_friends:
                onInviteClicked();
                break;
            case R.id.rl_help:
                startActivity(new Intent(getApplicationContext(), Help.class));
                break;
            case R.id.rl_settings:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            case R.id.rl_feedback:
                startActivity(new Intent(getApplicationContext(), Feedback.class));
                break;
            default:
                return;
        }
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
//                .setCustomImage(R.drawable.ic_launcher)
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private boolean dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return true;
        }

        return false;
    }

//    private void createDialog() {
//        if (dismissDialog()) {
//            return;
//        }
//
//        List<BottomSheet> list = new ArrayList<>();
////        list.add(new BottomSheet(R.string.share, R.mipmap.ic_launcher));
////        list.add(new BottomSheet(R.string.upload, R.mipmap.ic_launcher));
////        list.add(new BottomSheet(R.string.copy, R.mipmap.ic_launcher));
////        list.add(new BottomSheet(R.string.print, R.mipmap.ic_launcher));
//
//        BottomSheetAdapter adapter = new BottomSheetAdapter(list);
//        adapter.setOnItemClickListener(new BottomSheetAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BottomSheetAdapter.ItemHolder item, int position) {
//                //dismissDialog();
//            }
//        });
//
//        View view = getLayoutInflater(getArguments()).inflate(R.layout.bottom_sheet, null);
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setAdapter(adapter);
//
//
//        dialog = new BottomSheetDialog(getApplicationContext());
//        dialog.setContentView(view);
//        dialog.show();
//    }
}

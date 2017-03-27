package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import in.co.opensoftlab.yourstore.BuildConfig;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.fragment.AllProductFragment;
import in.co.opensoftlab.yourstore.fragment.BikeFragment;
import in.co.opensoftlab.yourstore.fragment.BuyerRequestFragment;
import in.co.opensoftlab.yourstore.fragment.CarFragment;
import in.co.opensoftlab.yourstore.fragment.ErrorConnectionFragment;
import in.co.opensoftlab.yourstore.fragment.InboxFragment;
import in.co.opensoftlab.yourstore.fragment.InboxFragmentSeller;
import in.co.opensoftlab.yourstore.fragment.ListingFragment;
import in.co.opensoftlab.yourstore.fragment.SelectCategoryDialog;
import in.co.opensoftlab.yourstore.fragment.SellerRequestFragment;
import in.co.opensoftlab.yourstore.fragment.UserProfile;
import in.co.opensoftlab.yourstore.fragment.ViewsFragment;
import in.co.opensoftlab.yourstore.fragment.WishlistFragment;
import in.co.opensoftlab.yourstore.helper.DatabaseHandler;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements
        UserProfile.onSwitchMode, SelectCategoryDialog.onSelectCategory,
        GoogleApiClient.OnConnectionFailedListener, InboxFragmentSeller.onMyListings,
        InboxFragment.onStartExploring, ErrorConnectionFragment.onRetry,
        ViewsFragment.goToListings {

    boolean doubleBackToExitPressedOnce = false;
    BottomNavigationView buyerBottomNavigationView;
    BottomNavigationView sellerBottomNavigationView;

    FrameLayout landingLayout;

    private static final String TAG = "UserAuth";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseHandler db;

    private GoogleApiClient googleApiClient;

    MixpanelAPI mMixpanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String projectToken = BuildConfig.ANALYTICS_TOKEN;
        mMixpanel = MixpanelAPI.getInstance(this, projectToken);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(googleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                }
                            }
                        });

        db = new DatabaseHandler(this);

        initUI();

        if (PrefUtils.getFromPrefs(getApplicationContext(), "SwitchMode", "buyer").contentEquals("buyer")) {
            openBuyerHome();
        } else {
            setToolbarSeller();
            openSellerInboxFrag();
        }

        mAuth = FirebaseAuth.getInstance();
        //Log.d("providerID",mAuth.getCurrentUser().getProviders().get(0).split());
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                }
            }
        };


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void tracking(String tabName) {
        try {
            JSONObject props = new JSONObject();
            props.put("TabName", tabName);
            mMixpanel.track("Navigation Tabs", props);
        } catch (JSONException e) {
            Log.e("Autoroom", "Unable to add properties to JSONObject", e);
        }
    }

    private void trackSearchCategory(String category) {
        try {
            JSONObject props = new JSONObject();
            props.put("categoryName", category);
            mMixpanel.track("Search Category", props);
        } catch (JSONException e) {
            Log.e("Autoroom", "Unable to add properties to JSONObject", e);
        }
    }

    private void initUI() {
        buyerBottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_buyer);
        sellerBottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_seller);
        landingLayout = (FrameLayout) findViewById(R.id.landing_layout);


        buyerBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_explore:
                                openBuyerHome();
                                break;
                            case R.id.action_wishlist:
                                openWishlistFrag();
                                break;
                            case R.id.action_buyer_chat:
                                openBuyerInboxFrag();
                                break;
                            case R.id.action_buyer_notify:
                                openOrders();
                                break;
                            case R.id.action_buyer_account:
                                openProfileActivity();
                                break;
                        }
                        return true;
                    }
                });

        sellerBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_seller_chats:
                                openSellerInboxFrag();
                                break;
                            case R.id.action_seller_notify:
                                openSellerOrders();
                                break;
                            case R.id.action_listings:
                                openListingFragment();
                                break;
                            case R.id.action_stats:
                                openStats();
                                break;
                            case R.id.action_seller_account:
                                openProfileActivity();
                                break;
                        }
                        return true;
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
        mAuth.addAuthStateListener(mAuthListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.disconnect();
    }

    private void setToolbarSeller() {
        buyerBottomNavigationView.setVisibility(View.GONE);
        sellerBottomNavigationView.setVisibility(View.VISIBLE);
        buyerBottomNavigationView.getMenu().getItem(0).setChecked(true);
        sellerBottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private void setToolbarBuyer() {
        sellerBottomNavigationView.setVisibility(View.GONE);
        buyerBottomNavigationView.setVisibility(View.VISIBLE);
        buyerBottomNavigationView.getMenu().getItem(0).setChecked(true);
        sellerBottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("Main Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }
    private void openStats() {
        if (isNetworkConnected()) {
            tracking("stats");
            Fragment orderFragment = new ViewsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.landing_layout, orderFragment);
            fragmentTransaction.commit();
        } else {
            openErrorFragment("accountSeller");
        }
    }

    private void openSellerOrders() {
        if (isNetworkConnected()) {
            tracking("sellerNotification");
            Fragment orderFragment = new SellerRequestFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.landing_layout, orderFragment);
            fragmentTransaction.commit();
        } else {
            openErrorFragment("accountSeller");
        }
    }

    private void openSellerInboxFrag() {
        if (isNetworkConnected()) {
            tracking("sellerInbox");
            Fragment inboxFragment = new InboxFragmentSeller();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.landing_layout, inboxFragment);
            fragmentTransaction.commit();
        } else {
            openErrorFragment("accountSeller");
        }
    }

    private void openWishlistFrag() {
        if (isNetworkConnected()) {
            tracking("wishlist");
            Fragment wishlistFragment = new WishlistFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.landing_layout, wishlistFragment);
            fragmentTransaction.commit();
        } else {
            openErrorFragment("accountBuyer");
        }
    }

    private void openOrders() {
        if (isNetworkConnected()) {
            tracking("buyerNotification");
            Fragment orderFragment = new BuyerRequestFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.landing_layout, orderFragment);
            fragmentTransaction.commit();
        } else {
            openErrorFragment("accountBuyer");
        }
    }

    private void openListingFragment() {
        if (isNetworkConnected()) {
            tracking("listing");
            Fragment listingFragmet = new ListingFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.landing_layout, listingFragmet);
            fragmentTransaction.commit();
        } else {
            openErrorFragment("accountSeller");
        }
    }

    private void openBuyerHome() {
        setToolbarBuyer();
        //testFunc();
        if (isNetworkConnected()) {
            tracking("explore");
            Fragment buyerFragmet = AllProductFragment.NewInstance("all");
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.landing_layout, buyerFragmet);
            fragmentTransaction.commit();
        } else {
            openErrorFragment("accountBuyer");
        }
    }

    private void openBuyerInboxFrag() {
        if (isNetworkConnected()) {
            tracking("buyerInbox");
            Fragment inboxFragment = new InboxFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.landing_layout, inboxFragment);
            fragmentTransaction.commit();
        } else {
            openErrorFragment("accountBuyer");
        }
    }

    private void openProfileActivity() {
        if (isNetworkConnected()) {
            if (PrefUtils.getFromPrefs(MainActivity.this, "loggedState", "false").contentEquals("true")) {
                Fragment userProf = new UserProfile();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.landing_layout, userProf);
                fragmentTransaction.commit();
            } else {
                Intent intent = new Intent(MainActivity.this, UserSignup.class);
                startActivity(intent);
            }
        } else {
            openErrorFragment("account");
        }
    }

    private void openErrorFragment(String tabName) {
        Fragment errorFragmet = ErrorConnectionFragment.NewInstance(tabName);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.landing_layout, errorFragmet);
        fragmentTransaction.commit();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static int randInt() {
        int min = 1000;
        int max = 9999;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    @Override
    public void switchEvent(String s) {
        if (PrefUtils.getFromPrefs(getApplicationContext(), "SwitchMode", "buyer").contentEquals("buyer")) {
            setToolbarBuyer();
            if (isNetworkConnected()) {
                Fragment buyerFragmet = AllProductFragment.NewInstance("all");
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.landing_layout, buyerFragmet);
                fragmentTransaction.commit();
            } else {
                openErrorFragment("accountBuyer");
            }
        } else {
            setToolbarSeller();
            openSellerInboxFrag();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void listingEvent() {
        sellerBottomNavigationView.getMenu().getItem(2).setChecked(true);
        openListingFragment();
    }

    @Override
    public void exploringEvent() {
        openBuyerHome();
    }

    @Override
    public void retryOpenTabEvent(String tabName) {
        if (tabName.contentEquals("accountBuyer")) {
            openBuyerHome();
        } else if (tabName.contentEquals("accountSeller")) {
            setToolbarSeller();
            openSellerInboxFrag();
        } else if (tabName.contentEquals("account")) {
            openProfileActivity();
        }

    }

    @Override
    public void emptyStatsEvent() {
        sellerBottomNavigationView.getMenu().getItem(2).setChecked(true);
        openListingFragment();
    }

    @Override
    public void selectCategoryEvent(String category) {
        if (isNetworkConnected()) {
            trackSearchCategory(category);
            if (category.contentEquals("Car")) {
                Fragment buyerFragmet = CarFragment.NewInstance("all");
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.landing_layout, buyerFragmet);
                fragmentTransaction.commit();
            } else if (category.contentEquals("Bike")) {
                Fragment buyerFragmet = BikeFragment.NewInstance("all");
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.landing_layout, buyerFragmet);
                fragmentTransaction.commit();
            } else {
                Fragment buyerFragmet = AllProductFragment.NewInstance("all");
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.landing_layout, buyerFragmet);
                fragmentTransaction.commit();
            }

        } else {
            openErrorFragment("accountBuyer");
        }
    }

    @Override
    protected void onDestroy() {
        mMixpanel.flush();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(MainActivity.this, "Click back again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

//    View view = MaterialRippleLayout.on(LayoutInflater.from(parent.getContext()).inflate(layoutR, parent, false))
//            .rippleOverlay(true)
//            .rippleAlpha(0.2f)
//            .rippleColor(Color.parseColor("#BDBDBD"))
//            .rippleHover(true)
//            .create();

//    Calendar c = Calendar.getInstance();
//    SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
//    String formattedDate = df.format(c.getTime());
//    String oldDate = db.getOrderHistory(bundle.getString("productId"));
//
//    Date d1 = null;
//    Date d2 = null;
//                    try {
//        d1 = df.parse(oldDate);
//        d2 = df.parse(formattedDate);
//    } catch (ParseException e) {
//        e.printStackTrace();
//    }
//    long diff = d2.getTime() - d1.getTime();
//    long diffMinutes = diff / (60 * 1000) % 60;
//    long diffHours = diff / (60 * 60 * 1000);
//                    Log.d("diffTime", diffMinutes + ":" + diffHours);
}

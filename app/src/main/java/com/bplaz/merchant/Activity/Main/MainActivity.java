package com.bplaz.merchant.Activity.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bplaz.merchant.Activity.Main.Tab.DashboardFragment;
import com.bplaz.merchant.Activity.Main.Tab.ManageFragment;
import com.bplaz.merchant.Activity.Main.Tab.ProductFragment;
import com.bplaz.merchant.Activity.Main.Tab.SalesFragment;
import com.bplaz.merchant.PreferanceManager.PreferenceManagerLogin;
import com.bplaz.merchant.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static long back_pressed;
    PreferenceManagerLogin session;
    Merlin merlin;
    LinearLayout linear_no_internet;
    String token;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        session = new PreferenceManagerLogin(getApplicationContext());
        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().build(getApplicationContext());
        linear_no_internet = findViewById(R.id.linear_no_internet);
        linear_no_internet.setVisibility(View.GONE);

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        //IF NOT LOGIN GO BACK TO LOGIN PAGE
        if(session.checkLogin()){
            finish();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE},1);

            bottomNavigationView = findViewById(R.id.navigation);
            BottomNavigationHelper.removeShiftMode(bottomNavigationView);
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.setOnNavigationItemSelectedListener
                    (new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            Fragment selectedFragment = null;
                            switch (item.getItemId()) {
                                case R.id.dashboard: //DASHBOARD
                                    selectedFragment = new DashboardFragment();
                                    loadFragment(selectedFragment);
                                    break;
                                case R.id.sales: //SALES
                                    selectedFragment = new SalesFragment();
                                    loadFragment(selectedFragment);
                                    break;
                                case R.id.products: //PRODUCT
                                    selectedFragment = new ProductFragment();
                                    loadFragment(selectedFragment);
                                    break;
                                case R.id.manage: //MANAGE
                                    selectedFragment = new ManageFragment();
                                    loadFragment(selectedFragment);
                                    break;
                            }

                            return true;
                        }
                    });

            //set first fragment
            DashboardFragment fragment = new DashboardFragment();
            loadFragment(fragment);
            bottomNavigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
        }
    }

    //BACK FUNCTION
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())  moveTaskToBack(true);
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();

        if(!isOnline()){
            noInternetConnectionDialog();
        }

        //IF INTERNET CONNECTION OK
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                linear_no_internet.setVisibility(View.GONE);
            }
        });

        //IF INTERNET CONNECTION NOT OK
        merlin.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noInternetConnectionDialog();
                    }
                });
            }
        });

    }

    @Override
    protected void onPause() {
        merlin.unbind();
        super.onPause();
    }

    @Override
    protected void onStop() {
        merlin.unbind();
        super.onStop();
    }

    //FOR REMOVE ANIMATION NAVIGATION BAR
    private static final class BottomNavigationHelper {
        @SuppressLint("RestrictedApi")
        static void removeShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShifting(false);
                item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void noInternetConnectionDialog(){
        linear_no_internet.setVisibility(View.VISIBLE);
    }
}

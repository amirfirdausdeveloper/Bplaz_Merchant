package com.bplaz.merchant.Activity.Staff;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bplaz.merchant.Activity.Product.EditProductDetails;
import com.bplaz.merchant.Activity.Profile.ProfileActivity;
import com.bplaz.merchant.Adapter.ProductAdapter;
import com.bplaz.merchant.Adapter.RiderAdapter;
import com.bplaz.merchant.Class.ProductClass;
import com.bplaz.merchant.Class.RiderClass;
import com.bplaz.merchant.Connection.BasedURL;
import com.bplaz.merchant.PreferanceManager.PreferenceManagerLogin;
import com.bplaz.merchant.R;
import com.bplaz.merchant.common.StandardProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class StaffActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    RecyclerView rv;
    PreferenceManagerLogin session;
    String token;
    List<RiderClass> riderClasses;
    RiderAdapter adapter;
    StandardProgressDialog dialogs;
    LinearLayout linear_staff_no;
    ImageView imageView_back;
    SearchView sc;
    TextView textView_header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        session = new PreferenceManagerLogin(getApplicationContext());
        dialogs = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        imageView_back = findViewById(R.id.imageView_back);
        textView_header = findViewById(R.id.textView_header);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        rv = findViewById(R.id.rv);
        linear_staff_no = findViewById(R.id.linear_staff_no);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),AddStaff.class);
                startActivity(next);
                StaffActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        sc = findViewById(R.id.sc);

        sc.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_back.setVisibility(View.GONE);
                textView_header.setVisibility(View.GONE);
            }
        });
        sc.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                imageView_back.setVisibility(View.VISIBLE);
                textView_header.setVisibility(View.VISIBLE);
                return false;
            }
        });

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sc.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        sc.setMaxWidth(Integer.MAX_VALUE);
        sc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRiderList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StaffActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void getRiderList(){
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        riderClasses = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/stafflist.json?page=1&limit=100",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray users_arr = new JSONArray(obj.getString("users"));
                            if(users_arr.length() == 0){
                                linear_staff_no.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                            }else {
                                rv.setVisibility(View.VISIBLE);
                                linear_staff_no.setVisibility(View.GONE);

                                for (int i =0; i < users_arr.length(); i++){
                                    JSONObject obj_rider = users_arr.getJSONObject(i);
                                    riderClasses.add(new RiderClass(
                                            obj_rider.getString("id"),
                                            obj_rider.getString("name"),
                                            obj_rider.getString("email"),
                                            obj_rider.getString("telephone_number")
                                    ));

                                    adapter = new RiderAdapter(getApplicationContext(), riderClasses, new RiderAdapter.onClickJobByMonth() {
                                        @Override
                                        public void onClick(final RiderClass jobByMonthClass) {
                                            Log.d("click","clic");

                                        }
                                    },StaffActivity.this);
                                    rv.setAdapter(adapter);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        parseVolleyError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+token);
                headers.put("Content-Type", "application/json");

                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Toast.makeText(getApplicationContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}

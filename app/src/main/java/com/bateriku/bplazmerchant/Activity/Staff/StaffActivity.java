package com.bateriku.bplazmerchant.Activity.Staff;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.bateriku.bplazmerchant.Activity.Sales.ViewSales;
import com.bateriku.bplazmerchant.Adapter.RiderAdapter;
import com.bateriku.bplazmerchant.Class.RiderClass;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.common.StandardProgressDialog;
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


                new AlertDialog.Builder(StaffActivity.this)
                        .setCancelable(true)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Add Staff / Rider")
                        .setPositiveButton("Add Staff", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent next = new Intent(getApplicationContext(),AddStafff.class);
                                startActivity(next);
                                StaffActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }
                        })
                        .setNegativeButton("Add Rider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent next = new Intent(getApplicationContext(),AddStaff.class);
                                startActivity(next);
                                StaffActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        })
                        .show();
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
                                    String info_rider = "";
                                    if(obj_rider.has("info_rider")){
                                        if(obj_rider.getString("info_rider").equals("null")){
                                            info_rider = "STAFF";

                                            riderClasses.add(new RiderClass(
                                                    obj_rider.getString("id"),
                                                    obj_rider.getString("name"),
                                                    obj_rider.getString("email"),
                                                    obj_rider.getString("telephone_number"),
                                                    info_rider,
                                                    "",
                                                    ""
                                            ));
                                        }else{
                                            JSONObject obj_info_rider = new JSONObject(obj_rider.getString("info_rider"));
                                            info_rider = "RIDER";

                                            riderClasses.add(new RiderClass(
                                                    obj_rider.getString("id"),
                                                    obj_rider.getString("name"),
                                                    obj_rider.getString("email"),
                                                    obj_rider.getString("telephone_number"),
                                                    info_rider,
                                                    obj_info_rider.getString("date_of_birth"),
                                                    obj_rider.getString("current_location")
                                            ));
                                        }

                                    }else{
                                            info_rider = "STAFF";

                                            riderClasses.add(new RiderClass(
                                                    obj_rider.getString("id"),
                                                    obj_rider.getString("name"),
                                                    obj_rider.getString("email"),
                                                    obj_rider.getString("telephone_number"),
                                                    info_rider,
                                                    "",
                                                    ""
                                            ));
                                    }



                                    adapter = new RiderAdapter(getApplicationContext(), riderClasses, new RiderAdapter.onClickJobByMonth() {
                                        @Override
                                        public void onClick(final RiderClass jobByMonthClass) {
                                            if (jobByMonthClass.getStaff_status().equals("STAFF")){
                                                showPictureDialogStaff(jobByMonthClass.getName(),jobByMonthClass.getId()
                                                        ,jobByMonthClass.getEmail(),jobByMonthClass.getTelephone_number(),jobByMonthClass.getDate_of_birth()
                                                        ,jobByMonthClass.getCurrent_location());
                                            }else{
                                                showPictureDialog(jobByMonthClass.getName(),jobByMonthClass.getId()
                                                        ,jobByMonthClass.getEmail(),jobByMonthClass.getTelephone_number(),jobByMonthClass.getDate_of_birth()
                                                        ,jobByMonthClass.getCurrent_location());
                                            }


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

    private void showPictureDialog(final String name, final String id, final String email, final String phone, final String dob, final String location) {
        new AlertDialog.Builder(StaffActivity.this)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure want to edit "+name+" ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent next = new Intent(getApplicationContext(),EditStaff.class);
                        next.putExtra("intent_id",id);
                        next.putExtra("intent_name",name);
                        next.putExtra("intent_email",email);
                        next.putExtra("intent_phone",phone);
                        next.putExtra("intent_password","");
                        next.putExtra("intent_dob",dob);
                        next.putExtra("intent_location",location);
                        startActivity(next);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showPictureDialogStaff(final String name, final String id, final String email, final String phone, final String dob, final String location) {
        new AlertDialog.Builder(StaffActivity.this)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure want to edit "+name+" ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent next = new Intent(getApplicationContext(),EditStafff.class);
                        next.putExtra("intent_id",id);
                        next.putExtra("intent_name",name);
                        next.putExtra("intent_email",email);
                        next.putExtra("intent_phone",phone);
                        next.putExtra("intent_password","");
                        next.putExtra("intent_dob",dob);
                        next.putExtra("intent_location",location);
                        startActivity(next);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}

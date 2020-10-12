package com.bateriku.bplazmerchant.Activity.Sales;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.Adapter.RiderAssignAdapter;
import com.bateriku.bplazmerchant.Class.RiderAssignClass;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.common.StandardProgressDialog;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class AssignRider extends AppCompatActivity {

    RecyclerView rv;
    PreferenceManagerLogin session;
    StandardProgressDialog dialog;
    String token,id;
    SearchableSpinner spinner_km;
    CheckBox checkBox_private;
    String latitude,longitude,sale_id,sale_partner_item_id;
    List<RiderAssignClass> riderAssignClasses;
    RiderAssignAdapter adapter;
    SearchView sv;
    ImageView imageView_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_rider);

        session = new PreferenceManagerLogin(getApplicationContext());
        dialog = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        sv = findViewById(R.id.sv);
        checkBox_private = findViewById(R.id.checkBox_private);
        spinner_km = findViewById(R.id.spinner_km);
        id = getIntent().getStringExtra("id");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        sale_id = getIntent().getStringExtra("sale_id");
        sale_partner_item_id = getIntent().getStringExtra("sale_partner_item_id");
        rv = findViewById(R.id.rv);
        imageView_back = findViewById(R.id.imageView_back);

        checkBox_private.setChecked(true);
        checkBox_private.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getRider();
            }
        });

        spinner_km.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getRider();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sv.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        sv.setMaxWidth(Integer.MAX_VALUE);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AssignRider.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getRider(){
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        riderAssignClasses = new ArrayList<>();

        final JSONObject jsonData = new JSONObject();
        try {

            if(checkBox_private.isChecked()){
                jsonData.put("check","true");
            }else{
                jsonData.put("check","");
            }
            jsonData.put("distance", spinner_km.getSelectedItem().toString());
            jsonData.put("latitude",latitude);
            jsonData.put("longitude",longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonData.toString();


        StringRequest stringRequest = new StringRequest(POST, BasedURL.ROOT_URL+"merchant_sales/rider_list.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray obj_riders = new JSONArray(obj.getString("riders"));
                            for (int i =0; i < obj_riders.length(); i++){
                                JSONObject objs = obj_riders.getJSONObject(i);

                                riderAssignClasses.add(new RiderAssignClass(
                                        objs.getString("id"),
                                        objs.getString("name"),
                                        objs.getString("email"),
                                        objs.getString("telephone_number"),
                                        objs.getString("distance")
                                ));

                                adapter = new RiderAssignAdapter(getApplicationContext(), riderAssignClasses, new RiderAssignAdapter.onClickJobByMonth() {
                                    @Override
                                    public void onClick(final RiderAssignClass jobByMonthClass) {

                                        new AlertDialog.Builder(AssignRider.this)
                                                .setCancelable(true)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setMessage("Are you sure want to Assign Sales to "+jobByMonthClass.getName() +" ?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        assignSales(jobByMonthClass.getId(),id,sale_id);
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
                                },AssignRider.this);
                                rv.setAdapter(adapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        parseVolleyError(error);
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void assignSales(String rider_id, String sale_partner_id, String sale_id) {
        final JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("rider_id", rider_id);
            jsonData.put("sale_partner_item_id",sale_partner_item_id);
            jsonData.put("sale_id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("jsonData.toString()",jsonData.toString());
        final String requestBody = jsonData.toString();


        StringRequest stringRequest = new StringRequest(POST, BasedURL.ROOT_URL+"merchant_sales/assign_motec.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Assign process saved.",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        parseVolleyError(error);
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            if (error.networkResponse != null) {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                Toast.makeText(getApplicationContext(),data.getString("message"),Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

}

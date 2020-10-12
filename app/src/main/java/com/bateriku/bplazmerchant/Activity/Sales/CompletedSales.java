package com.bateriku.bplazmerchant.Activity.Sales;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.Adapter.ToAcceptsAdapter;
import com.bateriku.bplazmerchant.Class.ToAcceptClass;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.common.StandardProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class CompletedSales extends AppCompatActivity {

    RecyclerView rv;
    LinearLayout linear_no_job;
    List<ToAcceptClass> toAcceptClasses;
    ToAcceptsAdapter adapter;
    public static String token;
    ImageView imageView_back;

    StandardProgressDialog dialogs;
    PreferenceManagerLogin session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        session = new PreferenceManagerLogin(getApplicationContext());
        dialogs = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        rv = findViewById(R.id.rv);
        linear_no_job = findViewById(R.id.linear_no_job);
        imageView_back = findViewById(R.id.imageView_back);


        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogs.show();
        getTotalSales();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CompletedSales.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getTotalSales(){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject paging = new JSONObject(obj.getString("paging"));
                            getListToAccept(paging.getString("count"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialogs.dismiss();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getListToAccept(String limit){
        rv.setLayoutManager(new LinearLayoutManager(this));
        toAcceptClasses = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales.json?status=6&limit="+limit,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogs.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray sales_arr = new JSONArray(obj.getString("sales"));
                            if(sales_arr.length() == 0){
                                linear_no_job.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                            }else {
                                rv.setVisibility(View.VISIBLE);
                                linear_no_job.setVisibility(View.GONE);

                                for (int i =0; i < sales_arr.length(); i++){
                                    JSONObject obj_rider = sales_arr.getJSONObject(i);
                                    JSONObject object_customer = new JSONObject(obj_rider.getString("customer"));
                                    String dates = "";
                                    JSONArray sale_partner_items_arr = new JSONArray(obj_rider.getString("sale_partner_items"));
                                    for (int ii = 0; ii < sale_partner_items_arr.length(); ii++){
                                        JSONObject object = sale_partner_items_arr.getJSONObject(ii);

                                        if (obj_rider.getString("assign_date").equals("null") || obj_rider.getString("assign_date").equals(null) || obj_rider.getString("assign_date").equals("")){
                                            dates = obj_rider.getString("job_date");
                                        }else{
                                            dates = obj_rider.getString("assign_date");
                                        }

                                        toAcceptClasses.add(new ToAcceptClass(
                                                obj_rider.getString("id"),
                                                obj_rider.getString("tenant_id"),
                                                dates,
                                                obj_rider.getString("latitude"),
                                                obj_rider.getString("longitude"),
                                                obj_rider.getString("geo_location"),
                                                object.getString("category_id"),
                                                obj_rider.getString("status"),
                                                obj_rider.getString("total_amount"),
                                                "",
                                                obj_rider.getString("sale_id"),
                                                object.getString("id"),
                                                object_customer.getString("name")
                                        ));
                                    }


                                    adapter = new ToAcceptsAdapter(getApplicationContext(), toAcceptClasses, new ToAcceptsAdapter.onClickJobByMonth() {
                                        @Override
                                        public void onClick(final ToAcceptClass jobByMonthClass) {
                                            String[] pictureDialogItemss = {
                                                    "View Sales"};
                                            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(CompletedSales.this);
                                            pictureDialog.setTitle("Select Action");
                                            pictureDialog.setItems(pictureDialogItemss,
                                                    new DialogInterface.OnClickListener() {
                                                        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case 0:
                                                                    Intent next = new Intent(getApplicationContext(),ViewSales.class);
                                                                    next.putExtra("id",jobByMonthClass.getId()+".json");
                                                                    startActivity(next);
                                                                    CompletedSales.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                    break;

                                                            }
                                                        }
                                                    });
                                            pictureDialog.show();
                                        }
                                    }, CompletedSales.this);
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
                        dialogs.dismiss();
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

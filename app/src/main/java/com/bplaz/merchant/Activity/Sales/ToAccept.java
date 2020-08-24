package com.bplaz.merchant.Activity.Sales;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bplaz.merchant.Activity.Product.EditProductDetails;
import com.bplaz.merchant.Activity.Staff.StaffActivity;
import com.bplaz.merchant.Adapter.RiderAdapter;
import com.bplaz.merchant.Adapter.ToAcceptAdapter;
import com.bplaz.merchant.Class.RiderClass;
import com.bplaz.merchant.Class.ToAcceptClass;
import com.bplaz.merchant.Connection.BasedURL;
import com.bplaz.merchant.PreferanceManager.PreferenceManagerLogin;
import com.bplaz.merchant.R;
import com.bplaz.merchant.common.StandardProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.PUT;

public class ToAccept extends AppCompatActivity {

    RecyclerView rv;
    LinearLayout linear_no_job;
    List<ToAcceptClass> toAcceptClasses;
    ToAcceptAdapter adapter;
    public static String token;
    ImageView imageView_back;

    StandardProgressDialog dialogs;
    PreferenceManagerLogin session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_accept);

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
        getListToAccept();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ToAccept.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getListToAccept(){
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        toAcceptClasses = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales.json?status=1",
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

                                    JSONArray sale_partner_items_arr = new JSONArray(obj_rider.getString("sale_partner_items"));
                                    for (int ii = 0; ii < sale_partner_items_arr.length(); ii++){
                                        JSONObject object = sale_partner_items_arr.getJSONObject(ii);

                                        toAcceptClasses.add(new ToAcceptClass(
                                                obj_rider.getString("id"),
                                                obj_rider.getString("tenant_id"),
                                                obj_rider.getString("job_date"),
                                                obj_rider.getString("latitude"),
                                                obj_rider.getString("longitude"),
                                                obj_rider.getString("geo_location"),
                                                object.getString("category_id"),
                                                obj_rider.getString("status"),
                                                obj_rider.getString("total_amount"),"",
                                                obj_rider.getString("sale_id"),
                                                object.getString("id")
                                        ));
                                    }


                                    adapter = new ToAcceptAdapter(getApplicationContext(), toAcceptClasses, new ToAcceptAdapter.onClickJobByMonth() {
                                        @Override
                                        public void onClick(final ToAcceptClass jobByMonthClass) {
                                            String[] pictureDialogItemss = {
                                                    "View Sales",
                                                    "Accept Sales","Reject Sales"};
                                            androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(ToAccept.this);
                                            pictureDialog.setTitle("Select Action");
                                            pictureDialog.setItems(pictureDialogItemss,
                                                    new android.content.DialogInterface.OnClickListener() {
                                                        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                                                        @Override
                                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case 0:
                                                                    Intent next = new Intent(getApplicationContext(),ViewSales.class);
                                                                    next.putExtra("id",jobByMonthClass.getId()+".json");
                                                                    startActivity(next);
                                                                    ToAccept.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                    break;
                                                                case 1:
                                                                    new AlertDialog.Builder(ToAccept.this)
                                                                            .setCancelable(true)
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setMessage("Are you sure want to Accept Sales?")
                                                                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    acceptSalesAPI(jobByMonthClass.getId()+".json");
                                                                                }
                                                                            })
                                                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            })
                                                                            .show();
                                                                    break;

                                                                case 2:
                                                                    new AlertDialog.Builder(ToAccept.this)
                                                                            .setCancelable(true)
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setMessage("Are you sure want to Reject Sales?")
                                                                            .setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    rejectSalesAPI(jobByMonthClass.getId()+".json");
                                                                                }
                                                                            })
                                                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            })
                                                                            .show();
                                                                    break;
                                                            }
                                                        }
                                                    });
                                            pictureDialog.show();
                                        }
                                    },ToAccept.this);
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
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Toast.makeText(getApplicationContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    private void acceptSalesAPI(String job_id){
        StringRequest stringRequest = new StringRequest(PUT, BasedURL.ROOT_URL+"merchant_sales/accept/"+job_id,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogs.dismiss();
                        Toast.makeText(getApplicationContext(),"Sales has been accepted",Toast.LENGTH_SHORT).show();
                        getListToAccept();

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("accept","1");
                return headers;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+token);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void rejectSalesAPI(String job_id){
        StringRequest stringRequest = new StringRequest(PUT, BasedURL.ROOT_URL+"merchant_sales/reject/"+job_id,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogs.dismiss();
                        Toast.makeText(getApplicationContext(),"Sales has been rejected",Toast.LENGTH_SHORT).show();
                        getListToAccept();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("reject","1");
                return headers;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+token);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}

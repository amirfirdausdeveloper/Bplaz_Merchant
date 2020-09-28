package com.bateriku.bplazmerchant.Activity.Main.Tab;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.Activity.Sales.AssignRider;
import com.bateriku.bplazmerchant.Activity.Sales.CreateSales;
import com.bateriku.bplazmerchant.Activity.Sales.ViewSales;
import com.bateriku.bplazmerchant.Adapter.ToAcceptAdapter;
import com.bateriku.bplazmerchant.Class.ToAcceptClass;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
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
import static com.android.volley.Request.Method.PUT;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment {

    LinearLayout linear_no_product;
    RecyclerView rv;
    List<ToAcceptClass> toAcceptClasses;
    ToAcceptAdapter adapter;
    public static String token;

    StandardProgressDialog dialogs;
    PreferenceManagerLogin session;
    String total_sales;
    FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sales, container, false);

        session = new PreferenceManagerLogin(getContext());
        dialogs = new StandardProgressDialog(getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        rv = v.findViewById(R.id.rv);
        linear_no_product = v.findViewById(R.id.linear_no_product);
        floatingActionButton = v.findViewById(R.id.floatingActionButton);

        dialogs.show();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getActivity(), CreateSales.class);
                startActivity(next);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getTotalSales();
    }

    private void getTotalSales(){
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        toAcceptClasses = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject paging = new JSONObject(obj.getString("paging"));

                            total_sales = paging.getString("count");
                            Log.d("TOTAL SALES",total_sales);
                            getListSales();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void getListSales(){
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        toAcceptClasses = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales.json?page=1&limit="+total_sales,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogs.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray sales_arr = new JSONArray(obj.getString("sales"));
                            if(sales_arr.length() == 0){
                                linear_no_product.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                            }else {
                                rv.setVisibility(View.VISIBLE);
                                linear_no_product.setVisibility(View.GONE);

                                for (int i =0; i < sales_arr.length(); i++){
                                    JSONObject obj_rider = sales_arr.getJSONObject(i);
                                    String rider_email = "";
                                    String dates = "";

                                    JSONArray sale_partner_items_arr = new JSONArray(obj_rider.getString("sale_partner_items"));
                                    for (int ii = 0; ii < sale_partner_items_arr.length(); ii++){
                                        JSONObject object = sale_partner_items_arr.getJSONObject(ii);

                                        String customer_names = "";
                                        if(obj_rider.has("customer")){
                                            if (obj_rider.getString("customer").equals("null") || obj_rider.getString("customer").equals(null) || obj_rider.getString("customer").equals("")){
                                                customer_names = "n/a";
                                            }else{
                                                JSONObject object_customer = new JSONObject(obj_rider.getString("customer"));
                                                customer_names = object_customer.getString("name");
                                            }
                                        }else{
                                            customer_names = "n/a";
                                        }


                                        //RIDER INFO
                                        if (obj_rider.getString("rider_job").equals("null") || obj_rider.getString("rider_job").equals(null) || obj_rider.getString("rider_job").equals("")){
                                            rider_email = "";
                                        }else{
                                            JSONObject rider_job_obj = new JSONObject(obj_rider.getString("rider_job"));
                                            JSONObject rider_obj = new JSONObject(rider_job_obj.getString("rider"));
                                            rider_email = rider_obj.getString("email");
                                        }

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
                                                rider_email,
                                                obj_rider.getString("sale_id"),
                                                object.getString("id"),
                                                customer_names
                                        ));
                                    }


                                    adapter = new ToAcceptAdapter(getActivity(), toAcceptClasses, new ToAcceptAdapter.onClickJobByMonth() {
                                        @Override
                                        public void onClick(final ToAcceptClass jobByMonthClass) {
                                            Log.d("status",jobByMonthClass.getStatus());

                                            if(jobByMonthClass.getSale_id().equals("") || jobByMonthClass.getSale_id().equals("null")){

                                                if (jobByMonthClass.getStatus().equals("1")){
                                                    String[] pictureDialogItemss = {
                                                            "View Sales",
                                                            "Accept Sales","Reject Sales"};
                                                    androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                                                    pictureDialog.setTitle("Select Action");
                                                    pictureDialog.setItems(pictureDialogItemss,
                                                            new android.content.DialogInterface.OnClickListener() {
                                                                @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                                                                @Override
                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                    switch (which) {
                                                                        case 0:
                                                                            Intent next = new Intent(getContext(),ViewSales.class);
                                                                            next.putExtra("id",jobByMonthClass.getId()+".json");
                                                                            startActivity(next);
                                                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                            break;
                                                                        case 1:
                                                                            new AlertDialog.Builder(getActivity())
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
                                                                            new AlertDialog.Builder(getActivity())
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
                                                }else{
                                                    String[] pictureDialogItems;
                                                    String[] pictureDialogItemss = {
                                                            "View Sales",
                                                            "Assign Rider",
                                                            "Cancel Sales"};
                                                    pictureDialogItems = pictureDialogItemss;

                                                    androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                                                    pictureDialog.setTitle("Select Action");
                                                    pictureDialog.setItems(pictureDialogItems,
                                                            new android.content.DialogInterface.OnClickListener() {
                                                                @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                                                                @Override
                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                    switch (which) {
                                                                        case 0:
                                                                            Intent next = new Intent(getContext(),ViewSales.class);
                                                                            next.putExtra("id",jobByMonthClass.getId()+".json");
                                                                            startActivity(next);
                                                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                            break;
                                                                        case 1:
                                                                            Intent nexts = new Intent(getContext(), AssignRider.class);
                                                                            nexts.putExtra("id",jobByMonthClass.getId());
                                                                            nexts.putExtra("latitude",jobByMonthClass.getLatitude());
                                                                            nexts.putExtra("longitude",jobByMonthClass.getLongitude());
                                                                            nexts.putExtra("sale_id",jobByMonthClass.getSale_id());
                                                                            nexts.putExtra("sale_partner_item_id",jobByMonthClass.getSale_partner_item_id());
                                                                            startActivity(nexts);
                                                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                            break;

                                                                        case 2:
                                                                            new AlertDialog.Builder(getActivity())
                                                                                    .setCancelable(true)
                                                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                    .setMessage("Are you sure want to Cancel Sales?")
                                                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            cancelSalesFunction(jobByMonthClass.getId()+".json");
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

                                            }else{
                                                if (jobByMonthClass.getStatus().equals("1")){
                                                    String[] pictureDialogItemss = {
                                                            "View Sales",
                                                            "Accept Sales","Reject Sales"};
                                                    androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                                                    pictureDialog.setTitle("Select Action");
                                                    pictureDialog.setItems(pictureDialogItemss,
                                                            new android.content.DialogInterface.OnClickListener() {
                                                                @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                                                                @Override
                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                    switch (which) {
                                                                        case 0:
                                                                            Intent next = new Intent(getContext(),ViewSales.class);
                                                                            next.putExtra("id",jobByMonthClass.getId()+".json");
                                                                            startActivity(next);
                                                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                            break;
                                                                        case 1:
                                                                            new AlertDialog.Builder(getActivity())
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
                                                                            new AlertDialog.Builder(getActivity())
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
                                                }else if(!jobByMonthClass.getStatus().equals("1")){
                                                    String[] pictureDialogItems;
                                                    Log.d("RIDER NAME",jobByMonthClass.getRider_name());
                                                    if(jobByMonthClass.getRider_name().equals("")){
                                                        String[] pictureDialogItemss = {
                                                                "View Sales",
                                                                "Assign Rider"};
                                                        pictureDialogItems = pictureDialogItemss;

                                                        androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                                                        pictureDialog.setTitle("Select Action");
                                                        pictureDialog.setItems(pictureDialogItems,
                                                                new android.content.DialogInterface.OnClickListener() {
                                                                    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                                                                    @Override
                                                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                                                        switch (which) {
                                                                            case 0:
                                                                                Intent next = new Intent(getContext(),ViewSales.class);
                                                                                next.putExtra("id",jobByMonthClass.getId()+".json");
                                                                                startActivity(next);
                                                                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                                break;
                                                                            case 1:
                                                                                Intent nexts = new Intent(getContext(), AssignRider.class);
                                                                                nexts.putExtra("id",jobByMonthClass.getId());
                                                                                nexts.putExtra("latitude",jobByMonthClass.getLatitude());
                                                                                nexts.putExtra("longitude",jobByMonthClass.getLongitude());
                                                                                nexts.putExtra("sale_id",jobByMonthClass.getSale_id());
                                                                                nexts.putExtra("sale_partner_item_id",jobByMonthClass.getSale_partner_item_id());
                                                                                startActivity(nexts);
                                                                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                                break;

                                                                            case 2:

                                                                                break;
                                                                        }
                                                                    }
                                                                });
                                                        pictureDialog.show();
                                                    }else{
                                                        String[] pictureDialogItemss = {
                                                                "View Sales"};
                                                        pictureDialogItems = pictureDialogItemss;

                                                        androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                                                        pictureDialog.setTitle("Select Action");
                                                        pictureDialog.setItems(pictureDialogItems,
                                                                new android.content.DialogInterface.OnClickListener() {
                                                                    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                                                                    @Override
                                                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                                                        switch (which) {
                                                                            case 0:
                                                                                Intent next = new Intent(getContext(),ViewSales.class);
                                                                                next.putExtra("id",jobByMonthClass.getId()+".json");
                                                                                startActivity(next);
                                                                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                                break;
                                                                        }
                                                                    }
                                                                });
                                                        pictureDialog.show();
                                                    }

                                                }
                                            }


                                        }
                                    },getActivity());
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void cancelSalesFunction(String job_id) {
        StringRequest stringRequest = new StringRequest(PUT, BasedURL.ROOT_URL+"merchant_sales/cancel/"+job_id,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogs.dismiss();
                        Toast.makeText(getContext(),"Sales has been Cancelled",Toast.LENGTH_SHORT).show();
                        dialogs.show();
                        getTotalSales();

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Toast.makeText(getContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getContext(),"Sales has been accepted",Toast.LENGTH_SHORT).show();
                        dialogs.show();
                        getTotalSales();

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void rejectSalesAPI(String job_id){
        StringRequest stringRequest = new StringRequest(PUT, BasedURL.ROOT_URL+"merchant_sales/reject/"+job_id,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogs.dismiss();
                        Toast.makeText(getContext(),"Sales has been rejected",Toast.LENGTH_SHORT).show();
                        dialogs.show();
                        getTotalSales();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}

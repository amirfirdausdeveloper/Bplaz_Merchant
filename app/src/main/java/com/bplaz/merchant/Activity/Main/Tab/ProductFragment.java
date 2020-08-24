package com.bplaz.merchant.Activity.Main.Tab;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bplaz.merchant.Activity.Product.AddProduct;
import com.bplaz.merchant.Activity.Product.EditProductDetails;
import com.bplaz.merchant.Activity.Profile.ProfileActivity;
import com.bplaz.merchant.Adapter.ProductAdapter;
import com.bplaz.merchant.Class.ProductClass;
import com.bplaz.merchant.Connection.BasedURL;
import com.bplaz.merchant.PreferanceManager.PreferenceManagerLogin;
import com.bplaz.merchant.R;
import com.bplaz.merchant.common.StandardProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.DELETE;
import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.android.volley.Request.Method.PUT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment {

    RecyclerView rv;
    LinearLayout linear_no_product;
    PreferenceManagerLogin session;
    String token;
    List<ProductClass> productClasses;
    ProductAdapter adapter;
    SearchView sc;
    TextView textView;
    StandardProgressDialog dialogs;
    FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_product, container, false);

        session = new PreferenceManagerLogin(getContext());
        dialogs = new StandardProgressDialog(getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        linear_no_product = v.findViewById(R.id.linear_no_product);
        textView = v.findViewById(R.id.textView);
        floatingActionButton = v.findViewById(R.id.floatingActionButton);
        rv = v.findViewById(R.id.rv);
        sc = v.findViewById(R.id.sc);


        sc.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setVisibility(View.GONE);
            }
        });
        sc.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                textView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        sc.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getActivity(), AddProduct.class);
                startActivity(next);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getProductList();
    }

    public void getProductList(){
        rv.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        productClasses = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/product.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray products_arr = new JSONArray(obj.getString("products"));
                            if(products_arr.length() == 0){
                                linear_no_product.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                                sc.setVisibility(View.GONE);
                            }else {
                                sc.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.VISIBLE);
                                linear_no_product.setVisibility(View.GONE);

                                for (int i =0; i < products_arr.length(); i++){
                                    JSONObject product_obj = products_arr.getJSONObject(i);
                                    if (product_obj.has("pricing_partner")){
                                        if(product_obj.getString("pricing_partner").equals(null) || product_obj.getString("pricing_partner").equals("null")){
                                            productClasses.add(new ProductClass(
                                                    product_obj.getString("id"),
                                                    product_obj.getString("brand"),
                                                    product_obj.getString("product_name"),
                                                    product_obj.getString("description"),
                                                    product_obj.getString("image_product"),
                                                    product_obj.getString("manufacturer"),
                                                    product_obj.getString("category"),
                                                    product_obj.getString("category_id"),
                                                    product_obj.getString("service"),
                                                    product_obj.getString("availability"),
                                                    product_obj.getString("price"),
                                                    product_obj.getString("status"),
                                                    "0"
                                            ));
                                        }else{
                                            JSONObject pricing_partner_obj = new JSONObject(product_obj.getString("pricing_partner"));
                                            productClasses.add(new ProductClass(
                                                    product_obj.getString("id"),
                                                    product_obj.getString("brand"),
                                                    product_obj.getString("product_name"),
                                                    product_obj.getString("description"),
                                                    product_obj.getString("image_product"),
                                                    product_obj.getString("manufacturer"),
                                                    product_obj.getString("category"),
                                                    product_obj.getString("category_id"),
                                                    product_obj.getString("service"),
                                                    product_obj.getString("availability"),
                                                    product_obj.getString("price"),
                                                    product_obj.getString("status"),
                                                    pricing_partner_obj.getString("rsp_price")
                                            ));
                                        }

                                    }else {
                                        productClasses.add(new ProductClass(
                                                product_obj.getString("id"),
                                                product_obj.getString("brand"),
                                                product_obj.getString("product_name"),
                                                product_obj.getString("description"),
                                                product_obj.getString("image_product"),
                                                product_obj.getString("manufacturer"),
                                                product_obj.getString("category"),
                                                product_obj.getString("category_id"),
                                                product_obj.getString("service"),
                                                product_obj.getString("availability"),
                                                product_obj.getString("price"),
                                                product_obj.getString("status"),
                                                "0"
                                        ));
                                    }

                                    adapter = new ProductAdapter(getContext(), productClasses, new ProductAdapter.onClickJobByMonth() {
                                        @Override
                                        public void onClick(final ProductClass jobByMonthClass) {
                                            Log.d("click","clic");
                                            String[] pictureDialogItems = new String[0];
                                            if(jobByMonthClass.getStatus().equals("0")){
                                                String[] pictureDialogItemss = {
                                                        "Edit Product",
                                                        "Delete Product","Submit To Bplaz"};
                                                pictureDialogItems = pictureDialogItemss;
                                            }else {
                                                String[] pictureDialogItemss = {
                                                        "Edit Product",
                                                        "Delete Product"};
                                                pictureDialogItems = pictureDialogItemss;
                                            }
                                            androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                                            pictureDialog.setTitle("Select Action");
                                            pictureDialog.setItems(pictureDialogItems,
                                                    new android.content.DialogInterface.OnClickListener() {
                                                        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                                                        @Override
                                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case 0:
                                                                    Intent next = new Intent(getContext(), EditProductDetails.class);
                                                                    next.putExtra("id",jobByMonthClass.getId());
                                                                    next.putExtra("availability",jobByMonthClass.getAvailability());
                                                                    next.putExtra("category_id",jobByMonthClass.getCategory_id());
                                                                    next.putExtra("service",jobByMonthClass.getService());
                                                                    next.putExtra("brand",jobByMonthClass.getBrand());
                                                                    next.putExtra("product_name",jobByMonthClass.getProduct_name());
                                                                    next.putExtra("price",jobByMonthClass.getRsp_price());
                                                                    next.putExtra("image",jobByMonthClass.getImage_product());
                                                                    startActivity(next);
                                                                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                    break;
                                                                case 1:
                                                                    dialogs.show();
                                                                    deleteProduct(jobByMonthClass.getId()+".json");
                                                                    break;

                                                                case 2:
                                                                    new AlertDialog.Builder(getActivity())
                                                                            .setCancelable(true)
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setMessage("Are you sure want to submit product?")
                                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    submitBplaz(jobByMonthClass.getId()+".json");
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

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Toast.makeText(getContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    private void deleteProduct(String id){
        final JSONObject jsonData = new JSONObject();

        final String requestBody = jsonData.toString();


        StringRequest stringRequest = new StringRequest(DELETE, BasedURL.ROOT_URL+"merchant_sales/product/"+id,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogs.dismiss();
                        Toast.makeText(getContext(),"Successfully update", Toast.LENGTH_SHORT).show();
                        getProductList();

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void submitBplaz(String id){
        StringRequest stringRequest = new StringRequest(PUT, BasedURL.ROOT_URL+"merchant_sales/submit-to-bplaz/"+id,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogs.dismiss();
                        Toast.makeText(getContext(),"Product successfully submit to Bplaz.", Toast.LENGTH_SHORT).show();
                        getProductList();
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
                headers.put("status","1");
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

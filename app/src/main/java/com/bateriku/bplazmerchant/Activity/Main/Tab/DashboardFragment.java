package com.bateriku.bplazmerchant.Activity.Main.Tab;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.Activity.Login.LoginActivity;
import com.bateriku.bplazmerchant.Activity.Sales.CompletedSales;
import com.bateriku.bplazmerchant.Activity.Sales.ToAccept;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.Request.Method.GET;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    TextView textView_company_name,textView_phone,textView_rating,textView_accept,textView_cancel,textView_to_accept,textView_completed;
    PreferenceManagerLogin session;
    String token;
    SwipeRefreshLayout swipe_refresh;
    CircleImageView profile_image;
    LinearLayout linear_to_accept,linear_complete,linear_invoices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        session = new PreferenceManagerLogin(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        swipe_refresh = v.findViewById(R.id.swipe_refresh);
        profile_image = v.findViewById(R.id.profile_image);
        textView_company_name = v.findViewById(R.id.textView_company_name);
        textView_phone = v.findViewById(R.id.textView_phone);
        textView_rating = v.findViewById(R.id.textView_rating);
        textView_accept = v.findViewById(R.id.textView_accept);
        textView_cancel = v.findViewById(R.id.textView_cancel);
        textView_to_accept = v.findViewById(R.id.textView_to_accept);
        textView_completed = v.findViewById(R.id.textView_completed);
        linear_to_accept = v.findViewById(R.id.linear_to_accept);
        linear_complete = v.findViewById(R.id.linear_complete);
        linear_invoices = v.findViewById(R.id.linear_invoices);


        apiGetSalesDetails();
        apiGetProfile();

        //swipe to refresh
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                apiGetSalesDetails();
                apiGetProfile();
                swipe_refresh.setRefreshing(false);
            }
        });

        linear_to_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getActivity(), ToAccept.class);
                startActivity(next);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        linear_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getActivity(), CompletedSales.class);
                startActivity(next);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        linear_invoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Coming soon",Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void apiGetSalesDetails(){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/dashboard.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject sales = new JSONObject(jsonObject.getString("sales"));
                            textView_accept.setText(sales.getString("acceptance"));
                            textView_cancel.setText(sales.getString("cancel"));
                            textView_rating.setText(sales.getString("rating"));
                            textView_to_accept.setText(sales.getString("to_accept"));
                            textView_completed.setText(sales.getString("complete"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void apiGetProfile(){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/profile.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject partner = new JSONObject(jsonObject.getString("partner"));
                            textView_company_name.setText(partner.getString("company_name"));
                            textView_phone.setText(partner.getString("telephone_number"));
                            Picasso.get().load(BasedURL.ROOT_URL_IMAGE + partner.getString("image_logo")).memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .into(profile_image);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Toast.makeText(getContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

            Intent next = new Intent(getContext(), LoginActivity.class);
            startActivity(next);
            getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}

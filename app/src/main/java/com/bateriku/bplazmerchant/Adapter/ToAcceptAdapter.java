package com.bateriku.bplazmerchant.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.Class.ToAcceptClass;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class ToAcceptAdapter extends RecyclerView.Adapter<ToAcceptAdapter.ProductViewHolder> implements Filterable {

    private List<ToAcceptClass> mData;
    private Context mCtx;
    public static List<ToAcceptClass> jobByMonthList;
    private onClickJobByMonth mListener;
    Activity activity;
    private List<ToAcceptClass> mDataListFiltered;
    public String token;
    PreferenceManagerLogin session;

    public ToAcceptAdapter(Context mCtx, List<ToAcceptClass> jobByMonthList, onClickJobByMonth mListener, Activity activity) {
        this.mCtx = mCtx;
        this.mData = jobByMonthList;
        this.jobByMonthList = jobByMonthList;
        this.mListener = mListener;
        this.activity = activity;


        session = new PreferenceManagerLogin(mCtx.getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.adapter_to_accept, null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ProductViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final ToAcceptClass jobByMonthClass = jobByMonthList.get(position);

        holder.textView_date.setText(convertDate(jobByMonthClass.getJob_date()));
        holder.textView_job_id.setText("JOB ID : #"+jobByMonthClass.getId());
        holder.textView_address.setText(jobByMonthClass.getGeo_location());
        getProduct(holder.textView_product,jobByMonthClass.getCategory_id());
        holder.textView_price.setText("RM "+jobByMonthClass.getPrice());
        getStatus(holder.textView_status,jobByMonthClass.getStatus());

        if(jobByMonthClass.getSale_id().equals(null) ||jobByMonthClass.getSale_id().equals("null")){
            holder.imageView_bplaz_order.setVisibility(View.GONE);
        }else{
            holder.imageView_bplaz_order.setVisibility(View.VISIBLE);
        }

        if(jobByMonthClass.getTenant_id().equals("2")){
            holder.imageView_etiqa.setVisibility(View.VISIBLE);
        }else{
            holder.imageView_etiqa.setVisibility(View.GONE);
        }

        holder.textView_cust_nams.setText(jobByMonthClass.getCustomer_name());
    }

    private void getProduct(final TextView textView_product , final String category_id){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"get/productCategory.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("categories"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);

                                if(obj.getString("id").equals(category_id)){
                                    textView_product.setText(obj.getString("name"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getStatus(final TextView textView_status, final String status) {
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales.json?status=1",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject sale_status = new JSONObject(obj.getString("sale_status"));
                            textView_status.setText(sale_status.getString(status));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return jobByMonthList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView textView_date,textView_job_id,textView_address,textView_product,textView_price,textView_status,textView_cust_nams;
        ImageView imageView_bplaz_order,imageView_etiqa;
        public ProductViewHolder(View itemView) {
            super(itemView);

            imageView_etiqa = itemView.findViewById(R.id.imageView_etiqa);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_job_id = itemView.findViewById(R.id.textView_job_id);
            textView_address = itemView.findViewById(R.id.textView_address);
            textView_product = itemView.findViewById(R.id.textView_product);
            textView_price = itemView.findViewById(R.id.textView_price);
            textView_status = itemView.findViewById(R.id.textView_status);
            imageView_bplaz_order = itemView.findViewById(R.id.imageView_bplaz_order);
            textView_cust_nams = itemView.findViewById(R.id.textView_cust_nams);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(jobByMonthList.get(getAdapterPosition()));
                }
            });

        }
    }

    private String convertDate(String dates){
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:a dd-MMM-yyyy");
        String date_json = "";
        try {
            Date parse = dateParser.parse(dates);
            date_json = dateFormatter.format(parse);
        } catch (ParseException e) {
            date_json = "N/A";
        }

        return date_json;
    }

    public interface onClickJobByMonth {
        void onClick(ToAcceptClass jobByMonthClass);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                List<ToAcceptClass> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList.addAll(mData);

                    mDataListFiltered = filteredList;
                } else {
                    for (ToAcceptClass row : mData) {
//                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
//                            filteredList.add(row);
//                        }
                    }

                    mDataListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                jobByMonthList = (ArrayList<ToAcceptClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
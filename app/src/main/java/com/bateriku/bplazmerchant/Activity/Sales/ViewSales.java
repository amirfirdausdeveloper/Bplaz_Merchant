package com.bateriku.bplazmerchant.Activity.Sales;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.common.StandardProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class ViewSales extends AppCompatActivity {

    StandardProgressDialog dialogs;
    PreferenceManagerLogin session;
    String token,id;

    ImageView imageView_back,imageView_rider,imageView_vehicle,imageView_product,imageView_address,imageView_customer;

    TextView textView_job_id,textView_status,textView_cust_name,textView_cust_phone,textView_cust_email,textView_geo_location,textView_geo_location_to,
            textView_delivery_date,textView_product,textView_price,textView_model,textView_plate,textView_memo,textView_rider_name,
            textView_rider_phone_no,textView_rider_status,textView_payment;

    LinearLayout linear_cust_hide_show,linear_address_hide_show,linear_payment_hide_show,linear_vehicle_hide_show,linear_rider_hide_show;

    String hide_cust = "1";
    String hide_add = "0";
    String hide_payment = "0";
    String hide_vech = "0";
    String hide_rider = "0";

    String latitude,longitude,latitude_to,longitude_to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sales);


        session = new PreferenceManagerLogin(getApplicationContext());
        dialogs = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        id = getIntent().getStringExtra("id");


        textView_job_id = findViewById(R.id.textView_job_id);
        textView_status = findViewById(R.id.textView_status);
        textView_cust_name = findViewById(R.id.textView_cust_name);
        textView_cust_phone = findViewById(R.id.textView_cust_phone);
        textView_cust_email = findViewById(R.id.textView_cust_email);
        textView_geo_location = findViewById(R.id.textView_geo_location);
        textView_geo_location_to = findViewById(R.id.textView_geo_location_to);
        textView_delivery_date = findViewById(R.id.textView_delivery_date);
        textView_product = findViewById(R.id.textView_product);
        textView_price = findViewById(R.id.textView_price);
        textView_model = findViewById(R.id.textView_model);
        textView_plate = findViewById(R.id.textView_plate);

        textView_memo = findViewById(R.id.textView_memo);
        textView_rider_name = findViewById(R.id.textView_rider_name);
        textView_rider_phone_no = findViewById(R.id.textView_rider_phone_no);
        textView_rider_status = findViewById(R.id.textView_rider_status);
        textView_payment = findViewById(R.id.textView_payment);

        linear_cust_hide_show = findViewById(R.id.linear_cust_hide_show);
        linear_address_hide_show = findViewById(R.id.linear_address_hide_show);
        linear_payment_hide_show = findViewById(R.id.linear_payment_hide_show);
        linear_vehicle_hide_show = findViewById(R.id.linear_vehicle_hide_show);
        linear_rider_hide_show = findViewById(R.id.linear_rider_hide_show);


        imageView_back = findViewById(R.id.imageView_back);
        imageView_rider = findViewById(R.id.imageView_rider);
        imageView_vehicle = findViewById(R.id.imageView_vehicle);
        imageView_product = findViewById(R.id.imageView_product);
        imageView_address = findViewById(R.id.imageView_address);
        imageView_customer = findViewById(R.id.imageView_customer);


        linear_address_hide_show.setVisibility(View.GONE);
        linear_payment_hide_show.setVisibility(View.GONE);
        linear_vehicle_hide_show.setVisibility(View.GONE);
        linear_rider_hide_show.setVisibility(View.GONE);


        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageView_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hide_cust.equals("1")){
                    hide_cust = "0";
                    Drawable customs = getDrawable(R.drawable.icon_expend_more);
                    imageView_customer.setBackground(customs);
                    linear_cust_hide_show.setVisibility(View.GONE);
                }else{
                    hide_cust = "1";
                    Drawable customs = getDrawable(R.drawable.ic_expand_less);
                    imageView_customer.setBackground(customs);
                    linear_cust_hide_show.setVisibility(View.VISIBLE);
                }
            }
        });

        imageView_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hide_add.equals("1")){
                    hide_add = "0";
                    Drawable customs = getDrawable(R.drawable.icon_expend_more);
                    imageView_address.setBackground(customs);
                    linear_address_hide_show.setVisibility(View.GONE);
                }else{
                    hide_add = "1";
                    Drawable customs = getDrawable(R.drawable.ic_expand_less);
                    imageView_address.setBackground(customs);
                    linear_address_hide_show.setVisibility(View.VISIBLE);
                }
            }
        });

        imageView_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hide_payment.equals("1")){
                    hide_payment = "0";
                    Drawable customs = getDrawable(R.drawable.icon_expend_more);
                    imageView_product.setBackground(customs);
                    linear_payment_hide_show.setVisibility(View.GONE);
                }else{
                    hide_payment = "1";
                    Drawable customs = getDrawable(R.drawable.ic_expand_less);
                    imageView_product.setBackground(customs);
                    linear_payment_hide_show.setVisibility(View.VISIBLE);
                }
            }
        });

        imageView_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hide_vech.equals("1")){
                    hide_vech = "0";
                    Drawable customs = getDrawable(R.drawable.icon_expend_more);
                    imageView_vehicle.setBackground(customs);
                    linear_vehicle_hide_show.setVisibility(View.GONE);
                }else{
                    hide_vech = "1";
                    Drawable customs = getDrawable(R.drawable.ic_expand_less);
                    imageView_vehicle.setBackground(customs);
                    linear_vehicle_hide_show.setVisibility(View.VISIBLE);
                }
            }
        });

        imageView_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hide_rider.equals("1")){
                    hide_rider = "0";
                    Drawable customs = getDrawable(R.drawable.icon_expend_more);
                    imageView_rider.setBackground(customs);
                    linear_rider_hide_show.setVisibility(View.GONE);
                }else{
                    hide_rider = "1";
                    Drawable customs = getDrawable(R.drawable.ic_expand_less);
                    imageView_rider.setBackground(customs);
                    linear_rider_hide_show.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        getDetails();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ViewSales.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getDetails(){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/"+id,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject sales_obj = new JSONObject(obj.getString("sale"));

                            textView_job_id.setText("#"+sales_obj.getString("id"));
                            getStatus(textView_status,sales_obj.getString("status"));

                            //CUSTOMER INFO
                            JSONObject customer_obj = new JSONObject(sales_obj.getString("customer"));
                            textView_cust_name.setText(customer_obj.getString("name"));
                            textView_cust_phone.setText(customer_obj.getString("telephone_number"));
                            textView_cust_email.setText(customer_obj.getString("email"));
                            textView_cust_phone.setPaintFlags(textView_geo_location.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            textView_cust_phone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent call = new Intent(Intent.ACTION_DIAL);
                                    call.setData(Uri.parse("tel:" +textView_cust_phone.getText().toString()));
                                    startActivity(call);
                                }
                            });

                            //ADDRESS INFO
                            textView_geo_location.setText(sales_obj.getString("geo_location"));
                            textView_geo_location.setPaintFlags(textView_geo_location.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            textView_geo_location.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    navigation(latitude,longitude);
                                }
                            });

                            if(sales_obj.getString("geo_location_to").equals("null") ||sales_obj.getString("geo_location_to").equals(null) ||
                                    sales_obj.getString("geo_location_to").equals("")){
                                textView_geo_location_to.setText("N/A");
                            }else{
                                textView_geo_location_to.setText(sales_obj.getString("geo_location_to"));
                                textView_geo_location_to.setPaintFlags(textView_geo_location_to.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                textView_geo_location_to.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        navigation(latitude_to,longitude_to);
                                    }
                                });
                            }

                            textView_delivery_date.setText(convertDate(sales_obj.getString("job_date")));
                            latitude = sales_obj.getString("latitude");
                            longitude = sales_obj.getString("longitude");
                            latitude_to = sales_obj.getString("latitude_to");
                            longitude_to = sales_obj.getString("longitude_to");

                            //PAYMENT PRODUCT INFO
                            JSONArray sale_partner_items_arr = new JSONArray(sales_obj.getString("sale_partner_items"));
                            for (int i =0; i < sale_partner_items_arr.length(); i++){
                                JSONObject sale_partner_items_obj = sale_partner_items_arr.getJSONObject(i);
                                JSONObject product_partner_obj = new JSONObject(sale_partner_items_obj.getString("product_partner"));
                                textView_product.setText(product_partner_obj.getString("product_name"));
                            }
                            textView_price.setText(sales_obj.getString("total_amount"));

                            //VEHICLE INFO
                            JSONObject vehicle_obj = new JSONObject(sales_obj.getString("vehicle"));
                            textView_model.setText(vehicle_obj.getString("name"));
                            textView_plate.setText(sales_obj.getString("plate_number"));

                            if(sales_obj.getString("memo").equals("null") ||sales_obj.getString("memo").equals(null) ||
                                    sales_obj.getString("memo").equals("")){
                                textView_memo.setText("N/A");
                            }else{
                                textView_memo.setText(sales_obj.getString("memo"));
                            }



                            if(sales_obj.getString("payment_status").equals("0")){
                                textView_payment.setText("UNPAID");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView_payment.setTextColor(getColor(R.color.colorRed));
                                }
                            }else {
                                textView_payment.setText("PAID");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView_payment.setTextColor(getColor(R.color.colorGreen2));
                                }
                            }


                            //RIDER INFO
                            if (sales_obj.getString("rider_job").equals("null") || sales_obj.getString("rider_job").equals(null) || sales_obj.getString("rider_job").equals("")){
                                textView_rider_name.setText("No rider assign yet");
                                textView_rider_phone_no.setText("No rider assign yet");
                                textView_rider_status.setText("No rider assign yet");
                            }else{
                                JSONObject rider_job_obj = new JSONObject(sales_obj.getString("rider_job"));
                                JSONObject rider_obj = new JSONObject(rider_job_obj.getString("rider"));
                                textView_rider_name.setText(rider_obj.getString("name"));
                                textView_rider_phone_no.setText(rider_obj.getString("telephone_number"));
                                textView_rider_status.setText(rider_obj.getString("email"));
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

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Toast.makeText(getApplicationContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void navigation(String latitude,String logitude){
        final String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + logitude + " (" + "PickUp Point" + ")";


        new AlertDialog.Builder(ViewSales.this)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Choose your navigation")
                .setPositiveButton("google maps", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PackageManager pm = getPackageManager();
                        boolean isInstalled = isPackageInstalled("com.google.android.apps.maps", pm);
                        if(isInstalled){
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.google.android.apps.maps"));
                            startActivity(intent);
                        }

                    }
                })
                .setNegativeButton("waze", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PackageManager pm = getPackageManager();
                        boolean isInstalled = isPackageInstalled("com.waze", pm);
                        if(isInstalled){
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage("com.waze");
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.waze"));
                            startActivity(intent);
                        }
                    }
                })
                .show();
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}

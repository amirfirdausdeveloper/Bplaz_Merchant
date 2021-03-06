package com.bateriku.bplazmerchant.Activity.Sales;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.common.StandardProgressDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

public class CreateSales extends AppCompatActivity {

    ImageView imageView_back;
    TextInputEditText et_cust_name,et_cust_phone,et_cust_email,et_plate_number,et_address_note,
            et_price_product,et_discount,et_total_all_price,et_delivery_date;
    public static TextInputEditText et_address_too,et_address,et_distance,et_discount_rm;
    SearchableSpinner spinner_product,spinner_vehicle_model,spinner_manufacture;
    Button button_create;
    RelativeLayout relative_address,relative_address_too,relative_delivery_date;

    PreferenceManagerLogin session;
    StandardProgressDialog dialog;
    String token;
    private ArrayList<String> manufacturer_list;
    private ArrayList<String> vehicle_list;
    private ArrayList<String> product_list;
    String manufacturer_id,vehicle_id,product_id;
    LinearLayout linear_towing;
    public static String latitude ="0",longitude="0",latitude_to="0",longitude_to="0";
    String status_towing = "0";
    String date_all;
    String come_from_where ="123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sales);

        session = new PreferenceManagerLogin(getApplicationContext());
        dialog = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);


        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        et_cust_name = findViewById(R.id.et_cust_name);
        et_cust_phone = findViewById(R.id.et_cust_phone);
        et_cust_email = findViewById(R.id.et_cust_email);
        et_discount_rm = findViewById(R.id.et_discount_rm);
        et_plate_number = findViewById(R.id.et_plate_number);
        et_address = findViewById(R.id.et_address);
        et_address_note = findViewById(R.id.et_address_note);
        et_price_product = findViewById(R.id.et_price_product);
        et_discount = findViewById(R.id.et_discount);
        et_total_all_price = findViewById(R.id.et_total_all_price);
        spinner_product = findViewById(R.id.spinner_product);
        spinner_vehicle_model = findViewById(R.id.spinner_vehicle_model);
        spinner_manufacture = findViewById(R.id.spinner_manufacture);
        button_create = findViewById(R.id.button_create);
        relative_address = findViewById(R.id.relative_address);
        et_address_too = findViewById(R.id.et_address_too);
        relative_address_too = findViewById(R.id.relative_address_too);
        relative_delivery_date = findViewById(R.id.relative_delivery_date);
        et_delivery_date = findViewById(R.id.et_delivery_date);
        linear_towing = findViewById(R.id.linear_towing);
        et_distance = findViewById(R.id.et_distance);

        relative_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),MapsSearchActivityPickup.class);
                startActivity(next);
                CreateSales.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        relative_delivery_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(CreateSales.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int years, int monthOfYears, int dayOfMonths) {

                                // Get Current Time
                                final Calendar c = Calendar.getInstance();
                                int mHour = c.get(Calendar.HOUR_OF_DAY);
                                int mMinute = c.get(Calendar.MINUTE);

                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateSales.this,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {

                                                et_delivery_date.setText(years + "-" + (monthOfYears + 1) + "-" + dayOfMonths+" "+hourOfDay + ":" + minute);
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        relative_address_too.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_address.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please insert pickup address first",Toast.LENGTH_SHORT).show();
                }else{
                    Intent next = new Intent(getApplicationContext(),MapsSearchActivityDelivery.class);
                    startActivity(next);
                    CreateSales.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        getManuFacturer();



        TextWatcher yourTextWatcherRM = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // your logic here
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // your logic here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(et_discount_rm.getText().toString().equals("0") || et_discount_rm.getText().toString().equals("")){
                    et_total_all_price.setText(et_price_product.getText().toString());
                }else{
                    if(et_price_product.getText().toString().equals("0") || et_price_product.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please enter price for product",Toast.LENGTH_SHORT).show();
                    }else{
                        double price_total = Double.parseDouble(et_price_product.getText().toString()) - (Double.parseDouble(et_discount_rm.getText().toString()));
                        NumberFormat nf = new DecimalFormat("##.##");
                        et_total_all_price.setText(nf.format(price_total));
                        double getPercentDiscount = (Double.parseDouble(et_price_product.getText().toString()) - price_total) / Double.parseDouble(et_price_product.getText().toString());
                        double total_all = getPercentDiscount * 100 ;
                        NumberFormat nfs = new DecimalFormat("##.##");
                        et_discount.setText(nfs.format(total_all));
                    }

                }
            }
        };

        TextWatcher yourTextWatcherPercent = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // your logic here
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // your logic here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(et_discount.getText().toString().equals("0") || et_discount.getText().toString().equals("")){
                    et_total_all_price.setText(et_price_product.getText().toString());
                }else{
                    if(et_price_product.getText().toString().equals("0") || et_price_product.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please enter price for product",Toast.LENGTH_SHORT).show();
                    }else{
                        double discount_int = Double.parseDouble(et_discount.getText().toString());
                        double total_discount = discount_int / 100;
                        double price_total = Double.parseDouble(et_price_product.getText().toString()) - (Double.parseDouble(et_price_product.getText().toString()) * total_discount);

                        NumberFormat nf = new DecimalFormat("##.##");
                        et_total_all_price.setText(nf.format(price_total));
                        double getPercentDiscount = (Double.parseDouble(et_price_product.getText().toString()) * total_discount);
                        NumberFormat nfs = new DecimalFormat("##.##");
                        et_discount_rm.setText(nfs.format(getPercentDiscount));
                    }



                }
            }
        };

        et_price_product.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                et_discount_rm.setText("0");
                et_discount.setText("0");
                et_discount_rm.removeTextChangedListener(yourTextWatcherRM);
                et_discount.removeTextChangedListener(yourTextWatcherPercent);
                et_total_all_price.setText(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        et_discount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_discount_rm.removeTextChangedListener(yourTextWatcherRM);
                    et_discount.addTextChangedListener(yourTextWatcherPercent);
                }
            }
        });

        et_discount_rm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_discount_rm.addTextChangedListener(yourTextWatcherRM);
                    et_discount.removeTextChangedListener(yourTextWatcherPercent);
                }
            }
        });
//        et_discount.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//
//
//            }
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(come_from_where.equals("123")){
//                    come_from_where = "discount percentage";
//                    if(et_discount.getText().toString().equals("0") || et_discount.getText().toString().equals("")){
//                        et_total_all_price.setText(et_price_product.getText().toString());
//                    }else{
//                        double discount_int = Double.parseDouble(et_discount.getText().toString());
//                        double total_discount = discount_int / 100;
//                        double price_total = Double.parseDouble(et_price_product.getText().toString()) - (Double.parseDouble(et_price_product.getText().toString()) * total_discount);
//
//                        NumberFormat nf = new DecimalFormat("##.##");
//                        et_total_all_price.setText(nf.format(price_total));
//                        double getPercentDiscount = (Double.parseDouble(et_price_product.getText().toString()) * total_discount);
//                        NumberFormat nfs = new DecimalFormat("##.##");
//                        et_discount_rm.setText(nfs.format(getPercentDiscount));
//
//                    }
//                }
//
//                if(come_from_where.equals("discount percentage")){
//                    if(et_discount.getText().toString().equals("0") || et_discount.getText().toString().equals("")){
//                        et_total_all_price.setText(et_price_product.getText().toString());
//                    }else{
//                        double discount_int = Double.parseDouble(et_discount.getText().toString());
//                        double total_discount = discount_int / 100;
//                        double price_total = Double.parseDouble(et_price_product.getText().toString()) - (Double.parseDouble(et_price_product.getText().toString()) * total_discount);
//
//                        NumberFormat nf = new DecimalFormat("##.##");
//                        et_total_all_price.setText(nf.format(price_total));
//                        double getPercentDiscount = (Double.parseDouble(et_price_product.getText().toString()) * total_discount);
//                        NumberFormat nfs = new DecimalFormat("##.##");
//                        et_discount_rm.setText(nfs.format(getPercentDiscount));
//                    }
//                }
//            }
//        });
//
//        et_discount_rm.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//
//
//            }
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(come_from_where.equals("123")){
//                    Log.d("MSUK","MSUK");
//                    come_from_where = "discount RM";
//
//                    if(et_discount_rm.getText().toString().equals("0") || et_discount_rm.getText().toString().equals("")){
//                        et_total_all_price.setText(et_price_product.getText().toString());
//                    }else{
//                        double price_total = Double.parseDouble(et_price_product.getText().toString()) - (Double.parseDouble(et_discount_rm.getText().toString()));
//                        NumberFormat nf = new DecimalFormat("##.##");
//                        et_total_all_price.setText(nf.format(price_total));
//                        double getPercentDiscount = (Double.parseDouble(et_price_product.getText().toString()) - price_total) / Double.parseDouble(et_price_product.getText().toString());
//                        double total_all = getPercentDiscount * 100 ;
//                        NumberFormat nfs = new DecimalFormat("##.##");
//                        et_discount.setText(nfs.format(total_all));
//                    }
//                }
//
//                if(come_from_where.equals("discount RM")){
//                    if(et_discount_rm.getText().toString().equals("0") || et_discount_rm.getText().toString().equals("")){
//                        et_total_all_price.setText(et_price_product.getText().toString());
//                    }else{
//                        double price_total = Double.parseDouble(et_price_product.getText().toString()) - (Double.parseDouble(et_discount_rm.getText().toString()));
//                        NumberFormat nf = new DecimalFormat("##.##");
//                        et_total_all_price.setText(nf.format(price_total));
//                        double getPercentDiscount = (Double.parseDouble(et_price_product.getText().toString()) - price_total) / Double.parseDouble(et_price_product.getText().toString());
//                        double total_all = getPercentDiscount * 100 ;
//                        NumberFormat nfs = new DecimalFormat("##.##");
//                        et_discount.setText(nfs.format(total_all));
//                    }
//                }
//            }
//        });



        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_cust_name.getText().toString().equals("")){
                    et_cust_name.setError("Empty");
                    et_cust_name.requestFocus();
                }else if (et_cust_phone.getText().toString().equals("")){
                    et_cust_phone.setError("Empty");
                    et_cust_phone.requestFocus();
                }else if (spinner_manufacture.getSelectedItem().toString().equals("Choose manufacturer")){
                    Toast.makeText(getApplicationContext(),"Choose manufacturer",Toast.LENGTH_SHORT).show();
                    spinner_manufacture.requestFocus();
                }else if (spinner_vehicle_model.getSelectedItem().toString().equals("Choose vehicle") || spinner_vehicle_model.getSelectedItem().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Choose vehicle",Toast.LENGTH_SHORT).show();
                    spinner_vehicle_model.requestFocus();
                }else if (et_plate_number.getText().toString().equals("")){
                    et_plate_number.setError("Empty");
                    et_plate_number.requestFocus();
                }else if (et_delivery_date.getText().toString().equals("")){
                    et_delivery_date.setError("Empty");
                    et_delivery_date.requestFocus();
                }else if (et_address.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please insert address",Toast.LENGTH_SHORT).show();
                }else if (et_address_note.getText().toString().equals("")){
                    et_address_note.setError("Empty");
                    et_address_note.requestFocus();
                }else if (spinner_product.getSelectedItem().toString().equals("Choose product") || spinner_product.getSelectedItem().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Choose vehicle",Toast.LENGTH_SHORT).show();
                    spinner_product.requestFocus();
                }else{

                    if(status_towing.equals("0")){
                        dialog.show();
                        createSalesAPI();
                    }else{
                        if(et_address_too.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"Please insert location towing",Toast.LENGTH_SHORT).show();
                        }else{
                            dialog.show();
                            createSalesAPI();
                        }
                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CreateSales.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getManuFacturer(){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"get/manufacturer.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        manufacturer_list = new ArrayList<String>();
                        manufacturer_list.add("Choose manufacturer");
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("manufacturers"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);
                                manufacturer_list.add(obj.getString("name"));
                                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, manufacturer_list);
                                adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_manufacture.setAdapter(adp1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        spinner_manufacture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (!spinner_manufacture.getSelectedItem().toString().equals("Choose manufacturer")){
                                    getManuFacturerFromName(spinner_manufacture.getSelectedItem().toString());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
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

    private void getManuFacturerFromName(final String name){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"get/manufacturer.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("manufacturers"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);
                                if(obj.getString("name").equals(name)){
                                    manufacturer_id = obj.getString("id");
                                    getVehicleModel(manufacturer_id);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getVehicleModel(String manufacturer_id){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"get/vehicle/"+manufacturer_id+".json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        vehicle_list = new ArrayList<String>();
                        vehicle_list.add("Choose vehicle");
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("vehicle"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);
                                vehicle_list.add(obj.getString("name"));
                                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, vehicle_list);
                                adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_vehicle_model.setAdapter(adp1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        spinner_vehicle_model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (!spinner_vehicle_model.getSelectedItem().toString().equals("Choose vehicle")){
                                    getVehicleModelFromName(spinner_vehicle_model.getSelectedItem().toString());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
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

    private void getVehicleModelFromName(final String vehicle_name){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"get/vehicle/"+manufacturer_id+".json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("vehicle"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);
                                if(obj.getString("name").equals(vehicle_name)){
                                    vehicle_id = obj.getString("id");
                                    getProduct();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getProduct(){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/product.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        product_list = new ArrayList<String>();
                        product_list.add("Choose product");
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("products"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);
                                if(obj.getString("brand").equals(null) || obj.getString("brand").equals("null")){
                                    product_list.add(obj.getString("product_name"));
                                }else{
                                    product_list.add(obj.getString("product_name")+" - "+obj.getString("brand"));
                                }
                                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, product_list);
                                adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_product.setAdapter(adp1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        spinner_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (!spinner_product.getSelectedItem().toString().equals("Choose product")){
                                    getProductByName(spinner_product.getSelectedItem().toString());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


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

    private void getProductByName(final String product_name){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/product.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("products"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);

                                if(obj.getString("brand").equals(null) || obj.getString("brand").equals("null")){
                                    if(obj.getString("product_name").equals(product_name)){
                                        product_id = obj.getString("id");
                                        JSONObject pricing_partner_obj = new JSONObject(obj.getString("pricing_partner"));
                                        et_price_product.setText(pricing_partner_obj.getString("rsp_price"));
                                        et_discount.setText("0");
                                        et_total_all_price.setText(pricing_partner_obj.getString("rsp_price"));

                                        if(obj.getString("product_name").toLowerCase().contains("towing")){
                                            linear_towing.setVisibility(View.VISIBLE);
                                            status_towing = "1";
                                        }else{
                                            linear_towing.setVisibility(View.GONE);
                                            status_towing = "0";

                                        }
                                    }
                                }else{
                                    if((obj.getString("product_name")+" - "+obj.getString("brand")).equals(product_name)){
                                        product_id = obj.getString("id");
                                        JSONObject pricing_partner_obj = new JSONObject(obj.getString("pricing_partner"));
                                        et_price_product.setText(pricing_partner_obj.getString("rsp_price"));
                                        et_discount.setText("0");
                                        et_total_all_price.setText(pricing_partner_obj.getString("rsp_price"));

                                        if(obj.getString("product_name").toLowerCase().contains("towing")){
                                            linear_towing.setVisibility(View.VISIBLE);
                                            status_towing = "1";
                                        }else{
                                            linear_towing.setVisibility(View.GONE);
                                            status_towing = "0";

                                        }
                                    }
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void createSalesAPI(){
        date_all = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        final JSONObject jsonData = new JSONObject();
        try {

            JSONObject customer = new JSONObject();
            customer.put("customer_name",et_cust_name.getText().toString());
            customer.put("telephone_number",et_cust_phone.getText().toString());
            if(!et_cust_email.getText().toString().equals("")){
                customer.put("email",et_cust_email.getText().toString());
            }

            jsonData.put("Customer_",customer);


            JSONArray sales = new JSONArray();
            JSONObject product = new JSONObject();
            product.put("product_partner_id",product_id);
            product.put("quantity","1");
            product.put("price_per_unit",et_price_product.getText().toString());
            product.put("discount_flag","fixed");
            product.put("discount",et_discount_rm.getText().toString());
            sales.put(product);
            jsonData.put("SalesProducts_",sales);

            if(status_towing.equals("0")){
                jsonData.put("geo_location",et_address.getText().toString());
                jsonData.put("coordinate","("+latitude+","+longitude+")");
            }else{
                jsonData.put("geo_location",et_address.getText().toString());
                jsonData.put("coordinate","("+latitude+","+longitude+")");
                jsonData.put("geo_location_to",et_address_too.getText().toString());
                jsonData.put("coordinate_to","("+latitude_to+","+longitude_to+")");
                jsonData.put("SalesProducts_[0][distance_value]",et_distance.getText().toString());
            }
            jsonData.put("vehicle_id",vehicle_id);
            jsonData.put("plate_number",et_plate_number.getText().toString());
            jsonData.put("address",et_address.getText().toString());
            jsonData.put("memo",et_address_note.getText().toString());
            jsonData.put("trade","1");
            jsonData.put("discount",et_discount_rm.getText().toString());
            jsonData.put("discount_flag","fixed");
            jsonData.put("total_amount",et_total_all_price.getText().toString());
            jsonData.put("subtotal",et_price_product.getText().toString());


            jsonData.put("invoice_date",date_all);
            jsonData.put("job_date",et_delivery_date.getText().toString());

            Log.d("JSON",jsonData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonData.toString();


        StringRequest stringRequest = new StringRequest(POST, BasedURL.ROOT_URL+"merchant_sales/add.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.d("response",response);
                        Toast.makeText(getApplicationContext(),"The sale has been created",Toast.LENGTH_SHORT).show();
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

package com.bateriku.bplazmerchant.Activity.Staff;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.android.material.textfield.TextInputEditText;
import com.novoda.merlin.Merlin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class EditStafff extends AppCompatActivity {

    ImageView imageView_back;
    TextInputEditText et_name,et_email,et_phone_number,et_password,et_dob;
    public static TextInputEditText et_address;
    RelativeLayout linear_editText,relative_dob;
    Button button_edit;

    Merlin merlin;
    PreferenceManagerLogin session;
    StandardProgressDialog dialog;
    String age,token;
    public static String latitude,longitude;

    String intent_id,intent_name,intent_email,intent_phone,intent_password,intent_dob,intent_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stafff);

        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().build(getApplicationContext());
        session = new PreferenceManagerLogin(getApplicationContext());
        dialog = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        intent_id = getIntent().getStringExtra("intent_id");
        intent_name = getIntent().getStringExtra("intent_name");
        intent_email = getIntent().getStringExtra("intent_email");
        intent_phone = getIntent().getStringExtra("intent_phone");
        intent_password = getIntent().getStringExtra("intent_password");
        intent_dob = getIntent().getStringExtra("intent_dob");
        intent_location = getIntent().getStringExtra("intent_location");

        imageView_back = findViewById(R.id.imageView_back);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_password = findViewById(R.id.et_password);
        et_dob = findViewById(R.id.et_dob);
        et_address = findViewById(R.id.et_address);

        et_name.setText(intent_name);
        et_email.setText(intent_email);
        et_phone_number.setText(intent_phone);
        et_password.setText(intent_password);

        button_edit = findViewById(R.id.button_edit);


        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter name",Toast.LENGTH_SHORT).show();
                }else if(et_email.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter email",Toast.LENGTH_SHORT).show();
                }else if(et_phone_number.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter phone number",Toast.LENGTH_SHORT).show();
                }else {
                    dialog.show();
                    add();
                }
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
        EditStafff.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



    private void add(){
        StringRequest stringRequest = new StringRequest(POST, BasedURL.ROOT_URL+"merchant_sales/edit_staff.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        try {
                            JSONObject object = new JSONObject(response);

                            if(object.getString("status").equals("Error")){
                                Toast.makeText(getApplicationContext(),"Cannot update user, phone number or email already exist",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"Staff Updated",Toast.LENGTH_SHORT).show();
                                onBackPressed();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();


                headers.put("user_id",intent_id);
                headers.put("name",et_name.getText().toString());
                headers.put("email",et_email.getText().toString());
                headers.put("password",et_password.getText().toString());
                headers.put("telephone_number",et_phone_number.getText().toString());

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

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Toast.makeText(getApplicationContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

}

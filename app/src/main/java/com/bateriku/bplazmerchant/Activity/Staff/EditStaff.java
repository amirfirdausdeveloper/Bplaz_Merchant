package com.bateriku.bplazmerchant.Activity.Staff;

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

import androidx.appcompat.app.AppCompatActivity;

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

public class EditStaff extends AppCompatActivity {

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
        setContentView(R.layout.activity_add_staff);

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
        et_dob.setText(convertDate(intent_dob));
        et_address.setText(intent_location);

        linear_editText = findViewById(R.id.linear_editText);
        relative_dob = findViewById(R.id.relative_dob);
        button_edit = findViewById(R.id.button_edit);


        relative_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(EditStaff.this, datePickerListener, mYear, mMonth, mDay);
                dateDialog.getDatePicker().setMaxDate(new Date().getTime());
                dateDialog.show();
            }
        });

        linear_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(), MapsSearchEditStaff.class);
                startActivity(next);
                EditStaff.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter name",Toast.LENGTH_SHORT).show();
                }else if(et_email.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter email",Toast.LENGTH_SHORT).show();
                }else if(et_phone_number.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter phone number",Toast.LENGTH_SHORT).show();
                }else if(et_dob.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter date of birth",Toast.LENGTH_SHORT).show();
                }else if(et_address.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter address",Toast.LENGTH_SHORT).show();
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
        EditStaff.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String format = new SimpleDateFormat("dd MMM YYYY").format(c.getTime());
            et_dob.setText(format);
        }
    };

    int calculateAge(long date){
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        return age;
    }

    private void add(){
        StringRequest stringRequest = new StringRequest(POST, BasedURL.ROOT_URL+"merchant_sales/edit_rider.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.d("update",response);
                        Toast.makeText(getApplicationContext(),"Rider Updated",Toast.LENGTH_SHORT).show();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();


                headers.put("id",intent_id);
                headers.put("name",et_name.getText().toString());
                headers.put("email",et_email.getText().toString());
                headers.put("password",et_password.getText().toString());
                headers.put("telephone_number",et_phone_number.getText().toString());
                headers.put("date_of_birth",et_dob.getText().toString());
                headers.put("language","1");
                headers.put("license","1");
                headers.put("age","99");
                headers.put("coordinate","("+latitude+","+longitude+")");
                headers.put("current_location",et_address.getText().toString());

                Log.d("HEADERS",headers.toString());

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
            if (error.networkResponse != null) {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                Toast.makeText(getApplicationContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    private String convertDate(String dates){
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
        String date_json = "";
        try {
            Date parse = dateParser.parse(dates);
            date_json = dateFormatter.format(parse);
        } catch (ParseException e) {
            date_json = "N/A";
        }

        return date_json;
    }
}

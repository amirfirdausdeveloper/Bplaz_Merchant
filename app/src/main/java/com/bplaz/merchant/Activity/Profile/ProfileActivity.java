package com.bplaz.merchant.Activity.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bplaz.merchant.Activity.Login.LoginActivity;
import com.bplaz.merchant.Connection.BasedURL;
import com.bplaz.merchant.PreferanceManager.PreferenceManagerLogin;
import com.bplaz.merchant.R;
import com.bplaz.merchant.common.StandardProgressDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.Request.Method.GET;

public class ProfileActivity extends AppCompatActivity {

    ImageView imageView_back;
    CircleImageView profile_image;
    TextInputEditText et_company_name,et_bank_acc_no,et_email,et_phone_no,et_address;
    Merlin merlin;
    PreferenceManagerLogin session;
    StandardProgressDialog dialog;
    LinearLayout linear_no_internet;
    String token;
    Button button_edit;
    SearchableSpinner spinner_bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().build(getApplicationContext());
        session = new PreferenceManagerLogin(getApplicationContext());
        dialog = new StandardProgressDialog(this.getWindow().getContext());


        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        imageView_back = findViewById(R.id.imageView_back);
        profile_image = findViewById(R.id.profile_image);
        et_company_name = findViewById(R.id.et_company_name);
        et_bank_acc_no = findViewById(R.id.et_bank_acc_no);
        et_email = findViewById(R.id.et_email);
        et_phone_no = findViewById(R.id.et_phone_no);
        et_address = findViewById(R.id.et_address);
        linear_no_internet = findViewById(R.id.linear_no_internet);
        button_edit = findViewById(R.id.button_edit);
        spinner_bank = findViewById(R.id.spinner_bank);

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
        ProfileActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();

        apiGetProfile();

        if(!isOnline()){
            noInternetConnectionDialog();
        }

        //IF INTERNET CONNECTION OK
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                linear_no_internet.setVisibility(View.GONE);
                button_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent next = new Intent(getApplicationContext(),EditProfile.class);
                        startActivity(next);
                        ProfileActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }
        });

        //IF INTERNET CONNECTION NOT OK
        merlin.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noInternetConnectionDialog();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        merlin.unbind();
        super.onPause();
    }

    @Override
    protected void onStop() {
        merlin.unbind();
        super.onStop();
    }

    private void noInternetConnectionDialog(){
        linear_no_internet.setVisibility(View.VISIBLE);
        button_edit.setOnClickListener(null);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void apiGetProfile(){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/profile.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject partner = new JSONObject(jsonObject.getString("partner"));
                            et_company_name.setText(partner.getString("company_name"));
                            et_phone_no.setText(partner.getString("telephone_number"));
                            Picasso.get().load(BasedURL.ROOT_URL_IMAGE + partner.getString("image_logo")).memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .into(profile_image);
                            et_address.setText(partner.getString("address"));
//                            textView_status.setText(partner.getString("active_status_description"));
                            et_email.setText(partner.getString("email"));
                            et_bank_acc_no.setText(partner.getString("bank_account_no"));
                            spinner_bank.setSelection(getIndex(spinner_bank, partner.getString("bank")));
//                            textView_bank_name.setText(partner.getString("bank"));

//                            if(partner.getString("self_assign").equals("0")){
//                                textView_self_assign.setText("No");
//                            }else if(partner.getString("self_assign").equals("1")){
//                                textView_self_assign.setText("Yes");
//                            }
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

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Toast.makeText(getApplicationContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

}

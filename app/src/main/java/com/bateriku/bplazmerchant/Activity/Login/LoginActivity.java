package com.bateriku.bplazmerchant.Activity.Login;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.R;
import com.bateriku.bplazmerchant.Activity.Main.MainActivity;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
import com.bateriku.bplazmerchant.common.StandardProgressDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class LoginActivity extends AppCompatActivity {

    LinearLayout linear_no_internet;
    EditText editText_email,editText_password;
    Button button_login;
    Merlin merlin;
    TextView textView_register;
    private static long back_pressed;
    PreferenceManagerLogin session;
    StandardProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE},1);

        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().build(getApplicationContext());
        session = new PreferenceManagerLogin(getApplicationContext());
        dialog = new StandardProgressDialog(this.getWindow().getContext());

        textView_register = findViewById(R.id.textView_register);
        linear_no_internet = findViewById(R.id.linear_no_internet);
        linear_no_internet.setVisibility(View.GONE);
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        button_login = findViewById(R.id.button_login);

//        editText_email.setText("bateriku@bateriku.com");
//        editText_password.setText("123");

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_email.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please insert email",Toast.LENGTH_SHORT).show();
                }else if(editText_password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please insert password",Toast.LENGTH_SHORT).show();
                }else {
                    dialog.show();
                    apiLogin();
                }
            }
        });

        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri webpage = Uri.parse("https://merchant.bplaz.com/register-partner");
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    //BACK FUNCTION
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())  moveTaskToBack(true);
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();

        if(!isOnline()){
            noInternetConnectionDialog();
            textView_register.setOnClickListener(null);
        }

        //IF INTERNET CONNECTION OK
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                linear_no_internet.setVisibility(View.GONE);
                clicklogin();
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
                        textView_register.setOnClickListener(null);
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
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void clicklogin(){

    }

    private void apiLogin(){
        String token =  FirebaseInstanceId.getInstance().getToken();
        Log.d("token",token);
        final JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("email",editText_email.getText().toString());
            jsonData.put("password",editText_password.getText().toString());
            jsonData.put("notification_key",token);
//            jsonData.put("notification_key","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                Request.Method.POST,
                BasedURL.ROOT_URL+"merchant_sales/token.json",
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject jsonObject) {
                        dialog.dismiss();
                        try {
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            apiGetProfile(data.getString("token"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                parseVolleyError(volleyError);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36");
                return super.getHeaders();
            }
        };

        jsonReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(jsonReq);
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

    private void apiGetProfile(String token){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/profile.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject partner = new JSONObject(jsonObject.getString("partner"));

                            session.createLoginSession(editText_email.getText().toString(),token,partner.getString("id"));
                            Intent next = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(next);
                            LoginActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
}

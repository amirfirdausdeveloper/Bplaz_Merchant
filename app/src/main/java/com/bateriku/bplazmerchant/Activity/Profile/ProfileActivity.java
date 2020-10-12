package com.bateriku.bplazmerchant.Activity.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

public class ProfileActivity extends AppCompatActivity {

    ImageView imageView_back;
    CircleImageView profile_image;
    TextInputEditText et_company_name,et_bank_acc_no,et_email,et_phone_no;
    public static AutoCompleteTextView et_address;
    PreferenceManagerLogin session;
    StandardProgressDialog dialog;
    String token;
    Button button_edit;
    SearchableSpinner spinner_bank,spinner_self_assign;
    RelativeLayout linear_editText;
    public static String latitude,longitude;
    private ArrayList<String> product_list;

    private int GALLERY_PROFILE = 1, CAMERA_PROFILE = 2;
    Bitmap selected_image = null;
    String bank_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ActivityCompat.requestPermissions(ProfileActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                1);

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
        button_edit = findViewById(R.id.button_edit);
        spinner_bank = findViewById(R.id.spinner_bank);
        spinner_self_assign = findViewById(R.id.spinner_self_assign);
        linear_editText = findViewById(R.id.linear_editText);

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        linear_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(), MapsSearchActivity.class);
                startActivity(next);
                ProfileActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        apiGetProfile();


        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                updateAPI();
            }
        });
    }

    private void showPictureDialog() {
        androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new android.content.DialogInterface.OnClickListener() {
                    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PROFILE) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    selected_image = rotateImageIfRequired(bitmap, this, contentURI);
                    profile_image.setImageBitmap(selected_image);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA_PROFILE) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            selected_image = Bitmap.createScaledBitmap(thumbnail, 920, 576, true);
            profile_image.setImageBitmap(selected_image);
        }
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {

        if (selectedImage.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_PROFILE);
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PROFILE);
    }

    private String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageBytes = baos.toByteArray();

        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return base64String;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ProfileActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getListofBanks(String bank, String bank_id){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/list_bank.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        product_list = new ArrayList<String>();
                        product_list.add("Choose Bank");
                        String bank_name_from = "";
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("bank_list"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);
                                product_list.add(obj.getString("name"));
                                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, product_list);
                                adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_bank.setAdapter(adp1);


                                if(obj.getString("id").equals(bank_id)){
                                    bank_name_from = obj.getString("name");
                                }
                            }

                            spinner_bank.setSelection(getIndex(spinner_bank,bank_name_from));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        spinner_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (!spinner_bank.getSelectedItem().toString().equals("Choose Bank")){
                                    getBankByName(spinner_bank.getSelectedItem().toString());
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

    private void getBankByName(String toString) {
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/list_bank.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("bank_list"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);


                                if(obj.getString("name").equals(toString)){
                                    bank_id = obj.getString("id");
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
                            et_email.setText(partner.getString("email"));
                            et_bank_acc_no.setText(partner.getString("bank_account_no"));

                            if(partner.getString("self_assign").equals("0")){
                                spinner_self_assign.setSelection(getIndex(spinner_self_assign, "No"));
                            }else if(partner.getString("self_assign").equals("1")){
                                spinner_self_assign.setSelection(getIndex(spinner_self_assign, "Yes"));
                            }

                            latitude = partner.getString("latitude");
                            longitude = partner.getString("longitude");

                            getListofBanks(partner.getString("bank"),partner.getString("bank_id"));
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
            if (error.networkResponse != null) {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                Toast.makeText(getApplicationContext(),data.getString("message"),Toast.LENGTH_SHORT).show();
            }

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

    private void updateAPI(){

        final JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("company_name",et_company_name.getText().toString());
            jsonData.put("company_name", et_company_name.getText().toString());
            jsonData.put("bank", spinner_bank.getSelectedItem().toString());
            if(!bank_id.equals("")){
                jsonData.put("bank_id", bank_id);
            }
            jsonData.put("address", et_address.getText().toString());
            jsonData.put("bank_account_no",et_bank_acc_no.getText().toString());
            if(spinner_self_assign.getSelectedItem().toString().equals("Yes")){
                jsonData.put("self_assign", "1");
            }else{
                jsonData.put("self_assign", "0");
            }
            jsonData.put("geo_location", et_address.getText().toString());
            jsonData.put("latitude", latitude);
            jsonData.put("longitude", longitude);

            if(selected_image!=null){
                jsonData.put("image_logo", "data:image/jpeg;base64,"+getBase64String(selected_image).trim());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonData.toString();


        StringRequest stringRequest = new StringRequest(POST, BasedURL.ROOT_URL+"merchant_sales/edit_profile.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Successfully update", Toast.LENGTH_SHORT).show();
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
}

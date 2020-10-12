package com.bateriku.bplazmerchant.Activity.CompletionReport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bateriku.bplazmerchant.Activity.Profile.ProfileActivity;
import com.bateriku.bplazmerchant.Activity.Sales.CreateSales;
import com.bateriku.bplazmerchant.Connection.BasedURL;
import com.bateriku.bplazmerchant.PreferanceManager.PreferenceManagerLogin;
import com.bateriku.bplazmerchant.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.bateriku.bplazmerchant.Connection.BasedURL.getContext;

public class AddCompletionReport extends AppCompatActivity {

    String sale_partner_id,token;
    PreferenceManagerLogin session;
    ImageView imageView_back;
    LinearLayout linear_other,linear_battery,linear_picture_2;
    Button button_add_photo;
    int i = 0;
    int which_click =0;
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_completion_report);

        session = new PreferenceManagerLogin(getContext());
        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        sale_partner_id = getIntent().getStringExtra("sale_partner_id");

        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(v -> onBackPressed());

        linear_other = findViewById(R.id.linear_other);
        linear_battery = findViewById(R.id.linear_battery);
        linear_picture_2 = findViewById(R.id.linear_picture_2);
        button_add_photo = findViewById(R.id.button_add_photo);

        button_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                final ImageView imageView = new ImageView(AddCompletionReport.this);
                imageView.setId(i);
                imageView.setImageResource(R.drawable.upload_photo);
                addView(imageView,0,0);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        which_click = imageView.getId();
                        imageView2 = imageView;

                        showPictureDialog(which_click);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AddCompletionReport.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void addView(ImageView imageView, int width, int height){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,900);
        layoutParams.setMargins(0,10,0,10);
        imageView.setLayoutParams(layoutParams);
        linear_picture_2.addView(imageView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSalesDetails();
    }

    private void getSalesDetails(){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"merchant_sales/"+sale_partner_id,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject sales = new JSONObject(obj.getString("sale"));
                            JSONArray sale_partner_items_arr = new JSONArray(sales.getString("sale_partner_items"));
                            for (int i =0; i < sale_partner_items_arr.length(); i++){
                                JSONObject sale_partner_items_obj = sale_partner_items_arr.getJSONObject(i);
                                JSONObject product_partner_obj = new JSONObject(sale_partner_items_obj.getString("product_partner"));

                                if(product_partner_obj.getString("category_id").equals("1")){
                                    linear_battery.setVisibility(View.VISIBLE);
                                    linear_other.setVisibility(View.GONE);
                                }else{
                                    linear_battery.setVisibility(View.GONE);
                                    linear_other.setVisibility(View.VISIBLE);
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

    private void showPictureDialog(int i) {
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
                                choosePhotoFromGallery(i);
                                break;
                            case 1:
                                takePhotoFromCamera(i);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallery(int i) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, i);
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    private void takePhotoFromCamera(int i) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri contentURI = data.getData();

            if(contentURI == null){  // untuk camera
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Bitmap selected_image = Bitmap.createScaledBitmap(thumbnail, 920, 576, true);
                imageView2.setImageBitmap(selected_image);
            }else{  // untuk gallery
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    imageView2.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

        }else{

        }
    }
}

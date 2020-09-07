package com.bplaz.merchant.Activity.Product;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bplaz.merchant.Class.MultipartUtility;
import com.bplaz.merchant.Class.VolleyMultipartRequest;
import com.bplaz.merchant.Connection.BasedURL;
import com.bplaz.merchant.PreferanceManager.PreferenceManagerLogin;
import com.bplaz.merchant.R;
import com.bplaz.merchant.common.StandardProgressDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.novoda.merlin.Merlin;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.android.volley.Request.Method.PUT;

public class AddProduct extends AppCompatActivity {

    CheckBox checkBox,checkBox_avaibility;
    TextInputEditText et_produt_name,et_brand,et_service_name,et_price;
    LinearLayout linear_product,linear_service;
    ImageView imageView_back,imageView_product_image;
    Button button_edit;
    String token;
    SearchableSpinner spinner_service_type;
    List<String> service_list;

    Merlin merlin;
    PreferenceManagerLogin session;
    StandardProgressDialog dialog;
    private String category_id;
    private int GALLERY_PROFILE = 1, CAMERA_PROFILE = 2;
    Bitmap selected_image = null;
    private String IMAGE_DIRECTORY = "/BplazMerchant/";
    String pathImageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ActivityCompat.requestPermissions(AddProduct.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                1);


        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().build(getApplicationContext());
        session = new PreferenceManagerLogin(getApplicationContext());
        dialog = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(PreferenceManagerLogin.KEY_TOKEN);

        checkBox = findViewById(R.id.checkBox);
        checkBox_avaibility = findViewById(R.id.checkBox_avaibility);
        et_produt_name = findViewById(R.id.et_produt_name);
        et_brand = findViewById(R.id.et_brand);
        et_service_name = findViewById(R.id.et_service_name);
        et_price = findViewById(R.id.et_price);
        linear_product = findViewById(R.id.linear_product);
        linear_service = findViewById(R.id.linear_service);
        imageView_back = findViewById(R.id.imageView_back);
        imageView_product_image = findViewById(R.id.imageView_product_image);
        button_edit = findViewById(R.id.button_edit);
        spinner_service_type = findViewById(R.id.spinner_service_type);

        checkBox.setChecked(false);
        linear_service.setVisibility(View.GONE);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    linear_service.setVisibility(View.VISIBLE);
                    linear_product.setVisibility(View.GONE);
                }else{
                    linear_product.setVisibility(View.VISIBLE);
                    linear_service.setVisibility(View.GONE);
                }
            }
        });

        getProductList();

        spinner_service_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!spinner_service_type.getSelectedItem().toString().equals("Choose service type")){
                    getProductListCategoryName(spinner_service_type.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imageView_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    if(et_service_name.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please insert service name",Toast.LENGTH_SHORT).show();
                    }else if(spinner_service_type.getSelectedItem().toString().equals("Choose service type")){
                        Toast.makeText(getApplicationContext(),"Please Choose service type",Toast.LENGTH_SHORT).show();
                    }else if(et_price.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please insert price",Toast.LENGTH_SHORT).show();
                    }else{
                        dialog.show();
                        add();
                    }
                }else{
                    if(et_produt_name.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please insert product name",Toast.LENGTH_SHORT).show();
                    }else if(et_brand.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please insert brand name",Toast.LENGTH_SHORT).show();
                    }else if(spinner_service_type.getSelectedItem().toString().equals("Choose service type")){
                        Toast.makeText(getApplicationContext(),"Please Choose service type",Toast.LENGTH_SHORT).show();
                    }else if(et_price.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please insert price",Toast.LENGTH_SHORT).show();
                    }else{
                        dialog.show();
                        add();
                    }
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
        AddProduct.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                    imageView_product_image.setImageBitmap(selected_image);
                    pathImageCapture = saveImage(selected_image);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddProduct.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA_PROFILE) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            selected_image = Bitmap.createScaledBitmap(thumbnail, 920, 576, true);
            imageView_product_image.setImageBitmap(selected_image);
            pathImageCapture = saveImage(selected_image);
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/BplazMerchant");
        Log.d("wallpaper", String.valueOf(wallpaperDirectory));
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
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

    private void getProductList(){

        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"get/productCategory.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        service_list = new ArrayList<String>();
                        service_list.add("Choose service type");
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("categories"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);
                                service_list.add(obj.getString("name"));
                                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, service_list);
                                adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_service_type.setAdapter(adp1);
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

    private void getProductListCategoryName(final String category_name){
        StringRequest stringRequest = new StringRequest(GET, BasedURL.ROOT_URL+"get/productCategory.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = new JSONArray(object.getString("categories"));
                            for (int i =0; i < arr.length(); i++){
                                final JSONObject obj = arr.getJSONObject(i);
                                if(obj.getString("name").equals(category_name)){
                                    category_id = obj.getString("id");
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

    private void add(){
        StringRequest stringRequest = new StringRequest(POST, BasedURL.ROOT_URL+"merchant_sales/product.json",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Product created",Toast.LENGTH_SHORT).show();
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


                headers.put("category",category_id);
                headers.put("category_id",category_id);
                headers.put("pricing[rsp_price]",et_price.getText().toString().trim());
                if(checkBox_avaibility.isChecked()){
                    headers.put("availability", "1");
                }else{
                    headers.put("availability", "0");
                }

                if(checkBox.isChecked()){
                    headers.put("product_name",et_service_name.getText().toString().trim());
                }else{
                    headers.put("product_name",et_produt_name.getText().toString().trim());
                    headers.put("brand",et_brand.getText().toString().trim());
                }

                if(selected_image!=null){
                    headers.put("image", "data:image/jpeg;base64,"+getBase64String(selected_image).trim());
                }
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

    private String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        byte[] imageBytes = baos.toByteArray();

        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return base64String;
    }
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Log.d("data",data.getString("message"));
            Toast.makeText(getApplicationContext(),data.getString("message"),Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}

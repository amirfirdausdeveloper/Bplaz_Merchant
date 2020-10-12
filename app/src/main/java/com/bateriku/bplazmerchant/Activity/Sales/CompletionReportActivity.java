package com.bateriku.bplazmerchant.Activity.Sales;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bateriku.bplazmerchant.R;

public class CompletionReportActivity extends AppCompatActivity {

    String sale_partner_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion_report);

        //GET INTENT
        sale_partner_id = getIntent().getStringExtra("sale_partner_id");
    }
}

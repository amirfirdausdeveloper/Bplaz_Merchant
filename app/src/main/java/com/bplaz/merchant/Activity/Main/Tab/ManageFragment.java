package com.bplaz.merchant.Activity.Main.Tab;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bplaz.merchant.Activity.Login.LoginActivity;
import com.bplaz.merchant.Activity.Main.MainActivity;
import com.bplaz.merchant.Activity.Profile.ProfileActivity;
import com.bplaz.merchant.Activity.Staff.StaffActivity;
import com.bplaz.merchant.PreferanceManager.PreferenceManagerLogin;
import com.bplaz.merchant.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFragment extends Fragment {

    LinearLayout linear_staff,linear_profile;
    Button button_logout;
    PreferenceManagerLogin session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manage, container, false);

        session = new PreferenceManagerLogin(getContext());
        linear_staff = v.findViewById(R.id.linear_staff);
        linear_profile = v.findViewById(R.id.linear_profile);
        button_logout = v.findViewById(R.id.button_logout);

        linear_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getActivity(), StaffActivity.class);
                startActivity(next);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        linear_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getActivity(), ProfileActivity.class);
                startActivity(next);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.logoutUser();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        return v;
    }

}

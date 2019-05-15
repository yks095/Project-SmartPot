package com.example.smartpot.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.fragments.MemberFragment;
import com.example.smartpot.requests.MemberRequest;

import org.json.JSONObject;

public class UpdateMemberActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private String userID;
    private String userEmail;
    private String potName;

    private MemberFragment memberFragment = new MemberFragment();
    private LoginActivity loginActivity = new LoginActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_member);

        final EditText hintMail = (EditText) findViewById(R.id.hintMail);
        final EditText hintPotName = (EditText) findViewById(R.id.hintPotName);
        final Button updateMemberButton2 = (Button) findViewById(R.id.updateMemberButton2);

        hintMail.setText(memberFragment.getEmail());
        hintPotName.setText(memberFragment.getPotName());

        updateMemberButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userID = loginActivity.getId().toString();
                userEmail = hintMail.getText().toString();
                potName = hintPotName.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateMemberActivity.this);
                                dialog = builder.setMessage("수정되었습니다..")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                Intent intent = new Intent(UpdateMemberActivity.this, MainActivity.class);
                                UpdateMemberActivity.this.startActivity(intent);
                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateMemberActivity.this);
                                dialog = builder.setMessage("수정에 실패했습니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                MemberRequest memberRequest = new MemberRequest(userID, userEmail, potName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UpdateMemberActivity.this);
                queue.add(memberRequest);
            }
        });

    }


}

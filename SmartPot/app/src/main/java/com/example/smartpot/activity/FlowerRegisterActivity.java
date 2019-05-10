package com.example.smartpot.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.requests.FlowerRegisterRequest;

import org.json.JSONObject;

public class FlowerRegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private Spinner spinner;
    private static int count = 0;
    private String flower;
    private LoginActivity loginActivity = new LoginActivity();
    private String userID = loginActivity.getId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_register);
        System.out.println(loginActivity.getId());

        spinner = (Spinner) findViewById(R.id.flowerSpinner);

        adapter = ArrayAdapter.createFromResource(this, R.array.flower, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button flowerRegisterButton = (Button) findViewById(R.id.flowerRegisterButton);

        flowerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flower = spinner.getSelectedItem().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FlowerRegisterActivity.this);
                                builder.setMessage("완료");
                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                                Intent mainIntent = new Intent(FlowerRegisterActivity.this, MainActivity.class);
                                FlowerRegisterActivity.this.startActivity(mainIntent);
                                count++;
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FlowerRegisterActivity.this);
                                builder.setMessage("실패");
                                builder.setNegativeButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                FlowerRegisterRequest flowerRegisterRequest = new FlowerRegisterRequest(flower, userID, responseListener);
//                FlowerRegisterRequest flowerRegisterRequest = new FlowerRegisterRequest(flower1, responseListener);
                RequestQueue queue = Volley.newRequestQueue(FlowerRegisterActivity.this);
                queue.add(flowerRegisterRequest);



            }
        });
    }
}
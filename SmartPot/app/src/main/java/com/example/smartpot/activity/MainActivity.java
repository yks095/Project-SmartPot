package com.example.smartpot.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.fragments.ClimateFragment;
import com.example.smartpot.fragments.DictionaryFragment;
import com.example.smartpot.fragments.MemberFragment;
import com.example.smartpot.requests.WaterRequest;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    DictionaryFragment dictionaryFragment;
    private String auto;
    private String manual;
    private FlowerRegisterActivity flowerRegisterActivity = new FlowerRegisterActivity();
    private String potCode = flowerRegisterActivity.getPotSerial();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(potCode);

        Button autoButton = (Button) findViewById(R.id.autoButton);
        autoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                auto = "1";
                manual = "0";
                System.out.println("자동수급 : 1");

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("자동수급");
                                builder.setMessage("완료");
                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("자동수급");
                                builder.setMessage("실패");
                                builder.setNegativeButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                WaterRequest waterRequest = new WaterRequest(auto, manual, potCode, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(waterRequest);

            }
        });


        Button manualButton = (Button) findViewById(R.id.manualButton);
        manualButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                auto = "0";
                manual = "1";
                System.out.println("수동수급 : 0");


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("수동수급");
                                builder.setMessage("완료");
                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("수동수급");
                                builder.setMessage("실패");
                                builder.setNegativeButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                WaterRequest waterRequest = new WaterRequest(auto, manual, potCode, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(waterRequest);

            }
        });

        ImageButton addButton = (ImageButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, FlowerRegisterActivity.class);
                MainActivity.this.startActivity(addIntent);
            }
        });

        dictionaryFragment = new DictionaryFragment();
        final Button climateButton = (Button) findViewById(R.id.climateButton);
        final Button memberButton = (Button) findViewById(R.id.memberButton);
        final Button dicButton = (Button) findViewById(R.id.dicButton);
        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        final Button homeButton = (Button) findViewById(R.id.homeButton);


        climateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                climateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                memberButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                dicButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new ClimateFragment());
                fragmentTransaction.commit();
            }
        });

        memberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                climateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                memberButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                dicButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new MemberFragment());
                fragmentTransaction.commit();
            }
        });

        dicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                climateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                memberButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                dicButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new DictionaryFragment());
                fragmentTransaction.commit();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(mainIntent);
            }
        });
    }
}
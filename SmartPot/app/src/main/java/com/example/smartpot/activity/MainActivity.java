package com.example.smartpot.activity;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.fragments.ClimateFragment;
import com.example.smartpot.fragments.DictionaryFragment;
import com.example.smartpot.fragments.MemberFragment;
import com.example.smartpot.requests.PumpTimeRequest;
import com.example.smartpot.requests.WaterRequest;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    DictionaryFragment dictionaryFragment;
    private String auto;
    private String manual;
    private FlowerRegisterActivity flowerRegisterActivity = new FlowerRegisterActivity();
    private String potCode = flowerRegisterActivity.getPotSerial();
    private int s;
    private int ms;
    private int result;



    public void notice(View view)   {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.drawable.smartpot);
        builder.setTicker("Test1");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("알림 제목");
        builder.setContentText("알림 세부 텍스트");
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setFullScreenIntent(pendingIntent, true);

        manager.notify(1, builder.build());
    }

    public void hide()  {
        NotificationManagerCompat.from(this).cancel(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(potCode);



        Button autoButton = (Button) findViewById(R.id.autoButton);
        autoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                notice(view);
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
                hide();
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

//                                final Button minusButton = new Button(MainActivity.this);
//                                final EditText valueText = new EditText(MainActivity.this);
//                                final Button plusButton = new Button(MainActivity.this);

                                final Spinner numSpinner = new Spinner(MainActivity.this);
                                final ArrayAdapter adapter;

                                adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.second, android.R.layout.simple_spinner_dropdown_item);
                                numSpinner.setAdapter(adapter);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("수동수급");

//                                builder.setView(minusButton);
//                                builder.setView(valueText);
//                                builder.setView(plusButton);

                                builder.setView(numSpinner);

                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                s = Integer.valueOf(numSpinner.getSelectedItem().toString());
                                                System.out.println("텍스트 값 : " + s);
                                                ms = 1000;
                                                result = s * ms;
                                                System.out.println("공급 시간 = " + result + "밀리세컨드");
                                                String manualPumpTime = String.valueOf(result);

                                                PumpTimeRequest pumpTimeRequest = new PumpTimeRequest(potCode, manualPumpTime);
                                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                                queue.add(pumpTimeRequest);

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


        dictionaryFragment = new DictionaryFragment();
        final ImageButton climateButton = (ImageButton) findViewById(R.id.climateButton);
        final Button memberButton = (Button) findViewById(R.id.memberButton);
        final Button dicButton = (Button) findViewById(R.id.dicButton);
        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        final ImageButton homeButton = (ImageButton) findViewById(R.id.homeButton);


        climateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                climateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                memberButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                dicButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
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
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
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
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.fragment, new DictionaryFragment());
                fragmentTransaction.commit();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);
                finish();
            }
        });

    }


}
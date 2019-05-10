package com.example.smartpot.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.fragments.DictionaryFragment;
import com.example.smartpot.fragments.ManageFragment;
import com.example.smartpot.fragments.MemberFragment;
import com.example.smartpot.requests.WaterRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView[] weather_tv = new TextView[4];
    ManageFragment manageFragment;
    SwipeRefreshLayout swipeRefreshLayout;
    private String auto;
    private AlertDialog dialog;
    private RegisterActivity registerActivity = new RegisterActivity();
    private String potCode = registerActivity.getPotSerial();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button autoButton = (Button) findViewById(R.id.autoButton);
        autoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                auto = "1";
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

                WaterRequest waterRequest = new WaterRequest(auto, potCode, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(waterRequest);

            }
        });


        Button manualButton = (Button) findViewById(R.id.manualButton);
        manualButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                auto = "0";
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

                WaterRequest waterRequest = new WaterRequest(auto, potCode, responseListener);
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

        manageFragment = new ManageFragment();
        final Button dicButton = (Button) findViewById(R.id.dicButton);
        final Button memberButton = (Button) findViewById(R.id.memberButton);
        final Button manageButton = (Button) findViewById(R.id.manageButton);
        final LinearLayout notice = (LinearLayout) findViewById(R.id.notice);


        dicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notice.setVisibility(View.GONE);
                dicButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                memberButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                manageButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new DictionaryFragment());
                fragmentTransaction.commit();
            }
        });

        memberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notice.setVisibility(View.GONE);
                dicButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                memberButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                manageButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new MemberFragment());
                fragmentTransaction.commit();
            }
        });

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notice.setVisibility(View.GONE);
                dicButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                memberButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                manageButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new ManageFragment());
                fragmentTransaction.commit();
            }
        });

        weather_tv[0] = (TextView) findViewById(R.id.weather_tv_time);
        weather_tv[1] = (TextView) findViewById(R.id.weather_tv_now);
        weather_tv[2] = (TextView) findViewById(R.id.weather_tv_temp);
        weather_tv[3] = (TextView) findViewById(R.id.weather_tv_mois);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        new WeatherAsynTask(weather_tv).execute("http://www.weather.go.kr/weather/observation/currentweather.jsp?stn=159");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new WeatherAsynTask(weather_tv).execute("http://www.weather.go.kr/weather/observation/currentweather.jsp?stn=159");

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}

class WeatherAsynTask extends AsyncTask<String, Void, String[]> {

    private TextView[] textView;
    private final String SELECTOR ="table[class = table_develop3] tbody tr:nth-child(1) td:nth-child(1), tr:nth-child(1) td:nth-child(2), tr:nth-child(1) td:nth-child(6), tr:nth-child(1) td:nth-child(10)  ";

    public WeatherAsynTask(TextView[] textView) {
        this.textView = textView;
    }

    @Override
    protected String[] doInBackground(String... params) {

        String URL = params[0];
        String[] result = new String[4];

        int count = 0;
        try {
            Document document = Jsoup.connect(URL).get();
            Elements elements = document.select(SELECTOR);

            for (Element element : elements) {

                if (element.text().trim().length() == 1) {
                    result[count] = "예측정보 없음\n";
                } else {
                    if (count == 2) result[count] = element.text() + "℃\n";
                    else if (count == 3) result[count] = element.text() + "%\n";

                    else result[count] = element.text() + "\n";
                }

                count++;

            }

            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);

        for (int i = 0; i < 4; i++) {
            textView[i].setText(s[i]);
        }
    }
}




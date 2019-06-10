package com.example.smartpot.fragments;

import android.app.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartpot.R;
import com.example.smartpot.activity.LoginActivity;
import com.example.smartpot.activity.MainActivity;
import com.example.smartpot.enums.ServerURL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClimateFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ClimateFragment.OnFragmentInteractionListener mListener;

    public ClimateFragment() {
    }

    public static ClimateFragment newInstance(String param1, String param2) {
        ClimateFragment fragment = new ClimateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView[] weather_tv = new TextView[4];
    //    ImageView humidity;
    SwipeRefreshLayout swipeRefreshLayout;
    private LoginActivity loginActivity = new LoginActivity();

    static ImageView humidity; // ImageView를 class내부에서 사용하기 위해서는 static으로 선언해줘야 함
    static ImageView cast;
    static TextView weather_tv1;
    static TextView weather_tv2;
    static TextView weather_tv3;
    static TextView weather_tv4;
    static TextView weather;
    static TextView weather_1hr_temp; // 1시간 후 기온을 받아오기 위한 textview
    static TextView weather_1hr_mois; // 1시간 후 습도를 받아오기 위한 textview

    static ImageView humidity_1hr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_climate, container, false);
        final String userID = loginActivity.getId();

        weather_tv1 = (TextView) view.findViewById(R.id.weather_tv_time);
        weather_tv2 = (TextView) view.findViewById(R.id.weather_tv_now);
        weather_tv3 = (TextView) view.findViewById(R.id.weather_tv_temp);
        weather_tv4 = (TextView) view.findViewById(R.id.weather_tv_mois);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    new WeatherAsynTask().execute();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                setTime();
            }
        }).start();
//        new WeatherAsynTask().execute();

        humidity = (ImageView) view.findViewById(R.id.humidity);
        cast = (ImageView)view.findViewById(R.id.cast);
        weather_1hr_mois = (TextView)view.findViewById(R.id.weather_forecast_1hr_mois);
        weather_1hr_temp = (TextView)view.findViewById(R.id.weather_forecast_1hr_temp);
        humidity_1hr = (ImageView)view.findViewById(R.id.humidity_1hr);
        weather = (TextView)view.findViewById(R.id.weather_tv_mois);

        //새로고침 시
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new WeatherAsynTask().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //부산광역시 남구 대연동
    static class WeatherAsynTask extends AsyncTask<String, Void, String[]> {


        private TextView[] textView;
        private static final String URL1 = "http://www.weather.go.kr/weather/observation/currentweather.jsp?stn=159";
        private static final String URL2 = "http://www.weather.go.kr/weather/forecast/timeseries.jsp";
        private final String SELECTOR1 = "table[class = table_develop3] tbody tr:nth-child(1) td:nth-child(1), tr:nth-child(1) td:nth-child(2), tr:nth-child(1) td:nth-child(6), tr:nth-child(1) td:nth-child(10)  ";
        private final String SELECTOR2 = "div[class = time_weather1] dl dt[class = w_hour2], div[class = time_weather1] dl dd[class = time_weather1_left temp2],div[class = time_weather1] dl dd:nth-child(5)";

        @Override
        protected String[] doInBackground (String...params){
            String[] result = new String[16];

            int count = 0;
            try {
                Document document1 = Jsoup.connect(URL1).get();
                Document document2 = Jsoup.connect(URL2)
                        .header("Host", "www.weather.go.kr")
                        .header("Referer", "http://www.weather.go.kr/weather/forecast/timeseries.jsp")
                        .header("Accept-Encoding", "gzip, deflate")
                        .data("myPointCode", "2629051000")
                        .data("unit", "K")
                        .get();

                Elements elements1 = document1.select(SELECTOR1);
                Elements elements2 = document2.select(SELECTOR2);

                /*현재 부산광역시의 날씨, 기온, 습도를 result배열에 저장,
                  원래 'http://www.weather.go.kr/weather/forecast/timeseries.jsp#'에도 현재 기온, 습도가 있어
                  한꺼번에 가져오려고 했으나 현재 날씨에 대한 정보가 없어 다른 url에서 가져옴. */
                for (Element element : elements1) {
                    if (element.text().trim().length() == 1) {
                        result[count] = "예측정보 없음\n";
                    } else {
                        if (count == 2) result[count] = element.text() + "℃\n";
                        else if (count == 3) result[count] = element.text() + "%\n";

                        else result[count] = element.text() + "\n";
                    }
                    count++;
                }

                //1시간, 2시간 후 예보를 result배열에 저장, 후에 동적뷰로 구성할 예정
                for (Element element : elements2) {
                    result[count] = element.text();
                    count++;
                }

                Thread.sleep(1000);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;


        }

        @Override
        protected void onPostExecute (String[]s){
            super.onPostExecute(s);
            //현재 날짜, 시간을 정확하게 표현하고 싶어 사용

            System.out.println("s : " + Arrays.toString(s));

//            for (int i = 1; i < 4; i++) {
//                textView[i].setText(s[i]);
//            }
            weather_tv2.setText(s[1]);
            weather_tv3.setText(s[2]);
            weather_tv4.setText(s[3]);
            weather_1hr_temp.setText(s[5]);
            weather_1hr_mois.setText(s[6]);

            //현재일기에 따라 이미지를 변경
            //정확히 '구름많음', '구름조금' 으로 읽어오지 않기 때문에 trim()을 사용
            if (s[1].trim().contains("비")) {
                cast.setImageResource(R.drawable.rain);
            }
            else if (s[1].trim().equals("흐림") || s[1].equals("박무")) cast.setImageResource(R.drawable.cloudy);
            else if (s[1].trim().equals("구름많음")) cast.setImageResource(R.drawable.m_cloudy);
            else if (s[1].trim().equals("구름조금")) {
                cast.setImageResource(R.drawable.l_cloudy);
//                back.setBackgroundResource(R.drawable.back_cloudy);
            }
            else if (s[1].trim().equals("맑음")) cast.setImageResource(R.drawable.sunny);

            //습도 값에 따라 이미지를 변경
            if (Integer.parseInt(s[3].substring(0, 2)) == 0) {
                humidity.setImageResource(R.drawable.humidity_zero);
                humidity_1hr.setImageResource(R.drawable.humidity_zero);
            }
            else if (Integer.parseInt(s[3].substring(0, 2)) > 0 && Integer.parseInt(s[3].substring(0, 2)) <= 40) {
                humidity.setImageResource(R.drawable.humidity_fourty);
                humidity_1hr.setImageResource(R.drawable.humidity_fourty);
            }
            else if (Integer.parseInt(s[3].substring(0, 2)) > 40 && Integer.parseInt(s[3].substring(0, 2)) <= 60) {
                humidity.setImageResource(R.drawable.humidity_fifty);
                humidity_1hr.setImageResource(R.drawable.humidity_fifty);
            }
            else if (Integer.parseInt(s[3].substring(0, 2)) > 60 && Integer.parseInt(s[3].substring(0, 2)) <= 80) {
                humidity.setImageResource(R.drawable.humidity_seventy);
                humidity_1hr.setImageResource(R.drawable.humidity_seventy);
            }
            else if (Integer.parseInt(s[3].substring(0, 2)) > 80) {
                humidity.setImageResource(R.drawable.humidity_eighty);
                humidity_1hr.setImageResource(R.drawable.humidity_eighty);
            }

        }

    }

    private void setTime(){
        while (true) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm:ss(E)");
            //System.currentTimeMillis()는 영국 기준시를 들고 오기 때문에 한국과 9시간 차이남.
            weather_tv1.setText(sdf.format(new Date(System.currentTimeMillis() + 32400000)));
//            weather_tv[0].setText(sdf.format(new Date(System.currentTimeMillis() + 32400000)));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
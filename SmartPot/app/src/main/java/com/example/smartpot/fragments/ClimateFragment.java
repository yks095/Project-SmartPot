package com.example.smartpot.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartpot.R;
import com.example.smartpot.activity.LoginActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
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
    SwipeRefreshLayout swipeRefreshLayout;
    private LoginActivity loginActivity = new LoginActivity();
    phpdo task;
    phpdo2 task2;
    private String tempSensor;
    private String highTemp;
    private String lowTemp;

    private class phpdo extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String idx = arg[0];

                String link = "http://pdh6547.iptime.org/android/FlowerTemp.php?userID="+idx;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception" + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {

            lowTemp = result.substring(0, 2);
            highTemp = result.substring(2, 4);

        }
    }

    private class phpdo2 extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String idx = arg[0];

                String link = "http://117.16.94.138/android/PotTemp.php?userID="+idx;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception" + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {

            tempSensor = result;

        }
    }

    static int counter = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_climate, container, false);
        final String userID = loginActivity.getId().toString();
        task = new phpdo();
        task.execute(userID);

//        TimerTask adTask = new TimerTask() {
//            @Override
//            public void run() {
//                task2 = new phpdo2();
//                task2.execute(userID);
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(adTask, 0, 10000);
//        TimerTask adTask2 = new TimerTask() {
//            @Override
//            public void run() {
//                counter++;
//                if (counter >= 2) {
//                    if (Integer.parseInt(tempSensor) >= Integer.parseInt(highTemp)) {
//                        System.out.println("크다아아아아아아ㅏ아아아아ㅏ아앙아");
//                    }
//                    else if (Integer.parseInt(tempSensor) <= Integer.parseInt(lowTemp)) {
//                        System.out.println("작드아아아ㅏ아아아");
//                    }
//                }
//            }
//        };
//        Timer timer2 = new Timer();
//        timer2.schedule(adTask2, 0, 10000);

        weather_tv[0] = (TextView) view.findViewById(R.id.weather_tv_time);
        weather_tv[1] = (TextView) view.findViewById(R.id.weather_tv_now);
        weather_tv[2] = (TextView) view.findViewById(R.id.weather_tv_temp);
        weather_tv[3] = (TextView) view.findViewById(R.id.weather_tv_mois);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        new WeatherAsynTask(weather_tv).execute("http://www.weather.go.kr/weather/observation/currentweather.jsp?stn=159");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new WeatherAsynTask(weather_tv).execute("http://www.weather.go.kr/weather/observation/currentweather.jsp?stn=159");

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

    static class WeatherAsynTask extends AsyncTask<String, Void, String[]> {

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
}
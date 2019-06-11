package com.example.smartpot.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.smartpot.enums.ServerURL;
import com.example.smartpot.fragments.ManageFragment;
import com.example.smartpot.R;
import com.example.smartpot.fragments.ClimateFragment;
import com.example.smartpot.fragments.DictionaryFragment;
import com.example.smartpot.fragments.MemberFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {

    private ManageFragment manageFragment;
    private ClimateFragment climateFragment;
    private DictionaryFragment dictionaryFragment;
    private MemberFragment memberFragment;
    boolean isReceived = false;

    int status;
    LoginActivity loginActivity = new LoginActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        manageFragment = new ManageFragment();
        climateFragment = new ClimateFragment();
        dictionaryFragment = new DictionaryFragment();
        memberFragment = new MemberFragment();

        initFragment();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (tabId == R.id.tab_manage) {
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    transaction.replace(R.id.contentContainer, manageFragment).commit();
                } else if (tabId == R.id.tab_dictionary) {
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    transaction.replace(R.id.contentContainer, dictionaryFragment).commit();
                } else if (tabId == R.id.tab_weather) {
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    transaction.replace(R.id.contentContainer, climateFragment).commit();
                } else if (tabId == R.id.tab_member) {
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    transaction.replace(R.id.contentContainer, memberFragment).commit();
                }
            }
        });

        final String userID = loginActivity.getId().toString();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getTempValue = new GetTempValue();
                getTempValue.execute(userID);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 10000);
        TimerTask getTemp2 = new TimerTask() {
            @Override
            public void run() {
                if (isReceived) {
                    if ("null".equals(tempSensor)) {
                    } else {
                        if (Double.parseDouble(tempSensor) >= Integer.valueOf(highTemp)) {
                            notice();
                            status = 1;
                        } else if (Double.parseDouble(tempSensor) <= Integer.valueOf(lowTemp)) {
                            notice();
                            status = 2;
                        }
                    }
                    isReceived = false;
                }
            }
        };
        Timer timer2 = new Timer();
        timer2.schedule(getTemp2, 0, 10000);
    }

    public void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentContainer, manageFragment);
        transaction.commit();
    }

//    private LoginActivity loginActivity = new LoginActivity();
    private String highTemp;
    private String lowTemp;
    private String tempSensor;
    GetTempValue getTempValue;

    public void notice() {
        String channelId = "channel";
        String channelName = "ChannelName";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationManager notifiManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notifiManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (status == 1) {
            builder.setContentTitle("경고!!!").
                    setContentText("식물의 온도가 너무 높습니다. 자리를 옮겨주세요.").
                    setDefaults(Notification.DEFAULT_ALL).
                    setAutoCancel(true).
                    setSmallIcon(R.drawable.ic_local_florist_black_24dp).
                    setContentIntent(pendingIntent);
            notifiManager.notify(0, builder.build());
        } else if (status == 2) {
            builder.setContentTitle("경고!!!").
                    setContentText("식물의 온도가 너무 낮습니다. 자리를 옮겨주세요.").
                    setDefaults(Notification.DEFAULT_ALL).
                    setAutoCancel(true).
                    setSmallIcon(R.drawable.ic_local_florist_black_24dp).
                    setContentIntent(pendingIntent);
            notifiManager.notify(0, builder.build());
        }

    }

    public void hide() {
        NotificationManagerCompat.from(this).cancel(0);
    }

    //식물의 적정 최고온도, 최저온도와 현재온도를 가져온다.
    private class GetTempValue extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try {
                String idx = arg[0];

                String link = ServerURL.URL.getUrl() + "/FlowerTemp.php?userID=" + idx;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
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

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    lowTemp = object.getString("lowTemp");
                    highTemp = object.getString("highTemp");
                    tempSensor = object.getString("tempSensor");
                    isReceived = true;
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // Back 버튼 2번 눌렀을 시 프로세스 종료
    private long pressedTime = 0;

    public interface OnBackPressedListener {
        public void onBack();
    }

    private OnBackPressedListener mBackListener;

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mBackListener != null) {
            mBackListener.onBack();
            Log.e("!!!", "Listener is not null");
        } else {
            Log.e("!!!", "Listener is null");
            if (pressedTime == 0) {
                Snackbar.make(findViewById(R.id.main), "한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if (seconds > 3000) {
                    Snackbar.make(findViewById(R.id.main), "한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show();
                    pressedTime = 0;
                } else {
                    super.onBackPressed();
                    Log.e("!!!", "onBackPressed : finished, killProcess");
                    //뒤로가기 두번 누르면 홈화면으로 감
                    finishAffinity();
                    System.runFinalization();
                    System.exit(0);
                }
            }
        }
    }
}
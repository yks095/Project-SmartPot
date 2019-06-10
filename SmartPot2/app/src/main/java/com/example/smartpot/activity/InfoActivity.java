package com.example.smartpot.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smartpot.Dictionary;
import com.example.smartpot.R;
import com.example.smartpot.enums.ServerURL;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    private String[] flower_info = {"학명", "과명", "유통명", "관리수준", "관리요구", "생장속도", "생육온도", "최저온도", "광요구도", "배치 장소",
            "물주기", "비료정보", "번식 방법", "번식 시기", "병충해관리정보", "습도", "토양", "원산지", "분류", "생육형태",
            "생장높이(cm)", "생장너비(cm)", "실내정원구성", "생태형", "잎형태", "잎무늬", "잎색", "꽃피는 계절", "꽃색", "열매 맺는 계절",
            "열매색", "향기", "TIP", "특별관리정보", "독성", "병충해 관리", "생장형", "번식", "식물 분류", "형태 분류",
            "엽색 변화", "뿌리형태", "꽃", "고온다습"};

    private String name;
    private String image;

    private TextView nameText;
    private ImageView imageView;
    private Dictionary dictionary;
    private LinearLayout dic;
    private TableLayout table;
    private TableRow row;
    private TextView[] tv;
    private String[] attrs = new String[46];
    private String[] values = new String[46];
    //    private LinearLayout.LayoutParams params;
    private TableLayout.LayoutParams params;

    phpdo task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        task = new phpdo();
        task.execute();

        table = (TableLayout) findViewById(R.id.table);
        params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.CENTER;


        dictionary = new Dictionary();
        name = getIntent().getExtras().getString("name");
        image = getIntent().getExtras().getString("image");


        nameText = (TextView) findViewById(R.id.nameText);
        imageView = (ImageView) findViewById(R.id.imageView);
        nameText.setText(name);
        Glide.with(this).load(image).into(imageView);

    }
    private static int cnt = 0;
    private static int width = 30;
    public void info(String attrs, String values)  {

        System.out.println("메소드 진입");
        TextView textView;
        TextView textView2;
        TextView textView3;
        TextView textView4;

        textView = new TextView(this);
        textView.setText("  " + values);
        textView.setTextSize(20);
        textView.setWidth(400);
        textView.setTextColor(Color.BLACK);

        textView2 = new TextView(this);
        textView2.setText(attrs);
        textView2.setTextSize(20);
        textView2.setTextColor(Color.BLACK);

        textView3 = new TextView(this);
        textView3.setText("");
        textView3.setTextSize(20);
        textView3.setWidth(300);

        textView4 = new TextView(this);
        textView4.setText("");
        textView4.setTextSize(20);



        TableRow tr = new TableRow(this);
        tr.setGravity(Gravity.CENTER);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow tr2 = new TableRow(this);
        tr2.setGravity(Gravity.CENTER);
        tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        tr.addView(textView);
        tr.addView(textView2, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));


        tr2.addView(textView3);
        tr2.addView(textView4, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));

        table.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.FILL_PARENT));
        table.addView(tr2, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.FILL_PARENT));

        System.out.println(values + " : " + attrs);
        cnt++;
    }

    private class phpdo extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(String... arg) {
            try{
                String link = ServerURL.URL.getUrl() + "/GetInfo.php?name="+name;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);

                StringBuffer sb = new StringBuffer("");
                String line = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

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

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    Iterator<String> keys = object.keys();
                    object.getString("name");
                    int cnt = 0;

                    while(keys.hasNext()) {
                        String key = keys.next();
                        values[cnt] = key;
                        attrs[cnt++] =  object.get(key).toString();
                    }

                    Dictionary dictionary = new Dictionary(attrs, values);
                    dictionary.setAttrs(attrs);
                    dictionary.setValues(values);

                    count++;
                }

                for(int i = 2; i < values.length; i++)  {
                    int j = i - 2;
                    String s = "ATTR_"+j;
                    if(s.equals(values[i]))    {
                        values[i] = flower_info[j];
                    }
                }

                for(int i = 2; i < values.length; i++)  {
                    if("null".equals(attrs[i]))
                        continue;
                    else    {
                        info(attrs[i], values[i]);
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

}



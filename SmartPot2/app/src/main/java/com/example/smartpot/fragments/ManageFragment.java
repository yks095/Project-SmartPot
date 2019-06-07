package com.example.smartpot.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.activity.FlowerRegisterActivity;
import com.example.smartpot.activity.LoginActivity;
import com.example.smartpot.enums.ServerURL;
import com.example.smartpot.requests.PumpTimeRequest;
import com.example.smartpot.requests.WaterRequest;

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


public class ManageFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static int MS = 1000; // DB에 ms단위로 저장하기위해

    private String mParam1;
    private String mParam2;

    Dialog epicDialog;
    ImageView hintImage;
    TextView hintText;
    Button hintCloseButton;

    private OnFragmentInteractionListener mListener;

    private String auto;
    private String manual;
    private FlowerRegisterActivity flowerRegisterActivity = new FlowerRegisterActivity();
    private String potCode = flowerRegisterActivity.getPotSerial();
    private int s;
    private int result;
    private LoginActivity loginActivity = new LoginActivity();
    private String userID = loginActivity.getId();
    SwipeRefreshLayout swipeRefreshLayout;

    public ManageFragment() {
    }

    public static ManageFragment newInstance(String param1, String param2) {
        ManageFragment fragment = new ManageFragment();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        Button autoButton = (Button) view.findViewById(R.id.autoButton);
        ImageButton hintButton = (ImageButton) view.findViewById(R.id.hintButton);
        potName = (TextView) view.findViewById(R.id.potName);
        nowCds = (TextView) view.findViewById(R.id.nowCds);
        nowPotTemp = (TextView) view.findViewById(R.id.nowPotTemp);
        nowPotWater = (TextView) view.findViewById(R.id.nowPotWater);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.manage_refresh_layout);
        task = new phpdo();
        task.execute(userID);

        epicDialog = new Dialog(getContext());
        hintText = (TextView)view.findViewById(R.id.hintText) ;
        hintImage = (ImageView) view.findViewById(R.id.hintImage);
        hintCloseButton = (Button) view.findViewById(R.id.hintCloseButton);

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto = "1";
                manual = "0";

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                WaterRequest waterRequest = new WaterRequest(auto, manual, potCode, userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(waterRequest);

            }
        });


        Button manualButton = (Button) view.findViewById(R.id.manualButton);
        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto = "0";
                manual = "1";

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {

                                final Spinner numSpinner = new Spinner(getContext());
                                final ArrayAdapter adapter;

                                adapter = ArrayAdapter.createFromResource(getContext(), R.array.second, android.R.layout.simple_spinner_dropdown_item);
                                numSpinner.setAdapter(adapter);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("수동수급 할 시간을 고르시오");

                                builder.setView(numSpinner);

                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String sub = numSpinner.getSelectedItem().toString();
                                                System.out.println("sub = " + sub);
                                                String second = sub.substring(0,1);
                                                System.out.println("second = " + second);
                                                s = Integer.valueOf(second);
                                                System.out.println("s = " + s);
                                                result = s * MS;
                                                String manualPumpTime = String.valueOf(result);
                                                PumpTimeRequest pumpTimeRequest = new PumpTimeRequest(potCode, userID, manualPumpTime);
                                                RequestQueue queue = Volley.newRequestQueue(getContext());
                                                queue.add(pumpTimeRequest);

                                            }
                                        });
                                builder.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                WaterRequest waterRequest = new WaterRequest(auto, manual, potCode, userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(waterRequest);

            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHintPopup();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                task = new phpdo();
                task.execute(userID);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    //hint 팝업창을 띄우기 위한 method
    public void showHintPopup(){

        epicDialog.setContentView(R.layout.custom_popup_hint);
        hintText = (TextView)epicDialog.findViewById(R.id.hintText);
        hintImage = (ImageView) epicDialog.findViewById(R.id.hintImage);
        hintCloseButton = (Button)epicDialog.findViewById(R.id.hintCloseButton);

        hintCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog.dismiss();
            }
        });

        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();

    }
    private String name;
    private String water;
    private String waterSensor;
    private String tempSensor;
    TextView potName;
    TextView nowCds;
    TextView nowPotTemp;
    TextView nowPotWater;

    phpdo task;

    private class phpdo extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try {
                String idx = arg[0];

                String link = ServerURL.URL.getUrl() + "/PotInfo.php?userID=" + idx;
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
                    name = object.getString("potName");
                    water = object.getString("cds_sensor");
                    waterSensor = object.getString("sensor");
                    tempSensor = object.getString("tempSensor");
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            potName.setText(name);
            if (water.equals("null")) {
                nowCds.setText("없음");
                nowCds.setCompoundDrawablesWithIntrinsicBounds(R.drawable.empty_water, 0, 0, 0);
            } else if (water.equals("1")) {
                nowCds.setText("충분");
                nowCds.setCompoundDrawablesWithIntrinsicBounds(R.drawable.full_water, 0, 0, 0);
            } else if (water.equals("0")) {
                nowCds.setText("부족");
                nowCds.setCompoundDrawablesWithIntrinsicBounds(R.drawable.empty_water, 0, 0, 0);
            }
            if ("null".equals(tempSensor)) {
                nowPotTemp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lowtemp, 0, 0, 0);
                nowPotTemp.setText("없음");
            } else if (Integer.parseInt(tempSensor) > 30) {
                nowPotTemp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hightemp, 0, 0, 0);
                nowPotTemp.setText(tempSensor + "℃");
            } else if (15 <= Integer.parseInt(tempSensor) && Integer.parseInt(tempSensor) <= 30) {
                nowPotTemp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_halftemp, 0, 0, 0);
                nowPotTemp.setText(tempSensor + "℃");
            } else if (15 > Integer.parseInt(tempSensor)) {
                nowPotTemp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lowtemp, 0, 0, 0);
                nowPotTemp.setText(tempSensor + "℃");
            }
            if (waterSensor.equals("null")) {
                nowPotWater.setText("없음");
            } else {
                nowPotWater.setText(waterSensor);
            }
        }
    }

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
}
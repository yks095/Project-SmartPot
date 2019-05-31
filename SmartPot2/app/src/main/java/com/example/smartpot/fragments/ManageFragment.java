package com.example.smartpot.fragments;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private OnFragmentInteractionListener mListener;

    private String auto;
    private String manual;
    private FlowerRegisterActivity flowerRegisterActivity = new FlowerRegisterActivity();
    private String potCode = flowerRegisterActivity.getPotSerial();
    private int s;
    private int result;
    private LoginActivity loginActivity = new LoginActivity();
    private String userID = loginActivity.getId();

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
        potName = (TextView) view.findViewById(R.id.potName);
        nowCds = (TextView) view.findViewById(R.id.nowCds);
        nowPotTemp = (TextView) view.findViewById(R.id.nowPotTemp);
        nowPotWater = (TextView) view.findViewById(R.id.nowPotWater);
        task = new phpdo();
        task.execute(userID);

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                notice(view);
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
//                hide();
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

                                final Spinner numSpinner = new Spinner(getContext());
                                final ArrayAdapter adapter;

                                adapter = ArrayAdapter.createFromResource(getContext(), R.array.second, android.R.layout.simple_spinner_dropdown_item);
                                numSpinner.setAdapter(adapter);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("수동수급");

                                builder.setView(numSpinner);

                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                s = Integer.valueOf(numSpinner.getSelectedItem().toString());
                                                System.out.println("텍스트 값 : " + s);
                                                result = s * MS;
                                                System.out.println("공급 시간 = " + result + "밀리세컨드");
                                                String manualPumpTime = String.valueOf(result);
                                                System.out.println("userid : " + userID);
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

        return view;
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
                    System.out.println(tempSensor);
                    System.out.println(waterSensor);
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
            if (tempSensor.equals("null")) {
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
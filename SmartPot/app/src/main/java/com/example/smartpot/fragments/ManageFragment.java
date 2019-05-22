package com.example.smartpot.fragments;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.activity.FlowerRegisterActivity;
import com.example.smartpot.requests.PumpTimeRequest;
import com.example.smartpot.requests.WaterRequest;

import org.json.JSONObject;


public class ManageFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

    private String auto;
    private String manual;
    private FlowerRegisterActivity flowerRegisterActivity = new FlowerRegisterActivity();
    private String potCode = flowerRegisterActivity.getPotSerial();
    private int s;
    private int ms;
    private int result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        Button autoButton = (Button) view.findViewById(R.id.autoButton);
        autoButton.setOnClickListener(new View.OnClickListener(){
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

                WaterRequest waterRequest = new WaterRequest(auto, manual, potCode, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(waterRequest);

            }
        });


        Button manualButton = (Button) view.findViewById(R.id.manualButton);
        manualButton.setOnClickListener(new View.OnClickListener(){
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
                                                ms = 1000;
                                                result = s * ms;
                                                System.out.println("공급 시간 = " + result + "밀리세컨드");
                                                String manualPumpTime = String.valueOf(result);

                                                PumpTimeRequest pumpTimeRequest = new PumpTimeRequest(potCode, manualPumpTime);
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
                WaterRequest waterRequest = new WaterRequest(auto, manual, potCode, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(waterRequest);

            }
        });

        return view;
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

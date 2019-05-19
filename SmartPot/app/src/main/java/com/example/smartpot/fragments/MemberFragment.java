package com.example.smartpot.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartpot.R;
import com.example.smartpot.activity.LoginActivity;
import com.example.smartpot.activity.UpdateMemberActivity;
import com.example.smartpot.activity.UpdatePasswordActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class MemberFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MemberFragment() {
    }

    public static MemberFragment newInstance(String param1, String param2) {
        MemberFragment fragment = new MemberFragment();
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

    private LoginActivity loginActivity = new LoginActivity();
    TextView nowUserEmail;
    TextView nowPotName;
    phpdo task;
    phpdo2 task2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);

        final Button updateMemberButton = (Button) view.findViewById(R.id.updateMemberButton);
        final Button updatePasswordButton = (Button) view.findViewById(R.id.updatePasswordButton);
        final TextView nowUserID = (TextView) view.findViewById(R.id.nowUserID);


        nowUserID.setText(loginActivity.getId());
        String userID = loginActivity.getId();
        task = new phpdo();
        task2 = new phpdo2();
        nowUserEmail = (TextView) view.findViewById(R.id.nowUserEmail);
        nowPotName = (TextView) view.findViewById(R.id.nowPotname);
        task.execute(userID);
        task2.execute(userID);

        updateMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getContext(), UpdateMemberActivity.class);
                getContext().startActivity(addIntent);

            }
        });

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getContext(), UpdatePasswordActivity.class);
                getContext().startActivity(addIntent);
            }
        });

        return view;
    }

    private static String email="";
    private static String potName="";

    private class phpdo extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String userID = arg[0];

                String link = "http://117.16.94.138/UserInfo.php?userID="+userID;
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

            nowUserEmail.setText(result);
            email = result;
        }
    }

    private class phpdo2 extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String userID = arg[0];

                String link = "http://117.16.94.138/UserPot.php?userID="+userID;
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

            nowPotName.setText(result);
            potName = result;
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
        void onFragmentInteraction(Uri uri);
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        MemberFragment.email = email;
    }

    public static String getPotName() {
        return potName;
    }

    public static void setPotName(String potName) {
        MemberFragment.potName = potName;
    }
}
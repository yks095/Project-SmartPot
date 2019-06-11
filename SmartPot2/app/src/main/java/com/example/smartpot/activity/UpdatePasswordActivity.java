package com.example.smartpot.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.enums.ServerURL;
import com.example.smartpot.requests.MemberRequest;
import com.example.smartpot.requests.PasswordRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class UpdatePasswordActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private String userID;
    private String nowPassword;
    private String userPassword;
    private String userPassword2;
    private LoginActivity loginActivity = new LoginActivity();
    phpdo task;

    private static String password="";

    private class phpdo extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String userID = arg[0];

                String link = ServerURL.URL.getUrl() + "/PasswordInfo.php?userID="+userID;
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
            password = result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        userID = loginActivity.getId();
        task = new phpdo();
        task.execute(userID);


        final Button updatePasswordButton2 = (Button) findViewById(R.id.updatePasswordButton2);
        final EditText nowPasswordText = (EditText) findViewById(R.id.nowPasswordText);
        final EditText newPasswordText = (EditText) findViewById(R.id.newPasswordText);
        final EditText newPasswordText2 = (EditText) findViewById(R.id.newPasswordText2);
        updatePasswordButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                nowPassword = nowPasswordText.getText().toString();
                userPassword = newPasswordText.getText().toString();
                userPassword2 = newPasswordText2.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePasswordActivity.this);
                                dialog = builder.setMessage("수정되었습니다..")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                Intent intent = new Intent(UpdatePasswordActivity.this, MainActivity.class);
                                UpdatePasswordActivity.this.startActivity(intent);

                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePasswordActivity.this);
                                dialog = builder.setMessage("수정에 실패했습니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                System.out.println("db 패스워드 : "+password);
                System.out.println("입력한 패스워드"+nowPassword);
                System.out.println("바꾸는거1"+userPassword);
                System.out.println("바꾸는거2"+userPassword2);
                if (nowPassword.equals(password) && userPassword.equals(userPassword2) ){

                    PasswordRequest passwordRequest = new PasswordRequest(userID, userPassword, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(UpdatePasswordActivity.this);
                    queue.add(passwordRequest);

                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePasswordActivity.this);
                    dialog = builder.setMessage("수정에 실패했습니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                }
            }
        });

    }
}
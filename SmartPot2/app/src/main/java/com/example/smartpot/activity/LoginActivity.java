package com.example.smartpot.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.enums.ServerURL;
import com.example.smartpot.requests.LoginRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {

    private AlertDialog dialog;
    private static int rowNum;
    private static String id="";

    private String id2 = ""; //로그인 정보 저장을 위한 id
    private String pwd = ""; //로그인 정보 저장을 위한 pwd

    PhpDo task;
    LoginButton loginButton;
    CallbackManager callbackManager;

    private SharedPreferences appData;
    private boolean saveLoginData;

    @NotEmpty(message = "아이디를 입력해주세요")
    private EditText idText;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    private EditText passwordText;
    private Button loginButton2;
    private TextView registerButton;
    private CheckBox checkBox;
    private boolean validationCheck = false;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginButton2 = (Button) findViewById(R.id.loginButton);
        registerButton = (TextView) findViewById(R.id.registerButton);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        validator = new Validator(this);
        validator.setValidationListener(this);
        //로그인 정보 유지 check한 후 로그인 성공 시 다음 로그인부터 실행
        if (saveLoginData) {
            idText.setText(id2);
            passwordText.setText(pwd);
            checkBox.setChecked(saveLoginData);
        }

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        loginButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                final String userId = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                id = userId;

                task = new PhpDo();
                task.execute(userId);

                validator.validate();
                if(validationCheck) return;

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success) {
                                save(); //로그인 성공 시 id, pwd 저장
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("로그인에 성공했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();

                                if(rowNum == 0)  {
                                    Intent intent = new Intent(LoginActivity.this, FlowerRegisterActivity.class);
                                    LoginActivity.this.startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(intent);
                                }

                            }

                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("계정을 다시 확인하세요.")
                                        .setNegativeButton("다시 시도", null)
                                        .create();
                                dialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userId, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            }


        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        initializeControls();
        loginWithFB();

    }

    //id, pwd를 저장
    private void save() {
        //SharedPreferences 객체만으로는 저장 불가능하여 Editor사용
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", idText.getText().toString().trim());
        editor.putString("PWD", passwordText.getText().toString().trim());
        editor.apply();
    }
    //id, pwd를 load
    private void load() {
        //SharedPreferences 객체.get타입
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        id2 = appData.getString("ID", "");
        pwd = appData.getString("PWD", "");
    }

    public void initializeControls(){
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.faceBookButton);

    }

    private void loginWithFB(){
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
            }

            @Override
            public void onCancel() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                dialog = builder.setMessage("계정을 다시 확인하세요.")
                        .setNegativeButton("다시 시도", null)
                        .create();
                dialog.show();
            }

            @Override
            public void onError(FacebookException error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                dialog = builder.setMessage("계정을 다시 확인하세요.")
                        .setNegativeButton("다시 시도", null)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void onValidationSucceeded() {
        validationCheck = false;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if(view instanceof EditText) ((EditText)view).setError(message);
            else Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        }
        validationCheck = true;

    }


    private class PhpDo extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg) {
            try{
                String userID = arg[0];
                String link = ServerURL.URL.getUrl() + "/PotRow.php?userID="+userID;
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

                String s = sb.toString();
                rowNum = Integer.valueOf(s);

                return sb.toString();
            } catch (Exception e) {
                return new String("Exception" + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            rowNum = Integer.valueOf(result);
        }

    }
}
package com.example.smartpot.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smartpot.R;
import com.example.smartpot.requests.FlowerRegisterRequest;
import com.example.smartpot.requests.PotCodeRequest;
import com.example.smartpot.requests.PotRequest;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONObject;

import java.util.List;

public class FlowerRegisterActivity extends AppCompatActivity implements Validator.ValidationListener {

    private ArrayAdapter adapter;
    private Spinner spinner;
    private String flower;
    private LoginActivity loginActivity = new LoginActivity();
    private boolean validate = false;
    private static String potSerial = "";
    private AlertDialog dialog;
    private String potCode;
    private String potName;
    private String userID = loginActivity.getId();


    @NotEmpty(message = "Serial Code를 입력해주세요")
    private EditText potCodeText;
    @NotEmpty(message = "화분 이름을 입력해주세요")
    private EditText potNameText;

    private Validator validator;
    private boolean validationCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_register);

        potCodeText = (EditText) findViewById(R.id.potCodeText);
        potNameText = (EditText) findViewById(R.id.potNameText);

        final Button potCodeButton = (Button) findViewById(R.id.potCodeButton);

        validator = new Validator(this);
        validator.setValidationListener(this);

        potCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String potCode = potCodeText.getText().toString();
                if (validate) {
                    return;
                }
                if (potCode.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FlowerRegisterActivity.this);
                    dialog = builder.setMessage("화분코드는 빈 칸일 수 없습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FlowerRegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 화분코드입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                potCodeText.setEnabled(false);
                                validate = true;
                                potCodeText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                potCodeButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FlowerRegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 화분코드입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                PotCodeRequest potCodeRequest = new PotCodeRequest(potCode, responseListener);
                RequestQueue queue = Volley.newRequestQueue(FlowerRegisterActivity.this);
                queue.add(potCodeRequest);
            }
        });

        spinner = (Spinner) findViewById(R.id.flowerSpinner);

        adapter = ArrayAdapter.createFromResource(this, R.array.flower, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button flowerRegisterButton = (Button) findViewById(R.id.flowerRegisterButton);

        flowerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String potNameID = potCodeText.getText().toString();
                flower = spinner.getSelectedItem().toString();
                potCode = potCodeText.getText().toString();
                potName = potNameText.getText().toString();

                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FlowerRegisterActivity.this);
                    dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                validator.validate();
                if(validationCheck) return;

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                potSerial = potNameID;
                                AlertDialog.Builder builder = new AlertDialog.Builder(FlowerRegisterActivity.this);
                                builder.setMessage("완료");
                                builder.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                                Intent mainIntent = new Intent(FlowerRegisterActivity.this, MainActivity.class);
                                FlowerRegisterActivity.this.startActivity(mainIntent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FlowerRegisterActivity.this);
                                builder.setMessage("실패");
                                builder.setNegativeButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                PotRequest potRequest = new PotRequest(potCode, potName, userID, responseListener);
                RequestQueue queue2 = Volley.newRequestQueue(FlowerRegisterActivity.this);
                queue2.add(potRequest);

                FlowerRegisterRequest flowerRegisterRequest = new FlowerRegisterRequest(flower, userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(FlowerRegisterActivity.this);
                queue.add(flowerRegisterRequest);





            }
        });
    }
    public static String getPotSerial() {
        return potSerial;
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
}

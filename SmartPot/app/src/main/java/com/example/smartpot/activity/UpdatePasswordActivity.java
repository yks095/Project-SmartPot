package com.example.smartpot.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.smartpot.R;

public class UpdatePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        final Button updatePasswordButton2 = (Button) findViewById(R.id.updatePasswordButton2);

        updatePasswordButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(UpdatePasswordActivity.this, MainActivity.class);
                UpdatePasswordActivity.this.startActivity(addIntent);
            }
        });

    }
}

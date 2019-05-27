package com.example.smartpot.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.example.smartpot.fragments.ManageFragment;
import com.example.smartpot.R;
import com.example.smartpot.fragments.ClimateFragment;
import com.example.smartpot.fragments.DictionaryFragment;
import com.example.smartpot.fragments.MemberFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends FragmentActivity {

    private ManageFragment manageFragment;
    private ClimateFragment climateFragment;
    private DictionaryFragment dictionaryFragment;
    private MemberFragment memberFragment;

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

    }

    // 뒤로가기 버튼 클릭시 이벤트 구현
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "뒤로가기 버튼을 누르셨습니다!", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    public void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentContainer, manageFragment);
        transaction.commit();
    }


}
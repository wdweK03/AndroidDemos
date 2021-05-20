package com.nekoneko.nekonekodemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import fragment.ProcessViewFragment;

import butterknife.ButterKnife;
import fragment.SwipeMenuFragment;


/**
 * Created by 想法的猫 on 2017/11/6 0006.
 */

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
        int action = getIntent().getIntExtra("action", 0);
        Fragment fragment = null;
        switch (action) {
            case R.id.processView:
                fragment = new ProcessViewFragment();
                break;
            case R.id.swipeMenuView:
                fragment = new SwipeMenuFragment();
                break;

        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}

package com.nekoneko.nekonekodemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.nekoneko.nekonekodemo.fragment.DrawableStateViewFragment;
import com.nekoneko.nekonekodemo.fragment.ProcessViewFragment;

import butterknife.ButterKnife;

import com.nekoneko.nekonekodemo.fragment.SwipeMenuFragment;
import com.nekoneko.nekonekodemo.fragment.VideoCallFragment;


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
            case R.id.drawableStateView:
                fragment = new DrawableStateViewFragment();
                break;
            case R.id.video_call_fragment:
                fragment = new VideoCallFragment();
                break;

        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}

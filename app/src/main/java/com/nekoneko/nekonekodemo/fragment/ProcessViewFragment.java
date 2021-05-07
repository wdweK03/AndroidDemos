package com.nekoneko.nekonekodemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nekoneko.nekonekodemo.R;


/**
 * 进度条
 * Created by 想法的猫 on 2017/11/6 0006.
 */

public class ProcessViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_process_view, container, false);
        return view;
    }

}

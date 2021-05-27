package com.nekoneko.nekonekodemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.nekoneko.nekonekodemo.R;
import com.nekoneko.nekonekodemo.chat.VideoCallActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 想法的猫 on 2017/11/17 0017.
 */

public class VideoCallFragment extends Fragment implements View.OnClickListener {
    Unbinder unbinder;
    @BindView(R.id.userName)
    EditText userName;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.toUserName)
    EditText toUserName;
    @BindView(R.id.video_call)
    Button videoCall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_call, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.login, R.id.video_call})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                String userNameValue = userName.getText().toString();
                if (TextUtils.isEmpty(userNameValue)) {
                    return;
                }
                EMClient.getInstance().login(userNameValue, "123456", new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        getView().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "LoginSuccess", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, final String s) {
                        getView().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                break;
            case R.id.video_call:
                String toUserNameValue = toUserName.getText().toString();
                if (TextUtils.isEmpty(toUserNameValue)) {
                    return;
                }
                startVideoCall(toUserNameValue);
                break;
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall(String toUserName) {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(getContext(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(getContext(), VideoCallActivity.class).putExtra("username", toUserName)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
        }
    }
}

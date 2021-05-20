package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nekoneko.nekonekodemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 滑动菜单
 * Created by 想法的猫 on 2017/11/7 0007.
 */

public class SwipeMenuFragment extends Fragment {
    @BindView(R.id.menu)
    TextView menu;
    @BindView(R.id.menu2)
    TextView menu2;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_menu, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.menu, R.id.menu2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                break;
            case R.id.menu2:
                break;
        }
    }
}

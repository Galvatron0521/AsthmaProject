package com.shenkangyun.asthmaproject.SurveyFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shenkangyun.asthmaproject.BeanFolder.MessageEvent;
import com.shenkangyun.asthmaproject.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TwoFragment extends Fragment {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.option_group)
    RadioGroup optionGroup;
    @BindView(R.id.option_A)
    RadioButton optionA;
    @BindView(R.id.option_B)
    RadioButton optionB;
    @BindView(R.id.option_C)
    RadioButton optionC;
    @BindView(R.id.option_D)
    RadioButton optionD;
    @BindView(R.id.option_E)
    RadioButton optionE;
    @BindView(R.id.option_F)
    RadioButton optionF;
    @BindView(R.id.option_G)
    RadioButton optionG;

    private String title = "2.平均来说,在过去一周里,当你早上醒来时,你的哮喘症状有多严重?";
    private String A = "0.没有症状";
    private String B = "1.很轻微的症状";
    private String C = "2.轻微的症状";
    private String D = "3.中等程度的症状";
    private String E = "4.较严重的症状";
    private String F = "5.严重的症状";
    private String G = "6.很严重的症状";

    private int Score = 9;
    private int position = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initView() {
        tvTitle.setText(title);
        optionA.setText(A);
        optionB.setText(B);
        optionC.setText(C);
        optionD.setText(D);
        optionE.setText(E);
        optionF.setText(F);
        optionG.setText(G);
    }

    private void initData() {
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.option_A:
                        Score = 0;
                        break;
                    case R.id.option_B:
                        Score = 1;
                        break;
                    case R.id.option_C:
                        Score = 2;
                        break;
                    case R.id.option_D:
                        Score = 3;
                        break;
                    case R.id.option_E:
                        Score = 4;
                        break;
                    case R.id.option_F:
                        Score = 5;
                        break;
                    case R.id.option_G:
                        Score = 6;
                        break;
                }
            }
        });

    }

    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                MessageEvent backEvent = new MessageEvent();
                backEvent.setStatus(0);
                backEvent.setDone(true);
                backEvent.setPosition(position - 1);
                EventBus.getDefault().post(backEvent);
                break;
            case R.id.btn_next:
                if (Score == 9) {
                    Toast.makeText(getContext(), "请选择一个选项", Toast.LENGTH_SHORT).show();
                } else {
                    MessageEvent nextEvent = new MessageEvent();
                    nextEvent.setStatus(1);
                    nextEvent.setDone(false);
                    nextEvent.setScore(Score);
                    nextEvent.setPosition(position);
                    EventBus.getDefault().post(nextEvent);
                }
                break;
        }
    }
}

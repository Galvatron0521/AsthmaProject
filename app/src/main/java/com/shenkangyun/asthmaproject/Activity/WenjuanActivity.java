package com.shenkangyun.asthmaproject.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.shenkangyun.asthmaproject.BeanFolder.MessageEvent;
import com.shenkangyun.asthmaproject.R;
import com.shenkangyun.asthmaproject.SurveyFragment.FiveFragment;
import com.shenkangyun.asthmaproject.SurveyFragment.FourFragment;
import com.shenkangyun.asthmaproject.SurveyFragment.OneFragment;
import com.shenkangyun.asthmaproject.SurveyFragment.ThreeFragment;
import com.shenkangyun.asthmaproject.SurveyFragment.TwoFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WenjuanActivity extends AppCompatActivity {

    @BindView(R.id.toolBar_title)
    TextView toolBarTitle;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.layout_public)
    FrameLayout layoutPublic;

    private FragmentManager fragmentManager;
    private List<Fragment> fragments = new ArrayList<>();
    private Fragment oneFragment, twoFragment, threeFragment, fourFragment, fiveFragment, replaceFragment;

    private String average;
    private int totalScore = 0;
    private boolean isFinish = true;
    private List<Integer> numList = new ArrayList<>();
    private List<Integer> scoreList = new ArrayList<>();
    private List<Integer> positionList = new ArrayList<>();

    private double bad = 1.5;
    private double good = 0.75;
    private String Result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wenjuan);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.toolBar));
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            toolBarTitle.setText("问卷");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        initView();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        fragmentManager = getSupportFragmentManager();

        oneFragment = new OneFragment();
        twoFragment = new TwoFragment();
        threeFragment = new ThreeFragment();
        fourFragment = new FourFragment();
        fiveFragment = new FiveFragment();

        fragments.add(oneFragment);
        fragments.add(twoFragment);
        fragments.add(threeFragment);
        fragments.add(fourFragment);
        fragments.add(fiveFragment);
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layout_public, oneFragment).commit();
        replaceFragment = oneFragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        scoreList.add(event.getScore());
        positionList.add(event.getPosition());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (event.getStatus() == 0) {
            switch (event.getPosition()) {
                case 0:
                    isFinish = event.isDone();
                    replaceFragment(oneFragment, fragmentTransaction);
                    break;
                case 1:
                    isFinish = event.isDone();
                    replaceFragment(twoFragment, fragmentTransaction);
                    break;
                case 2:
                    isFinish = event.isDone();
                    replaceFragment(threeFragment, fragmentTransaction);
                    break;
                case 3:
                    isFinish = event.isDone();
                    replaceFragment(fourFragment, fragmentTransaction);
                    break;
            }

        } else {
            switch (event.getPosition()) {
                case 0:
                    isFinish = event.isDone();
                    replaceFragment(twoFragment, fragmentTransaction);
                    break;
                case 1:
                    isFinish = event.isDone();
                    replaceFragment(threeFragment, fragmentTransaction);
                    break;
                case 2:
                    isFinish = event.isDone();
                    replaceFragment(fourFragment, fragmentTransaction);
                    break;
                case 3:
                    isFinish = event.isDone();
                    replaceFragment(fiveFragment, fragmentTransaction);
                    break;
            }
        }
    }

    public void replaceFragment(Fragment showFragment, FragmentTransaction fragmentTransaction) {
        if (showFragment.isAdded()) {
            fragmentTransaction.hide(replaceFragment).show(showFragment).commit();
        } else {
            fragmentTransaction.hide(replaceFragment).add(R.id.layout_public, showFragment).commit();
        }
        replaceFragment = showFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.tv_commit, R.id.btn_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                initFragment();
                break;
            case R.id.tv_commit:
                if (!isFinish) {
                    for (int i = 0; i < positionList.size(); i++) {
                        //如果集合里面没有相同的元素才往里存
                        if (!numList.contains(positionList.get(i))) {
                            numList.add(positionList.get(i));
                            totalScore = totalScore + scoreList.get(i);
                        }
                    }

                    double decimal = (double) totalScore / 5;
                    average = String.format("%.1f", decimal);

                    BigDecimal goodBig = new BigDecimal(good);
                    BigDecimal badBig = new BigDecimal(bad);
                    BigDecimal decimalBig = new BigDecimal(decimal);

                    if (decimalBig.compareTo(goodBig) < 0) {
                        Result = "您的评分为:" + average + ",您的哮喘得到良好控制";
                    } else if (decimalBig.compareTo(badBig) > 0) {
                        Result = "您的评分为:" + average + ",您的哮喘未得到控制";
                    } else {
                        Result = "您的评分为:" + average + ",您的哮喘得到控制";
                    }


                    AlertDialog dialog = new AlertDialog
                            .Builder(this)
                            .setTitle("问卷评分")//标题
                            .setMessage(Result)//内容
                            .setIcon(R.drawable.icon_average)//图标
                            .setCancelable(false)
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .create();
                    dialog.show();
                }
                break;
        }
    }
}

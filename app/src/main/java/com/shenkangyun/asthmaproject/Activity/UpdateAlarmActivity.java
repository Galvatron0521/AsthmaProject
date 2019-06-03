package com.shenkangyun.asthmaproject.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.jaeger.library.StatusBarUtil;
import com.shenkangyun.asthmaproject.DBFolder.AlarmDB;
import com.shenkangyun.asthmaproject.R;
import com.shenkangyun.asthmaproject.Utils.AlarmManagerUtil;
import com.shenkangyun.asthmaproject.View.SelectRemindCyclePopup;
import com.shenkangyun.asthmaproject.View.SelectRemindWayPopup;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolBar_title)
    TextView toolBarTitle;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.date_tv)
    TextView date_tv;
    @BindView(R.id.tv_repeat)
    TextView tvRepeat;
    @BindView(R.id.arrow_one)
    TextView arrowOne;
    @BindView(R.id.tv_repeat_value)
    TextView tv_repeat_value;
    @BindView(R.id.repeat_rl)
    RelativeLayout repeat_rl;
    @BindView(R.id.ring_tv)
    TextView ringTv;
    @BindView(R.id.arrow_two)
    TextView arrowTwo;
    @BindView(R.id.tv_ring_value)
    TextView tv_ring_value;
    @BindView(R.id.ring_rl)
    RelativeLayout ring_rl;
    @BindView(R.id.set_btn)
    TextView set_btn;
    @BindView(R.id.all_layout)
    LinearLayout allLayout;

    private TimePickerView pvTime;
    private String time;
    private int cycle;
    private int ring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_alarm);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.toolBar));
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            toolBarTitle.setText("提醒修改");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        initView();
    }

    private void initView() {
        set_btn.setOnClickListener(this);
        repeat_rl.setOnClickListener(this);
        ring_rl.setOnClickListener(this);

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                time = getTime(date);
                date_tv.setText(getTime(date));
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentTextSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("选择时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLACK)//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();

        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show();
            }
        });

    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(3, intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repeat_rl:
                selectRemindCycle();
                break;
            case R.id.ring_rl:
                selectRingWay();
                break;
            case R.id.set_btn:
                setClock();
                break;
            default:
                break;
        }
    }

    private void setClock() {

        int alarmID = 0;
        List<AlarmDB> all = LitePal.findAll(AlarmDB.class);

        if (0 != all.size()) {
            AlarmDB last = LitePal.findLast(AlarmDB.class);
            alarmID = last.getAlarmID();
        }

        if (time != null && time.length() > 0) {
            String[] times = time.split(":");
            if (cycle == 0) {//是每天的闹钟
                AlarmManagerUtil.setAlarm(this, 0, Integer.parseInt(times[0]), Integer.parseInt
                        (times[1]), alarmID + 1, 0, "您的闹铃响了", ring);
                AlarmDB alarmDB = new AlarmDB();
                alarmDB.setAlarmID(alarmID + 1);
                alarmDB.setHourNum(Integer.parseInt(times[0]));
                alarmDB.setMinNum(Integer.parseInt(times[1]));
                alarmDB.setSoundOrVibrator(ring);
                alarmDB.setFlag(0);
                alarmDB.setCycleNum(0);
                alarmDB.setEnabled(true);
                alarmDB.save();
            }
            if (cycle == -1) {//是只响一次的闹钟
                AlarmManagerUtil.setAlarm(this, 1, Integer.parseInt(times[0]), Integer.parseInt
                        (times[1]), alarmID + 1, 0, "您的闹铃响了", ring);
                AlarmDB alarmDB = new AlarmDB();
                alarmDB.setAlarmID(alarmID + 1);
                alarmDB.setHourNum(Integer.parseInt(times[0]));
                alarmDB.setMinNum(Integer.parseInt(times[1]));
                alarmDB.setSoundOrVibrator(ring);
                alarmDB.setFlag(1);
                alarmDB.setCycleNum(-1);
                alarmDB.setEnabled(true);
                alarmDB.save();
            }

            Intent intent = new Intent();
            setResult(1, intent);
            finish();
            Toast.makeText(this, "闹钟设置成功", Toast.LENGTH_LONG).show();
        }

    }


    public void selectRemindCycle() {
        final SelectRemindCyclePopup fp = new SelectRemindCyclePopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindCyclePopupListener(new SelectRemindCyclePopup
                .SelectRemindCyclePopupOnClickListener() {

            @Override
            public void obtainMessage(int flag, String ret) {
                switch (flag) {
                    case 8:
                        tv_repeat_value.setText("每天");
                        cycle = 0;
                        fp.dismiss();
                        break;
                    case 9:
                        tv_repeat_value.setText("只响一次");
                        cycle = -1;
                        fp.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    public void selectRingWay() {
        SelectRemindWayPopup fp = new SelectRemindWayPopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindWayPopupListener(new SelectRemindWayPopup
                .SelectRemindWayPopupOnClickListener() {

            @Override
            public void obtainMessage(int flag) {
                switch (flag) {
                    // 震动
                    case 0:
                        tv_ring_value.setText("震动");
                        ring = 0;
                        break;
                    // 铃声
                    case 1:
                        tv_ring_value.setText("铃声");
                        ring = 1;
                        break;
                    default:
                        break;
                }
            }
        });
    }
}

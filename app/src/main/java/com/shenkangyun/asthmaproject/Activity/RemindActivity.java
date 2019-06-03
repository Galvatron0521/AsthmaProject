package com.shenkangyun.asthmaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.shenkangyun.asthmaproject.Adapter.AlarmAdapter;
import com.shenkangyun.asthmaproject.DBFolder.AlarmDB;
import com.shenkangyun.asthmaproject.R;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RemindActivity extends AppCompatActivity {


    @BindView(R.id.toolBar_title)
    TextView toolBarTitle;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.clockRecycler)
    RecyclerView clockRecycler;

    private AlarmAdapter alarmAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.toolBar));
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            toolBarTitle.setText("服药提醒");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        initView();
        initDB();
    }

    private void initView() {
        alarmAdapter = new AlarmAdapter();
        layoutManager = new LinearLayoutManager(this);
        clockRecycler.setLayoutManager(layoutManager);
        clockRecycler.setAdapter(alarmAdapter);

    }

    private void initDB() {
        List<AlarmDB> alarmDBS = LitePal.findAll(AlarmDB.class);
        alarmAdapter.setNewData(alarmDBS);
        alarmAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(RemindActivity.this,UpdateAlarmActivity.class);
                startActivityForResult(intent,1);
            }
        });

        alarmAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(RemindActivity.this, "长按点击", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @OnClick(R.id.floatingButton)
    public void onViewClicked() {
        Intent intent = new Intent(this, AddAlarmActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) {
            initDB();
        }
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
}

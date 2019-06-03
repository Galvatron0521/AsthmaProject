package com.shenkangyun.asthmaproject.Adapter;

import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shenkangyun.asthmaproject.BaseFolder.MyApp;
import com.shenkangyun.asthmaproject.DBFolder.AlarmDB;
import com.shenkangyun.asthmaproject.R;

public class AlarmAdapter extends BaseQuickAdapter<AlarmDB, BaseViewHolder> {
    public AlarmAdapter() {
        super(R.layout.item_alarm, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, AlarmDB item) {
        StringBuilder builder = new StringBuilder();
        builder.append(item.getHourNum()).append(":").append(item.getMinNum());
        helper.setText(R.id.tv_time, builder.toString());
        switch (item.getCycleNum()) {
            case 0:
                helper.setText(R.id.tv_count, "每天");
                break;
            case -1:
                helper.setText(R.id.tv_count, "一次");
                break;
        }
        helper.addOnClickListener(R.id.layout_public);
        helper.addOnLongClickListener(R.id.layout_public);
        helper.setChecked(R.id.switchButton, item.isEnabled());
        Switch view = helper.getView(R.id.switchButton);
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(MyApp.getContext(), "" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

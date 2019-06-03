package com.shenkangyun.asthmaproject.Adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shenkangyun.asthmaproject.BeanFolder.NewsEntity;
import com.shenkangyun.asthmaproject.R;

public class KnowledgeAdapter extends BaseQuickAdapter<NewsEntity.DataBean.TechBean, BaseViewHolder> {
    public KnowledgeAdapter() {
        super(R.layout.item_knowledage, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsEntity.DataBean.TechBean item) {
        helper.setText(R.id.tv_title, item.getTitle());
        helper.addOnClickListener(R.id.tv_title);
    }
}

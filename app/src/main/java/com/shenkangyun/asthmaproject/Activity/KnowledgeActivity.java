package com.shenkangyun.asthmaproject.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.shenkangyun.asthmaproject.Adapter.KnowledgeAdapter;
import com.shenkangyun.asthmaproject.BaseFolder.Const;
import com.shenkangyun.asthmaproject.BeanFolder.NewsEntity;
import com.shenkangyun.asthmaproject.R;
import com.shenkangyun.asthmaproject.View.DividerItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class KnowledgeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolBar_title)
    TextView toolBarTitle;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    private KnowledgeAdapter knowledgeAdapter;
    private LinearLayoutManager layoutManager;

    private List<NewsEntity.DataBean.TechBean> techBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.toolBar));
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            toolBarTitle.setText("哮喘知识");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        initView();
        initData();
    }

    private void initView() {
        knowledgeAdapter = new KnowledgeAdapter();
        layoutManager = new LinearLayoutManager(this);
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(knowledgeAdapter);
        swipeLayout.setOnRefreshListener(this);
    }

    private void initData() {
        OkHttpUtils.get()
                .url(Const.URL_NEWS)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int id) {
                        swipeLayout.setRefreshing(false);
                        Gson gson = new Gson();
                        NewsEntity newsEntity = gson.fromJson(response, NewsEntity.class);
                        getNewsInfo(newsEntity);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                });
    }

    private void getNewsInfo(NewsEntity newsEntity) {
        List<NewsEntity.DataBean.TechBean> techList = new ArrayList<>();
        for (int i = 0; i < newsEntity.getData().getTech().size(); i++) {
            NewsEntity.DataBean.TechBean techBean = new NewsEntity.DataBean.TechBean();

            techBean.setLink(newsEntity.getData().getTech().get(i).getLink());
            techBean.setTitle(newsEntity.getData().getTech().get(i).getTitle());
            techBean.setCategory(newsEntity.getData().getTech().get(i).getCategory());

            techList.add(techBean);
            techBeans.add(techBean);
        }
        knowledgeAdapter.setNewData(techList);
        knowledgeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(KnowledgeActivity.this, KnowledgeInfoActivity.class);
                intent.putExtra("link", techBeans.get(position).getLink());
                intent.putExtra("category", techBeans.get(position).getCategory());
                startActivity(intent);
            }
        });
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
    public void onRefresh() {
        initData();
    }
}


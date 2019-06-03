package com.shenkangyun.asthmaproject.Activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.shenkangyun.asthmaproject.Adapter.MyAdapter;
import com.shenkangyun.asthmaproject.BeanFolder.Subject;
import com.shenkangyun.asthmaproject.R;
import com.shenkangyun.asthmaproject.View.DividerGridItemDecoration;
import com.shenkangyun.asthmaproject.Utils.GlideImageLoader;
import com.shenkangyun.asthmaproject.Utils.OnRecyclerItemClickListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolBar_title)
    TextView toolBarTitle;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.carousel)
    Banner carousel;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private Context mContext = MainActivity.this;

    private String[] titles = {"服药提醒", "问卷", "天气", "哮喘知识"};
    private Integer[] imageUrl = {R.drawable.img1, R.drawable.img2, R.drawable.img3};
    private int[] imageIds = {R.drawable.icon_fuyao, R.drawable.icon_wenjuan,
            R.drawable.icon_tianqi, R.drawable.icon_zhishi};
    private List<Subject> datas = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.toolBar));
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            toolBarTitle.setText("首页");
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        initData();
        initImgData();
        initView();
    }


    private void initData() {
        //初始化data
        for (int i = 0; i < titles.length; i++) {
            datas.add(new Subject(titles[i], imageIds[i]));
        }
    }

    private void initImgData() {
        List<Integer> imgUrls = Arrays.asList(imageUrl);
        carousel.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        carousel.setImageLoader(new GlideImageLoader());
        //设置图片集合
        carousel.setImages(imgUrls);
        //设置banner动画效果
        carousel.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        carousel.isAutoPlay(true);
        //设置轮播时间
        carousel.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        carousel.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        carousel.start();
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        myAdapter = new MyAdapter(datas, mContext);
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {

                if (datas.get(vh.getLayoutPosition()).getTitle().equals("服药提醒")) {
                    Intent intent = new Intent(MainActivity.this, RemindActivity.class);
                    startActivity(intent);
                }
                if (datas.get(vh.getLayoutPosition()).getTitle().equals("问卷")) {
                    Intent intent = new Intent(MainActivity.this, WenjuanActivity.class);
                    startActivity(intent);
                }
                if (datas.get(vh.getLayoutPosition()).getTitle().equals("天气")) {
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    startActivity(intent);
                }
                if (datas.get(vh.getLayoutPosition()).getTitle().equals("哮喘知识")) {
                    Intent intent = new Intent(MainActivity.this, KnowledgeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                //判断被拖拽的是否是前两个，如果不是则执行拖拽
                mItemTouchHelper.startDrag(vh);
                //获取系统震动服务
                Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
                vib.vibrate(70);
            }
        });

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            /**
             * 是否处理滑动事件 以及拖拽和滑动的方向 如果是列表类型的RecyclerView的只存在UP和DOWN，如果是网格类RecyclerView则还应该多有LEFT和RIGHT
             * @param recyclerView
             * @param viewHolder
             * @return
             */
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(datas, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(datas, i, i - 1);
                    }
                }
                myAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            /**
             * 重写拖拽可用
             * @return
             */
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            /**
             * 长按选中Item的时候开始调用
             *
             * @param viewHolder
             * @param actionState
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 手指松开的时候还原
             * @param recyclerView
             * @param viewHolder
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(0);
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}

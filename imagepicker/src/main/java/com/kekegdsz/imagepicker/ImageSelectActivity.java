package com.kekegdsz.imagepicker;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kekegdsz.imagepicker.adapter.BaseRecyclerAdapter;
import com.kekegdsz.imagepicker.adapter.ImageGridAdapter;
import com.kekegdsz.imagepicker.config.ImageConfig;
import com.kekegdsz.imagepicker.decoration.GridSpacingItemDecoration;
import com.kekegdsz.imagepicker.entity.LocalMedia;
import com.kekegdsz.imagepicker.entity.LocalMediaFolder;
import com.kekegdsz.imagepicker.utils.ScreenUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class ImageSelectActivity extends ImageBaseActivity implements BaseRecyclerAdapter.OnItemClickListener<LocalMedia> {

    private TextView tv_empty;
    private RecyclerView image_recycler;

    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private LocalMediaLoader mediaLoader;
    private ImageGridAdapter adapter;
    private final static int mSpanCount = 4;


    public static void start(final Activity context) {
        RxPermissions.getInstance(context)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean hasPermissions) {
                        if (hasPermissions) {//true表示获取权限成功（注意这里在android6.0以下默认为true）
                            context.startActivity(new Intent(context, ImageSelectActivity.class));
                        } else {
                            Toast.makeText(context, "无权限访问相册", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        findViews();
        init();

        mediaLoader = new LocalMediaLoader(this, ImageConfig.TYPE_ALL, false, 0, 0);
        readLocalMedia();
    }


    private void findViews() {
        tv_empty = findViewById(R.id.tv_empty);
        image_recycler = findViewById(R.id.image_recycler);
    }

    private void init() {
        adapter = new ImageGridAdapter(this, images);
        GridLayoutManager layoutManager = new GridLayoutManager(this, mSpanCount);
        //设置布局管理器
        image_recycler.setLayoutManager(layoutManager);
        image_recycler.setHasFixedSize(true);
        image_recycler.addItemDecoration(new GridSpacingItemDecoration(mSpanCount,
                ScreenUtils.dip2px(this, 2), false));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) image_recycler.getItemAnimator())
                .setSupportsChangeAnimations(false);
        //设置Adapter
        image_recycler.setAdapter(adapter);
        image_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                        //当屏幕停止滚动，加载图片
                        Glide.with(ImageSelectActivity.this).resumeRequests();
                        break;
                    case SCROLL_STATE_DRAGGING: // The RecyclerView is currently being dragged by outside input such as user touch input.
                        //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                        Glide.with(ImageSelectActivity.this).pauseRequests();
                        break;
                    case SCROLL_STATE_SETTLING: // The RecyclerView is currently animating to a final position while not under outside control.
                        //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                        Glide.with(ImageSelectActivity.this).pauseRequests();
                        break;
                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        adapter.setOnItemClickListener(this);
    }

    protected void readLocalMedia() {
        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    foldersList = folders;
                    LocalMediaFolder folder = folders.get(0);
                    folder.setChecked(true);
                    List<LocalMedia> localImg = folder.getImages();
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                    if (adapter != null) {
                        if (localImg.size() >= images.size()) {
                            images = localImg;
                        }
                        adapter.setDataSource(images);
                    }
                }
                tv_empty.setVisibility(images.size() > 0
                        ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }


    @Override
    public void onItemClick(View view, int position, LocalMedia data) {
        ImagePreviewActivity.start(this, (ArrayList<LocalMedia>) images, position);
    }
}

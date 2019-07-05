package com.kekegdsz.imagepicker;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.TextView;

import com.kekegdsz.imagepicker.adapter.ImageGridAdapter;
import com.kekegdsz.imagepicker.config.ImageConfig;
import com.kekegdsz.imagepicker.decoration.GridSpacingItemDecoration;
import com.kekegdsz.imagepicker.entity.LocalMedia;
import com.kekegdsz.imagepicker.entity.LocalMediaFolder;
import com.kekegdsz.imagepicker.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageSelectActivity extends ImageBaseActivity {

    private TextView tv_empty;
    private RecyclerView image_recycler;

    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private LocalMediaLoader mediaLoader;
    private ImageGridAdapter adapter;
    private final static int mSpanCount = 4;

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


}

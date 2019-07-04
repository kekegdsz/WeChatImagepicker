package com.kekegdsz.imagepicker;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kekegdsz.imagepicker.config.ImageConfig;
import com.kekegdsz.imagepicker.entity.LocalMedia;
import com.kekegdsz.imagepicker.entity.LocalMediaFolder;

import java.util.ArrayList;
import java.util.List;

public class ImageSelectActivity extends ImageBaseActivity {

    private TextView tv_empty;

    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private LocalMediaLoader mediaLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

        tv_empty = findViewById(R.id.tv_empty);

        mediaLoader = new LocalMediaLoader(this, ImageConfig.TYPE_IMAGE, false, 0, 0);
        readLocalMedia();
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
                    if (localImg.size() >= images.size()) {
                        images = localImg;
                    }
                }
                tv_empty.setVisibility(images.size() > 0
                        ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }
}

package com.kekegdsz.imagepicker.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kekegdsz.imagepicker.ImagePreviewActivity;
import com.kekegdsz.imagepicker.R;
import com.kekegdsz.imagepicker.anim.OptAnimationLoader;
import com.kekegdsz.imagepicker.config.ImageConfig;
import com.kekegdsz.imagepicker.config.ImageMimeType;
import com.kekegdsz.imagepicker.entity.LocalMedia;
import com.kekegdsz.imagepicker.utils.DateUtils;
import com.kekegdsz.imagepicker.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends BaseRecyclerAdapter<LocalMedia, ImageGridAdapter.ViewHolder> {

    private Animation animation;
    private List<LocalMedia> selectImages = new ArrayList<>();
    private int mimeType = ImageConfig.TYPE_ALL;

    public ImageGridAdapter(Context context, List<LocalMedia> mData) {
        super(context, mData);
        animation = OptAnimationLoader.loadAnimation(context, R.anim.modal_in);
    }

    @Override
    protected ViewHolder createVH(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(final ViewHolder holder, final LocalMedia localMedia) {
        final String path = localMedia.getPath();
        final String pictureType = localMedia.getPictureType();
        boolean gif = ImageMimeType.isGif(pictureType);
        holder.tv_isGif.setVisibility(gif ? View.VISIBLE : View.GONE);
        //初始化
        selectImage(holder, isSelected(localMedia), false);
        int mediaMimeType = ImageMimeType.isPictureType(pictureType);
        if (mimeType == ImageMimeType.ofAudio()) {
            holder.tv_duration.setVisibility(View.VISIBLE);
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.picture_audio);
            StringUtils.modifyTextViewDrawable(holder.tv_duration, drawable, 0);
        } else {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.video_icon);
            StringUtils.modifyTextViewDrawable(holder.tv_duration, drawable, 0);
            holder.tv_duration.setVisibility(mediaMimeType == ImageConfig.TYPE_VIDEO
                    ? View.VISIBLE : View.GONE);
        }
        boolean eqLongImg = ImageMimeType.isLongImg(localMedia);
        holder.tv_long_chart.setVisibility(eqLongImg ? View.VISIBLE : View.GONE);
        long duration = localMedia.getDuration();
        holder.tv_duration.setText(DateUtils.timeParse(duration));
        if (mimeType == ImageMimeType.ofAudio()) {
            holder.iv_picture.setImageResource(R.drawable.audio_placeholder);
        } else {
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            options.centerCrop();
            options.dontAnimate();
            options.placeholder(R.drawable.image_placeholder);
            Glide.with(mContext)
                    .asBitmap()
                    .load(path)
                    .apply(options)
                    .into(holder.iv_picture);
        }
        holder.ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如原图路径不存在或者路径存在但文件不存在
                if (!new File(path).exists()) {
                    Toast.makeText(mContext.getApplicationContext(), "文件已损坏", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                changeCheckboxState(holder, localMedia);
            }
        });

    }

    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected int findResById() {
        return R.layout.image_grid_item;
    }


    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isChecked = contentHolder.check.isSelected();
        if (isChecked) {
            for (LocalMedia media : selectImages) {
                if (media.getPath().equals(image.getPath())) {
                    selectImages.remove(media);
                    break;
                }
            }
        } else {
            selectImages.add(image);
            image.setNum(selectImages.size());
        }
        //通知点击项发生了改变
        notifyItemChanged(contentHolder.getAdapterPosition());
        selectImage(contentHolder, !isChecked, true);
    }


    /**
     * 选中的图片并执行动画
     *
     * @param holder
     * @param isChecked
     * @param isAnim
     */
    public void selectImage(ViewHolder holder, boolean isChecked, boolean isAnim) {
        holder.check.setSelected(isChecked);
        if (isChecked) {
            if (isAnim) {
                if (animation != null) {
                    holder.check.startAnimation(animation);
                }
            }
            holder.iv_picture.setColorFilter(ContextCompat.getColor
                    (mContext, R.color.image_overlay_true), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.iv_picture.setColorFilter(ContextCompat.getColor
                    (mContext, R.color.image_overlay_false), PorterDuff.Mode.SRC_ATOP);
        }
    }


    public static class ViewHolder extends BaseRecyclerViewHolder {
        ImageView iv_picture;
        TextView check;
        View contentView;
        LinearLayout ll_check;
        TextView tv_duration, tv_isGif, tv_long_chart;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            iv_picture = itemView.findViewById(R.id.iv_picture);
            check = itemView.findViewById(R.id.check);
            ll_check = itemView.findViewById(R.id.ll_check);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_isGif = itemView.findViewById(R.id.tv_isGif);
            tv_long_chart = itemView.findViewById(R.id.tv_long_chart);
        }
    }
}

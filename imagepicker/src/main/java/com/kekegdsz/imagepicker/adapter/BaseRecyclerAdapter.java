package com.kekegdsz.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected List<E> mData = new ArrayList<>();
    protected OnItemClickListener<E> mOnItemClickListener;

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public BaseRecyclerAdapter(Context context, List<E> mData) {
        this.mContext = context;
        this.mData = mData;

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(findResById(), parent, false);
        return createVH(view);
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, mData.get(position));
    }

    protected abstract void onBindViewHolder(VH holder, E E);

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    protected E getItemByPosition(int position) {
        return mData.get(position);
    }

    protected abstract VH createVH(View view);

    protected abstract int findResById();

    public void setOnItemClickListener(OnItemClickListener<E> listener) {
        mOnItemClickListener = listener;
    }

    public void setDataSource(List<E> dataSource) {
        this.mData = dataSource;
        notifyDataSetChanged();
    }

    /**
     * RecyclerView的item点击事件接口
     */
    public interface OnItemClickListener<E> {
        void onItemClick(View view, int position, E data);
    }

}

package com.itvillagerr.cameraroll.adapter.item.viewHolder;

import android.view.View;
import android.view.ViewGroup;

import com.itvillagerr.cameraroll.data.models.AlbumItem;
import com.itvillagerr.cameraroll.ui.ItemActivity;
import com.itvillagerr.cameraroll.util.ItemViewUtil;

public abstract class ViewHolder {

    View itemView;
    public AlbumItem albumItem;
    private int position;

    public ViewHolder(AlbumItem albumItem, int position) {
        this.albumItem = albumItem;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    ViewGroup inflatePhotoView(ViewGroup container) {
        ViewGroup v = ItemViewUtil.inflatePhotoView(container);
        v.setTag(albumItem.getPath());
        this.itemView = v;
        return v;
    }

    ViewGroup inflateVideoView(ViewGroup container) {
        ViewGroup v = ItemViewUtil.inflateVideoView(container);
        v.setTag(albumItem.getPath());
        this.itemView = v;
        return v;
    }

    void imageOnClick(View view) {
        try {
            ((ItemActivity) view.getContext()).imageOnClick();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public View getView(ViewGroup container) {
        if (itemView == null) {
            itemView = inflateView(container);
        }
        return itemView;
    }

    public abstract View inflateView(ViewGroup container);

    public void onDestroy() {
        this.itemView.setOnClickListener(null);
        this.itemView = null;
        this.albumItem = null;
    }

    public String getTag() {
        return albumItem.getPath();
    }

    //called when the viewHolder is shown after shared element transition
    public abstract void onSharedElementEnter();

    //called when shared element is about to start
    public abstract void onSharedElementExit(ItemActivity.Callback callback);
}

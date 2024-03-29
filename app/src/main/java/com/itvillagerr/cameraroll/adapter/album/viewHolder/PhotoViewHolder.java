package com.itvillagerr.cameraroll.adapter.album.viewHolder;

import android.view.View;
import android.widget.ImageView;

import com.itvillagerr.cameraroll.data.models.AlbumItem;

public class PhotoViewHolder extends AlbumItemHolder {

    public PhotoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void loadImage(final ImageView imageView, final AlbumItem albumItem) {
        super.loadImage(imageView, albumItem);
    }
}

package com.itvillagerr.cameraroll.adapter.item.viewHolder;

import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.davemorrissey.labs.subscaleview.decoder.ImageDecoder;
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;

import com.itvillagerr.cameraroll.R;
import com.itvillagerr.cameraroll.data.models.AlbumItem;
import com.itvillagerr.cameraroll.data.models.Photo;
import com.itvillagerr.cameraroll.imageDecoder.CustomImageDecoder;
import com.itvillagerr.cameraroll.imageDecoder.CustomRegionDecoder;
import com.itvillagerr.cameraroll.ui.ItemActivity;
import com.itvillagerr.cameraroll.util.ExifUtil;
import com.itvillagerr.cameraroll.util.ItemViewUtil;

public class PhotoViewHolder extends ViewHolder {

    private boolean imageViewWasBound = false;

    public PhotoViewHolder(AlbumItem albumItem, int position) {
        super(albumItem, position);
    }

    @Override
    public View inflateView(ViewGroup container) {
        ViewGroup v = super.inflatePhotoView(container);
        final View view = v.findViewById(R.id.subsampling);
        final View transitionView = itemView.findViewById(R.id.image);

        //hide transitionView, when config was changed
        if (albumItem instanceof Photo
                && ((Photo) albumItem).getImageViewSavedState() != null) {
            transitionView.setVisibility(View.INVISIBLE);
        }
        ItemViewUtil.bindTransitionView((ImageView) transitionView, albumItem);
        view.setVisibility(View.INVISIBLE);
        return v;
    }

    private void swapView(final boolean isReturning) {
        final View view = itemView.findViewById(R.id.subsampling);
        final View transitionView = itemView.findViewById(R.id.image);
        if (!isReturning) {
            view.setVisibility(View.VISIBLE);
            bindImageView(view, transitionView);
        } else {
            transitionView.setTranslationX(0);
            view.setVisibility(View.INVISIBLE);
            transitionView.setVisibility(View.VISIBLE);
        }
    }

    void bindImageView(View view, final View transitionView) {
        if (albumItem.error) {
            transitionView.setVisibility(View.VISIBLE);
            ItemViewUtil.bindTransitionView((ImageView) transitionView, albumItem);
            return;
        }

        if (imageViewWasBound) {
            return;
        }

        final SubsamplingScaleImageView imageView
                = (SubsamplingScaleImageView) view;

        // use custom decoders
        imageView.setBitmapDecoderClass(getImageDecoderClass());
        imageView.setRegionDecoderClass(getBitmapRegionDecoderClass());

        imageView.setMinimumTileDpi(196);
        imageView.setMinimumDpi(80);
        //imageView.setDoubleTapZoomDpi(196);
        imageView.setDoubleTapZoomScale(1.0f);

        //imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        int orientation = ExifUtil.getExifOrientationAngle(view.getContext(), albumItem);
        imageView.setOrientation(orientation);

        final GestureDetector gestureDetector
                = new GestureDetector(imageView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        PhotoViewHolder.super.imageOnClick(imageView);
                        return super.onSingleTapUp(e);
                    }
                });
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        ItemViewUtil.bindSubsamplingImageView(
                imageView,
                (Photo) albumItem,
                new SubsamplingScaleImageView.DefaultOnImageEventListener() {
                    @Override
                    public void onImageLoaded() {
                        super.onImageLoaded();
                        transitionView.setVisibility(View.INVISIBLE);
                        imageViewWasBound = true;
                        PhotoViewHolder.this.onImageLoaded();
                    }
                });
    }

    private void scaleDown(final ItemActivity.Callback callback) {
        final SubsamplingScaleImageView imageView = itemView.findViewById(R.id.subsampling);
        if (imageView != null) {
            try {
                imageView.animateScale(0.0f)
                        .withDuration(300)
                        .withOnAnimationEventListener(
                                new SubsamplingScaleImageView.DefaultOnAnimationEventListener() {
                                    @Override
                                    public void onComplete() {
                                        super.onComplete();
                                        swapView(true);
                                        callback.done();
                                        //imageView.recycle();
                                    }
                                })
                        .start();
            } catch (NullPointerException e) {
                swapView(true);
                callback.done();
                //imageView.recycle();
            }
        }
    }

    @Override
    public void onSharedElementEnter() {
        swapView(false);
    }

    @Override
    public void onSharedElementExit(final ItemActivity.Callback callback) {
        scaleDown(new ItemActivity.Callback() {
            @Override
            public void done() {
                callback.done();
            }
        });
    }

    @Override
    public void onDestroy() {
        final SubsamplingScaleImageView imageView = itemView.findViewById(R.id.subsampling);
        if (imageView != null) {
            imageView.recycle();
        }
        super.onDestroy();
    }


    @SuppressWarnings("WeakerAccess")
    public Class<? extends ImageDecoder> getImageDecoderClass() {
        return CustomImageDecoder.class;
    }

    public Class<? extends ImageRegionDecoder> getBitmapRegionDecoderClass() {
        return CustomRegionDecoder.class;
    }

    public void onImageLoaded() {

    }
}

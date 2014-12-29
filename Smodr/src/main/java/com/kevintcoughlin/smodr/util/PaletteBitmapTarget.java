package com.kevintcoughlin.smodr.util;

import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;

public class PaletteBitmapTarget extends ImageViewTarget<PaletteBitmapWrapper> {
    public PaletteBitmapTarget(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(PaletteBitmapWrapper paletteBitmapWrapper) {
        view.setImageBitmap(paletteBitmapWrapper.getBitmap());
    }
}

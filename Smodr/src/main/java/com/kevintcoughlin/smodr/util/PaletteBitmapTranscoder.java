package com.kevintcoughlin.smodr.util;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

public class PaletteBitmapTranscoder implements ResourceTranscoder<Bitmap, PaletteBitmapWrapper> {
    @Override
    public Resource<PaletteBitmapWrapper> transcode(Resource<Bitmap> bitmapResource) {
        Palette currentPalette;
        Bitmap currentBitmap;

        currentBitmap = bitmapResource.get();
        currentPalette = Palette.generate(currentBitmap);

        PaletteBitmapWrapper paletteBitmapWrapper = new PaletteBitmapWrapper(currentPalette, currentBitmap);

        return new SimpleResource<>(paletteBitmapWrapper);
    }

    @Override
    public String getId() {
        return PaletteBitmapTranscoder.class.getSimpleName();
    }
}
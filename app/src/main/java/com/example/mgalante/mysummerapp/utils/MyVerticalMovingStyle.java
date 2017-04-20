package com.example.mgalante.mysummerapp.utils;

import android.graphics.Canvas;
import android.widget.ImageView;

import com.gjiazhe.scrollparallaximageview.ScrollParallaxImageView;

/**
 * Created by mgalante on 20/04/17.
 */

public class MyVerticalMovingStyle implements ScrollParallaxImageView.ParallaxStyle {

    @Override
    public void onAttachedToImageView(ScrollParallaxImageView view) {
        // only supports CENTER_CROP
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public void onDetachedFromImageView(ScrollParallaxImageView view) {

    }

    @Override
    public void transform(ScrollParallaxImageView view, Canvas canvas, int x, int y) {
        if (view.getScaleType() != ImageView.ScaleType.CENTER_CROP) {
            return;
        }

        // image's width and height
        int iWidth = view.getDrawable().getIntrinsicWidth();
        int iHeight = view.getDrawable().getIntrinsicHeight();
        if (iWidth <= 0 || iHeight <= 0) {
            return;
        }

        // view's width and height
        int vWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
        int vHeight = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom();

        // device's height
        int dHeight = view.getResources().getDisplayMetrics().heightPixels;

        if (iWidth * vHeight < iHeight * vWidth) {
            // avoid over scroll
            if (y < -vHeight) {
                y = -vHeight;
            } else if (y > dHeight) {
                y = dHeight;
            }

            float imgScale = (float) vWidth / (float) iWidth;
            float max_dy = Math.abs((iHeight * imgScale - vHeight) * 0.9f);
            float translateY = -(20 * max_dy * y + max_dy * (vHeight - dHeight)) / (vHeight + dHeight);
            canvas.translate(0, translateY);
        }
    }
}

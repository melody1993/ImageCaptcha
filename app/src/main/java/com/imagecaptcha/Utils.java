package com.imagecaptcha;

import android.content.Context;

/**
 * Created by w
 */

public class Utils {

    public static int dp2px(Context ctx, float dip) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return (int) (dip * density);
    }
}

package aahl.quickinv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hoko.blur.HokoBlur;

import aahl.quickinv.R;

public class BackBlurHandler {

    private static Bitmap blurredBackground;

    public static Bitmap getBlurredBackground(Context context) {
        if (blurredBackground == null){

            Bitmap imgFondo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logisticaimagenstock);

            blurredBackground = HokoBlur.with(context)
                    .scheme(HokoBlur.SCHEME_NATIVE)
                    .mode(HokoBlur.MODE_STACK)
                    .radius(10)
                    .blur(imgFondo);
        }

        return blurredBackground;
    }

}

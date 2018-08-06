package base.nestedscrolltitlebar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewUtil {

    private ViewUtil() {
    }


    public static Drawable setColorDrawableFilter(Drawable drawable, int color) {
        if (drawable != null) {
            drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        return drawable;
    }

    public static void setColorForView(ImageView img, int color){
        if(img != null){
            Drawable drawable = img.getDrawable();
            img.setImageDrawable(setColorDrawableFilter(drawable, color));
        }
    }
}

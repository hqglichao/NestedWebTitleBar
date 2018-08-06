package base.nestedscrolltitlebar;

import android.os.Looper;

/**
 * Created by beyond on 18-8-6.
 */

public class Utils {
    public static boolean checkRunningMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

}

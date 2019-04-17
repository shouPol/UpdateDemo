package cn.humanetplan.updateappdemo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Android_2 on 2016/12/8.
 */
public class ToastUtils {
    public static Toast mToast;
    public static void showToast(Context context, String msg ){
//        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }

        mToast.show();
    }

}

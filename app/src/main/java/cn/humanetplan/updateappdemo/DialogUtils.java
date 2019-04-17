package cn.humanetplan.updateappdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


/**
 * Created by Administrator on 2018/5/11.
 */

public class DialogUtils {


    public static void ShowTipsDialog(final Activity context, String tit, String defaultContent, final DialogReturnListner listner) {

        final Dialog dialog = new Dialog(context, R.style.bleDialog);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_showtips, null);
        TextView tv_dialog_title = (TextView) v.findViewById(R.id.tv_dialog_title);
        TextView tv_ok = (TextView) v.findViewById(R.id.tv_dialog_ok);
        TextView tv_cancle = (TextView) v.findViewById(R.id.tv_dialog_cancel);
        final TextView tv_content = (TextView) v.findViewById(R.id.tv_content);

        tv_content.setText(defaultContent);
//        tv_content.setVisibility(View.GONE);
        tv_dialog_title.setText(TextUtils.isEmpty(tit) ? "提示:" : tit);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listner.onResultReturn(true, "");
                dialog.dismiss();

//                dialog.dismiss();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(v);
        dialog.setCancelable(false);

        SetDialogStyles(dialog, android.R.color.transparent, Gravity.CENTER, 0.8f, 0.7f, context);


    }


    public static void HideKeyBorad(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }


    public static void SetDialogStyles(Dialog dialog, int backResourceId, int location, float wRotes, float hRotes, Activity context) {
        dialog.getWindow().setBackgroundDrawableResource(backResourceId);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(location);

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        // lp.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * wRotes); // 宽度设置为屏幕的0.65
//        lp.verticalMargin= DensityUtil.dip2px(WeiZhiGuanLiActivity.this,20);
        dialogWindow.setAttributes(lp);
        dialog.show();

    }

    public static int getAndroiodScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        return height;
    }

    private static boolean isSoftShowing(Activity context) {
        //获取当前屏幕内容的高度
        int screenHeight = context.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom - getSoftButtonsBarHeight(context) != 0;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getSoftButtonsBarHeight(Activity mActivity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    class MyViewClickListener implements View.OnClickListener {
        EditText et;
        List<String> list;

        public MyViewClickListener(EditText et, List<String> list) {
            this.et = et;
            this.list = list;
        }

        @Override
        public void onClick(View v) {

        }
    }

}

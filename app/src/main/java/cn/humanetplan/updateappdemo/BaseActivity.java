package cn.humanetplan.updateappdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;




public abstract class BaseActivity extends AppCompatActivity {

    public ProgressDialog tipDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DoSthBeforeInflate();
        super.onCreate(savedInstanceState);
        //设置软件盘弹出后不挡住输入框
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(setContentLayout());
        initState();

        tipDialog = new ProgressDialog(this);
        tipDialog.setTitle("提示:");
        tipDialog.setMessage("正在加载中...请稍后");
        tipDialog.setCancelable(true);
        init();

    }

    /**
     * 需要执行在setcontentView之前的操作
     */
    public abstract void DoSthBeforeInflate();

    /**
     * 检测有没有权限
     * @param permissions 所有权限
     * @return
     */
    public List<String>[] CheckPermissions(@Size(min = 1) @NonNull String[] permissions) {

        List<String>[] lists = new List[2];
        List<String> p_deneid_need_show_reasons = new ArrayList<>();
        List<String> noPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                //有权限
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //用户拒绝了，且选择了不再提示
                    p_deneid_need_show_reasons.add(permission);

                } else {
                    //没有相关权限，用户也没有明确的拒绝，不再提示
                    noPermissions.add(permission);
                }
            }
        }
        lists[0] = p_deneid_need_show_reasons;
        lists[1] = noPermissions;
        if (lists[0].size() !=0 || lists[1].size() != 0){
            if (p_deneid_need_show_reasons.size()!=0){
                ShowDeniedPerMissions(p_deneid_need_show_reasons);
            }
            if (noPermissions.size()!=0){
                ShowShouldRequestPermissions(noPermissions);
            }
        }else {
            HasPermissionsNow();
        }

        return lists;
    }

    /**
     * 当前没有的权限，用户已经明确拒绝了，你可以在这里向用户解释为什么需要这个权限
     * @param pemissions
     */
    public void ShowDeniedPerMissions(List<String> pemissions){

    }

    /**
     * 需要向用户申请的权限（用户还没明确拒绝），在这里可以向用户发起权限请求
     * @param pemissions
     */
    public void ShowShouldRequestPermissions(List<String> pemissions){

    }

    /**
     * 这次要检测的所有权限都有了，可以执行app的正常流程
     */
    public void HasPermissionsNow(){

    }

    protected abstract int setContentLayout();

    protected abstract void init();

    protected void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 动态的设置状态栏  实现沉浸式状态栏
     */
    private void initState() {

        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
    }

    /**
     * 通过反射的方式获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void JumpToActivity(Class tClass) {
        Intent intent = new Intent(this, tClass);
        startActivity(intent);
    }


}

package cn.humanetplan.updateappdemo;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;




/**
 * 指定 自己的 application
 * Created by user on 2015/12/15.
 */
public class MyApp extends Application {

    private static int windowWidth;
    private static int windowHeight;
    private static int windowDen;
    private static float density;
    private static MyApp instance;


    @Override
    public void onCreate() {
        super.onCreate();
        initWindowMetrics();
        instance = this;



    }

    public static MyApp getInstance() {
        return instance;
    }


    /**
     * 测量 屏幕 宽度 和 高度
     */
    public void initWindowMetrics() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        defaultDisplay.getMetrics(outMetrics);
        windowWidth = outMetrics.widthPixels;
        windowHeight = outMetrics.heightPixels;
        windowDen = outMetrics.densityDpi;
        density = outMetrics.density;

    }


    public static int getWindowHeight() {
        return windowHeight;
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowDen() {
        return windowDen;
    }

    public static float getDensity() {
        return density;
    }

    public void resetAddress() {

    }
}

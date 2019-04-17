package cn.humanetplan.updateappdemo;

/**
 *各种弹窗的返回值处理
 * Created by Administrator on 2018/5/11.
 */

public interface DialogReturnListner {
    /**
     * 返回的全是字符串,自己取参数的时候记住相应的顺序
     * @param params
     */
    void onResultReturn(String... params);

    /**
     * 返回一个int和不定的字符串
     * @param p1
     * @param params
     */
    void onResultReturn(int p1, String... params);

    /**
     * 返回的是一个bool和不定字符串
     * @param p1
     * @param params
     */
    void onResultReturn(boolean p1, String... params);
}

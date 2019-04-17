package cn.humanetplan.updateappdemo;

/**
 * author   : 肖波
 * e-mail   : xiaoboabc168@163.com
 * date     :  2019/4/13.
 */

public class VersionBean {
    private String versionName;
    private String apkPath;
    private int versionCode;
    private String remark;
    private boolean success;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "{" +
                "'versionName':'" + versionName + '\'' +
                ", 'apkPath':'" + apkPath + '\'' +
                ", 'versionCode':'" + versionCode + '\'' +
                ", 'remark':'" + remark + '\'' +
                ", 'success':" + success +
                '}';
    }
}

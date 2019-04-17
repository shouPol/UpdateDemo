package cn.humanetplan.updateappdemo;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    TextView tv_check, tv_versionName;

    @Override
    public void DoSthBeforeInflate() {

    }

    @Override
    protected int setContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        tv_check = findViewById(R.id.tv_check);
        tv_versionName = findViewById(R.id.tv_versionName);
        tv_versionName.setText("当前版本V"+BuildConfig.VERSION_NAME);
        tv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckVersion();
            }
        });
    }
    VersionBean versionBean;
    private void CheckVersion() {
        tipDialog.show();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        httpUtils.send(HttpRequest.HttpMethod.GET, "http://47.107.173.197/Android/GetVersionInfo.php", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                tipDialog.dismiss();
                try {
                    Gson gson = new Gson();

                    versionBean = gson.fromJson(responseInfo.result, VersionBean.class);
                    if (versionBean != null && versionBean.isSuccess()) {
                        DialogUtils.ShowTipsDialog(MainActivity.this, "发现新版本，是否立即更新？", "当前版本V"+versionBean.getVersionName()+"\n"+versionBean.getRemark(), new DialogReturnListner() {
                            @Override
                            public void onResultReturn(String... params) {

                            }

                            @Override
                            public void onResultReturn(int p1, String... params) {

                            }

                            @Override
                            public void onResultReturn(boolean p1, String... params) {
                                if (p1) {
                                    if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                        StartUpdate(versionBean);
                                    }else {
                                        EasyPermissions.requestPermissions(MainActivity.this,"升级程序将App下载到手的过程中需要用到手机文件操作权限，请同意后才能进行正常升级",1234,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                    }


                                }
                            }
                        });
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                tipDialog.dismiss();
            }
        });

    }
    private void StartUpdate(VersionBean versionBean) {
        if (versionBean==null){
            return;
        }
        ToastUtils.showToast(MainActivity.this, "正在更新...");
        Intent intent=new Intent(MainActivity.this,ServiceLoadNewVersion.class);
        intent.putExtra("path",versionBean.getApkPath());
        startService(intent);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode==1234){
            if (perms.contains(Manifest.permission.READ_EXTERNAL_STORAGE) && perms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                StartUpdate(versionBean);
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showToast(this,"没有获取相关的权限，无法正常操作!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}

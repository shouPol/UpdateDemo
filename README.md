# UpdateDemo
兼容android7.0,8.0以上的APP版本升级demo
原理：

1.将新版本上传到自己的服务器，有服务器将最新版本信息记录

2.当用户打开app或者手动触发版本检查时向服务器请求版本信息以及最新版本apk的下载地址

3.判断当前版本是不是最新版本，如果不是则通过下载地址下载apk

4.下载完成后吊起安装程序进行安装覆盖

5.实现了版本更新

本次例子用到框架：

1.easypermissions  权限控制

2.gson  json对象解析

3.xUtils-2.6.14.jar 网络请求

所用权限（注意最后一个权限）：

 <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

    <!--以下这个权限如果不加，在高版本的android手机上如果没有开启运行未知来源的安装，将会在下载完成后无法调起安装程序-->
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

注意点：

 1."android.permission.REQUEST_INSTALL_PACKAGES"这个权限不行加上，否则可能会在apk下载完成后不能吊起安装程序（一闪而逝）

2.Android8以上如果要在通知栏显示下载进度，需要进行notification的适配
3.在Android6.0以上需要进行权限的申请

4.Android7.0以上文件读取需要通过FileProvider进行操作

package com.example.abu.updatedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.abu.update_library.IUpdateParser;
import com.example.abu.update_library.OnFailureListener;
import com.example.abu.update_library.UpdateError;
import com.example.abu.update_library.UpdateInfo;
import com.example.abu.update_library.UpdateManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void checkUpdate(View view) {
        UpdateManager.getInstance(this)
                .setCheckUrl("https://www.baidu.com/")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOnFailListener(new OnFailureListener() {
                    @Override
                    public void onFail(UpdateError updateError) {
                        Toast.makeText(MainActivity.this,updateError.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                .setUpdateParser(new IUpdateParser() {
                    @Override
                    public UpdateInfo parser(String updateInfo) {
                        UpdateInfo info = new UpdateInfo();
                        info.updateTitle="版本更新";
                        info.hasUpdate = true;
                        info.isForced=false;
                        info.updateContent = "• 支持文字、贴纸、背景音乐，尽情展现欢乐气氛；\n• 两人视频通话支持实时滤镜，丰富滤镜，多彩心情；\n• 图片编辑新增艺术滤镜，一键打造文艺画风；\n• 资料卡新增点赞排行榜，看好友里谁是魅力之王。• 支持文字、贴纸、背景音乐，尽情展现欢乐气氛；\n• 两人视频通话支持实时滤镜，丰富滤镜，多彩心情；\n• 图片编辑新增艺术滤镜，一键打造文艺画风；\n• 资料卡新增点赞排行榜，看好友里谁是魅力之王。";
                        info.versionCode = 587;
                        info.versionName = "v5.8.7";
                        info.downloadUrl = "https://dldir1.qq.com/dmpt/apkSet/8.5.9/qqcomic_android_8.5.9_dm2017.apk";
                        info.md5 = "56cf48f10e4cf6043fbf53bbbc4009e3";
                        info.size = 10149314;
                        return info;
                    }
                }).check();
    }
}

package com.chenya.android_app_launch_plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AndroidAppLaunchPlugin */
public class AndroidAppLaunchPlugin implements FlutterPlugin, MethodCallHandler {
  private static Context ctx;
  private final Object lock_obj=new Object();
  private Map<String,AppInfo> apps=new HashMap<>();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "android_app_launch_plugin");
    channel.setMethodCallHandler(new AndroidAppLaunchPlugin());
    ctx=flutterPluginBinding.getApplicationContext();
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "android_app_launch_plugin");
    channel.setMethodCallHandler(new AndroidAppLaunchPlugin());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    //System.out.println("onMethodCall");
    //Log.e("LogUtils","onMethodCall");
    if(call.method.equals("Init")){
      Init();
      result.success(null);
    }else if(call.method.equals("GetApps")){
      List<Map<String,Object>> out=GetApps();
      result.success(out);
    }else if(call.method.equals("LaunchApp")){
      String pkg_name=call.argument("pkg_name");

      LaunchApp(pkg_name);
      result.success(null);
    }else if(call.method.equals("RefreshApps")){
      RefreshApps();
      result.success(null);
    }
    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

  }

  private void Init(){
    if(ctx==null){
      return;
    }
    synchronized(lock_obj) {
      Map<String, AppInfo> apps = GetAppList(ctx);
      this.apps.clear();
      this.apps.putAll(apps);
    }
  }

  //启动app
  private void LaunchApp(String pkg_name){
    synchronized(lock_obj) {
      Intent intent = this.apps.get(pkg_name).getIntent();
      if (intent != null) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
      }
    }
  }

  private void RefreshApps(){
    if(ctx==null){
      return;
    }
    synchronized(lock_obj){
      Map<String,AppInfo> apps=GetAppList(ctx);
      this.apps.clear();
      this.apps.putAll(apps);
    }

  }

  //获得缓存的app列表
  private List<Map<String,Object>> GetApps(){
    List<Map<String,Object>> out=new ArrayList<>();
    for (AppInfo item:apps.values()) {
      out.add(item.getMap());
    }
    return out;
  }

  //缓存app列表
  private Map<String,AppInfo> GetAppList(Context context){
    Map<String,AppInfo> list=new HashMap();
    PackageManager pm = context.getPackageManager();
    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    List<ResolveInfo> activities   = pm.queryIntentActivities(mainIntent, 0);
    for(ResolveInfo info : activities){
      String packName = info.activityInfo.packageName;
      if(packName.equals(context.getPackageName())){
        continue;
      }
      AppInfo mInfo = new AppInfo();
      mInfo.setIco(info.activityInfo.applicationInfo.loadIcon(pm));
      mInfo.setName(info.activityInfo.applicationInfo.loadLabel(pm).toString());
      mInfo.setPackageName(packName);
      // 为应用程序的启动Activity 准备Intent
      Intent launchIntent = new Intent();
      launchIntent.setComponent(new ComponentName(packName,
              info.activityInfo.name));
      mInfo.setIntent(launchIntent);
      list.put(packName,mInfo);
    }
    return list;
  }
}

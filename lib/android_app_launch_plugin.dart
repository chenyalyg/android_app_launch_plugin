import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

class AppInfo{
  String packageName;
  String name;
  Image ico;
}

class AndroidAppLaunchPlugin {
  static const MethodChannel _channel =
      const MethodChannel('android_app_launch_plugin');


  static Future<void> Init() async {
    final void out = await _channel.invokeMethod('Init');
    return out;
  }

  static Future<List> GetApps() async {
    final List out = await _channel.invokeMethod('GetApps');
    List _out=new List();
    for(int i=0;i<out.length;i++){
      var app=new AppInfo();
      app.packageName=out[i]["packageName"];
      app.name=out[i]["name"];
      app.ico=Image.memory(out[i]["ico"]);
      _out.add(app);
    }

    return _out;
  }

  static Future<void> RefreshApps() async {
    _channel.invokeMethod('RefreshApps');
    return null;
  }

  static Future<void> LaunchApp(String pkg_name) async {
    _channel.invokeMethod('LaunchApp',{'pkg_name':pkg_name});

    return null;
  }
}

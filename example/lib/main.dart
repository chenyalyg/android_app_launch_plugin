import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:android_app_launch_plugin/android_app_launch_plugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}



class _MyAppState extends State<MyApp> {

  List<Widget> apps=[];
  @override
  void initState() {
    super.initState();
    initPlatformState();

  }

  void itemOnPressed(String pkg_name){
    AndroidAppLaunchPlugin.LaunchApp(pkg_name);
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    List<Widget> apps=new List();
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      AndroidAppLaunchPlugin.Init();
      Future<List> out_apps=AndroidAppLaunchPlugin.GetApps();
      out_apps.then((o){
        for(int i=0;i<o.length;i++){
          apps.add(IconButton(
            icon: o[i].ico,
            onPressed: ()=> itemOnPressed(o[i].packageName),
          ));
        }

        setState(() {
          this.apps=apps;
        });
      }).catchError((e){
        print("AndroidAppLaunchPlugin.GetApps:"+e);
        return;
      });
    } on PlatformException {
      return;
    }

  }

  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: GridView(
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 5,
                mainAxisSpacing: 30,
                childAspectRatio: 3
            ),
            padding: const EdgeInsets.only(left: 10,right: 10,top: 10),
            children: apps,
          ),
        ),
      ),
    );
  }
}
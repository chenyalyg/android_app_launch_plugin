import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:android_app_launch_plugin/android_app_launch_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('android_app_launch_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AndroidAppLaunchPlugin.platformVersion, '42');
  });
}

# AutoUpdate
按照Tinker原理用最少代码实现原理功能，手机型号需要是6.0


# 使用方法:
1, 把MainActivity中的hello修改成Hello word.  然后打包apk，得到helloWorld.apk， 运行该apk会看到页面显示hello world
2, 卸载安装了的helloWorld.apk.  把apk中的dex之外的东西删除后(压缩文件打开后直接删除即可)，得到只包含classes.dex的helloWorld.apk
3, 把第二步骤中的apk放到sdcard位置上/sdcard/TM/auto_update.apk [adb push helloWorld.apk /sdcard/TM/auto_update.apk]
4, 把第一步中修改的hello world, 修改成Hello. 然后运行项目，会看到页面显示的是hello world.

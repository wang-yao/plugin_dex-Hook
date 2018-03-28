Hook需要在Application里初始化，实现不在AndroidManifest.xml注册即可启动Activity <br>
HookUtil hookUtil = new HookUtil(MainActivity.class, this);<br>
        hookUtil.hookSystemHandler();<br>
        hookUtil.hookAms();<br>


自定义DexClassLoader实现从外部加载包含dex的apk，jar文件，通过反射创建对象

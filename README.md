Hook需要在Application里初始化，实现不在AndroidManifest.xml注册即可启动Activity <br>
HookUtil hookUtil = new HookUtil(MainActivity.class, this);<br>
        hookUtil.hookSystemHandler();<br>
        hookUtil.hookAms();<br>


自定义DexClassLoader实现从外部加载包含dex的apk，jar文件，通过反射创建对象。5.0以下系统外部dex和主dex中不能有共有的class，否则提示异常，可能是加载外部dex的class时如果有需要提前加载的与主dex共有的class，则主dex默认使用了系统的classloader，具体原因有待分析

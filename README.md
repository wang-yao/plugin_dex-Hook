Hook需要在Application里初始化 <br>
HookUtil hookUtil = new HookUtil(MainActivity.class, this);<br>
        hookUtil.hookSystemHandler();<br>
        hookUtil.hookAms();<br>

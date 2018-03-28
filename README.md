Hook需要在Application里初始化 <br>
HookUtil hookAmsUtil = new HookUtil(MainActivity.class, this);
        hookAmsUtil.hookSystemHandler();
        hookAmsUtil.hookAms();

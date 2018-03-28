package com.example.classloader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class HookUtil {
	private Class<?> proxyActivity;

	private Context context;

	public HookUtil(Class<?> proxyActivity, Context context) {
		this.proxyActivity = proxyActivity;
		this.context = context;
	}

	public void hookAms() {
		try {
			Class<?> ActivityManagerNativeClss = Class.forName("android.app.ActivityManagerNative");
			Field defaultFiled = ActivityManagerNativeClss.getDeclaredField("gDefault");
			defaultFiled.setAccessible(true);
			
			Object defaultValue = defaultFiled.get(null);
			
			Class<?> SingletonClass = Class.forName("android.util.Singleton");
			Field mInstance = SingletonClass.getDeclaredField("mInstance");
			mInstance.setAccessible(true);
			Object iActivityManagerObject = mInstance.get(defaultValue);

			Class<?> IActivityManagerIntercept = Class.forName("android.app.IActivityManager");

			AmsInvocationHandler handler = new AmsInvocationHandler(iActivityManagerObject);

			Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
					new Class<?>[] { IActivityManagerIntercept }, handler);

			mInstance.set(defaultValue, proxy);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class AmsInvocationHandler implements InvocationHandler {

		private Object iActivityManagerObject;

		private AmsInvocationHandler(Object iActivityManagerObject) {
			this.iActivityManagerObject = iActivityManagerObject;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			if ("startActivity".contains(method.getName())) {
				Intent intent = null;
				int index = 0;
				for (int i = 0; i < args.length; i++) {
					Object arg = args[i];
					if (arg instanceof Intent) {
						intent = (Intent) args[i];
						index = i;
					}
				}

				Intent proxyIntent = new Intent();
				ComponentName componentName = new ComponentName(context, proxyActivity);
				proxyIntent.setComponent(componentName);
				proxyIntent.putExtra("oldIntent", intent);
				args[index] = proxyIntent;
			}

			return method.invoke(iActivityManagerObject, args);
		}
	}

	public void hookSystemHandler() {
		try {

			Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
			Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
			currentActivityThreadMethod.setAccessible(true);
			Object activityThread = currentActivityThreadMethod.invoke(null);
			Field mH = activityThreadClass.getDeclaredField("mH");
			mH.setAccessible(true);
			Handler handler = (Handler) mH.get(activityThread);
			Field mCallBack = Handler.class.getDeclaredField("mCallback");
			mCallBack.setAccessible(true);
			mCallBack.set(handler, new ActivityThreadHandlerCallback(handler));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ActivityThreadHandlerCallback implements Handler.Callback {
		private Handler handler;

		private ActivityThreadHandlerCallback(Handler handler) {
			this.handler = handler;
		}

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 100) {
				handleLauchActivity(msg);
			}

			handler.handleMessage(msg);
			return true;
		}

		private void handleLauchActivity(Message msg) {
			Object obj = msg.obj;
			try {
				Field intentField = obj.getClass().getDeclaredField("intent");
				intentField.setAccessible(true);
				Intent proxyInent = (Intent) intentField.get(obj);
				Intent realIntent = proxyInent.getParcelableExtra("oldIntent");
				if (realIntent != null) {
					proxyInent.setComponent(realIntent.getComponent());
				}
			} catch (Exception e) {
			}
		}
	}
}

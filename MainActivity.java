package com.example.classloader;


import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.example.interlib.Inter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

public class MainActivity extends ActionBarActivity {
	String TAG = "tag";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
    //测试hook
		Intent intent=  new Intent(MainActivity.this, TwoActivity.class);
		startActivity(intent);
		
		ClassLoader classLoader = getClassLoader();
        if (classLoader != null){
            
			Log.e(TAG, "[onCreate] classLoader " +" : " + classLoader.toString());
            while (classLoader.getParent()!=null){
                classLoader = classLoader.getParent();
                Log.e(TAG,"[onCreate] classLoader " + " : " + classLoader.toString());
            }
        }
        
        findViewById(R.id.click).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Class pro = DexClassLoader(MainActivity.this);
		        Object obj = null;
				try {
					obj = pro.newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
//		        try {
//					Method read = pro.getMethod("read", null);
//					String readstr = (String)read.invoke(obj, null);
//					 Toast.makeText(MainActivity.this, readstr+"", Toast.LENGTH_LONG).show();
//				} catch (NoSuchMethodException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (SecurityException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				
				
//		        InvocationHandler stuHandler = new ProxyInvocationHandler<Inter>((Inter) obj);
//		        
//		        Object stuProxy =  Proxy.newProxyInstance(Inter.class.getClassLoader(), new Class<?>[]{Inter.class}, stuHandler);

//
//		        String read = stuProxy.read();
//		        boolean wri = stuProxy.wirter();
//		        
		       
			}
		});
        
        
        
	}
	class ProxyInvocationHandler<T> implements InvocationHandler{
		T target;
	    
	    public ProxyInvocationHandler(T target) {
	       this.target = target;
	    }
	   
	    @Override
	    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	       
	        Object result = method.invoke(target, args);
	        return result;
	    }
	}
	@SuppressLint("NewApi")
	public Class  DexClassLoader(Context context) {  
        // dex压缩文件的路径（可以是apk,jar,zip格式）  
        String dexPath = Environment.getExternalStorageDirectory().toString() + File.separator + "plugin.apk";  
  
        File dexOutputDir = context.getDir("dex", 0);
//        final File optimizedDexOutputPath = getDir("outdex",
//				Context.MODE_PRIVATE);
         
        // 定义DexClassLoader  
        // 第一个参数：是dex压缩文件的路径  
        // 第二个参数：是dex解压缩后存放的目录  
        // 第三个参数：是C/C++依赖的本地库文件目录,可以为null  
        // 第四个参数：是上一级的类加载器  
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath,dexOutputDir.getAbsolutePath(),null,getClassLoader());  
//        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, optimizedDexOutputPath.getAbsolutePath(), null,getClassLoader());
  
        Class libProvierClazz = null;  
        try {  
            libProvierClazz = dexClassLoader.loadClass("com.example.plugin.InterTwo");  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return libProvierClazz;
    }  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

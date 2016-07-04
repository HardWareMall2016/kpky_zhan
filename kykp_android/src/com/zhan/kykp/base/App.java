package com.zhan.kykp.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.jpush.android.api.JPushInterface;

public class App extends Application {
	public static App ctx;
	public static boolean debug = true;
	
	@Override
	public void onCreate() {
		super.onCreate();
		ctx = this;
		initImageLoader(getApplicationContext());
		JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
		JPushInterface.init(this);
	}


	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
}

package com.zhan.kykp.exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 捕获全局异常,因为有的异常我们捕获不到
 * 
 * @author river
 * 
 */
public class UncaughtException implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	// CrashHandler 实例
	private static UncaughtException INSTANCE = new UncaughtException();

	// 程序的 Context 对象
	private static Context mContext;

	// 系统默认的 UncaughtException 处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 保证只有一个 CrashHandler 实例 */
	private UncaughtException() {
	}

	/** 获取 CrashHandler 实例 ,单例模式 */
	public static UncaughtException getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;

		// 获取系统默认的 UncaughtException 处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

		// 设置该 CrashHandler 为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当 UncaughtException 发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}

			// 退出程序,注释下面的重启启动程序代码
			android.os.Process.killProcess(android.os.Process.myPid());
			// System.exit(1);
			// 　　 // 重新启动程序，注释上面的退出程序
		}
	}

	/**
	 * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
	 * 
	 * @param ex
	 * @return true：如果处理了该异常信息；否则返回 false
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}


		// 收集设备参数信息
		 collectDeviceInfo(mContext);
		// // 保存日志文件
		 saveCrashInfo2File(ex);
		/*new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendEmail(ex);

			}
		}).start();*/

		return true;
	}


	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);

			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中 *
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".log";

			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = "/sdcard/ZHAN/crash/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}

			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}

		return null;
	}

	/**
	 * send
	 * 
	 * @throws
	 */
	public static void sendEmail(Throwable e) {
		Log.i("fuck", "?");
            StackTraceElement[] trace = e.getStackTrace();
            StringBuffer sb = new StringBuffer();
            for (int i=0; i < trace.length; i++)
            	sb.append("\tat " + trace[i]+"\r\n");

		 MailSenderInfo mailInfo = new MailSenderInfo();
		 mailInfo.setMailServerHost("smtp.tom.com");
		 mailInfo.setMailServerPort("25");
		 mailInfo.setValidate(true);
		 mailInfo.setUserName("crash_@tom.com");
		 mailInfo.setPassword("123456");//您的邮箱密码
		 mailInfo.setFromAddress("crash_@tom.com");
		 mailInfo.setToAddress("xianshuo.liu@zhan.com");
		 mailInfo.setSubject(new Build().MODEL+" : "+e.toString());
		 mailInfo.setContent("设置邮箱内容"+sb);
		 //这个类主要来发送邮件
		 SimpleMailSender sms = new SimpleMailSender();
		 sms.sendTextMail(mailInfo);//发送文体格式
//		 sms.sendHtmlMail(mailInfo);//发送html格式

	}

}

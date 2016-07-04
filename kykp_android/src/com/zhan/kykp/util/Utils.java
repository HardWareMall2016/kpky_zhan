package com.zhan.kykp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zhan.kykp.R;
import com.zhan.kykp.base.App;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.bean.BaseBean;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static AlertDialog.Builder getBaseDialogBuilder(Activity activity, String s) {
        return getBaseDialogBuilder(activity).setMessage(s);
    }

    public static int getWindowWidth(Activity cxt) {
        int width;
        DisplayMetrics metrics = cxt.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        return width;
    }

    public static long getLongByTimeStr(String begin) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");
        String origin = "00:00:00.00";
        Date parse = format.parse(begin);
        return parse.getTime() - format.parse(origin).getTime();
    }

    public static String getEquation(int finalNum, int delta) {
        String equation;
        int abs = Math.abs(delta);
        if (delta >= 0) {
            equation = String.format("%d+%d=%d", finalNum - delta, abs, finalNum);
        } else {
            equation = String.format("%d-%d=%d", finalNum - delta, abs, finalNum);
        }
        return equation;
    }

    public static Uri getCacheUri(String path, String url) {
        Uri uri = Uri.parse(url);
        uri = Uri.parse("cache:" + path + ":" + uri.toString());
        return uri;
    }

    public static void showInfoDialog(Activity cxt, String msg, String title) {
        AlertDialog.Builder builder = getBaseDialogBuilder(cxt);
        builder.setMessage(msg)
                .setPositiveButton(cxt.getString(R.string.utils_right), null)
                .setTitle(title)
                .show();
    }

    public static AlertDialog.Builder getBaseDialogBuilder(Activity ctx) {
        return new AlertDialog.Builder(ctx).setTitle(R.string.utils_tips).setIcon(R.drawable.utils_icon_info);
    }

    public static String getStrByRawId(Context ctx, int id) throws UnsupportedEncodingException {
        InputStream is = ctx.getResources().openRawResource(id);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "gbk"));
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void showInfoDialog(Activity cxt, int msgId, int titleId) {
        showInfoDialog(cxt, cxt.getString(msgId), cxt.getString(titleId));
    }

    public static void notifyMsg(Context cxt, Class<?> toClz, int titleId, int msgId, int notifyId) {
        notifyMsg(cxt, toClz, cxt.getString(titleId), null, cxt.getString(msgId), notifyId);
    }

    public static String getTodayDayStr() {
        String dateStr;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateStr = sdf.format(new Date());
        return dateStr;
    }

    public static Ringtone getDefaultRingtone(Context ctx, int type) {

        return RingtoneManager.getRingtone(ctx,
                RingtoneManager.getActualDefaultRingtoneUri(ctx, type));

    }

    public static Uri getDefaultRingtoneUri(Context ctx, int type) {
        return RingtoneManager.getActualDefaultRingtoneUri(ctx, type);
    }

    public static boolean isEmpty(Activity activity, String str, String prompt) {
        if (str.isEmpty()) {
            toast(activity, prompt);
            return true;
        }
        return false;
    }

    public static String getWifiMac(Context cxt) {
        WifiManager wm = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo().getMacAddress();
    }

    public static String quote(String str) {
        return "'" + str + "'";
    }

    public static String formatString(Context cxt, int id, Object... args) {
        return String.format(cxt.getString(id), args);
    }

    public static void notifyMsg(Context context, Class<?> clz, String title, String ticker, String msg, int notifyId) {
        int icon = context.getApplicationInfo().icon;
        PendingIntent pend = PendingIntent.getActivity(context, 0,
                new Intent(context, clz), 0);
        Notification.Builder builder = new Notification.Builder(context);
        if (ticker == null) {
            ticker = msg;
        }
        builder.setContentIntent(pend)
                .setSmallIcon(icon)
                .setWhen(System.currentTimeMillis())
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true);
        NotificationManager man = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        man.notify(notifyId, builder.getNotification());
    }

    public static void sleep(int partMilli) {
        try {
            Thread.sleep(partMilli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setLayoutTopMargin(View view, int topMargin) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
                view.getLayoutParams();
        lp.topMargin = topMargin;
        view.setLayoutParams(lp);
    }

    public static List<?> getCopyList(List<?> ls) {
        List<?> l = new ArrayList(ls);
        return l;
    }

    public static void fixAsyncTaskBug() {
        // android bug
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }
        }.execute();
    }

    public static void openUrl(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static Bitmap getCopyBitmap(Bitmap original) {
        Bitmap copy = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), original.getConfig());
        Canvas copiedCanvas = new Canvas(copy);
        copiedCanvas.drawBitmap(original, 0f, 0f, null);
        return copy;
    }

    public static Bitmap getEmptyBitmap(int w, int h) {
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    public static void intentShare(Context context, String title, String shareContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.utils_share));
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.utils_please_choose)));
    }

    public static void toast(int id) {
        toast(App.ctx, id);
    }

    public static void toast(String s) {
        toast(App.ctx, s);
    }

    public static void toast(String s, String exceptionMsg) {
        if (App.debug) {
            s = s + exceptionMsg;
        }
        toast(s);
    }

    public static void toast(int resId, String exceptionMsg) {
        String s = App.ctx.getString(resId);
        toast(s, exceptionMsg);
    }

    public static void toast(Context cxt, int id) {
        Toast.makeText(cxt, id, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(Context cxt, int id) {
        Toast.makeText(cxt, id, Toast.LENGTH_LONG).show();
    }

    public static void toastLong( String s) {
        Toast.makeText(App.ctx, s, Toast.LENGTH_LONG).show();
    }

    //这个仅判断正整数，如果需要判断正负整数，正则表达式相应修改为 ^-?[0-9]+
    //如果要判断全部数字，正则表达式需要修改为 -?[0-9]+.?[0-9]+
    public static boolean isNumeric(String str) {
        if(TextUtils.isEmpty(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static ProgressDialog showHorizontalDialog(Activity activity) {
        //activity = modifyDialogContext(activity);
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        if (activity.isFinishing() == false) {
            dialog.show();
        }
        return dialog;
    }

    public static int currentSecs() {
        int l;
        l = (int) (new Date().getTime() / 1000);
        return l;
    }

    public static String md5(String string) {
        byte[] hash = null;
        try {
            hash = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh,UTF-8 should be supported?", e);
        }
        return computeMD5(hash);
    }

    public static String computeMD5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input, 0, input.length);
            byte[] md5bytes = md.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < md5bytes.length; i++) {
                String hex = Integer.toHexString(0xff & md5bytes[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean doubleEqual(double a, double b) {
        return Math.abs(a - b) < 1E-8;
    }

  /*public static String getPrettyDistance(double distance) {
    if (distance < 1000) {
      int metres = (int) distance;
      return String.valueOf(metres) + App.ctx.getString(R.string.discover_metres);
    } else {
      String num = String.format("%.1f", distance / 1000);
      return num + App.ctx.getString(R.string.utils_kilometres);
    }
  }*/

    public static ProgressDialog showSpinnerDialog(Activity activity) {
        //activity = modifyDialogContext(activity);
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setMessage(App.ctx.getString(R.string.utils_hardLoading));
        if (!activity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    public static boolean filterException(Exception e) {
        if (e != null) {
            toast(e.getMessage());
            return false;
        } else {
            return true;
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
        }
    }

    public static final SimpleDateFormat sFullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getFullFormateTimeStr(long time) {
        // SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        String timeStr = sFullDateFormat.format(date);
        return timeStr;
    }

    public static String getBetweenDateFormate(long time) {
        Date date = new Date(time);
        String timeStr = sDateFormat.format(date);
        return timeStr;
    }

    
    /**
     * 将GMT Unix时间戳转换为系统默认时区的Unix时间戳
     * @param gmtUnixTime GMT Unix时间戳
     * @return 系统默认时区的Unix时间戳
     * */
    public static long getCurrentTimeZoneUnixTime(long gmtUnixTime)
    {
        return gmtUnixTime*1000 + TimeZone.getDefault().getRawOffset();
    }
    
    
    
    public static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getDateFormateStr(long time) {
        Date date = new Date(time);
        String timeStr = sDateFormat.format(date);
        return timeStr;
    }
    

    public static final SimpleDateFormat sDateFormatYear = new SimpleDateFormat("yyyy");

    public static String getDateFormateStrYear(long time) {
        Date date = new Date(time);
        String timeStr = sDateFormatYear.format(date);
        return timeStr;
    }

    public static final SimpleDateFormat sDateFormatMouth = new SimpleDateFormat("MMM", new Locale("English"));

    public static String getDateFormateStrMouth(long time) {
        Date date = new Date(time);
        String timeStr = sDateFormatMouth.format(date);
        return timeStr;
    }

    public static final SimpleDateFormat sDateFormatDay = new SimpleDateFormat("dd");

    public static String getDateFormateStrDay(long time) {
        Date date = new Date(time);
        String timeStr = sDateFormatDay.format(date);
        return timeStr;
    }

    public static final SimpleDateFormat sDateFormatWeek = new SimpleDateFormat("E", new Locale("English"));

    public static String getDateFormateStrWeek(long time) {
        Date date = new Date(time);
        String timeStr = sDateFormatWeek.format(date);
        return timeStr;
    }

    /**
     * 解压缩一个文件
     *
     * @param zipFile 要解压的压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */

    /**
     * 解压一个压缩文档 到指定位置
     *
     * @param zipFileString 压缩包的名字
     * @param outPathString 指定的路径
     * @throws IOException
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws IOException {
        PathUtils.checkAndMkdirs(outPathString);

        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFileString));
        java.util.zip.ZipEntry zipEntry;
        String szName = "";

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                java.io.File folder = new java.io.File(outPathString + java.io.File.separator + szName);
                folder.mkdirs();
            } else {

                java.io.File file = new java.io.File(outPathString + java.io.File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                java.io.FileOutputStream out = new java.io.FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }

        inZip.close();

    }

    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public static void saveBitmap(Bitmap bmp, String savePath) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(savePath);
            bmp.compress(CompressFormat.PNG, 40, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 剪裁圆形头像
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPX = bitmap.getWidth() / 2;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return outBitmap;
    }


    public static void hideSoftInputFromWindow(View view) {
        InputMethodManager imm = (InputMethodManager) App.ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int getStatusBarHeight(Context ctx) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = ctx.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Log.i("BaseActivity", "statusBarHeight = " + statusBarHeight);
        return statusBarHeight;
    }

    /**
     * 下载并安装app
     * @param url
     */
    public static void installApp(String url) {
        final DownloadManager systemService = (DownloadManager) App.ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "beikao_upgrade.apk");
        systemService.enqueue(request);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
                myDownloadQuery.setFilterById(reference);

                Cursor myDownload = systemService.query(myDownloadQuery);
                if (myDownload.moveToFirst()) {
                    int fileUriIdx = myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);

                    String fileUri = myDownload.getString(fileUriIdx);

                    Intent ViewInstallIntent = new Intent(Intent.ACTION_VIEW);
                    ViewInstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ViewInstallIntent.setDataAndType(Uri.parse(fileUri), "application/vnd.android.package-archive");
                    context.startActivity(ViewInstallIntent);
                }
                myDownload.close();

                App.ctx.unregisterReceiver(this);
            }
        };
        App.ctx.registerReceiver(receiver, filter);
    }

    
    /***
     * 装换Json
     * @param json
     * @param beanClass
     * @return 如果getStatus==0 或 Json 转换出错 返回 null
     */
    public static  <T> T parseJson(String json ,Class<T> beanClass) {
    	Gson gson = new Gson();
    	try{
    		//如果就是转换BaseBean
    		if(beanClass.equals(BaseBean.class)){
    			T statusBean=gson.fromJson(json, beanClass);
    			return statusBean;
    		}
    	}catch(JsonSyntaxException exp){
			Log.e("Utils", "json String error : "+exp.getMessage());
			return null;
		}
		
		T bean=null;
		try{
    		BaseBean statusBean=gson.fromJson(json, BaseBean.class);
    		if(statusBean.getStatus()==0){
    			Log.e("Utils", "statusBean == 0 ");
    			return null;
    		}
			bean=gson.fromJson(json, beanClass);
		}catch(JsonSyntaxException exp){
			Log.e("Utils", "fromJson error : "+exp.getMessage());
		}
		return bean;
    }


    /**
     * 得到全局唯一UUID
     */
    public static String getUUID(Context context){
        final String UUID_KEY="UUID";
        String uuid=ShareUtil.getValue(context, UUID_KEY);

        if(TextUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            ShareUtil.setValue(context,UUID_KEY,uuid);
        }
        return uuid;
    }

    /**
     * 用户id-hashid(能标识这台手机的唯一id) 例如：20000-fsdflsjdfklsdjf
     * @param context
     * @return
     */
    public static String getQAuthor(Context context) {
        UserInfo user = UserInfo.getCurrentUser();
        if (user != null) {
            return user.getObjectId() + "-" + getUUID(context);
        } else {
            return "0-" + getUUID(context);
        }
    }

    private static Pattern phoneNumPattern = Pattern.compile("[1]\\d{10}");

    public static boolean checkMobilePhoneNumber(String phoneNumber) {
        String telRegex = "[1]\\d{10}";
        if (TextUtils.isEmpty(phoneNumber)) return false;
        else return phoneNumber.matches(telRegex);
    }

    private static Pattern postCodePattern = Pattern.compile("^\\d{6}$");

    public static boolean checkPostCodeNumber(String postcodeNumber) {
        return postCodePattern.matcher(postcodeNumber).find();
    }


    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}

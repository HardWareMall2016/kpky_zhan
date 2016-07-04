package com.zhan.kykp.util;

import com.zhan.kykp.R;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogUtils {
	public static Dialog getProgressDialog(Activity context, String summary) {

		final Dialog dialog = new Dialog(context, R.style.LoadingDialog);
		dialog.setContentView(R.layout.progress_dialog_view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		lp.width = (int) (0.6 * screenW);

		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
		titleTxtv.setText(summary);
		
		
		return dialog;
	}
	
	public static Dialog getCircleProgressDialog(Activity context, String summary) {

		final Dialog dialog = new Dialog(context, R.style.Dialog);
		dialog.setContentView(R.layout.progress_dialog_circle_view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		lp.width = (int) (0.6 * screenW);

		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
		titleTxtv.setText(summary);
		
		
		return dialog;
	}
	
	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
}

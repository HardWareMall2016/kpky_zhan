package com.zhan.kykp.userCenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.message.PrivateLetterActivity;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.network.bean.UserInfoAvatarBean;
import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;
import com.zhan.kykp.widget.ActionSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserInfoActivity extends BaseActivity implements
		View.OnClickListener {
	private final int PicCode = 256;
	private final int TakePicCode = 257;
	private int photoMode;
	private ImageView mAvatar;
	private Uri takePhotoUri;
	private TextView phonenum;
	private TextView nickname;
	private TextView userinfo_age ;
	private TextView userinfo_sex ;

	private String imagePath;
	private ProgressBar progressBar;
	private String sex ;

	private String mAvatarLocalPath;
	private BaseHttpRequest mHttpRequest;
	private RequestHandle mRequestHandle;
	private RequestHandle mSexRequesHandle ;

	private final static String USERID = "user";
	private final static String AUATERFILE = "avatar";
	private final static String SEX = "sex";

	@Override
	protected void onDestroy() {
		release(mRequestHandle);
		release(mSexRequesHandle);
		super.onDestroy();
	}

	private void release(RequestHandle request) {
		if (request != null && !request.isFinished()) {
			request.cancel(true);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);
		mAvatarLocalPath = PathUtils.getUserAvatarPath(UserInfo.getCurrentUser().getObjectId());

		initView();
	}


	private void initView() {
		findViewById(R.id.head).setOnClickListener(this);
		findViewById(R.id.nick).setOnClickListener(this);
		findViewById(R.id.phone).setOnClickListener(this);
		findViewById(R.id.passwd).setOnClickListener(this);
		if (UserInfo.getCurrentUser().getUserType() == LoginType.PHONE) {
			findViewById(R.id.passwd).setVisibility(View.VISIBLE);
        } else {
			findViewById(R.id.passwd).setVisibility(View.GONE);
        }
        findViewById(R.id.age).setOnClickListener(this);
		findViewById(R.id.sex).setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		mAvatar = (ImageView) findViewById(R.id.userinfo_avatar);
		nickname = (TextView) findViewById(R.id.nickname);
		phonenum = (TextView) findViewById(R.id.phone_number);
		userinfo_age = (TextView) findViewById(R.id.userinfo_age) ;
		userinfo_sex = (TextView) findViewById(R.id.userinfo_sex);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(TextUtils.isEmpty(UserInfo.getCurrentUser().getMobilePhoneNumber())){
			phonenum.setText(R.string.user_info_please_binding_phone);
		}else{
			phonenum.setText(UserInfo.getCurrentUser().getMobilePhoneNumber());
		}
		nickname.setText(UserInfo.getCurrentUser().getNickname());
		userinfo_age.setText(UserInfo.getCurrentUser().getAge());
		userinfo_sex.setText(UserInfo.getCurrentUser().getSex());
		setAvatar();
	}

	private void setAvatar() {
		UserInfo user = UserInfo.getCurrentUser();
		String avatarPath = user.getAvatar();
		ImageLoader.getInstance().displayImage(avatarPath, mAvatar,
				PhotoUtils.buldDisplayImageOptionsForAvatar());
	}

	/**
	 * 相册获取
	 */
	protected void getImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");// 相片类型
		startActivityForResult(intent, PicCode);
	}

	private void takePic() {
		// 创建拍照意图
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// 指明图片的保存路径,相机拍照后，裁剪前，会先保存到该路径下；裁剪时，再从该路径加载图片
		// 若无这句，则拍照后，图片会放入内存中，从而由于占用内存太大导致无法剪切或者剪切后无法保存
		camera.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
		startActivityForResult(camera, TakePicCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == PicCode) {
			photoMode = PicCode;
			Uri uri = data.getData();
			takePhotoUri = uri;
			cropImage(uri, 80, 80, 250);

		} else if (requestCode == TakePicCode) {
			photoMode = TakePicCode;
			cropImage(takePhotoUri, 80, 80, 250);
		} else if (requestCode == 250) {
			Bitmap photo = null;
			Bundle extra = data.getExtras();
			if (extra != null) {
				photo = (Bitmap) extra.get("data");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				mAvatar.setImageBitmap(photo);
				final UserInfo user = UserInfo.getCurrentUser();
				try {
					if (photoMode == PicCode) {
						takePhotoUri = Uri.fromFile(new File(mAvatarLocalPath));
						saveBitmap(takePhotoUri, photo);
						imagePath = (takePhotoUri.getPath());
					} else if (photoMode == TakePicCode) {
						saveBitmap(takePhotoUri, photo);
						imagePath = (takePhotoUri.getPath());
					}
					progressBar.setVisibility(View.VISIBLE);

					// 上传头像
					mHttpRequest = new BaseHttpRequest();
					RequestParams requestParams = new RequestParams();
					requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
					requestParams.put(AUATERFILE, new File(imagePath));
					mRequestHandle = mHttpRequest.startRequest(
							getApplicationContext(), ApiUrls.USER_UPDATEAVATAR,
							requestParams, requestCallback,
							BaseHttpRequest.RequestType.POST);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private HttpRequestCallback requestCallback = new HttpRequestCallback() {

		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
		}

		@Override
		public void onRequestSucceeded(String content) {

			UserInfoAvatarBean baseBean = Utils
					.parseJson(content, UserInfoAvatarBean.class);
			if (baseBean.getStatus() == 0) {
				Utils.toast(baseBean.getMessage());
			} else {
				UserInfo userInfo = UserInfo.getCurrentUser();
				userInfo.setAvatar(baseBean.getDatas().getAvatar());
				UserInfo.saveLoginUserInfo(userInfo);
				progressBar.setVisibility(View.GONE);
				Utils.toast(baseBean.getMessage());

				// 清除头像缓存
				String avatarPath = UserInfo.getCurrentUser().getAvatar();
				MemoryCacheUtils.removeFromCache(avatarPath, ImageLoader
						.getInstance().getMemoryCache());
				DiskCacheUtils.removeFromCache(avatarPath, ImageLoader
						.getInstance().getDiskCache());
				
				setAvatar();
			}
		}

	};

	/**
	 * 裁剪图片
	 * 
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	private void cropImage(Uri uri, int outputX, int outputY, int requestCode) {

		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");

		// 制定待裁剪的 image 所在路径 uri
		intent.setDataAndType(uri, "image/*");

		// 意图的 为 crop(裁剪) true
		intent.putExtra("crop", "true");

		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// 裁剪后，输出图片的尺寸
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);

		// 图片格式
		intent.putExtra("outputFormat", "JPEG");

		// 取消人脸识别功能
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, requestCode);
	}

	protected String getAbsoluteImagePath(Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, // Which columns to return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	/**
	 * 将bitmap 保存到sd卡
	 * 
	 * @param uri
	 * @param bitmap
	 */
	public void saveBitmap(Uri uri, Bitmap bitmap) {
		File file = new File(uri.getPath());
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream);
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.head:
			StatisticUtils.onEvent(R.string.user_info, R.string.user_info_head);
			new AlertDialog.Builder(UserInfoActivity.this).setItems(
					new String[] { "相机", "相册" },
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface,
								int i) {
							switch (i) {
							case 0:
								takePhotoUri = Uri.fromFile(new File(
										mAvatarLocalPath));
								takePic();
								break;
							case 1:
								getImageFromAlbum();
								break;
							}
						}
					}).show();
			break;
		case R.id.nick:
			StatisticUtils.onEvent(R.string.user_info, R.string.user_info_nickname);
			startActivity(new Intent(UserInfoActivity.this,
					ModifyNickActivity.class));
			break;
		case R.id.phone:
			StatisticUtils.onEvent(R.string.user_info, R.string.user_info_phone_number);
			Intent intent = new Intent(UserInfoActivity.this,
					ModifyPhoneActivity.class);
			startActivity(intent);
			break;
		case R.id.passwd:
			StatisticUtils.onEvent(R.string.user_info, R.string.user_info_loginpass);
			startActivity(new Intent(UserInfoActivity.this,
					ModifyPasswdActivity.class));
			break;
			case R.id.age:
				StatisticUtils.onEvent(R.string.user_info, R.string.user_info_age);
				startActivity(new Intent(UserInfoActivity.this,
						ModifyAgeActivity.class));
				break;
			case R.id.sex:
				new ActionSheetDialog(UserInfoActivity.this)
						.builder()
						.setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.addSheetItem(getString(R.string.sex_male), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener(){
							public void onClick(int which) {
								userinfo_sex.setText(getString(R.string.sex_male));
								sex = userinfo_sex.getText().toString() ;
								setNickSex(sex);
							}
						}).addSheetItem(getString(R.string.sex_female), ActionSheetDialog.SheetItemColor.Blue,new ActionSheetDialog.OnSheetItemClickListener() {
					public void onClick(int which) {
						userinfo_sex.setText(getString(R.string.sex_female));
						sex = userinfo_sex.getText().toString() ;
						setNickSex(sex);
					}
				}).show();

				break;
		}
	}

	private void setNickSex(String sex) {
		mHttpRequest = new BaseHttpRequest();
		RequestParams requestParams=new RequestParams();
		requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
		requestParams.put(SEX, sex);
		mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.USER_UPDATESEX, requestParams, sexrequestCallback, BaseHttpRequest.RequestType.POST);
	}

	private HttpRequestCallback sexrequestCallback = new HttpRequestCallback(){

		@Override
		public void onRequestFailed(String errorMsg) {
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
		}

		@Override
		public void onRequestSucceeded(String content) {
			BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
			if (baseBean.getStatus() == 0) {
			} else {
				UserInfo userInfo = UserInfo.getCurrentUser();
				userInfo.setSex(sex);
				UserInfo.saveLoginUserInfo(userInfo);
			}
			Utils.toast(baseBean.getMessage());
		}
	};

}

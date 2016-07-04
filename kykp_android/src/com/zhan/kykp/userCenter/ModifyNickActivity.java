package com.zhan.kykp.userCenter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.BaseBean;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class ModifyNickActivity extends BaseActivity implements View.OnClickListener{

    private ImageView avatarImage;
    private EditText nameEdit;
    
    private BaseHttpRequest mHttpRequest ;
    private RequestHandle mRequestHandle;
    
    private final static String USERID = "user";
    private final static String NICKNAME = "nickname";
    
    private Dialog mProgressDlg;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick);
        initView();
    }

    private void initView() {
        avatarImage = (ImageView) findViewById(R.id.avatar);
        nameEdit = (EditText) findViewById(R.id.phone_number);
        nameEdit.setText(UserInfo.getCurrentUser().getNickname());
        findViewById(R.id.btn_next).setOnClickListener(this);
        setAvatar();
    }

    
    @Override
	protected void onDestroy() {
    	release(mRequestHandle);
		super.onDestroy();
	}
    private void release(RequestHandle request) {
    	if(request!=null && !request.isFinished()){
			request.cancel(true);
		}
	}

	@Override
    public void onClick(View view) {
        if (checkedUser()) {
			StatisticUtils.onEvent(R.string.title_activity_modify_nick, R.string.finish);
            UserInfo user = UserInfo.getCurrentUser();
            user.setNickname(nameEdit.getText().toString().trim());
            showProgressDlg();
            querData(); 
        }
    }

	private void querData() {
		 mHttpRequest = new BaseHttpRequest();
		 RequestParams requestParams=new RequestParams();
		 requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
		 requestParams.put(NICKNAME, nameEdit.getText().toString().trim());
		 mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.USER_UPDATENICKNAME, requestParams, requestCallback, BaseHttpRequest.RequestType.POST);
	}
	
	private HttpRequestCallback requestCallback = new HttpRequestCallback(){

		@Override
		public void onRequestFailed(String errorMsg) {
			 closeProgressDlg();
			Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			 closeProgressDlg();
			Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
			 closeProgressDlg();
		}

		@Override
		public void onRequestSucceeded(String content) {
			 closeProgressDlg();
			 
			BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
			if (baseBean.getStatus() == 0) {
			} else {
				UserInfo userInfo = UserInfo.getCurrentUser();
	            userInfo.setNickname(nameEdit.getText().toString().trim());
	            UserInfo.saveLoginUserInfo(userInfo);
	            finish();
			}
			Utils.toast(baseBean.getMessage());
		}
	};

	private boolean checkedUser() {
		String nickName= nameEdit.getText().toString().trim();
		if (TextUtils.isEmpty(nickName)) {
			Utils.toast(R.string.input_nickname);
			return false;
		}else if(nickName.length()>12){
			Utils.toast(R.string.nick_name_too_long);
			return false;
		}
		return true;
	}
	
	private void setAvatar(){
		UserInfo user = UserInfo.getCurrentUser();
		String avatarPath = user.getAvatar();
		ImageLoader.getInstance().displayImage(avatarPath, avatarImage,
				PhotoUtils.buldDisplayImageOptionsForAvatar());
    }
	
	private void showProgressDlg() {
        mProgressDlg = DialogUtils.getCircleProgressDialog(this, getString(R.string.logining));
        mProgressDlg.setCancelable(false);
        mProgressDlg.show();
    }

    private void closeProgressDlg() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }
}

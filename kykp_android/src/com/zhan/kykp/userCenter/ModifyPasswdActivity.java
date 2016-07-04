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
import com.zhan.kykp.util.Connectivity;
import com.zhan.kykp.util.DialogUtils;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;

public class ModifyPasswdActivity extends BaseActivity implements View.OnClickListener {
    public final static String EXTRA_KEY_PHONE_NUMBER = "phone_number";

    private EditText mEditOldPwd;
    private EditText mEditPasswaord;
    private EditText mEditRePasswaord;
    private ImageView mImgAvatar;

    private String oldPass;
    private String mStrPassword;

    private Dialog mProgressDlg;
    private UserInfo mUser;
    
    private BaseHttpRequest mHttpRequest ;
    private RequestHandle mRequestHandle;
    
    private final static String USERID = "user";
    private final static String OLDPASSWORD = "oldPassword";
    private final static String NEWPASSWORD = "newPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_passwd);
        initView();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_finish:
                StatisticUtils.onEvent(R.string.title_activity_modify_passwd, R.string.finish);
                if (checkPassword()) {
                    changePwd();
                }
                break;
        }
    }

    private void initView() {
        mUser = UserInfo.getCurrentUser();
        mEditOldPwd = (EditText) findViewById(R.id.old_pwd);
        mEditPasswaord = (EditText) findViewById(R.id.password);
        mEditRePasswaord = (EditText) findViewById(R.id.re_password);
        findViewById(R.id.btn_finish).setOnClickListener(this);
        mImgAvatar=(ImageView) findViewById(R.id.avatar);
        setAvatar();
    }

	private void setAvatar(){
		UserInfo user = UserInfo.getCurrentUser();
		String avatarPath = user.getAvatar();
		ImageLoader.getInstance().displayImage(avatarPath, mImgAvatar,
				PhotoUtils.buldDisplayImageOptionsForAvatar());
    }
	
    private void changePwd() {
    	
        if (!Connectivity.isConnected(this)) {
            Utils.toast(R.string.network_disconnect);
            return;
        }
        showProgressDialog();
        querData(); 
    }

    private void querData() {
    	 mHttpRequest = new BaseHttpRequest();
		 RequestParams requestParams=new RequestParams();
		 requestParams.put(USERID, UserInfo.getCurrentUser().getObjectId());
		 requestParams.put(OLDPASSWORD, mEditOldPwd.getText().toString().trim());
		 requestParams.put(NEWPASSWORD, mEditPasswaord.getText().toString().trim());
		 mRequestHandle = mHttpRequest.startRequest(getApplicationContext(), ApiUrls.USER_UPDATEPASSWORD, requestParams, requestCallback, BaseHttpRequest.RequestType.POST);
	}
    
    private HttpRequestCallback requestCallback = new HttpRequestCallback(){

		@Override
		public void onRequestFailed(String errorMsg) {
			    closeDialog();
				Utils.toast(errorMsg);
		}

		@Override
		public void onRequestFailedNoNetwork() {
			    closeDialog();
				Utils.toast(R.string.network_error);
		}

		@Override
		public void onRequestCanceled() {
			closeDialog();
		}

		@Override
		public void onRequestSucceeded(String content) {
			closeDialog();
			BaseBean baseBean = Utils.parseJson(content, BaseBean.class);
			if (baseBean.getStatus() == 0) {
			} else {
	            finish();
			}
			Utils.toast(baseBean.getMessage());
		}
    	
    };
    
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


	private void showProgressDialog() {
        mProgressDlg = DialogUtils.getProgressDialog(this, getString(R.string.modifying));
        mProgressDlg.setCancelable(false);
        mProgressDlg.show();
    }

    private void closeDialog() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    private boolean checkPassword() {
        oldPass = mEditOldPwd.getText().toString().trim();
        mStrPassword = mEditPasswaord.getText().toString();

        String rePwd = mEditRePasswaord.getText().toString();

        if (TextUtils.isEmpty(mStrPassword)) {
            Utils.toast(R.string.pwd_empty);
            return false;
        }

        if (TextUtils.isEmpty(rePwd)) {
            Utils.toast(R.string.reinput_pwd);
            return false;
        }

        if (mStrPassword.length() < 6 || mStrPassword.length() > 16) {
            Utils.toast(R.string.pwd_length_error);
            return false;
        }
        if (TextUtils.isEmpty(oldPass)) {
            Utils.toast(R.string.reinput_pwd);
            return false;
        }

        if (oldPass.length() < 6 || oldPass.length() > 16) {
            Utils.toast(R.string.pwd_length_error);
            return false;
        }

        if (!mStrPassword.equals(rePwd)) {
            Utils.toast(R.string.re_pwd_not_equal);
            return false;
        }

        return true;

    }

}

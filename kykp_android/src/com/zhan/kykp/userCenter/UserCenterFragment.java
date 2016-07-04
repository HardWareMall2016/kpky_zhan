package com.zhan.kykp.userCenter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhan.kykp.R;
import com.zhan.kykp.entity.persistobject.UserInfo;
import com.zhan.kykp.network.ApiUrls;
import com.zhan.kykp.network.BaseHttpRequest;
import com.zhan.kykp.network.HttpRequestCallback;
import com.zhan.kykp.network.bean.UserInfoBean;
import com.zhan.kykp.util.PhotoUtils;
import com.zhan.kykp.util.StatisticUtils;
import com.zhan.kykp.util.Utils;


public class UserCenterFragment extends Fragment implements OnClickListener {

	//Views
    private ImageView mImgAvatar;
    private TextView mTVNickname;
    private TextView mFollowee ;
    private TextView mFollower ;
    private TextView mTVLevel ;
    private TextView mTVIntegral;
    private TextView mTVScholarship;
    private ProgressBar mPBintegral;
    private RelativeLayout mLinearLayout ;
    private RelativeLayout mFocus ;
    private RelativeLayout mFans ;
    private RelativeLayout mScholarship ;
    //个人信息选项
    private View mItemViewUserProfile;

    //Tools
    private DisplayImageOptions options;
    
    //Network
    private BaseHttpRequest mHttpRequest ;
    private RequestHandle mRequestHandle;

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
        queryUserInfo();
    }
    @Override
    public void onDestroy() {
    	release(mRequestHandle);
    	super.onDestroy();
    }

    private void release(RequestHandle request) {
    	if(request!=null && !request.isFinished()){
			request.cancel(true);
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.frag_layout_user_center, null);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //contentView.setPadding(0, Utils.getStatusBarHeight(this.getActivity()), 0, 0);
            FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(0, Utils.getStatusBarHeight(this.getActivity()), 0, 0);
			contentView.setLayoutParams(lp);
        }*/

        options=PhotoUtils.buldDisplayImageOptionsForAvatar();
        
        initView(contentView);
        return contentView;
    }

	private void queryUserInfo() {
        UserInfo user = UserInfo.getCurrentUser();
        if(user==null){
            return;
        }

        release(mRequestHandle);

        mHttpRequest = new BaseHttpRequest();
        RequestParams requestParams = new RequestParams();
        requestParams.put("user", user.getObjectId());
        mRequestHandle = mHttpRequest.startRequest(getActivity(), ApiUrls.USER_USERINFO, requestParams, requestCallback, BaseHttpRequest.RequestType.GET);
    }

    private HttpRequestCallback requestCallback = new HttpRequestCallback(){

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
            UserInfo userInfo = UserInfo.getCurrentUser();
            UserInfoBean result = Utils.parseJson(content, UserInfoBean.class);
            if(result!=null&&userInfo!=null){
                userInfo.setFollower(result.getDatas().getFollower());
                userInfo.setNickname(result.getDatas().getNickname());
                userInfo.setUsername(result.getDatas().getUsername());
                userInfo.setApplicationLimit(result.getDatas().getApplicationLimit());
                userInfo.setEmailVerified(result.getDatas().getEmailVerified());
                userInfo.setAvatar(result.getDatas().getAvatar());
                userInfo.setFollowee(result.getDatas().getFollowee());
                userInfo.setMobilePhoneVerified(result.getDatas().getMobilePhoneVerified());
                userInfo.setUserType(result.getDatas().getIsBBSUser());
                userInfo.setCredit(result.getDatas().getCredit());
                userInfo.setLevel(result.getDatas().getLevel());
                userInfo.setMobilePhoneNumber(result.getDatas().getMobilePhoneNumber());
                userInfo.setCurrentIntegral(result.getDatas().getCurrent_Integral());
                userInfo.setMaximumIntegral(result.getDatas().getMaximum_Integral());
                UserInfo.saveLoginUserInfo(userInfo);

                refreshView();
            }
        }
    };

	public void refreshView() {
        UserInfo user = UserInfo.getCurrentUser();
        if(user==null){
            return;
        }

        mTVLevel.setText(user.getLevel());

        mTVIntegral.setText(String.format("%d/%d", user.getCurrentIntegral(), user.getMaximumIntegral()));

        mTVScholarship.setText(String.valueOf(user.getCredit()));

        if(user.getCurrentIntegral()==0||user.getMaximumIntegral()==0){
            mPBintegral.setProgress(0);
        }else{
            mPBintegral.setProgress(user.getCurrentIntegral()*100/ user.getMaximumIntegral());
        }


        String avatarPath = user.getAvatar();
        ImageLoader.getInstance().displayImage(avatarPath, mImgAvatar, options);

        mTVNickname.setText(user.getNickname());
        //第三方登录不显示个人信息
//        if (user.getUserType() == LoginType.PHONE) {
//            mItemViewUserProfile.setVisibility(View.VISIBLE);
//        } else {
//            mItemViewUserProfile.setVisibility(View.GONE);
//        }

        mFollowee.setText(String.valueOf(user.getFollowee()));
        mFollower.setText(String.valueOf(user.getFollower()));
    }


    private void initView(View contentView) {
        mTVNickname = (TextView) contentView.findViewById(R.id.nickname);
        mImgAvatar = (ImageView) contentView.findViewById(R.id.avatar);

        mFollowee = (TextView) contentView.findViewById(R.id.user_center_focus);
        mFollower = (TextView) contentView.findViewById(R.id.user_center_fans);

        mTVLevel=(TextView) contentView.findViewById(R.id.level);

        mTVIntegral=(TextView) contentView.findViewById(R.id.integral);

        mTVScholarship=(TextView) contentView.findViewById(R.id.user_center_scholarship);

        mPBintegral=(ProgressBar) contentView.findViewById(R.id.integral_progressbar);
        
        mFocus = (RelativeLayout) contentView.findViewById(R.id.rl_user_center_focus);
        mFans = (RelativeLayout) contentView.findViewById(R.id.rl_user_center_fans);
        mScholarship = (RelativeLayout) contentView.findViewById(R.id.rl_user_center_scholarship);

        mFocus.setOnClickListener(this);
        mFans.setOnClickListener(this);
        mScholarship.setOnClickListener(this);
        
        //个人主页
        mLinearLayout = (RelativeLayout) contentView.findViewById(R.id.personcenter);
        mLinearLayout.setOnClickListener(this);

        // 我的口语练习
        View itemView = contentView.findViewById(R.id.my_speaking);
        ImageView itemIcon = getItemIcon(itemView);
        TextView itemTitle = getItemTitle(itemView);
        itemIcon.setImageResource(R.drawable.icon_speaking);
        itemTitle.setText(R.string.my_speaking);
        itemView.setOnClickListener(this);

        // 我的跟读
        itemView = contentView.findViewById(R.id.my_practice);
        itemIcon = getItemIcon(itemView);
        itemTitle = getItemTitle(itemView);
        itemIcon.setImageResource(R.drawable.icon_practice);
        itemTitle.setText(R.string.my_practice);
        itemView.setOnClickListener(this);

        // 我的模考
        itemView = contentView.findViewById(R.id.my_tpo);
        itemIcon = getItemIcon(itemView);
        itemTitle = getItemTitle(itemView);
        itemIcon.setImageResource(R.drawable.icon_tpo);
        itemTitle.setText(R.string.my_tpo);
        itemView.setOnClickListener(this);
        
        //我的任务
        itemView = contentView.findViewById(R.id.my_task);
        itemIcon = getItemIcon(itemView);
        itemTitle = getItemTitle(itemView);
        itemIcon.setImageResource(R.drawable.icon_task);
        itemTitle.setText(R.string.my_task);
        itemView.setOnClickListener(this);

        // 个人信息
        mItemViewUserProfile = contentView.findViewById(R.id.my_profile);
        itemIcon = getItemIcon(mItemViewUserProfile);
        itemTitle = getItemTitle(mItemViewUserProfile);
        itemIcon.setImageResource(R.drawable.iocn_user_profile);
        itemTitle.setText(R.string.my_profile);
        mItemViewUserProfile.setOnClickListener(this);

        //意见反馈
        View feedback = contentView.findViewById(R.id.feedback);
        feedback.setOnClickListener(this);
        ((TextView)feedback.findViewById(R.id.title)).setText(R.string.us_feedback);
        ((ImageView)feedback.findViewById(R.id.left_img)).setImageResource(R.drawable.icon_feedback);

        // 关于我们
        itemView = contentView.findViewById(R.id.about_us);
        itemIcon = getItemIcon(itemView);
        itemTitle = getItemTitle(itemView);
        itemIcon.setImageResource(R.drawable.icon_about_us);
        itemTitle.setText(R.string.system);
        itemView.setOnClickListener(this);

        contentView.findViewById(R.id.exit_account).setOnClickListener(this);
    }

    private ImageView getItemIcon(View itemView) {
        return (ImageView) itemView.findViewById(R.id.left_img);
    }

    private TextView getItemTitle(View itemView) {
        return (TextView) itemView.findViewById(R.id.title);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.my_speaking:
                StatisticUtils.onEvent(R.string.personal_center, R.string.my_speaking);
                Intent mySpeakingIntent = new Intent(getActivity(), MySpeakingActivity.class);
                startActivity(mySpeakingIntent);
                break;
            case R.id.my_practice:
                StatisticUtils.onEvent(R.string.personal_center, R.string.my_practice);
                Intent mypracticeIntent = new Intent(getActivity(), MyPracticeActivity.class);
                startActivity(mypracticeIntent);
                break;
            case R.id.my_tpo:
                StatisticUtils.onEvent(R.string.personal_center, R.string.my_tpo);
                Intent myTPOIntent = new Intent(getActivity(), MyTPOListActivity.class);
                startActivity(myTPOIntent);
                break;
            case R.id.my_task:
                StatisticUtils.onEvent(R.string.personal_center, R.string.my_task);
            	Intent mytaskIntent = new Intent(getActivity(), MyTaskActivity.class);
                startActivity(mytaskIntent);
            	break;
            case R.id.my_profile:
                StatisticUtils.onEvent(R.string.personal_center, R.string.my_profile);
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.about_us:
                StatisticUtils.onEvent(R.string.personal_center, R.string.system);
                startActivity(new Intent(getActivity(), SystemActivity.class));
                break;
            case R.id.personcenter:
                StatisticUtils.onEvent(R.string.personal_center, R.string.person_center);
            	Intent centerIntent = new Intent(getActivity(), PersonCenterActivity.class);
            	Bundle bundle = new Bundle();                           
        		bundle.putString(PersonCenterActivity.EXT_KEY_USEROBJECT, UserInfo.getCurrentUser().getObjectId());
        		centerIntent.putExtras(bundle); 
                startActivity(centerIntent);
            	break;
            case R.id.rl_user_center_focus:
                StatisticUtils.onEvent(R.string.personal_center, R.string.person_focus);
            	Intent focusIntent = new Intent(getActivity(),MyPersonFocus.class);
            	Bundle focus_bundle = new Bundle(); 
            	focus_bundle.putString(MyPersonFocus.EXT_KEY_PERSONOBJECT, UserInfo.getCurrentUser().getObjectId());
            	focus_bundle.putString(MyPersonFocus.EXT_KEY_TYPE, "1");    
            	focusIntent.putExtras(focus_bundle); 
                startActivity(focusIntent);
            	break;
            case R.id.rl_user_center_fans:
                StatisticUtils.onEvent(R.string.personal_center, R.string.person_fans);
            	Intent fansIntent_fans = new Intent(getActivity(),MyPersonFocus.class);
            	Bundle bundle_fans = new Bundle(); 
    			bundle_fans.putString(MyPersonFocus.EXT_KEY_PERSONOBJECT, UserInfo.getCurrentUser().getObjectId());
    			bundle_fans.putString(MyPersonFocus.EXT_KEY_TYPE, "0");    
    			fansIntent_fans.putExtras(bundle_fans); 
                startActivity(fansIntent_fans);
            	break;
            case R.id.rl_user_center_scholarship:
                Intent scholarshipIntent = new Intent(getActivity(),MyScholarShip.class);
                startActivity(scholarshipIntent);
                break;
            case R.id.feedback:
                StatisticUtils.onEvent(R.string.system, R.string.us_feedback);
                Intent feedbackIntent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(feedbackIntent);
                break;
        }
    }
}

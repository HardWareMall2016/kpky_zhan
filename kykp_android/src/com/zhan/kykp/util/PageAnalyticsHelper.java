package com.zhan.kykp.util;

import android.util.Log;

import com.zhan.kykp.HomePageActivity;
import com.zhan.kykp.MXK.MXKActivity;
import com.zhan.kykp.R;
import com.zhan.kykp.TPO.TPOActivity;
import com.zhan.kykp.TPO.TPOListActivity;
import com.zhan.kykp.base.BaseActivity;
import com.zhan.kykp.message.MyRecommendActivity;
import com.zhan.kykp.message.PrivateLetterActivity;
import com.zhan.kykp.message.SystemMsgDetailsActivity;
import com.zhan.kykp.practice.PracticeDetailsActivity;
import com.zhan.kykp.practice.PracticeListActivity;
import com.zhan.kykp.practice.PracticeResolvingIdea;
import com.zhan.kykp.practice.TansDetailsActivity;
import com.zhan.kykp.speaking.SpeakingMainActivity;
import com.zhan.kykp.speakingIelts.SpeakingIeltsListActivity;
import com.zhan.kykp.spokenSquare.SpokenSquareActivity;
import com.zhan.kykp.spokenSquare.TopicHistoryActivity;
import com.zhan.kykp.userCenter.BBSLoginActivity;
import com.zhan.kykp.userCenter.ForgetPasswordActivity;
import com.zhan.kykp.userCenter.IndexActivity;
import com.zhan.kykp.userCenter.LoginActivity;
import com.zhan.kykp.userCenter.ModifyNickActivity;
import com.zhan.kykp.userCenter.ModifyPasswdActivity;
import com.zhan.kykp.userCenter.ModifyPhoneActivity;
import com.zhan.kykp.userCenter.MyPersonFocus;
import com.zhan.kykp.userCenter.MyPracticeActivity;
import com.zhan.kykp.userCenter.MySpeakingActivity;
import com.zhan.kykp.userCenter.MySpeakingCorrectDetailsActivity;
import com.zhan.kykp.userCenter.MyTPODetailsActivity;
import com.zhan.kykp.userCenter.MyTPOListActivity;
import com.zhan.kykp.userCenter.PersonCenterActivity;
import com.zhan.kykp.userCenter.RegisterActivity;
import com.zhan.kykp.userCenter.RegisterProfileActivity;
import com.zhan.kykp.userCenter.SystemActivity;
import com.zhan.kykp.userCenter.UserInfoActivity;
import com.zhan.kykp.userCenter.aboutActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuyue on 2015/10/13.
 */
public class PageAnalyticsHelper {
    private static Map<String, Integer> sNameMap = new HashMap<String, Integer>();
    static{
        //sNameMap.put(HomePageActivity.class.getName(), R.string.home_page);
        //sNameMap.put(LauncherPageActivity.class.getName(), "起始页");
        sNameMap.put(LoginActivity.class.getName(), R.string.login);
        //sNameMap.put(AuthActivity.class.getName(), "QQ授权");
        //sNameMap.put(AssistActivity.class.getName(), "QQ授权");
        sNameMap.put(BBSLoginActivity.class.getName(), R.string.bbs_login);
        sNameMap.put(RegisterActivity.class.getName(), R.string.register);
        sNameMap.put(ForgetPasswordActivity.class.getName(), R.string.forget_pwd);
        sNameMap.put(RegisterProfileActivity.class.getName(), R.string.statistic_register_profile);
        sNameMap.put(MyTPOListActivity.class.getName(), R.string.my_tpo);
        sNameMap.put(TPOListActivity.class.getName(), R.string.statistic_tpo_list);
        sNameMap.put(TPOActivity.class.getName(), R.string.tpo);
        sNameMap.put(MyTPODetailsActivity.class.getName(), R.string.statistic_my_tpo_details);
        sNameMap.put(SpeakingMainActivity.class.getName(), R.string.speaking);
        //sNameMap.put(SpeakingActivity.class.getName(), R.string.login);//通过getTitle获取
        sNameMap.put(MySpeakingActivity.class.getName(), R.string.my_speaking_answer_record);
        sNameMap.put(MySpeakingCorrectDetailsActivity.class.getName(), R.string.my_speaking_correct_details);
        sNameMap.put(UserInfoActivity.class.getName(), R.string.user_info);
        sNameMap.put(ModifyPhoneActivity.class.getName(), R.string.title_activity_modify_phone);
        sNameMap.put(ModifyPasswdActivity.class.getName(), R.string.title_activity_modify_passwd);
        sNameMap.put(ModifyNickActivity.class.getName(), R.string.title_activity_modify_nick);
        sNameMap.put(SystemActivity.class.getName(), R.string.system);
        sNameMap.put(IndexActivity.class.getName(), R.string.title_activity_index);
        //sNameMap.put(WXEntryActivity.class.getName(), R.string.login);
        sNameMap.put(PracticeListActivity.class.getName(), R.string.statistic_practice_list);
        sNameMap.put(PracticeDetailsActivity.class.getName(), R.string.statistic_practice_details);
        sNameMap.put(PracticeResolvingIdea.class.getName(), R.string.practice_resolving_idea);
        sNameMap.put(TansDetailsActivity.class.getName(), R.string.practice_word_details);
        sNameMap.put(MyPracticeActivity.class.getName(), R.string.my_practice);
        sNameMap.put(SystemMsgDetailsActivity.class.getName(), R.string.sys_msg);
        sNameMap.put(PrivateLetterActivity.class.getName(), R.string.statistic_private_letter);
        sNameMap.put(MXKActivity.class.getName(), R.string.MXKActivity);
        sNameMap.put(aboutActivity.class.getName(), R.string.title_activity_about);
        sNameMap.put(SpeakingIeltsListActivity.class.getName(), R.string.speaking_ielts_library);
        sNameMap.put(SpokenSquareActivity.class.getName(), R.string.spoken_square_title);
        sNameMap.put(TopicHistoryActivity.class.getName(), R.string.spoken_square_topic_history);
        sNameMap.put(PersonCenterActivity.class.getName(), R.string.statistic_person_center);
        sNameMap.put(MyPersonFocus.class.getName(), R.string.myperson_focus);
        sNameMap.put(MyRecommendActivity.class.getName(), R.string.recommend_title);
    }

    public static String getPageName(BaseActivity activity) {
        String pageName;
        String className = activity.getClass().getName();
        Integer stringId=sNameMap.get(className);

        if(stringId==null){
            pageName= (String) activity.getTitle();
        }else{
            pageName= activity.getString(stringId);
        }
        Log.i("PageAnalyticsHelper", "className = " + className);
        Log.i("PageAnalyticsHelper", "pageName = " + pageName);

        return pageName;
    }
}

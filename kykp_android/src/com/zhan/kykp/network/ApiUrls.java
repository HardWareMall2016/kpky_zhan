package com.zhan.kykp.network;

public class ApiUrls {
    // Topic - 回答列表
    public static final String TOPIC_ANSSERLIST = "/topic/answerList";
    // Topic - 话题首页
    public static final String TOPIC_INDEX = "/topic/index";
    // Topic - 我来打分
    public static final String TOPIC_MARK_INDEX = "/topic/markindex";
    // Topic - 评分
    public static final String TOPIC_MARK = "/topic/mark";
    // Topic - 点赞
    public static final String TOPIC_PRAISE = "/topic/praise";
    // Topic - 回答
    public static final String TOPIC_UPLOAD_RECORDING = "/topic/record";
    // Topic - 关注
    public static final String TOPIC_ATTENTION = "/topic/followe";
    //Topic - 话题历史
    public static final String TOPIC_TOPICHISTORY = "/topic/topicHistory";


    //Topic - 获取关注&粉丝列表
    public static final String FOLLOWE_GETFOLLOWELIST = "/followe/getFolloweList";
    


    //Topic - 取消关注
    public static final String TOPIC_CANCELFOLLOWE = "/topic/cancelFollowe";
    //Topic - 点赞列表
    public static final String TOPIC_PRAISELIST = "/topic/praiseList";
    //Topic - 获取关注&粉丝数
    public static final String FOLLOWE_GETFOLLOWENUM = "/followe/getFolloweNum";
    //Message - 更新点赞状态
    public static final String MESSAGE_UPDATE_PRAISE = "/topic/updatePraise";


    //Message - 总消息列表
    public static final String MESSAGE_STATUS= "/message/messageStatus";
    //Message - 总消息列表
    public static final String MESSAGE_ALL_LIST = "/message/messageList";
    //Message - 系统消息列表
    public static final String MESSAGE_SYS_MSG = "/message/systemMsg";
    //Message - 更新系统消息状态
    public static final String MESSAGE_UPDATE_SYS_MSG = "/message/updateSystemMsg";
    //Message - 获取私信
    public static final String MESSAGE_GET_PRIV_MSG = "/message/getPrivateMsg";
    //Message - 发送私信
    public static final String MESSAGE_SEND_PRIV_MSG = "/message/sendPrivateMsg";
    //Message - 私信状态更新
    public static final String MESSAGE_UPDATE_PRIV_MSG = "/message/updatePrivateMsg";

    //口语练习-雅思题库
    public static final String SPEAKINGPRACTICE_GETIELTS = "/SpeakingPractice/getIelts";
    //口语练习-机经预测
    public static final String SPEAKINGPRACTICE_GETJIJING = "/SpeakingPractice/getJijing";
    //口语练习-托福题库
    public static final String SPEAKINGPRACTICE_GETTOEFL = "/SpeakingPractice/getToefl";
    //SpeakingPractice - 机经预测-Title
    public static final String SPEAKINGPRACTICE_GETJIJING_TITLE = "/SpeakingPractice/getJijingTitle";
    //SpeakingPractice - 录音回答-答题分类[全部|未答题]
    public static final String SPEAKINGPRACTICE_ANSWER_STATUS = "/SpeakingPractice/answerSpeaking";
    
    //User - 用户主页
    public static final String USER_USERHOME = "/user/userHome";
    //User - 口语练习列表
    public static final String USER_TOPICANSWER = "/user/topicAnswer";
    //User - 发送注册手机验证码
    public static final String USER_SEND_MOBILE_CODE = "/user/sendMobileCode";
    //user - 发送忘记密码验证码
    public static final String USER_SEND_FORGET_CODE = "/user/sendForgetPwdCode";
    //User - 手机验证码验证
    public static final String USER_VERIFY_CODE = "/user/verifyCode";
    //User - 修改手机号码
    public static final String USER_UPDATE_MOBILE = "/user/updateMobile";
    //User - 修改密码
    public static final String USER_FORGET_PASSWORD = "/user/forgetPassword";
    //User - 用户登录
    public static final String USER_LOGIN = "/user/login";
    //User - 用户注册
    public static final String USER_REGISTER = "/user/register";

    //User - 第三方登录
    public static final String USER_THIRD_LOGIN = "/user/thirdLogin";
    //User - 用户信息
    public static final String USER_USERINFO = "/user/userInfo";
    //User - 修改昵称
    public static final String USER_UPDATENICKNAME= "/user/updateNickname";
    //User - 修改密码
    public static final String USER_UPDATEPASSWORD= "/user/updatePassword";
    //User - 修改头像
    public static final String USER_UPDATEAVATAR= "/user/updateAvatar";
    //User - 修改性别
    public static final String USER_UPDATESEX = "/user/updateSex";
    //User - 修改年龄
    public static final String  USER_UPDATEAGE = "/user/updateAge";

    //Practice - 跟读练习列表
    public static final String PRACTICE_LISTS= "/Practice/lists";
    //Practice - 保存录音
    public static final String PRACTICE_SAVE= "/Practice/save";

    //Tpo - 口语模考列表
    public static final String TPO_LIST= "/Tpo/lists";
    //Tpo - 保存录音
    public static final String TPO_SAVE= "/Tpo/save";
    //每日一句话
    public static final String  PROVERB_GETPROVERB = "/proverb/getProverb";

    //APP升级
    public static final String  APPCLINE_UPGRADE = "/AppClient/upgrade";
    //AppClient - 抢考位开关
    public static final String  APPCLINE_KAOWEI = "/AppClient/kaowei";

    //Celebrity - 名人英语列表
    public static final String  CELEBRITY_LISTS = "/Celebrity/lists";
    //Celebrity - 名人英语点赞
    public static final String  CELEBRITY_PRAISE = "/Celebrity/praise";
    //Celebrity - 名人英语详情
    public static final String  CELEBRITY_DETAIL = "/Celebrity/detail";

    //留学攻略
    public static final String  RAIDERS_GETRAIDERS = "/Raiders/getRaiders";
    //留学攻略点赞
    public static final String  RAIDERS_PRAISERAIDERS = "/Raiders/praiseRaiders";
    //用户签到
    public static final String  USER_USERSIGN = "/user/userSign";
    //User - 判断用户签到及考试日期状态
    public static final String  USER_IS_SIGN = "/user/isSign";

    //Exam - 选择考试时间
    public static final String  EXAM_SET_UPTIME = "/Exam/setUpTime";
    //Exam - 考试时间列表
    public static final String  EXAM_GET_EXAM_TIME = "/Exam/getExamTime";
    //User - 用户任务列表
    public static final String  USER_USERTASKLIST = "/user/userTaskList";

    //Goods - 编辑收货地址
    public static final String  GOODS_SAVE_ADDR = "/goods/saveAddress";
    //Goods - 获取商品列表
    public static final String  GOODS_GET_LIST = "/goods/getGoodsList";
    //Goods - 获取收货地址
    public static final String  GOODS_GET_ADDR = "/goods/getAddress";
    //Goods - 确认订单
    public static final String  GOODS_SAVE_ORDER= "/goods/saveOrder";
    //Goods - 兑换记录
    public static final String  GOODS_GET_ORDER= "/goods/getOrder";
}

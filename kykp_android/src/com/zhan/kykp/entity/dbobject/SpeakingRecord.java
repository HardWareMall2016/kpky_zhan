package com.zhan.kykp.entity.dbobject;

public class SpeakingRecord extends BaseData{
	public String questionObjectId;
	public String userObjectId;
	public String imgKeyWord;//根据这个关键字，显示图片
	public String title;
	public String subTitle;
	public int questionType;
	public String recordingFilePath;
	public int recordingSeconds;
	public long startTime;
	public String extData;

	public final static int TYPE_TOEFL=1;
	public final static int TYPE_Ielts=2;
}

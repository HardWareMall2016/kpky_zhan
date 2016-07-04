package com.zhan.kykp.userCenter;

import com.zhan.kykp.base.BaseActivity;

public class MySpeakingCorrectDetailsActivity extends BaseActivity /*implements OnClickListener, OnCompletionListener*/ {
	/*private final static String TAG = MySpeakingCorrectDetailsActivity.class.getSimpleName();

	public final static String EXT_KEY_OBJECT_ID = "object_id";

	// Title
	private TextView mTVTitleMyRecording;
	private TextView mTVTitleViewSample;

	private View mMyRecordingContent;
	private TextView mTVQuestion;

	private View mViewSampleConetnt;
	private TextView mTVSample;

	// Student
	private ImageView mStudentImg;
	private TextView mStudentScore;
	private TextView mStudentName;
	private TextView mStudentDate;
	private TextView mStudentAudioSec;
	private ImageView mStudentAudio;
	private AnimationDrawable mStudentAnimSound;
	private View mStudentAudioConetnt;

	// Teacher
	private ImageView mTeacherImg;
	private TextView mTeacherName;
	private TextView mTeacherDate;
	private TextView mTeacherAudioSec;
	private ImageView mTeacherAudio;
	private AnimationDrawable mTeachAnimSound;
	private View mTeachtAudioConetnt;
	
	private MediaPlayer mMediaPlayer;
	private QuestionLibDB mQuestionLibDB;
	// private SpeakingRecordDB mSpeakingRecordDB;

	private String mOrderObjectId;
	//private SpeakingRecord mSpeakingRecord;

	private boolean mShowMyrecordingContent = true;
	public final SimpleDateFormat DateFormat = new SimpleDateFormat("MM-dd HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_speaking_correct_details);

		// mSpeakingRecordDB=new SpeakingRecordDB(this);
		mQuestionLibDB=new QuestionLibDB(this);
		
		initMediaPlayer();

		initView();
	}

	private void initView() {
		mTVTitleMyRecording = (TextView) findViewById(R.id.my_recording);
		mTVTitleMyRecording.setOnClickListener(this);
		mTVTitleViewSample = (TextView) findViewById(R.id.view_example);
		mTVTitleViewSample.setOnClickListener(this);

		mMyRecordingContent = findViewById(R.id.content_my_recording);
		mTVQuestion = (TextView) findViewById(R.id.tpo_question);

		mViewSampleConetnt = findViewById(R.id.content_view_sample);
		mTVSample = (TextView) findViewById(R.id.tpo_sample);
		mTVSample.setMovementMethod(ScrollingMovementMethod.getInstance());

		// Student
		mStudentImg = (ImageView) findViewById(R.id.img_student);
		mStudentScore= (TextView) findViewById(R.id.score);
		mStudentName = (TextView) findViewById(R.id.student_name);
		mStudentDate = (TextView) findViewById(R.id.student_date);
		mStudentAudioSec = (TextView) findViewById(R.id.student_audio_second);
		
		final String avatarLocalPath=PathUtils.getUserAvatarPath(User.getCurrentUserId());
        File imgAvatar=new File(avatarLocalPath);
        if(imgAvatar.exists()){
        	Bitmap bmpAvatar=BitmapFactory.decodeFile(avatarLocalPath);
        	mStudentImg.setImageBitmap(bmpAvatar);
        }
        
		mStudentAnimSound = (AnimationDrawable) getResources().getDrawable(R.drawable.sound_anim_right);
		mStudentAudio = (ImageView) findViewById(R.id.student_audio);
		mStudentAudio.setBackground(mStudentAnimSound);

		mStudentAudioConetnt=findViewById(R.id.student_audio_content);
		mStudentAudioConetnt.setOnClickListener(mOnAudioBtnClick);
		
		// Teacher
		mTeacherImg = (ImageView) findViewById(R.id.img_teacher);
		mTeacherName = (TextView) findViewById(R.id.teacher_name);
		mTeacherDate = (TextView) findViewById(R.id.teacher_date);
		mTeacherAudioSec = (TextView) findViewById(R.id.teacher_audio_second);
		mTeachAnimSound = (AnimationDrawable) getResources().getDrawable(R.drawable.sound_anim_left);
		mTeacherAudio = (ImageView) findViewById(R.id.teacher_audio);
		mTeacherAudio.setBackground(mTeachAnimSound);
		mTeachtAudioConetnt=findViewById(R.id.teach_audio_content);
		mTeachtAudioConetnt.setOnClickListener(mOnAudioBtnClick);

		refreshViews();

		mOrderObjectId = getIntent().getStringExtra(EXT_KEY_OBJECT_ID);
		
		querySpeakingInfo();
	}

	@Override
	protected void onDestroy() {
		releaseMedia();
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.my_recording:
			mShowMyrecordingContent = true;
			break;
		case R.id.view_example:
			mShowMyrecordingContent = false;
			break;
		}
		refreshViews();
	}
	
	private OnClickListener mOnAudioBtnClick=new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			switch(arg0.getId()){
			case R.id.student_audio_content:
				if(mStudentAnimSound.isRunning()){
					mStudentAnimSound.stop();
					mTeachAnimSound.stop();
					stopMedia();
				}else{
					mTeachAnimSound.stop();
					mStudentAnimSound.start();
					play(getStudentAudioFilePath());
				}
				break;
			case R.id.teach_audio_content:
				if(mTeachAnimSound.isRunning()){
					mStudentAnimSound.stop();
					mTeachAnimSound.stop();
					stopMedia();
				}else{
					mStudentAnimSound.stop();
					mTeachAnimSound.start();
					play(getTeacherAudioFilePath());
				}
				break;
			}
		}
	};

	private void refreshViews() {
		if (mShowMyrecordingContent) {
			mMyRecordingContent.setVisibility(View.VISIBLE);
			mViewSampleConetnt.setVisibility(View.GONE);

			mTVTitleMyRecording.setBackgroundResource(R.drawable.bg_dark_red_underline);
			mTVTitleViewSample.setBackgroundColor(Color.WHITE);
			
			mTVTitleMyRecording.setTextColor(getResources().getColor(R.color.dark_red));
			mTVTitleViewSample.setTextColor(getResources().getColor(R.color.text_color_content));

		} else {
			mMyRecordingContent.setVisibility(View.GONE);
			mViewSampleConetnt.setVisibility(View.VISIBLE);

			mTVTitleMyRecording.setBackgroundColor(Color.WHITE);
			mTVTitleViewSample.setBackgroundResource(R.drawable.bg_dark_red_underline);
			
			mTVTitleMyRecording.setTextColor(getResources().getColor(R.color.text_color_content));
			mTVTitleViewSample.setTextColor(getResources().getColor(R.color.dark_red));
		}
	}

	private void querySpeakingInfo() {

		AVQuery<SpeakingOrder> query = AVObject.getQuery(SpeakingOrder.class);
		query.whereEqualTo(Constant.OBJECT_ID, mOrderObjectId);
		query.include(SpeakingOrder.TEACHER);
		query.getFirstInBackground(new GetCallback<SpeakingOrder>() {
			@Override
			public void done(SpeakingOrder arg0, AVException arg1) {
				if (arg1 == null && arg0 != null) {
					final int uid = arg0.getUid();
					final Date syudentDate = arg0.getStudentDate();
					
					Question question=mQuestionLibDB.queryData(uid);
					mTVQuestion.setText(question.Question);
					mTVSample.setText(question.Answer);
					mStudentName.setText(User.getCurrentUserInfo().getNickname());
					mStudentDate.setText(getFormateTimeStr(syudentDate.getTime()));
					mStudentScore.setText(String.valueOf(arg0.getScore()));
					
					arg0.getStudentAudioFile().getDataInBackground(new GetDataCallback() {
						@Override
						public void done(byte[] arg0, AVException arg1) {
							if (arg1 == null) {
								String recordPath = getStudentAudioFilePath();
								File file=new File(recordPath);
								if(file.exists()){
									file.delete();
								}
								Utils.getFileFromBytes(arg0, recordPath);

								MediaPlayer mp = MediaPlayer.create(MySpeakingCorrectDetailsActivity.this, Uri.parse(recordPath));
								int seconds=mp.getDuration() / 1000;
								mStudentAudioSec.setText(String.format("%d\"", seconds));
							}

						}
					});
					
					Teacher teacher=arg0.getTeacher();
					mTeacherName.setText(teacher.getUserName());
					mTeacherDate.setText(getFormateTimeStr(arg0.getTeacherDate().getTime()));
					
					arg0.getTeacherAudioFile().getDataInBackground(new GetDataCallback() {
						@Override
						public void done(byte[] arg0, AVException arg1) {
							if (arg1 == null) {
								String recordPath = getTeacherAudioFilePath();
								File file=new File(recordPath);
								if(file.exists()){
									file.delete();
								}
								Utils.getFileFromBytes(arg0, recordPath);

								MediaPlayer mp = MediaPlayer.create(MySpeakingCorrectDetailsActivity.this, Uri.parse(recordPath));
								int seconds=mp.getDuration() / 1000;
								mTeacherAudioSec.setText(String.format("%d\"", seconds));
							}
							
						}});
				}
			}
		});
	}

	public String getFormateTimeStr(long time) {
		Date date = new Date(time);
		String timeStr = DateFormat.format(date);
		return timeStr;
	}
	
	private void initMediaPlayer() {
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnCompletionListener(this);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "IllegalArgumentException : " + e.getMessage());
		} catch (SecurityException e) {
			Log.e(TAG, "SecurityException : " + e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException : " + e.getMessage());
		}
	}

	public void play(String audioFile) {
		try {
			mMediaPlayer.setDataSource(audioFile);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "IllegalArgumentException : " + e.getMessage());
		} catch (SecurityException e) {
			Log.e(TAG, "SecurityException : " + e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException : " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException : " + e.getMessage());
		}
	}

	private void stopMedia() {
		mStudentAnimSound.stop();
		mTeachAnimSound.stop();
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
	}

	private void releaseMedia() {
		mStudentAnimSound.stop();
		mTeachAnimSound.stop();
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		mStudentAnimSound.stop();
		mTeachAnimSound.stop();
	}

	private String getStudentAudioFilePath() {
		String recordDir = PathUtils.getExternalRecordFilesDir().getAbsolutePath();
		String filePath = recordDir + "/" + "temp_student.amr";
		return filePath;
	}

	private String getTeacherAudioFilePath() {
		String recordDir = PathUtils.getExternalRecordFilesDir().getAbsolutePath();
		String filePath = recordDir + "/" + "temp_teacher.amr";
		return filePath;
	}*/
}

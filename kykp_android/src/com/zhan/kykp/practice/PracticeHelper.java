package com.zhan.kykp.practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhan.kykp.util.PathUtils;
import com.zhan.kykp.util.Utils;

import android.util.Log;

public class PracticeHelper {
	public final static String TAG=PracticeHelper.class.getSimpleName();
	
	private final String JSON_FILE_NAME="document.json";
	
	private final String JSON_ATTR_CORE_TOPIC="Core Topic";
	private final String JSON_ATTR_TITLE="Title";
	private final String JSON_ATTR_TITLE_TRANS="TitleTranslation";
	private final String JSON_ATTR_CONTENT="Content";
	private final String JSON_ATTR_FILE="File";
	private final String JSON_ATTR_DIFFCULTY="Diffculty";
	private final String JSON_ATTR_HINT="Hint";
	private final String JSON_ATTR_PARAGRAPHS="Paragraphs";
	private final String JSON_ATTR_PARAGRAPHS_CONTENT="Content";
	private final String JSON_ATTR_PARAGRAPHS_FILE="File";
	private final String JSON_ATTR_PARAGRAPHS_COMM="Comment";
	private final String JSON_ATTR_PARAGRAPHS_TIME="Time";
	private final String JSON_ATTR_PARAGRAPHS_PRO="Pronunciations";
	private final String JSON_ATTR_PARAGRAPHS_PRO_TAG="Tag";
	private final String JSON_ATTR_PARAGRAPHS_PRO_CONTNT="Content";
	private final String JSON_ATTR_PARAGRAPHS_PRO_FILE="File";
	private final String JSON_ATTR_PARAGRAPHS_WORDS="Words";
	private final String JSON_ATTR_PARAGRAPHS_WORDS_WORD="Word";
	private final String JSON_ATTR_PARAGRAPHS_WORDS_COMM="Comment";
	private final String JSON_ATTR_PARAGRAPHS_WORDS_EXPLAIN="Explain";
	private final String JSON_ATTR_PARAGRAPHS_WORDS_TRANS="Translation";
	
	private static PracticeHelper sPracticeHelper;
	
	private String mPracticeFolderPath;

	public class PracticeJsonObj {
		public String CoreTopic;
		public String Title;
		public String TitleTranslation;
		public String Content;
		public String File;
		public String Diffculty;
		public String Hint;
		public List<Paragraph> Paragraphs;
	}

	public class Paragraph {
		public String Content;
		public String File;
		public String Comment;
		public int Time;
		public List<Pronunciation> Pronunciations;
		public List<Word> Words;
	}

	public class Pronunciation {
		public String Tag;
		public String Content;
		public String File;
	}

	public class Word {
		public String Word;
		public String Comment;
		public String Explain;
		public String Translation;
	}

	private PracticeHelper() {
		mPracticeFolderPath=PathUtils.getExternalPracticeFilesDir().getAbsolutePath();
	}

	public static PracticeHelper getInstance() {
		if (sPracticeHelper == null) {
			sPracticeHelper = new PracticeHelper();
		}
		return sPracticeHelper;
	}

	public PracticeJsonObj parsePracticeFromJsonFile(int index) {
		String practiceJsonPath=mPracticeFolderPath + "/" + index+"/"+JSON_FILE_NAME;
		
		File file = new File(practiceJsonPath);
		if(!file.exists()){
			Log.e(TAG, "JSON File does not exits : "+practiceJsonPath);
			return null;
		}
		
		PracticeJsonObj object = null ;
		StringBuffer sb = new StringBuffer();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			JSONObject documentJson = new JSONObject(sb.toString());
			object=parseDocJson(documentJson);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException : "+e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException : "+e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, "JSONException : "+e.getMessage());
		}
		return object;
	}
	
	private PracticeJsonObj parseDocJson(JSONObject jsonDoc){
		PracticeJsonObj practice=new PracticeJsonObj();
		try {
			practice.CoreTopic=jsonDoc.optString(JSON_ATTR_CORE_TOPIC);
			practice.Title=jsonDoc.optString(JSON_ATTR_TITLE);
			practice.TitleTranslation=jsonDoc.optString(JSON_ATTR_TITLE_TRANS);
			practice.Content=jsonDoc.optString(JSON_ATTR_CONTENT);
			practice.File=jsonDoc.optString(JSON_ATTR_FILE);
			practice.Diffculty=jsonDoc.optString(JSON_ATTR_DIFFCULTY);
			practice.Hint=jsonDoc.optString(JSON_ATTR_HINT);
			
			practice.Paragraphs=new ArrayList<Paragraph>();
			JSONArray jsonArryParagraphs=jsonDoc.getJSONArray(JSON_ATTR_PARAGRAPHS);
			JSONObject jsonParagraph;
			for(int i=0;i<jsonArryParagraphs.length();i++){
				jsonParagraph=jsonArryParagraphs.getJSONObject(i);
				Paragraph paragraph=new Paragraph();
				paragraph.Content=jsonParagraph.optString(JSON_ATTR_PARAGRAPHS_CONTENT);
				paragraph.File=jsonParagraph.optString(JSON_ATTR_PARAGRAPHS_FILE);
				paragraph.Comment=jsonParagraph.optString(JSON_ATTR_PARAGRAPHS_COMM);
				paragraph.Time=jsonParagraph.optInt(JSON_ATTR_PARAGRAPHS_TIME,-1);
				
				//Pronunciations
				paragraph.Pronunciations=new ArrayList<Pronunciation>();
				JSONArray jsonArryPronunciation=jsonParagraph.optJSONArray(JSON_ATTR_PARAGRAPHS_PRO);
				if(jsonArryPronunciation!=null){
					for(int j=0;j<jsonArryPronunciation.length();j++){
						JSONObject jsonPronunciation=jsonArryPronunciation.getJSONObject(j);
						
						Pronunciation pronunciation=new Pronunciation();
						pronunciation.Tag=jsonPronunciation.optString(JSON_ATTR_PARAGRAPHS_PRO_TAG);
						pronunciation.Content=jsonPronunciation.optString(JSON_ATTR_PARAGRAPHS_PRO_CONTNT);
						pronunciation.File=jsonPronunciation.optString(JSON_ATTR_PARAGRAPHS_PRO_FILE);
						
						paragraph.Pronunciations.add(pronunciation);
					}
				}
				
				
				//Words
				paragraph.Words=new ArrayList<Word>();
				JSONArray jsonArryWord=jsonParagraph.optJSONArray(JSON_ATTR_PARAGRAPHS_WORDS);
				if(jsonArryWord!=null){
					for(int k=0;k<jsonArryWord.length();k++){
						JSONObject jsonWord=jsonArryWord.getJSONObject(k);
						
						Word word=new Word();
						word.Word=jsonWord.optString(JSON_ATTR_PARAGRAPHS_WORDS_WORD);
						word.Comment=jsonWord.optString(JSON_ATTR_PARAGRAPHS_WORDS_COMM);
						word.Explain=jsonWord.optString(JSON_ATTR_PARAGRAPHS_WORDS_EXPLAIN);
						word.Translation=jsonWord.optString(JSON_ATTR_PARAGRAPHS_WORDS_TRANS);
						
						paragraph.Words.add(word);
					}
				}
				
				practice.Paragraphs.add(paragraph);
			}
			
		} catch (JSONException e) {
			Log.e(TAG, "JSONException :"+e.getMessage());
			practice=null;
		}
		
		return practice;
	}
	

	public String getZipFileSavePath(String index){
		String practiceJsonPath=mPracticeFolderPath + "/" + index + ".zip";
		return practiceJsonPath;
	}

	public void unZipFile(File zipFile,String index){
		if(zipFile==null&&zipFile.length()==0){
			return;
		}

		//删除解压文件夹
		String unZipPath=mPracticeFolderPath + "/" + index;
		PathUtils.recursionDeleteFile(new File(unZipPath));

		try {
			Utils.UnZipFolder(zipFile.getAbsolutePath(), unZipPath);
		} catch (IOException e) {
			Log.e(TAG, "IOException " + e.getMessage());
		}

		//最后删除下载的Zip文件
		if (zipFile.exists()) {
			zipFile.delete();
		}
	}
	
	public void saveAndUnZipFile(String index, byte[] fileBuffer) {
		if(fileBuffer==null||fileBuffer.length==0){
			Log.e(TAG, "saveAndUnZipFile Error no Data to save");
			return;
		}
		
		final File savedFile = new File(mPracticeFolderPath + "/" + index + ".zip");
		if (savedFile.exists()) {
			savedFile.delete();
		}
		
		String unZipPath=mPracticeFolderPath + "/" + index;
		PathUtils.recursionDeleteFile(new File(unZipPath));
		
		FileOutputStream buffer = null;
		try {
			buffer = new FileOutputStream(savedFile, false);
			buffer.write(fileBuffer);
			buffer.flush();
			
			Utils.UnZipFolder(savedFile.getAbsolutePath(), unZipPath);
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException = " + savedFile.getAbsolutePath());
		} catch (IOException e) {
			Log.e(TAG, "IOException "+e.getMessage());
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
				}
			}
		}
		//最后删除下载的Zip文件
		if (savedFile.exists()) {
			savedFile.delete();
		}
	}
	
	public boolean isDownloaded(String index){
		String practiceJsonPath=mPracticeFolderPath + "/" + index+"/"+JSON_FILE_NAME;
		File file = new File(practiceJsonPath);
		return file.exists();
		
		/*File unzip=new File(mPracticeFolderPath+"/"+index);
		return unzip.exists();*/
	}
	
	public String getAudioPath(int index,String audioName){
		return mPracticeFolderPath + "/" + index+"/"+audioName;
	}
}

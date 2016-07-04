package com.zhan.kykp.spokenSquare;

import java.io.File;

import com.zhan.kykp.util.PathUtils;

public class SpokenSquareUtils {
	public static String getSpokenSquareAudioPath(String answerObjectId) {
		String spokenSquarePath = PathUtils.getExternalSpokenSquareFilesDir().getAbsolutePath();
		return spokenSquarePath + "/" + answerObjectId + ".amr";
	}

	public static boolean hasDownloadAudio(String answerObjectId) {
		File audioFile = new File(getSpokenSquareAudioPath(answerObjectId));
		return audioFile.exists();
	}
}

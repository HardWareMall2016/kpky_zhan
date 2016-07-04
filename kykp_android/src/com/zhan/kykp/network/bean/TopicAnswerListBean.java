package com.zhan.kykp.network.bean;

import java.util.List;

public class TopicAnswerListBean extends BaseBean {
	private List<DatasEntity> datas;

	public void setDatas(List<DatasEntity> datas) {
		this.datas = datas;
	}

	public List<DatasEntity> getDatas() {
		return datas;
	}

	public static class DatasEntity {
		private int praise;
		private String updatedAt;
		private float mark;
		private String objectId;
		private int markerCount;
		private long createdAt;
		private int recordTime;
		private int ismake;
		private int ispraise;
		private int isfollowe;
		private String audio;
		private String nickname;
		private String avatar;
		private String userId;
		private int isteacher;
		private String level;

		public void setPraise(int praise) {
			this.praise = praise;
		}

		public void setUpdatedAt(String updatedAt) {
			this.updatedAt = updatedAt;
		}

		public void setMark(float mark) {
			this.mark = mark;
		}

		public void setObjectId(String objectId) {
			this.objectId = objectId;
		}

		public void setMarkerCount(int markerCount) {
			this.markerCount = markerCount;
		}

		public void setCreatedAt(long createdAt) {
			this.createdAt = createdAt;
		}

		public void setRecordTime(int recordTime) {
			this.recordTime = recordTime;
		}

		public void setIsmake(int ismake) {
			this.ismake = ismake;
		}

		public void setIspraise(int ispraise) {
			this.ispraise = ispraise;
		}

		public void setIsfollowe(int isfollowe) {
			this.isfollowe = isfollowe;
		}

		public void setAudio(String audio) {
			this.audio = audio;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setIsteacher(int isteacher) {
			this.isteacher = isteacher;
		}

		public int getPraise() {
			return praise;
		}

		public String getUpdatedAt() {
			return updatedAt;
		}

		public float getMark() {
			return mark;
		}

		public String getObjectId() {
			return objectId;
		}

		public int getMarkerCount() {
			return markerCount;
		}

		public long getCreatedAt() {
			return createdAt;
		}

		public int getRecordTime() {
			return recordTime;
		}

		public int getIsmake() {
			return ismake;
		}

		public int getIspraise() {
			return ispraise;
		}

		public int getIsfollowe() {
			return isfollowe;
		}

		public String getAudio() {
			return audio;
		}

		public String getNickname() {
			return nickname;
		}

		public String getAvatar() {
			return avatar;
		}

		public String getUserId() {
			return userId;
		}

		public int getIsteacher() {
			return isteacher;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}
	}
}

package com.zhan.kykp.network.bean;

public class MessageStatusBean extends BaseBean {
	/**
	 * datas : {"status":0}
	 */
	private DatasEntity datas;

	public void setDatas(DatasEntity datas) {
		this.datas = datas;
	}

	public DatasEntity getDatas() {
		return datas;
	}

	public static class DatasEntity {
		/**
		 * status : 0
		 */

		private int status;

		public void setStatus(int status) {
			this.status = status;
		}

		public int getStatus() {
			return status;
		}
	}
}

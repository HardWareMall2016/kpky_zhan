package com.zhan.kykp.network.bean;

public class PraiseBean extends BaseBean {
	private DatasEntity datas;

	public DatasEntity getDatas() {
		return datas;
	}

	public void setDatas(DatasEntity datas) {
		this.datas = datas;
	}

	public static class DatasEntity {
		int praise;
		int ispraise;

		public int getPraise() {
			return praise;
		}

		public void setPraise(int praise) {
			this.praise = praise;
		}

		public int getIspraise() {
			return ispraise;
		}

		public void setIspraise(int ispraise) {
			this.ispraise = ispraise;
		}
	}
}

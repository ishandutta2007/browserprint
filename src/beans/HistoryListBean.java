package beans;

import java.util.ArrayList;

public class HistoryListBean {
	private ArrayList<HistoryBean> history;

	public HistoryListBean(){
		history = new ArrayList<HistoryBean>();
	}
	
	public void addHistoryBean(HistoryBean bean){
		history.add(bean);
	}
	
	public ArrayList<HistoryBean> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<HistoryBean> history) {
		this.history = history;
	}
}

package beans;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class HistoryListBean implements Serializable{
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

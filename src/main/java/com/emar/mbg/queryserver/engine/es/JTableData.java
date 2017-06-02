package com.emar.mbg.queryserver.engine.es;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EasyUI DataGrid Model
 * @author JiFengmin
 */
public class JTableData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long total;
	private List<Map<String, Object>> rows;
	// Statistics. eg, Average, Total etc.
	private List<Map<String, Object>> footer;

	public JTableData () {
		this(new ArrayList<Map<String, Object>>(0));
	}
	
	public JTableData (long total) {
		this(total, new ArrayList<Map<String, Object>>(0));
	}
	
	public JTableData (List<Map<String, Object>> rows) {
		this.rows = rows;
	}
	
	public JTableData (long total, List<Map<String, Object>> rows) {
		this.total = total;
		this.rows = rows;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, Object>> rows) {
		this.rows = rows;
	}

	public List<Map<String, Object>> getFooter() {
		return footer;
	}

	public void setFooter(List<Map<String, Object>> footer) {
		this.footer = footer;
	}

	public void addSum(Map<String, Object> sumData) {
		if (this.footer == null) {
			this.footer = new ArrayList<Map<String, Object>>();
		}
		this.footer.add(sumData);
	}
}

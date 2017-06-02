/**
 * 
 */
package com.emar.mbg.queryserver.engine.es;

import org.elasticsearch.client.Client;

/**
 * @author Franplk
 */
public abstract class AbstractESOperation {

	protected int max;
	protected String type;
	protected String prefix;
	protected ESDataSource esDataSource;

	protected abstract JTableData queryTable(ESQuery query);
	
	protected Client getClient() {
		return esDataSource.getClient();
	}
	
	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ESDataSource getEsDataSource() {
		return esDataSource;
	}

	public void setEsDataSource(ESDataSource esDataSource) {
		this.esDataSource = esDataSource;
	}
}

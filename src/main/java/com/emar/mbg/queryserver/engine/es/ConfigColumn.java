package com.emar.mbg.queryserver.engine.es;

import com.emar.mbg.queryserver.constants.QueryServerConstants;

/**
 * 封装域
 * 
 * @author franplk 2016.08.24
 */
public class ConfigColumn extends Column implements Comparable<ConfigColumn> {

	private static final long serialVersionUID = 1L;

	private int dim;//是不是聚合的维度,维度不需要聚合，比如sum() 0:不是，1:是
	private int flag;//是否可见
	private int code;//顺序
	private int sorting;//是否排序。0:否
	private String formula;//公式
	private String exValue;//exclude value。当dim为1时，排除此值
	private String mapName;//别名
	private String mapTitle;//用于前台展示的别名
	private int sortType;
	private int valueType;  //值类型

	public ConfigColumn() {
	}

	public ConfigColumn(String field) {
		super(field);
	}

	public ConfigColumn(String field, String title) {
		this.field = field;
		this.title = title;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getDim() {
		return dim;
	}

	public void setDim(int dim) {
		this.dim = dim;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getSorting() {
		return sorting;
	}

	public void setSorting(int sorting) {
		this.sorting = sorting;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getExValue() {
		return exValue;
	}

	public void setExValue(String exValue) {
		this.exValue = exValue;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapTitle() {
		return mapTitle;
	}

	public void setMapTitle(String mapTitle) {
		this.mapTitle = mapTitle;
	}

	public TableColumn convert2TableColumn() {
		TableColumn tableCol = new TableColumn(field, title);
		tableCol.setSortable(true);
		tableCol.setDataType(dataType);
		if (dim != 1) {
			tableCol.setAlign("right");
		}
		return tableCol;
	}

	@Override
	public int compareTo(ConfigColumn column) {
		int code = column.getCode();
		return this.code - code;
	}

	public int getSortType() {
		return sortType;
	}

	public void setSortType(int sortType) {
		this.sortType = sortType;
	}
	
	public void setSortType(QueryServerConstants.SortType sortType) {
		this.sortType = sortType.getType();
	}

	public int getValueType() {
		return valueType;
	}

	public void setValueType(int valueType) {
		this.valueType = valueType;
	}
	
	
}

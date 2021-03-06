package com.emar.mbg.queryserver.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.emar.mbg.queryserver.constants.ErrorConstants;
import com.emar.mbg.queryserver.constants.QueryServerConstants;
import com.emar.mbg.queryserver.dto.QueryActionDto;
import com.emar.mbg.queryserver.engine.es.ConfigColumn;
import com.emar.mbg.queryserver.engine.es.DateSpan;
import com.emar.mbg.queryserver.engine.es.ESFilter;
import com.emar.mbg.queryserver.engine.es.ESQuery;
import com.emar.mbg.queryserver.engine.es.Pagination;
import com.emar.mbg.queryserver.exceptions.QueryServerException;
import com.emar.mbg.queryserver.utils.DateUtil;
import com.emar.mbg.queryserver.utils.NoSQLDataUtil;
import com.emar.mbg.queryserver.utils.QueryToESParserUtil;

public class GetFlowDomainReportAction extends AbstractQueryAction {
	private boolean todayFlag;
	private String dimColumn = "domain";
	private Map<String,QueryServerConstants.ValueType> resultKeys;
	private Pagination page = new Pagination(1,50);
	private Map<String,String> resultDims;
	
	public boolean isTodayFlag() {
		return todayFlag;
	}

	public void setTodayFlag(boolean todayFlag) {
		this.todayFlag = todayFlag;
	}

	@Override
	protected QueryActionDto getDataFromCache() {
		System.out.println(this.getApiName()+"\t"+this.getCacheDependency()+"\tgetDataFromCache");
		
		QueryActionDto dto = null;
		String key = this.getUrl();
		try{
			dto = (QueryActionDto)NoSQLDataUtil.getEhCache().getObject(key);
			if (dto!=null) {
				//保存进入Cache
				NoSQLDataUtil.getEhCache().setValue(key,dto);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}

	@Override
	protected QueryActionDto getDataFromDB() {
		QueryActionDto dto = this.getTmpDto();
		Map<String,String> params = this.getParameters();
		if (dto == null) {
			ESQuery query = new ESQuery();
			query.setIndex("xview_report_top_500");
			//从ES查询离线数据
			String rep_date = params.get("rep_date");
			DateSpan dateSpan =	QueryToESParserUtil.getDateSpan(rep_date);
			if (dateSpan != null) {
				//有离线查询需求
				query.setDateSpan(dateSpan);
				query.setDimColumn(new ConfigColumn(this.dimColumn));
				
				//查询条件
				List<ESFilter> dimFilterList = QueryToESParserUtil.getDimFilterList(params, this.resultKeys);
				query.setDimFilter(dimFilterList);
				
				//需要计算的列
				List<ConfigColumn> idxList = this.getFieldList();
				query.setIdxList(idxList);
				
				//排序
				List<ConfigColumn> sortList = QueryToESParserUtil.getSortColumn(params, this.resultDims, this.resultKeys);
				ConfigColumn column = sortList.get(0);
				if(column.getSortType()==QueryServerConstants.SortType.DESC.getType()){
					query.setSortType("desc");
				}else {
					query.setSortType("asc");
				}
				query.setSortField(column);
				
				//分页情况
				query.setPage(this.page);
				
				dto =  NoSQLDataUtil.getESHistoryDao().findByQuery(query);
			}
			
			if (dto != null) {
				//有离线数据
				//保存入Cache
				long total_num = dto.getTotal_num();
				if (total_num>0 && this.getCacheDependency() != QueryServerConstants.CacheDependency.HARDLY.getDependency()) {
					//需要保存进入cache
					String key = this.getUrl();
					NoSQLDataUtil.getEhCache().setValue(key,dto);
				}
			}else {
				dto = new QueryActionDto();
				dto.setTotal_num(0);
				dto.setResults(new ArrayList<Object>());
			}
		}
		
		if (todayFlag == true) {
			//查询今日数据
			ESQuery todayQuery = new ESQuery();
			todayQuery.setIndex("report-"+DateUtil.getTodayStr(QueryServerConstants.dateFormatter_old_version));
			
			DateSpan dateSpan = QueryToESParserUtil.getTodayDateSpan();
			
			todayQuery.setDateSpan(dateSpan);
			todayQuery.setDimColumn(new ConfigColumn(this.dimColumn));
			
			//需要计算的列
			List<ConfigColumn> idxList = this.getFieldList();
			todayQuery.setIdxList(idxList);
			
			//排序
			List<ConfigColumn> sortList = QueryToESParserUtil.getSortColumn(params, this.resultDims, this.resultKeys);
			ConfigColumn column = sortList.get(0);
			if(column.getSortType()==QueryServerConstants.SortType.DESC.getType()){
				todayQuery.setSortType("desc");
			}else {
				todayQuery.setSortType("asc");
			}
			todayQuery.setSortField(column);

			//分页情况
			todayQuery.setPage(this.page);
					
			if(dto.getTotal_num()>0) {
				//有离线数据
				//TODO
				List<ESFilter> dimFilterList = QueryToESParserUtil.getDimFilterList(params, this.resultKeys);
				
				List<Object> list = dto.getResults();
				StringBuffer buffer = new StringBuffer();
				for(int i=0;i<list.size();i++) {
					Object obj = list.get(i);
					if(obj instanceof JSONObject) {
						JSONObject itemJson = (JSONObject)obj;
						buffer.append(itemJson.get(this.dimColumn));
						if(i < list.size()-1){
							buffer.append(",");
						}
					}
				}
				if(buffer.length()>0) {
					ESFilter filter = new ESFilter();
					filter.setColumn(new ConfigColumn(this.dimColumn));
					filter.setValue(buffer.toString());
					dimFilterList.add(filter);
				}
				
				todayQuery.setDimFilter(dimFilterList);
				QueryActionDto realtimeDto = NoSQLDataUtil.getESRealtimeDao().findByQuery(todayQuery);
				if(realtimeDto.getTotal_num()>0) {
					//合并离线与实时的指标
					List<String> unikeys = new ArrayList<String>();
					for(String key:this.resultKeys.keySet()) {
						unikeys.add(key);
					}
					Map<String,JSONObject> onlineDtos = new HashMap<String,JSONObject>();
					list = realtimeDto.getResults();
					for(int i=0;i<list.size();i++) {
						Object obj = list.get(i);
						if (obj instanceof JSONObject) {
							JSONObject itemJson = (JSONObject)obj;
							
							String dimValue = itemJson.getString(dimColumn);
							buffer.append(dimValue);
							if (i<list.size()-1) {
								buffer.append(",");
							}
							StringBuffer keyBuffer = new StringBuffer();
							for(int j=0;j<unikeys.size();j++) {
								String key = unikeys.get(j);
								String keyValue = "";
								if(itemJson.has(key)) {
									keyValue = itemJson.getString(key);
								}
								keyBuffer.append(keyValue);
								if (j<unikeys.size()-1) {
									keyBuffer.append("##");
								}
							}
							onlineDtos.put(keyBuffer.toString(), itemJson);
						}
					}
					
					list = dto.getResults();
					List<Object> newDtos = new ArrayList<Object>();
					for(int i=0;i<list.size();i++) {
						Object obj = list.get(i);
						if (obj instanceof JSONObject) {
							JSONObject offlineDto = (JSONObject)obj;
							
							StringBuffer keyBuffer = new StringBuffer();
							for(int j=0;j<unikeys.size();j++) {
								String key = unikeys.get(j);
								String keyValue = "";
								if(offlineDto.has(key)) {
									keyValue = offlineDto.getString(key);
								}
								keyBuffer.append(keyValue);
								if (j<unikeys.size()-1) {
									keyBuffer.append("##");
								}
							}
							JSONObject newDto = null;
							if (onlineDtos.containsKey(keyBuffer.toString())) {
								JSONObject onlineDto = onlineDtos.get(keyBuffer.toString());
								for(String dim:this.resultDims.keySet()) {
									if(onlineDto.has(dim)&&offlineDto.has(dim)) {
										if(this.resultDims.get(dim).equals("int")) {
											int newValue = onlineDto.getInt(dim)+offlineDto.getInt(dim);
											offlineDto.put(dim, newValue);
										}
										if(this.resultDims.get(dim).equals("digit")) {
											double newValue = onlineDto.getDouble(dim)+offlineDto.getDouble(dim);
											offlineDto.put(dim, newValue);
										}
										newDto = offlineDto;
									}
								}
							}else {
								newDto = offlineDto;
							}
							String[] keyValues = keyBuffer.toString().split("##");
							for(int j=0;j<unikeys.size();j++) {
								if(j<keyValues.length){
									if(keyValues[j].equals("")) {
										newDto.put(unikeys.get(j),(Object) null);
									}
								}else{
									newDto.put(unikeys.get(j),(Object) null);
								}
							}
							
							//不额外排序
							newDtos.add(newDto);
						}
					}
					dto.setResults(newDtos);
				}
				
			}else {
				//没有有离线数据
				//查询条件
				List<ESFilter> dimFilterList = QueryToESParserUtil.getDimFilterList(params, this.resultKeys);
				todayQuery.setDimFilter(dimFilterList);
				dto =  NoSQLDataUtil.getESRealtimeDao().findByQuery(todayQuery);
			}
			
			if(dto==null) {
				dto =  new QueryActionDto();
				dto.setTotal_num(0);
			}
			
		}
		
		return dto;
	}
	
	@Override
	protected void prepare() throws Exception {
		//同时支持两个接口
		String apiName = this.getApiName();
		if(apiName.equals("get_flow_domain_report")) {
			this.dimColumn = "domain";
		}else {
			if(apiName.equals("get_flow_adslot_report")) {
				this.dimColumn = "adslot_id";
			}
		}
		
		String key = "page_num";
		if (this.getParameters().containsKey(key)) {
			try{
				int page_num = Integer.parseInt(this.getParameters().get(key).toString());
				if (page_num <1) {
					page_num = 1;
				}
				this.page.setCurrPage(page_num);
			}catch (Exception e) {
				QueryServerException exception = new QueryServerException();
				exception.setErrorCode(ErrorConstants.ERROR_CODE.PARAM_ERROR);
				exception.setDescription(key+"参数错误");
				throw exception;
			}
		}
		
		key = "page_size";
		if (this.getParameters().containsKey(key)) {
			try{
				int page_size = Integer.parseInt(this.getParameters().get(key).toString());
				if (page_size < QueryServerConstants.min_page_size) {
					page_size = QueryServerConstants.default_page_size;
				}else {
					if (page_size > QueryServerConstants.max_page_size) {
						page_size = QueryServerConstants.max_page_size;
					}
				}
				this.page.setPageSize(page_size);
			}catch (Exception e) {
				QueryServerException exception = new QueryServerException();
				exception.setErrorCode(ErrorConstants.ERROR_CODE.PARAM_ERROR);
				exception.setDescription(key+"参数错误");
				throw exception;
			}
		}
		
		key = "rep_date";
		String rep_date_param = this.getParameters().get(key);
		if (rep_date_param == null) {
			QueryServerException exception = new QueryServerException();
			exception.setErrorCode(ErrorConstants.ERROR_CODE.PARA_LOSS.getCode());
			exception.setDescription("缺少必要参数"+key);
			throw exception;
		} else {
			DateSpan dateSpan = QueryToESParserUtil.getDateSpan(rep_date_param, false);
			if(dateSpan == null) {
				QueryServerException exception = new QueryServerException();
				exception.setErrorCode(ErrorConstants.ERROR_CODE.PARAM_ERROR);
				exception.setDescription(key+"参数错误");
				throw exception;
			}else {
				Date today = DateUtil.getToday();
				Date startDate = DateUtil.getDate(dateSpan.getStartDate(),QueryServerConstants.dateFormatter);
				Date endDate = DateUtil.getDate(dateSpan.getEndDate(),QueryServerConstants.dateFormatter);
				if(today.compareTo(startDate)>=0 && today.compareTo(endDate)<=0) {
					//包含今天
					this.setCacheDependency(QueryServerConstants.CacheDependency.PARTLY);
					this.setTodayFlag(true);
				}else {
					//不包含今天
					this.setCacheDependency(QueryServerConstants.CacheDependency.FULLY);
					this.setTodayFlag(false);
				}
			}
		
		}
		
		key="group_by";
		if(this.getParameters().containsKey(key)) {
			String dim = this.getParameters().get(key);
			if(dim.equals("domain")||dim.equals("rep_date")||dim.equals("channel_id")||dim.equals("creative_id")||dim.equals("adslot_id")) {
				this.dimColumn = dim;
			}else {
				QueryServerException exception = new QueryServerException();
				exception.setErrorCode(ErrorConstants.ERROR_CODE.PARAM_ERROR);
				exception.setDescription(key+"参数错误");
				throw exception;
			}
		}
		
		this.resultKeys = new HashMap<String,QueryServerConstants.ValueType>();
		this.resultKeys.put("domain", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("rep_date", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("channel_id", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("subdomain", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("flow_type", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("adslot_id", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("creative_id", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("category_id", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("campaign_id", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("project_id", QueryServerConstants.ValueType.STRING);
		this.resultKeys.put("trade_type", QueryServerConstants.ValueType.INT);
		this.resultKeys.put("is_rd", QueryServerConstants.ValueType.INT);
		
		
		this.resultDims = new HashMap<String,String>();
		this.resultDims.put("req_num", "int");
		this.resultDims.put("bid", "int");
		this.resultDims.put("imp", "int");
		this.resultDims.put("clk", "int");
		this.resultDims.put("bid_cost", "digit");
		this.resultDims.put("rtb_cost", "digit");
		this.resultDims.put("order_num", "int");
		this.resultDims.put("order_price", "digit");
		this.resultDims.put("rtb_price", "digit");
		this.resultDims.put("arrival_num", "int");
		this.resultDims.put("pv", "int");
		this.resultDims.put("stop_time", "int");
		this.resultDims.put("jump_num", "int");
		this.resultDims.put("reg_num", "int");
		this.resultDims.put("shop_cart", "int");
		this.resultDims.put("shop_cart_price", "digit");
		this.resultDims.put("uv", "int");
		this.resultDims.put("activate_num", "int");
	}
	
	private List<ConfigColumn> getFieldList() {
		List<ConfigColumn> idxList = new ArrayList<ConfigColumn>();
		for(String field:this.resultDims.keySet()) {
			ConfigColumn column =new ConfigColumn(field);
			column.setDim(1);
			column.setDataType(this.resultDims.get(field));
			
			idxList.add(column);
		}
		
		return idxList;
	}
}

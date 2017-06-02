package com.emar.mbg.queryserver.utils;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


public class UrlUtil {
	/**
	 * 从url中截取接口名称
	 * @param url
	 * @return
	 */
	public static String getApiName(String url){
		String result = "";
		try {
			int i = url.lastIndexOf("/");
			String substr = url.substring(i+1);
			int end = substr.indexOf("?");
			if (end >0) {
				result = substr.substring(0,substr.indexOf("?"));
			}else {
				result = substr.substring(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取url里的参数
	 * @return
	 */
	public static Map<String,String> getParameters(String url){
		Map<String,String> parameters = new HashMap<String,String>();
		
		try {

			int end = url.indexOf("?");
			if (end >0) {
				String[] infos = url.substring(end+1).split("&");
				for (String info:infos) {
					try{
						String [] phrases = info.split("=");
						String key = phrases[0];
						String value = URLDecoder.decode(phrases[1],"utf-8");
						parameters.put(key, value);
					}catch (Exception e) {
						
					}
					
				}
			}
		}catch (Exception e) {
			
		}
		
		return parameters;
	}



	public static void main(String [] args ) {
		String url="/hello?uid=1";
		System.out.println(UrlUtil.getApiName(url));
		
		Map<String,String> parameters = UrlUtil.getParameters(url);
		if(parameters.containsKey("uid")){
			System.out.println("uid:\t"+parameters.get("uid"));
		}
	
		url = "/hello1";
		System.out.println(UrlUtil.getApiName(url));
	}
			
}

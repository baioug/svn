package cn.phoniex.ssg.util;

public class GetStrValue {
	
	public static  String getValueOf(long value) {
		String valueStr;
		if (value == 0) {
			
			return "0KB";
		}
		if (value < 1024) {
			valueStr = String.valueOf(value)+"Byte";
		}else if (value < 1024*1024) {
			valueStr = String.valueOf(value/1024)+"Kb";
		}else if (value<1024*1024*1024) {
			valueStr = String.valueOf(value/(1024*1024))+"MB";
		}else if (value<1024*1024*1024*1024) {
			valueStr = String.valueOf(value/(1024*1024*1024))+"GB";
		}else {
			valueStr = String.valueOf(value/(1024*1024*1024*1024))+"TB";
		}
		return valueStr;
	}
	
	public static  String getStrByArg(long value, int mode) {
		String valueStr;
		for (int i = 0; i < mode; i++) {
			value = value * 1024;
		}
		if (value < 1024) {
			valueStr = String.valueOf(value)+"Byte";
		}else if (value < 1024*1024) {
			valueStr = String.valueOf(value/1024)+"Kb";
		}else if (value<1024*1024*1024) {
			valueStr = String.valueOf(value/(1024*1024))+"MB";
		}else if (value<1024*1024*1024*1024) {
			valueStr = String.valueOf(value/(1024*1024*1024))+"GB";
		}else {
			valueStr = String.valueOf(value/(1024*1024*1024*1024))+"TB";
		}
		return valueStr;
	}

}

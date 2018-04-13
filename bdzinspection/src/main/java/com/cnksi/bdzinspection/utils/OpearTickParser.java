package com.cnksi.bdzinspection.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OpearTickParser {

	public static OperateTick parseOpearTick(String path) {
		String encoding = "gbk";
		OperateTick operateTick = new OperateTick();
		
		
		String content = readTxtFile(path,encoding);
		
		//对每行trim,如果是空行去掉
		content = rowTrim(content);
		
		
		//在第一页中获取单位、编号、任务名称
		String page1 = content.split(OperateTick.pageSplit)[0];
		
		int startIndex = page1.indexOf(OperateTick.unitStartSplits)+OperateTick.unitStartSplits.length();
		int endIndex = page1.indexOf(OperateTick.unitEndStartSplits);
		String s = page1.substring(startIndex, endIndex).replace(" ", "").replace("\n", "");
		operateTick.setUnit(s);
		
		startIndex = page1.indexOf(OperateTick.codeStartSplit)+OperateTick.codeStartSplit.length();
		endIndex = page1.indexOf(OperateTick.codeEndSplit);
		s = page1.substring(startIndex, endIndex).replace(" ", "").replace("\n", "");
		operateTick.setCode(s);
		
		startIndex = page1.indexOf(OperateTick.taskStartSplit)+OperateTick.taskStartSplit.length();
		endIndex = page1.indexOf(OperateTick.taskEndSplit);
		s = page1.substring(startIndex, endIndex).replace(" ", "").replace("\n", "").replace(OperateTick.taskSplit, "");
		operateTick.setTaskName(s);
		
				
		
		
		//获取所有的操作项
		//去除打印的页面信息
		content = removePageInfo(content);
		
		//处理一个操作在多行的问题
		ArrayList<String> opears = hanleMultRowOpear(content);
		operateTick.setOpears(opears);
		return operateTick;
	}

	/**
	 * 处理一个操作在多行的问题
	 */
	private static ArrayList<String> hanleMultRowOpear(String content) {
		ArrayList<String> opears = new ArrayList<String>();
		
		String[] rows = content.split("\n");
		int currentRowNum = 1;String currentRowContent = "";
		for (String row : rows) {
			String startChar = currentRowNum+"  ";
			
			if(row.startsWith(startChar)){		//新的一行
				row = row.substring(startChar.length());
				
				//将前一行的内容添加到list
				if(currentRowContent.length()>0)opears.add(currentRowContent);
				
				//num+1
				currentRowNum ++;
				
				//content设为新一行的内容
				currentRowContent = row;
			}else{
				
				currentRowContent = currentRowContent + row;
			}
		}
		if(currentRowContent.length()>0)opears.add(currentRowContent);
		return opears;
	}

	private static String rowTrim(String content) {
		StringBuilder result = new StringBuilder();
		
		String[] rows = content.split("\n");
		for (String row : rows) {
			
			//trim
			row = row.trim();
			
			//去掉空行
			if(row.length() <= 0) continue;
				
			result.append(row+"\n");
		}
				
		return result.toString();
	}

	/**
	 * 去除打印的页面信息
	 */
	private static String removePageInfo(String content) {
		StringBuilder result = new StringBuilder();
		
		//区分出每页
		String[] pages = content.split(OperateTick.pageSplit);
		
		for (String page : pages) {
			
			if(page.trim().length() <= 0) continue;
			
			//页面开始内容
			
			int begin = page.indexOf(OperateTick.startSplit);
			
			//页面结束内容
			
			int end = page.lastIndexOf(OperateTick.endSplit);
			page = page.substring(begin+OperateTick.startSplit.length(),end);
			
			result.append(page+"\n");
		}
		
		return result.toString();
	}

	public static String readTxtFile(String filePath,String encoding) {
		StringBuilder result = new StringBuilder();
		try {
			if(encoding == null)encoding = "iso8859-1";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result.append(lineTxt+"\n");
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		
		return result.toString();

	}

}

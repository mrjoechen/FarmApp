package com.xindany.tao_fen_ny;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * 此类用来操作http请求
 *
 */
public class HttpRequestUtil {
	/**
	 * 发送GET请求
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	public static URLConnection sendGetRequest(String url,
			Map<String, String> params, Map<String, String> headers)
			throws Exception {
		StringBuilder buf = new StringBuilder(url);
		Set<Entry<String, String>> entrys = null;
		// 如果是GET请求，则请求参数在URL中
		if (params != null && !params.isEmpty()) {
			buf.append("?");
			entrys = params.entrySet();
			for (Map.Entry<String, String> entry : entrys) {
				buf.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), "UTF-8"))
						.append("&");
			}
			buf.deleteCharAt(buf.length() - 1);
		}
		URL url1 = new URL(buf.toString());
		HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
		conn.setRequestMethod("GET");
		// 设置请求头
		if (headers != null && !headers.isEmpty()) {
			entrys = headers.entrySet();
			for (Map.Entry<String, String> entry : entrys) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		conn.getResponseCode();
		return conn;
	}
	/**
	 * 发送POST请求
	 * @param url	
	 * @param params
	 * @param headers
	 * @return 
	 * @throws Exception
	 */
	public static URLConnection sendPostRequest(String url,
			Map<String, String> params, Map<String, String> headers)
			throws Exception {
		StringBuilder buf = new StringBuilder();
		Set<Entry<String, String>> entrys = null;
		// 如果存在参数，则放在HTTP请求体，形如name=aaa&age=10
		if (params != null && !params.isEmpty()) {
			entrys = params.entrySet();
			for (Map.Entry<String, String> entry : entrys) {
				buf.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), "UTF-8"))
						.append("&");
			}
			buf.deleteCharAt(buf.length() - 1);
		}
		URL url1 = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		OutputStream out = conn.getOutputStream();
		out.write(buf.toString().getBytes("UTF-8"));
		if (headers != null && !headers.isEmpty()) {
			entrys = headers.entrySet();
			for (Map.Entry<String, String> entry : entrys) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		conn.getResponseCode();  // 为了发送成功
		return conn;
	}
	
	/**
	 *将输入流转为字节数组
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] read2Byte(InputStream inStream)throws Exception{
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inStream.read(buffer)) !=-1 ){
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}
	/**
	 *将输入流转为字符串
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static String read2String(InputStream inStream)throws Exception{
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inStream.read(buffer)) !=-1 ){
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return new String(outSteam.toByteArray(),"UTF-8");
	}
	/**
	 * 发送xml数据
	 * @param path 请求地址
	 * @param xml xml数据
	 * @param encoding 编码
	 * @return
	 * @throws Exception
	 */
	public static byte[] postXml(String path, String xml, String encoding) throws Exception{
		byte[] data = xml.getBytes(encoding);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "text/xml; charset="+ encoding);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setConnectTimeout(5 * 1000);
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();
		if(conn.getResponseCode()==200){
			return read2Byte(conn.getInputStream());
		}
		return null;
	}
	//测试函数
	public static void main(String args[]) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "xiazdong");
		params.put("age", "10");
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil
				.sendGetRequest(
						"http://192.168.0.103:8080/Server/PrintServlet",
						params, null);
		int code = conn.getResponseCode();
		InputStream in = conn.getInputStream();
		byte[]data = read2Byte(in);
	}
}
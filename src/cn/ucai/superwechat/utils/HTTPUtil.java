package cn.ucai.superwechat.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class HTTPUtil {
	public static void main(String[] args) {
		/*String httpUrl = "https://a1.easemob.com/i/superwechat201610/chatrooms/";
		String authorization = "Bearer YWMtjegRkKYdEeao5amvG2ZEMAAAAAAAAAAAAAAAAAAAAAEDVLoAnxYR5r9Ia2MF9bssAgMAAAFYRr668wBPGgBPEzqhygGx4l1_i8Ho7QbgGylncuZoSqPFY_bBI3IunA";
		String str = "{\"name\":\"chatroomTest\",\"description\":\"server create chatroom\",\"owner\":\"ccc7788\",\"maxusers\":300,\"members\":[]}";
		String jsonResult = requestPost(httpUrl, str,authorization);
//		String jsonResult = requestGet(httpUrl,authorization);
		System.out.println(jsonResult);*/
		
//		System.out.println(getToken());
		
	}

	public static String getToken(){
		JSONObject object = new JSONObject();
		try {
			object.put("grant_type", PropertiesUtils.getValue("grant_type", "token.properties"));
			object.put("client_id", PropertiesUtils.getValue("client_id", "token.properties"));
			object.put("client_secret", PropertiesUtils.getValue("client_secret", "token.properties"));
			String result = requestPost(I.REQUEST_URL_GET_TOKEN,object.toString(),null);
			JSONObject tokenJson = new JSONObject(result);
			return "Bearer "+tokenJson.getString("access_token");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param urlAll
	 *            :请求接口
	 * @param strJson
	 *            :参数
	 * @return 返回结果
	 */
	public static String requestPost(String httpUrl, String strJson,String authorization) {
	    BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();

	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type",
	                        "application/x-www-form-urlencoded");
	        // 填入apikey到HTTP header
	        if(authorization!=null){
	        	connection.setRequestProperty("Authorization",authorization);
	        }
	        connection.setDoOutput(true);
	        if(strJson!=null){
	        	connection.getOutputStream().write(strJson.getBytes("UTF-8"));
	        }
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}

	/**
	 * @param urlAll
	 *            :请求接口
	 * @param httpArg
	 *            :参数
	 * @return 返回结果
	 */
	public static String requestGet(String httpUrl,String authorization) {
	    BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();

	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        // 填入apikey到HTTP header
	        connection.setRequestProperty("Authorization",authorization);
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	/**
	 * @param urlAll
	 *            :请求接口
	 * @param httpArg
	 *            :参数
	 * @return 返回结果
	 */
	public static String requestDelete(String httpUrl,String authorization) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("DELETE");
			connection.setRequestProperty("Authorization",authorization);
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

package cn.ucai.superwechat.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import cn.ucai.superwechat.bean.Result;

public class JsonUtil {
	private static final ObjectMapper mapper = new ObjectMapper();
	public static void writeJsonToClient(Result result , HttpServletResponse response){
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			mapper.writeValue(pw, result);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(pw!=null){
				pw.close();
			}
		}
	}
	public static void writeJsonToClient(String json , HttpServletResponse response){
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.println(json);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(pw!=null){
				pw.close();
			}
		}
	}
}

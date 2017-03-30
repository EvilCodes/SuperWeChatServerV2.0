package cn.ucai.superwechat.utils;

public class Utils {
	public static boolean int2boolean(int i) {
		if(i>0){
			return true;
		}
		return false;
	}
	
	public static int boolean2int(boolean i) {
		if(i){
			return 1;
		}
		return 0;
	}
}

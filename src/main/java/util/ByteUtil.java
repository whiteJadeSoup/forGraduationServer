package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class ByteUtil {

	public static byte[] byteMergerAll(byte[]... values) {
	    int length_byte = 0;
	        for (int i = 0; i < values.length; i++) {
	            length_byte += values[i].length;
	        }
	        
	        
	        byte[] all_byte = new byte[length_byte];
	        int countLength = 0;
	        for (int i = 0; i < values.length; i++) {
	            byte[] b = values[i];
	            System.arraycopy(b, 0, all_byte, countLength, b.length);
	            countLength += b.length;
	        }
	        return all_byte;
	}
	
	
	// 生成指定长度的字符串
	public static String getRandomString(int length){
	    //定义一个字符串（A-Z，a-z，0-9）即62位；
	    String str="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
	    //由Random生成随机数
	        Random random=new Random();  
	        StringBuffer sb=new StringBuffer();
	        //长度为几就循环几次
	        for(int i=0; i<length; ++i){
	          //产生0-61的数字
	          int number=random.nextInt(62);
	          //将产生的数字通过length次承载到sb中
	          sb.append(str.charAt(number));
	        }
	        //将承载的字符转换成字符串
	        return sb.toString();
	  }
	
	
	
	
	
	public static byte[] toLH(int n) {  
		  byte[] b = new byte[4];  
		  b[0] = (byte) (n & 0xff);  
		  b[1] = (byte) (n >> 8 & 0xff);  
		  b[2] = (byte) (n >> 16 & 0xff);  
		  b[3] = (byte) (n >> 24 & 0xff);  
		  return b;  
		}
	
}

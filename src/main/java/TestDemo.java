import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.crypto.Data;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import util.CacheUtil;
import util.DBUtil;

public class TestDemo {
	
	
	
	
	
	private void checkSymmetric(Pairing pairing) {
		if (!pairing.isSymmetric()) {
			throw new RuntimeException("密钥不对称!");
		}
	}
	
	public void test() throws Exception {
		Pairing pairing = PairingFactory.getPairing("src/main/resources/a.properties");//
		PairingFactory.getInstance().setUsePBCWhenPossible(true);
		checkSymmetric(pairing);
		
		
		
		
		
		Element x1 = pairing.getZr().newRandomElement();
		Element x2 = pairing.getZr().newRandomElement();
		Element x3 = pairing.getZr().newRandomElement();
		Element x4 = pairing.getZr().newRandomElement();
		
		
		Element  g1 = pairing.getG1().newRandomElement();
		System.out.println(g1);
		System.out.println(g1.toBytes());
		System.out.println(g1.toBytes());
		
		
//		Element g1x1 = g1.getImmutable().powZn(x1);
//		System.out.println(g1x1);		
		byte[] testg1 = g1.toBytes();
		System.out.println(testg1);
		
		byte[] testg2 = g1.toBytes();
		System.out.println(testg2);
		
		
		byte[] testg3 = g1.toBytes();
		System.out.println(testg3);
		
		
		byte[] testg4 = g1.toBytes();
		System.out.println(testg4);
		
		byte[] testg5 = g1.toBytes();
		System.out.println(testg5);
		String str1 = new String(testg5, "iso-8859-1");
		Element g1copy = pairing.getG1().newElementFromBytes(testg1);
		
		if (g1copy.equals(g1)) {
			System.out.println("!!!!!!!!!!!!!!");
		}
	
		
		
		//string to bytes

		byte[] strBytes = str1.getBytes("iso-8859-1");
		System.out.println(strBytes);
		Element g1copy2 = pairing.getG1().newElementFromBytes(strBytes);
		if (g1copy2.equals(g1)) {
			System.out.println("what?>>");
		}
		
		
		
		Element g1copy1 = pairing.getG1().newElementFromBytes(testg5);
		
		if (g1copy1.equals(g1)) {
			System.out.println("!!!!!!!!!!!!!!");
		}
		
		
		if (g1copy1.equals(g1copy)) {
			System.out.println("COPY1 == COPY!!!!!!!!!!!!!!");
		}
		
		
		
		//写入文件
		File file = new File("generateG1.data");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileOutputStream fw = new FileOutputStream(file);
		fw.write(testg5);
		fw.close();
		
		System.out.println("写入文件成功！");
		
		
		
		//读入文件
		FileInputStream in = new FileInputStream(file);
		byte[] testCopy5 = in.readAllBytes();
		in.close();
		
		
		Element g1test5copy = pairing.getG1().newElementFromBytes(testCopy5);
		
		if (g1test5copy.equals(g1)) {
			System.out.println("testcopy5 !!!!!!!!!!!!!!");
		}
		
		
		
		
		
		
		
		
		
		System.out.println(g1copy);
		System.out.println(g1copy.toBytes());
		System.out.println(g1copy.toBytes());
		System.out.println(g1copy.toBytes());
		
		

		
		
//		Element g1copy = pairing.getG1().newElement();
//		g1copy.setFromBytes(g1.toString().getBytes());
//		
		
//		
	}
	
	

	public static void main(String[] args) throws Exception {
		//1 得到现在的时间
		//2 相加减得到截止时间
		
		//3 如何将datatime set sql？
		
		Date date = new Date();
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.println(dateFormat.format(date));
		
		Date newTime = new Date(date.getTime() + 60 *1000);
		System.out.println(dateFormat.format(newTime));
		
		

		Timestamp timeStamep = new Timestamp(date.getTime());
		System.out.println(dateFormat);
		
		
//		
//		Calendar calendar = new GregorianCalendar();
//
//		calendar.setTime(date); 
//		calendar.add(calendar.MINUTE, 1);//把日期往后增加
//		
//		Date newTime = new Date(calendar.getTime());  
		CacheUtil.init();
		DBUtil.init();
//		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into selection_info(selectionId, beginTime, endTime) values(?, ?, ?)");
		
		PreparedStatement psql = DBUtil.con.prepareStatement(sql.toString());
		psql.setString(1, "11"); 
		psql.setTimestamp(2, new Timestamp(date.getTime()));
		psql.setTimestamp(3, new Timestamp(newTime.getTime()));
//		
		psql.executeUpdate();
		
//		ResultSet rs = psql.executeQuery();
//		 if (rs.next()) {
//	            int num = rs.getInt(1);    
//	            System.out.println(num);
//		 }
//		

        
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	

}

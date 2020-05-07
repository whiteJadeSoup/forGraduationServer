package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import it.unisa.dia.gas.jpbc.Element;

public class DBUtil {
	
	public static Connection con = null;
	//驱动程序名
	static  final String driver = "com.mysql.cj.jdbc.Driver";
	//URL指向要访问的数据库名mydata
	static final String url = "jdbc:mysql://localhost:3306/forgraduation";
	//MySQL配置时的用户名
	static final String user = "root";
	//MySQL配置时的密码
	static final String password = "root";
	
	
	public static void doInit() throws ClassNotFoundException, SQLException {
		init();
		doBackup();
	}
	
	
	public static void init () throws ClassNotFoundException, SQLException {
		if (con == null) {
			Class.forName(driver);
			//1.getConnection()方法，连接MySQL数据库！！
			con = DriverManager.getConnection(url,user,password);
			if(!con.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			
		}
	}
	
	
	
	
	private static void doBackup() throws ClassNotFoundException, SQLException {
//		Class.forName(driver);
//		//1.getConnection()方法，连接MySQL数据库！！
//		con = DriverManager.getConnection(url,user,password);
//		if(!con.isClosed())
//			System.out.println("Succeeded connecting to the Database!");
		
		
		
		
		
		//去收集用户的key信息
		StringBuilder sql = new StringBuilder();
		sql.append("select voteId, publicKey, commonKey from user_key_info");
		
		
		PreparedStatement psql = DBUtil.con.prepareStatement(sql.toString());
		ResultSet rs = psql.executeQuery();
		
		
		while (rs.next()) {
			int id = rs.getInt(1);
			byte[] pkBytes = rs.getBytes(2);
			byte[] commonKeyBytes = rs.getBytes(3);
			
			
			
			Element pkElement = CacheUtil.gPairing.getG1().newElementFromBytes(pkBytes);
			Element commonKeyElement = CacheUtil.gPairing.getG1().newElementFromBytes(commonKeyBytes);
			
			System.out.println("-----------------------");
			System.out.println("voteid: " + id);
			System.out.println("pk: " + pkElement);
			System.out.println("commonKey: " + commonKeyElement);
			System.out.println("-----------------------");
			
			
			
			CacheUtil.gPublickKeys.put(id, pkElement);
			CacheUtil.gCommonKeys.put(id, commonKeyElement);
			
		}
		
		
		//todo:  获得最后的选票信息
		//暂时不考虑
		
		
		// 去收集用户的选票的信息。
		sql = new StringBuilder();
		sql.append("select voteId, selectionId, ballotInfo from ballot_info");
		
		
		psql = DBUtil.con.prepareStatement(sql.toString());
		rs = psql.executeQuery();
		while (rs.next()) {
			int id = rs.getInt(1);
			String selectionId = rs.getString(2);
			byte[] ballotBytes = rs.getBytes(3);
			
			
			Element ballotElement = CacheUtil.gPairing.getGT().newElementFromBytes(ballotBytes);
			ConcurrentHashMap<Integer, Element> m =  
					CacheUtil.gVotes.getOrDefault(selectionId, new ConcurrentHashMap<Integer, Element>());
			
			m.put(id, ballotElement);
			
			
			
			System.out.println("-----------------------");
			System.out.println("voteid: " + id);
			System.out.println("selectionId: " + selectionId);
			System.out.println("ballotElement: " + ballotElement);
			System.out.println("-----------------------");
			
		}
		
		
		
		
		
		
		
	}
	
}

package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class CacheUtil {
	public static Map<Integer, Element> gPublickKeys = new ConcurrentHashMap<>();

	
	//
	//public static Map<Integer, Element> gVotes = new ConcurrentHashMap<>();
	//<selectionid,  <voteid， vote> >
	public static ConcurrentHashMap<String, ConcurrentHashMap<Integer, Element>> gVotes = new ConcurrentHashMap<
			String,  ConcurrentHashMap<Integer, Element> >();
	
	
	public static Map<Integer, Element> gCommonKeys = new ConcurrentHashMap<>();
	
	
	//选举id -> 结果
	public static Map<String, Integer> gVoteResults = new ConcurrentHashMap<>();
	
	
	
	
	public static int gVoteNum = 0;
	public static Pairing gPairing = null;
	public static String gSelectionId;
	public static Element gGeneratorG1 = null;
	public static Date gDeadLine;
	
	
	private static final String generatorFile = "generatorG1.data";
	
	public static void init() throws Exception {
		//0 初始化
		gPairing = PairingFactory.getInstance().getPairing("a.properties");//
		PairingFactory.getInstance().setUsePBCWhenPossible(true);
		if (!gPairing.isSymmetric()) {
			throw new RuntimeException("密钥不对称!");
		}
		
		
		
		
		
		//1 g1生成元
		//是否生成了g1
		gGeneratorG1 = gPairing.getG1().newElement();
		File file = new File(generatorFile);
		if (file.exists()) {
			//存在了。 
			FileInputStream in = new FileInputStream(file);
			
			byte[] generatorBytes = in.readAllBytes();
			in.close();
			
			
			
			gGeneratorG1.setFromBytes(generatorBytes);
			System.out.println("g1 read sk from file: " + gGeneratorG1);
			
		} else {
			//不存在
			//3 没有 就生成一个。 
			// 并且存在当地。
			gGeneratorG1 = gPairing.getG1().newRandomElement();
			System.out.println("now, we know generatorg1: "  + gGeneratorG1);
			
			
			FileUtil.WriteToFile( generatorFile, gGeneratorG1.toBytes());
		}
		
		
		//2 选举的id  先随机生成
		gSelectionId = ByteUtil.getRandomString(128);
		
		//现在的时候
		Date beginTime = new Date();
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		gDeadLine = new Date(beginTime.getTime() + 2 * 60 *1000); // 两分钟
		System.out.println("endTime: " + dateFormat.format(gDeadLine));
		
		
		//插入到数据库
		DBUtil.init();
		StringBuilder sql = new StringBuilder();
		sql.append("insert into selection_info(selectionId, beginTime, endTime) values(?, ?, ?)");
		
		PreparedStatement psql = DBUtil.con.prepareStatement(sql.toString());
		psql.setString(1, gSelectionId); 
		psql.setTimestamp(2, new Timestamp(beginTime.getTime()));
		psql.setTimestamp(3, new Timestamp(gDeadLine.getTime()));		
		psql.executeUpdate();
		
		
		
		System.out.println("g1: " + gGeneratorG1 + " selection id: " + gSelectionId);
		
		
	}
}

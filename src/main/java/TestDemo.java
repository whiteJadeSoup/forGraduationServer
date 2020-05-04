import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

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
		// TODO Auto-generated method stub
//		List<String> src = new ArrayList<String>();
//		src.add("msgpack");
//		src.add("kumofs");
//		src.add("viver");
//
//		
//		MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
//        packer.packInt(1);
//        packer.packString("leo");
//        packer.packArrayHeader(src.size());
//        
//        
//        for (String v : src) {
//        	packer.packString(v);
//        }
//        packer.close(); // Never forget to close (or flush) the buffer
//        byte[] res = packer.toByteArray();
//        System.out.println(res);
        
        
        
        TestDemo d = new TestDemo();
        d.test();
        
        
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	

}

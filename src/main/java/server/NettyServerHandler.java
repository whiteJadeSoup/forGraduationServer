package server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.CalCommonKeyProto;
import com.test.proto.GetCommonParamProto;
import com.test.proto.GetCommonParamProto.GetCommonParamResponse.Builder;
import com.test.proto.GetVoteResultProto;
import com.test.proto.GoVoteProto;
import com.test.proto.PublicKeyProto;
import com.test.proto.PublicKeyProto.BoardcastPublicKeyRequest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import net.sf.json.JSONObject;
import util.ByteUtil;
import util.CacheUtil;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
	//private HashMap<Integer, Element> mPublickKeys = new HashMap<Integer, Element>();  
	
	// 返回公共参数
	private final String getCommonParam = "/getCommonParam";
	
	// 传播私钥
	private final String boardcastPrivateKey = "/boardcastPrivateKey";
	
	// 计算g_{y_{i}}
	private final String calCommonGyi = "/calCommonGyi";
	
	// 投票
	private final String vote =  "/goVote";
	
	// 计算结果
	private final String getVoteResult = "/getVoteResult";
	
	// 设置下一个选举。
	private final String goNextSelection = "/goNextSelection";
	
	
	
	//private int id ;
	
	public NettyServerHandler() {
		
	}
	

	
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (! (msg instanceof FullHttpRequest)) {
			String result = "Unkown request!";
			send(ctx, result, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		
		
		
		
		FullHttpRequest httpRequest = (FullHttpRequest) msg;
		try {
			String path = httpRequest.uri();
			String body = getBody(httpRequest);
			HttpMethod method = httpRequest.method();
			
			
			System.out.println("path: " + path);
			System.out.println("method: " + method);
			
			
			String result = "";
			switch (path) {
			case getCommonParam: 
				result = handleGetCommonParam(body);
				break;
				
				
			case boardcastPrivateKey:
				result = handleBoardcastPrivateKey(body);
				break;
				
				
			case calCommonGyi: 
				result = handleCalCommonGyi(body);
				break;
				
				
			case vote: 
				result = handleGoVote(body);
				break;
				
				
			case getVoteResult:
				result = handleGetVoteResult(body);
				break;
				
				
			case goNextSelection:
				result = handleGoNextSelection(body);
				break;
			
			}
			
			send(ctx, result, HttpResponseStatus.ACCEPTED);
			
			
		} catch (Exception e) {
			System.out.println("Failed to handle request");
			e.printStackTrace();
			
		} finally {
			httpRequest.release();
			
		}
		
		
		
	}
	
	
	private String handleGoNextSelection(String body) {
		System.out.println("json: " + body);
		String id = ByteUtil.getRandomString(128); ;
		
		//1 json来解析一下。
		try {
			JSONObject jsonobj = JSONObject.fromObject(body); 
			id = jsonobj.getString("SelectionId");
			
		} catch (Exception e){
		}
		
		
		
		// 是不是有selectionId?
		if (id != "") {
			CacheUtil.gSelectionId = id;
			
		} else {
			CacheUtil.gSelectionId = ByteUtil.getRandomString(128);
			
		}
		
		
		System.out.println("selectionid: " + CacheUtil.gSelectionId);
		return "true";	
	}
	
	
	
	
	private String handleGetVoteResult(String body) throws Exception {
		GetVoteResultProto.GetVoteResultResponse.Builder responseBuilder = 
				GetVoteResultProto.GetVoteResultResponse.newBuilder();

		
		//1 反序列化
		byte[] bodyBytes = body.getBytes("iso-8859-1");
		GetVoteResultProto.GetVoteResultRequest  request =  
				GetVoteResultProto.GetVoteResultRequest.parseFrom(bodyBytes);
		
		
		System.out.println("请求的选举id: " + request.getSelectionId());
		
		
				
		int statusCode  = 0;
		responseBuilder.setStatusCode(statusCode);
		responseBuilder.setSelectionId(request.getSelectionId());
		
		System.out.println("response id: "  + responseBuilder.getSelectionId());
		
		
		
		//已经计算过了
		//直接返回
		String selectionId = CacheUtil.gSelectionId;
		
		int prevVoteResult = CacheUtil.gVoteResults.getOrDefault(selectionId, -1);
		if (prevVoteResult != -1) {
			responseBuilder.setVoteResult(prevVoteResult);
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
			
		}
		
		
		
		
		// 选举标识符没有设置
		if (selectionId == "") {
			statusCode = 5;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("没有设置选举标识符!");
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		
		
		//请求的不一样
		if (!request.getSelectionId().equalsIgnoreCase(CacheUtil.gSelectionId)) {
			responseBuilder.setExtra("当前选举标识符: " + CacheUtil.gSelectionId + 
					" 请求的选举标识符: " + request.getSelectionId());
		}
		
		
		
		
		
		//先得到指定选举的所有的投票
		ConcurrentHashMap<Integer, Element> votes = CacheUtil.gVotes.getOrDefault(
				selectionId, null);
		
		
		//todo 必须全部投完票才能计票
		
		// 现在没有人投票
		if (votes == null) {
			statusCode = 1;
			
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("无人投票！");
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		
		
		
		Element result = CacheUtil.gPairing.getGT().newOneElement();
		for (Element elem : votes.values()) {
			result = result.mul(elem);
		}
		

		System.out.println("generator of g1: " + CacheUtil.gGeneratorG1);
		Element hash_G_2 = CacheUtil.gPairing.getG2().newRandomElement();
		try {
			// Static getInstance method is called with hashing SHA 
	        MessageDigest md = MessageDigest.getInstance("SHA-256"); 
	        
	     // digest() method called 
	        // to calculate message digest of an input 
	        // and return array of byte 
	        // 替换成全局选举id
	        byte[] messageDigest = md.digest(CacheUtil.gSelectionId.getBytes()); 
	        hash_G_2 = CacheUtil.gPairing.getG2().newElement().setFromHash(messageDigest, 0, messageDigest.length);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			
			
			statusCode = 2;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("no hash algo!");
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		};
		
		
		
		
		
		
		int voteResult = -1;
		Element gid = CacheUtil.gPairing.pairing(CacheUtil.gGeneratorG1, hash_G_2);
		for (int i = 0; i <= 200; i++) {
			BigInteger bigI = BigInteger.valueOf(i);
			
			if (gid.getImmutable().pow(bigI).isEqual(result)) {
				System.out.println("selection id: " + CacheUtil.gSelectionId + " vote result: " + i);
				voteResult = i;
				
				break;
			}
			
		}
		
		
		//4 返回
		if (voteResult == -1) {
			statusCode = 5;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("cant find result!");
			
		} else {
			responseBuilder.setVoteResult(voteResult);
			
			//设置结果
			CacheUtil.gVoteResults.put(selectionId, voteResult);
		}
		
		GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
		return new String(response.toByteArray(), "iso-8859-1");
		
	}
	
	
	
	private String handleGoVote(String body) throws Exception {
		//1 反序列化
		byte[] bodyBytes = body.getBytes("iso-8859-1");
		GoVoteProto.GoVoteRequest request =  
				GoVoteProto.GoVoteRequest.parseFrom(bodyBytes);
		
		
		
		
		
		
		int statusCode = 0;
		GoVoteProto.GoVoteResponse.Builder responseBuilder = 
				GoVoteProto.GoVoteResponse.newBuilder();
		
		responseBuilder.setStatusCode(statusCode);
		responseBuilder.setVoteId(request.getVoteId());
		responseBuilder.setSelectionId(CacheUtil.gSelectionId);
		
		
		
		
		
		//是不是已经存在了？
		//selection id是否一致?
		if (!request.getSelectionId().equalsIgnoreCase(CacheUtil.gSelectionId)) {
			statusCode = 3;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("请拉取最新选举标识符!");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		
		
		
		ConcurrentHashMap<Integer, Element> m = CacheUtil.gVotes.
				getOrDefault(CacheUtil.gSelectionId, new ConcurrentHashMap<Integer, Element>());
		
		Element prevVote = m.getOrDefault(request.getVoteId(), null);
		if (prevVote != null) {
			statusCode = 1;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("重复投票了!");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		
		
		
		
		
		
		
		
		// 先计算 gid  h?
		// 这里假设选举id为1
		MessageDigest md;
		Element hash_G_2 = CacheUtil.gPairing.getG2().newRandomElement();
		try {
			// Static getInstance method is called with hashing SHA 
	        md = MessageDigest.getInstance("SHA-256"); 
	        
	        // digest() method called 
	        // to calculate message digest of an input 
	        // and return array of byte 
	        byte[] messageDigest = md.digest(CacheUtil.gSelectionId.getBytes()); 
	        hash_G_2 = CacheUtil.gPairing.getG2().newElement().setFromHash(messageDigest, 0, messageDigest.length);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			
			
			statusCode = 3;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("no hash algorithm");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		};
		
		
		
		

		Element gid = CacheUtil.gPairing.pairing(CacheUtil.gGeneratorG1,  hash_G_2);
		Element commonGyi = CacheUtil.gCommonKeys.getOrDefault(request.getVoteId(), null);
		
		
		// 如果没有计算过公钥
		if (commonGyi == null) {
			statusCode = 3;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("No common key!");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
			
		}
		
		
		
		Element h = CacheUtil.gPairing.pairing(commonGyi, hash_G_2);
		System.out.println("voteid: " + request.getVoteId() + " commonKeys: " + commonGyi + " hash_G2: " + hash_G_2);
		System.out.println("h: " + h);
		
		
		
		//2 是否通过零知识证明？
		//2.1 先得到所有的参数
		Element w = CacheUtil.gPairing.getZr().newElementFromBytes(request.getW().toByteArray());
		
		//a1 b1 gt
		Element a1 = CacheUtil.gPairing.getGT().newElementFromBytes(request.getA1().toByteArray());
		Element b1 = CacheUtil.gPairing.getGT().newElementFromBytes(request.getB1().toByteArray());
		
		
		//x y gt
		Element x = CacheUtil.gPairing.getGT().newElementFromBytes(request.getX().toByteArray());
		Element y = CacheUtil.gPairing.getGT().newElementFromBytes(request.getY().toByteArray());
		
		
		//a2 b2 gt
		Element a2 = CacheUtil.gPairing.getGT().newElementFromBytes(request.getA2().toByteArray());
		Element b2 = CacheUtil.gPairing.getGT().newElementFromBytes(request.getB2().toByteArray());
		
		
		
		//d1 d2 zr
		Element d1 = CacheUtil.gPairing.getZr().newElementFromBytes(request.getD1().toByteArray());
		Element d2 = CacheUtil.gPairing.getZr().newElementFromBytes(request.getD2().toByteArray());
		
		
		//r1 r2 zr
		Element r1 = CacheUtil.gPairing.getZr().newElementFromBytes(request.getR1().toByteArray());
		Element r2 = CacheUtil.gPairing.getZr().newElementFromBytes(request.getR2().toByteArray());
		
		
		// c
		byte[] c = md.digest(ByteUtil.byteMergerAll(ByteUtil.toLH(request.getVoteId()),
				x.toBytes(),
				y.toBytes(),
				a1.toBytes(),
				b1.toBytes(),
				a2.toBytes(),
				b2.toBytes()));
		Element ce = CacheUtil.gPairing.getZr().newElementFromBytes(c);
		
		
		
		
		
		//打印出来。
		System.out.println("\n------------------------");
		System.out.println("voteid: " + request.getVoteId() + " gid: " + gid + " h: " + h);
		System.out.println("w: " + w + 
						"r1: " + r1 +
						"r2: " + r2 +
						"a1: " + a1 + 
						"b1: " + b1 +
						"a2: " + a2 + 
						"b2: " + b2 + 
						"d1: " + d1 + 
						"d2: " + d2 + 
						"c: " + c);
		
		
		
		
		
		
		//2.2 判断
		//2.2.1 c ?= d1 + d2
		if (!ce.isEqual(d1.getImmutable().add(d2))) {
			System.out.println("c ?= d1 + d2");
			System.out.println("------------------------\n");
			
			
			statusCode = 2;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("c ?= d1 + d2");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		// 2.2.2 a1 ?= gid^{r1} *  x^{d1},
		Element a1L = gid.getImmutable().powZn(r1);
		Element a1R = x.getImmutable().powZn(d1);
		if (!a1.isEqual(a1L.getImmutable().mul(a1R))) {
			System.out.println("a1 ?= gid^{r1} *  x^{d1}");
			System.out.println("------------------------\n");
			

			statusCode = 2;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("a1 ?= gid^{r1} *  x^{d1}");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		
		//2.2.3 b1 ?= h^{r1} y^{d1}
		Element b1L = h.getImmutable().powZn(r1);
		Element b1R = y.getImmutable().powZn(d1);
		if (!b1.isEqual(b1L.getImmutable().mul(b1R))) {
			System.out.println("b1 ?= h^{r1} y^{d1}");
			System.out.println("------------------------\n");

			
			
			statusCode = 2;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("b1 ?= h^{r1} y^{d1}");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		//2.2.4 a2 ?=  gid^{r2} x^{d2}
		Element a2L = gid.getImmutable().powZn(r2);
		Element a2R = x.getImmutable().powZn(d2);
		if (!a2.isEqual(a2L.getImmutable().mul(a2R))) {
			System.out.println("a2 ?=  gid^{r2} x^{d2}");
			System.out.println("------------------------\n");

			
			
			statusCode = 2;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("a2 ?=  gid^{r2} x^{d2}");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		
		//2.2.5 b2 ?= h^{r2} (y / gid) ^{d2}
		Element b2L = h.getImmutable().powZn(r2);
		Element b2R = y.getImmutable().div(gid).getImmutable().powZn(d2);
		if (!b2.isEqual(b2L.getImmutable().mul(b2R))) {
			System.out.println("b2 ?= h^{r2} (y / gid) ^{d2}");
			System.out.println("------------------------\n");

			
			
			statusCode = 2;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("b2 ?= h^{r2} (y / gid) ^{d2}");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
			
		}
		
		
		
		
		System.out.println("pass the test!");
		System.out.println("------------------------\n");
		
		
		//3 得到结果并 打印、保存
		//当前的selection id
		//CacheUtil.gVotes.put(request.getVoteId(),  y);
		m.put(request.getVoteId(), y);
		CacheUtil.gVotes.put(CacheUtil.gSelectionId, m);
		
		
		
		
		
		//4 返回
		GoVoteProto.GoVoteResponse response = responseBuilder.build();
		return new String(response.toByteArray(), "iso-8859-1");
		
	}
	
	
	
	private String handleGetCommonParam(String body) throws Exception {
		//1  解析body
		byte[] bodyBytes = body.getBytes("iso-8859-1");
		
		//2 处理body
		//得到结构体， 返回结构体。
		GetCommonParamProto.GetCommonParamRequest request = 
				GetCommonParamProto.GetCommonParamRequest.parseFrom(bodyBytes);
		
		
		
		
		
		//3 返回
		//status code : 0-ok
		Builder b = GetCommonParamProto.GetCommonParamResponse.newBuilder();
		b.setStatusCode(0);
		b.setGeneratorG1(ByteString.copyFrom(CacheUtil.gGeneratorG1.toBytes()));
		b.setSelectionId(ByteString.copyFrom(CacheUtil.gSelectionId.getBytes("iso-8859-1")));
	
		
		GetCommonParamProto.GetCommonParamResponse response = b.build();
		System.out.println("voteid: " + request.getVoteId() + " generatorG1: " 
				+ CacheUtil.gGeneratorG1 + " selectionid: " + CacheUtil.gSelectionId);
		
		return new String(response.toByteArray(), "iso-8859-1");
		
	}
	
	
	private String handleCalCommonGyi(String body) throws Exception {

		
		//1 反序列化
		byte[] bodyBytes = body.getBytes("iso-8859-1");
		CalCommonKeyProto.CalCommonKeyRequest   request =  
				CalCommonKeyProto.CalCommonKeyRequest.parseFrom(bodyBytes);
		
		
		//2 得到投票者id
		int voteId = Integer.valueOf(request.getVoteId());
		CalCommonKeyProto.CalCommonKeyResponse.Builder responseBuilder = 
				CalCommonKeyProto.CalCommonKeyResponse.newBuilder();
		responseBuilder.setVoteId(voteId);
		responseBuilder.setStatusCode(0);
		
		
		//3 计算过了？
		Element commonKey = CacheUtil.gCommonKeys.getOrDefault(voteId, null);
		if (commonKey != null) {
			responseBuilder.setStatusCode(1);
			responseBuilder.setCommonKey(ByteString.copyFrom(commonKey.toBytes()));
			
			
			
			CalCommonKeyProto.CalCommonKeyResponse response = responseBuilder.build();
			return new String(response.toByteArray(), "iso-8859-1");
			
		}
		
		
		
		
		
		//3 计算
		//sum ( 1 - i-1)  / sum( i + 1 - n)
		int n = CacheUtil.gPublickKeys.keySet().size();
		System.out.println("voteid: " + voteId + " n: " + n);
		
		Element leftAns = CacheUtil.gPairing.getG1().newOneElement(); 
		for (int i = 1; i < voteId; i++) {
			leftAns = leftAns.mul(CacheUtil.gPublickKeys.get(i));
		}
		
		Element rightAns = CacheUtil.gPairing.getG1().newOneElement(); 
		for (int i = voteId + 1; i <= n; i++) {
			rightAns = rightAns.mul(CacheUtil.gPublickKeys.get(i));
		}
		
		commonKey = leftAns.div(rightAns).duplicate();
		
		
		
		
		
		//4 print 保存
		System.out.println("voteid: " + voteId + " finalAns: " + commonKey);
		CacheUtil.gCommonKeys.put(voteId,  commonKey.duplicate());
		
		
		
		

		responseBuilder.setCommonKey(ByteString.copyFrom(commonKey.toBytes()));
		CalCommonKeyProto.CalCommonKeyResponse response = responseBuilder.build();
		return new String(response.toByteArray(), "iso-8859-1");
		
	}
	
	
	private String handleBoardcastPrivateKey(String body) throws Exception  {	
		PublicKeyProto.BoardcastPublicKeyResponse.Builder responseBuidler = 
				PublicKeyProto.BoardcastPublicKeyResponse.newBuilder();
				
		
		
		
		//1  解析body
		//1.1 投票者id
		//1.2 公钥
		byte[] bodyBytes = body.getBytes("iso-8859-1");
		BoardcastPublicKeyRequest requestCopy = 
				PublicKeyProto.BoardcastPublicKeyRequest.parseFrom(bodyBytes);
		
		
		responseBuidler.setVoteId(requestCopy.getVoteId());
		int statusCode = 0;
		
	
	
		// 是否之前传播了
		Element prevKey = CacheUtil.gPublickKeys.getOrDefault(requestCopy.getVoteId(), null);
		if (prevKey != null) {
			//之前已经广播了
			statusCode = 1;
			responseBuidler.setStatusCode(statusCode);
			
			
			System.out.println("voteid: " + requestCopy.getVoteId() + " 已经公布公钥了！");
			PublicKeyProto.BoardcastPublicKeyResponse response = responseBuidler.build();
			return new String(response.toByteArray(), "iso-8859-1");
		}
		
		
		
		
		
		
		//2 得到参数，   验证零知识证明。
		//byte[] realPkBytes = requestCopy.getPublickey().getBytes("iso-8859-1");
		Element pkElement = CacheUtil.gPairing.getG1().newRandomElement();
		pkElement.setFromBytes(requestCopy.getPublickey().toByteArray());
		
		
		// 得到gq  r   z
		// 先得到gq
//		byte[] generator = util.FileUtil.ReadFromFile(generatorFile);
//		Element generatorG1 = CacheUtil.gPairing.getG1().newElementFromBytes(generator);
		System.out.println("generator of g1: " + CacheUtil.gGeneratorG1);
		Element gq = CacheUtil.gPairing.getG1().newElementFromBytes(requestCopy.getGq().toByteArray());
		
		
		// 再得到z = hash(g, gq, gxi, i)
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			
			statusCode = 2;
			System.out.println("voteid: " + requestCopy.getVoteId() + "  hash算法找不到");
			PublicKeyProto.BoardcastPublicKeyResponse response = responseBuidler.build();
			return new String(response.toByteArray(), "iso-8859-1");
		} 
		
		
		
		
		byte[] z = md.digest(ByteUtil.byteMergerAll(CacheUtil.gGeneratorG1.toBytes(),
	    		gq.toBytes(),
	    		pkElement.toBytes(),
	    		ByteUtil.toLH(requestCopy.getVoteId())));		
		Element zZrElement = CacheUtil.gPairing.getZr().newElementFromBytes(z);
		
		
		
		//再得到r  gr
		Element rElement = CacheUtil.gPairing.getZr().newElementFromBytes(requestCopy.getR().toByteArray());
		Element gr = CacheUtil.gGeneratorG1.getImmutable().powZn(rElement);
		
		
		// 计算gr gxi * z
		Element temp = pkElement.getImmutable().powZn(zZrElement);
		Element newFinalAns = gr.getImmutable().mul(temp);
		
		
		
		
		System.out.println("\n--------------------------");
		System.out.println("voteid: " + requestCopy.getVoteId());
		System.out.println("pk: " + pkElement);
		if (!newFinalAns.isEqual(gq)) {
			System.out.println("cant pass the test!");
			
			
			statusCode = 3;
			System.out.println("voteid: " + requestCopy.getVoteId() + " 零知识证明失败！");
			PublicKeyProto.BoardcastPublicKeyResponse response = responseBuidler.build();
			return new String(response.toByteArray(), "iso-8859-1");
			
		} else {
			System.out.println("pass the test!\n");
			
		}
		System.out.println("--------------------------\n");
		
		

		
//		
//		//4 保存映射
		CacheUtil.gPublickKeys.put(requestCopy.getVoteId(), pkElement.duplicate());		
		System.out.println("Printall pks: ");
		for (Entry<Integer, Element> entry : CacheUtil.gPublickKeys.entrySet()) {
			  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			}		
		
		
		
		//5  返回
		PublicKeyProto.BoardcastPublicKeyResponse response = responseBuidler.build();
		return new String(response.toByteArray(), "iso-8859-1");
		
	}
	
	
	
	
	
	
	// 获取body参数
	//@param request FullHttpRequest  http请求
	//@return http请求体
	private String getBody(FullHttpRequest request) {
		ByteBuf buf = request.content();
		return buf.toString(CharsetUtil.ISO_8859_1);
	}
	
	
	//发送http response
	//@param ctx handler 上下文
	//@param context response body
	//@param status http response status
	//@return void
	private void send (ChannelHandlerContext ctx,  String context, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, status, 
				Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
		
		
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	
	
	
	// 建立连接的 打印log
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
		
//		ctx.writeAndFlush("客户端"+ InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
//		super.channelActive(ctx);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

package server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;



import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.test.proto.CalCommonKeyProto;
import com.test.proto.ChangeSelectionInfo;
import com.test.proto.GetCommonParamProto;
import com.test.proto.GetCommonParamProto.GetCommonParamResponse.Builder;
import com.test.proto.GetVoteResultProto;
import com.test.proto.GoVoteProto;
import com.test.proto.LoginInfo;
import com.test.proto.MsgInfo.MsgBody;
import com.test.proto.PublicKeyProto;
import com.test.proto.PublicKeyProto.BoardcastPublicKeyRequest;
import com.test.proto.RegisterInfo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
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
import util.DBUtil;
import util.LoginUtil;
import util.MsgUtil;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
	//private HashMap<Integer, Element> mPublickKeys = new HashMap<Integer, Element>();  
//	
//	// 返回公共参数
//	private final String getCommonParam = "/getCommonParam";
//	
//	// 传播私钥
//	private final String boardcastPrivateKey = "/boardcastPrivateKey";
//	
//	// 计算g_{y_{i}}
//	private final String calCommonGyi = "/calCommonGyi";
//	
//	// 投票
//	private final String vote =  "/goVote";
//	
//	// 计算结果
//	private final String getVoteResult = "/getVoteResult";
//	
//	// 设置下一个选举。
//	private final String goNextSelection = "/goNextSelection";
	
	
	
	//private int id ;
	
	public NettyServerHandler() {
		
	}
	

	
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (! (msg instanceof MsgBody)) {
			System.out.println("fatal error！");
			return;
		}
		
		
		MsgBody body = (MsgBody) (msg) ;
		
		
		MsgBody responseBody;
		switch (body.getCommand()) {
		case 1:
			//登录
			responseBody = handleLoginRequest(body, ctx.channel());
			ctx.writeAndFlush(responseBody);
			break;
			
		case 2:
			// 注册
			responseBody = handleRegisterRequest(body, ctx.channel());
			ctx.writeAndFlush(responseBody);
			break;
			
			
		case 3:
			// 拉取公共参数
			// 必须登录
			responseBody = handleGetCommonParam(body, ctx.channel());
			ctx.writeAndFlush(responseBody);
			break;
			
		case 4:
			// 传播公钥
			// 必须登录
			responseBody = handleBoardcastPrivateKey(body, ctx.channel());
			ctx.writeAndFlush(responseBody);
			break;
			
		case 5:
			// 计算公共公钥
			// 必须登录
			responseBody = handleCalCommonGyi(body, ctx.channel());
			ctx.writeAndFlush(responseBody);
			break;
			
		case 6:
			// 投票
			// 必须登录
			responseBody = handleGoVote(body, ctx.channel());
			ctx.writeAndFlush(responseBody);
			break;
			
		case 7:
			// 得到投票结果
			// 必须登录
			responseBody = handleGetVoteResult(body, ctx.channel());
			ctx.writeAndFlush(responseBody);
			break;
			
			
		case 8:
			// 设置下一个选举id
			responseBody = handleGoNextSelection(body);
			ctx.writeAndFlush(responseBody);
			break;
			
			
		case 9:
			// 设置注册时间等
			break;
			
			
		}
		
		
		
		
		
		
		
//		if (! (msg instanceof FullHttpRequest)) {
//			String result = "Unkown request!";
//			send(ctx, result, HttpResponseStatus.BAD_REQUEST);
//			return;
//		}
//		
//		
//		
//		
//		FullHttpRequest httpRequest = (FullHttpRequest) msg;
//		try {
//			String path = httpRequest.uri();
//			String body = getBody(httpRequest);
//			HttpMethod method = httpRequest.method();
//			
//			
//			System.out.println("path: " + path);
//			System.out.println("method: " + method);
//			
//			
//			String result = "";
//			switch (path) {
//			case getCommonParam: 
//				result = handleGetCommonParam(body);
//				break;
//				
//				
//			case boardcastPrivateKey:
//				result = handleBoardcastPrivateKey(body);
//				break;
//				
//				
//			case calCommonGyi: 
//				result = handleCalCommonGyi(body);
//				break;
//				
//				
//			case vote: 
//				result = handleGoVote(body);
//				break;
//				
//				
//			case getVoteResult:
//				result = handleGetVoteResult(body);
//				break;
//				
//				
//			case goNextSelection:
//				result = handleGoNextSelection(body);
//				break;
//			
//			}
//			
//			send(ctx, result, HttpResponseStatus.ACCEPTED);
//			
//			
//		} catch (Exception e) {
//			System.out.println("Failed to handle request");
//			e.printStackTrace();
//			
//		} finally {
//			httpRequest.release();
//			
//		}
//		
		
		
	}
	
	
	
	private MsgBody handleRegisterRequest(MsgBody body, Channel ch) throws InvalidProtocolBufferException  {
		RegisterInfo.RegisterRequest request = RegisterInfo.RegisterRequest.parseFrom(body.getContent().toByteArray());
		
		
		//1 先打印下信息
		System.out.println("remote address: " + ch.remoteAddress() + " pw: " + request.getPassword());
		
		
		RegisterInfo.RegisterResponse.Builder b = RegisterInfo.RegisterResponse.newBuilder();
		b.setStatusCode(0);
		
		
		//2 是否能够插入？
		//2.0 现在是否能够注册？
		
		
		

		PreparedStatement psql;
		try {
			
			////2.1 先看现在的编号到什么了？
			psql = DBUtil.con.prepareStatement("select count(*) from user");
			ResultSet res = psql.executeQuery();

		
			
			
			
			ResultSet rs = psql.executeQuery();
			int rowCount = 0;
			if (rs.next()) {
				rowCount= rs.getInt(1);    
			}
			CacheUtil.gVoteNum = rowCount + 1;
			
			
			
			
			//2.2 插入
			psql = DBUtil.con.prepareStatement("insert into user (voteId, password, registerTime) "
			+ "values(?,?,now())");
			
			psql.setInt(1, CacheUtil.gVoteNum);
			psql.setString(2,  request.getPassword());
			
			psql.executeUpdate();
			
			
			//2.3 返回
			b.setVoteId(CacheUtil.gVoteNum);
			b.setPassword(request.getPassword());
			
			
			System.out.println("注册成功!");
			System.out.println("address: " + ch.remoteAddress() + " id: " 
					+ CacheUtil.gVoteNum + " passwd: " + request.getPassword());
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			b.setStatusCode(1);
			b.setExtra(e.getMessage());
			
			
			System.out.println("注册失败!");
			System.out.println("address: " + ch.remoteAddress() + " id: " 
					+ CacheUtil.gVoteNum + " passwd: " + request.getPassword());
			
		} finally {
			RegisterInfo.RegisterResponse response = b.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
			
		}
		
		
		
	}
	
	
	
	
	private MsgBody handleLoginRequest(MsgBody body, Channel ch) throws Exception {
		LoginInfo.LoginRequest request = LoginInfo.LoginRequest.parseFrom(body.getContent().toByteArray());
		
		// 0 打印出
		System.out.println("id: " + request.getVoteId() + " pd: " + request.getPassword());
		
		
		
		
		
		LoginInfo.LoginResponse.Builder b = LoginInfo.LoginResponse.newBuilder();
		
		
		// 1 判断是否能够登录
		// 去数据库查询。
		// select * from ?? where id = ? and password = ? 
		//预处理添加数据，其中有两个参数--“？”
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from user where voteId=? and password=?");
		
		
		PreparedStatement psql = DBUtil.con.prepareStatement(sql.toString());
		psql.setInt(1, request.getVoteId()); 
		psql.setString(2, request.getPassword());    
		
		
		ResultSet rs = psql.executeQuery();
		
		int rowCount = 0;
		if (rs.next()) {
			rowCount= rs.getInt(1);    
		 }
		
		
		
		if (rowCount <= 0) {
			System.out.println("id: " + request.getVoteId() + " pd: " + request.getPassword());
			System.out.println("没有找到这行记录！");
			
			
			// 没有查到这个记录
			b.setStatusCode(1);
			b.setExtra("账号密码错误!");
			
			
			LoginInfo.LoginResponse response = b.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
		}
		
		
		
		
		// 2 返回信息。
		// 登录成功
		System.out.println("登录成功！");
		System.out.println("address： " + ch.remoteAddress() + " id: " + request.getVoteId() 
			+ " pd: " + request.getPassword());
		LoginUtil.markAsLogin(ch);
		
		b.setStatusCode(0);
		b.setVoteId(request.getVoteId());
		LoginInfo.LoginResponse response = b.build();
		return MsgUtil.build(body.getCommand(), response.toByteArray());
		
	} 
	
	
	
	
	private MsgBody handleGoNextSelection(MsgBody body) throws InvalidProtocolBufferException, SQLException {
		//1 反序列化
		ChangeSelectionInfo.ChangeSelectionRequest request = 
				ChangeSelectionInfo.ChangeSelectionRequest.parseFrom(body.getContent().toByteArray());
		
		
		
		//2 是否指定了id
		if (request.getSelectionId().trim().equalsIgnoreCase("")) {
			CacheUtil.gSelectionId = ByteUtil.getRandomString(128);
		} else {
			CacheUtil.gSelectionId = request.getSelectionId();
		}
		
		
		
		
		//设置截止时间
		//现在的时候
				Date beginTime = new Date();
				SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				CacheUtil.gDeadLine = new Date(beginTime.getTime() + 2 * 60 *1000); // 默认两分钟
				System.out.println("endTime: " + dateFormat.format(CacheUtil.gDeadLine));
				
				
				//插入到数据库
				StringBuilder sql = new StringBuilder();
				sql.append("insert into selection_info(selectionId, beginTime, endTime) values(?, ?, ?)");
				
				PreparedStatement psql = DBUtil.con.prepareStatement(sql.toString());
				psql.setString(1, CacheUtil.gSelectionId); 
				psql.setTimestamp(2, new Timestamp(beginTime.getTime()));
				psql.setTimestamp(3, new Timestamp(CacheUtil.gDeadLine.getTime()));		
				psql.executeUpdate();
		
		
		
		ChangeSelectionInfo.ChangeSelectionResponse.Builder b = 
				ChangeSelectionInfo.ChangeSelectionResponse.newBuilder();
		b.setStatusCode(0);
		b.setSelectionId(CacheUtil.gSelectionId);
		
		
		System.out.println("change selectionid: " + CacheUtil.gSelectionId);
		ChangeSelectionInfo.ChangeSelectionResponse response = b.build();
		return MsgUtil.build(body.getCommand(), response.toByteArray()); 
	}
	
	
	
	
	private MsgBody handleGetVoteResult(MsgBody body, Channel ch) throws Exception {
		GetVoteResultProto.GetVoteResultResponse.Builder responseBuilder = 
				GetVoteResultProto.GetVoteResultResponse.newBuilder();

		
		//1 反序列化
		GetVoteResultProto.GetVoteResultRequest  request =  
				GetVoteResultProto.GetVoteResultRequest.parseFrom(body.getContent().toByteArray());
		System.out.println("请求的选举id: " + request.getSelectionId());
		
		
				
		
		int statusCode  = 0;
		responseBuilder.setStatusCode(statusCode);
		responseBuilder.setSelectionId(request.getSelectionId());
		
		System.out.println("response id: "  + responseBuilder.getSelectionId());
		
		
		
		
		
		
		if (!LoginUtil.hasLogin(ch)) {
			statusCode = 4;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("没有登录!");
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
			
		}
		
		
		
		
		
		//现在的时候是不是没有到?
		Date now = new Date();
		long nowSecond = now.getTime();
		long deadLineSecond = CacheUtil.gDeadLine.getTime();
		if (deadLineSecond > nowSecond) {
			// 还没有到
			statusCode = 6;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("投票还没有截止!");
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
			
		}
		
		
		
		
		
		
		//已经计算过了
		//直接返回
		String selectionId = CacheUtil.gSelectionId;
		
		int prevVoteResult = CacheUtil.gVoteResults.getOrDefault(selectionId, -1);
		if (prevVoteResult != -1) {
			responseBuilder.setVoteResult(prevVoteResult);
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
			
		}
		
		
		
		
		// 选举标识符没有设置
		if (selectionId == "") {
			statusCode = 5;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("没有设置选举标识符!");
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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

	        // 替换成全局选举id
	        byte[] messageDigest = md.digest(CacheUtil.gSelectionId.getBytes()); 
	        hash_G_2 = CacheUtil.gPairing.getG2().newElement().setFromHash(messageDigest, 0, messageDigest.length);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			
			
			statusCode = 2;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("no hash algo!");
			GetVoteResultProto.GetVoteResultResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
		return MsgUtil.build(body.getCommand(), response.toByteArray());
		
	}
	
	
	
	private MsgBody handleGoVote(MsgBody body, Channel ch) throws Exception {
		//1 反序列化
		GoVoteProto.GoVoteRequest request =  
				GoVoteProto.GoVoteRequest.parseFrom(body.getContent().toByteArray());
		
		
		
		
		
		
		int statusCode = 0;
		GoVoteProto.GoVoteResponse.Builder responseBuilder = 
				GoVoteProto.GoVoteResponse.newBuilder();
		
		responseBuilder.setStatusCode(statusCode);
		responseBuilder.setVoteId(request.getVoteId());
		responseBuilder.setSelectionId(CacheUtil.gSelectionId);
		
		
		
		
		
		// 没有登录?
		if (!LoginUtil.hasLogin(ch)) {
			statusCode = 4;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("没有登录!");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
		}
		
		
		
		
		
		//是不是已经存在了？
		//selection id是否一致?
		if (!request.getSelectionId().equalsIgnoreCase(CacheUtil.gSelectionId)) {
			statusCode = 3;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("请拉取最新选举标识符!");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
		}
		
		
		
		
		
		ConcurrentHashMap<Integer, Element> m = CacheUtil.gVotes.
				getOrDefault(CacheUtil.gSelectionId, new ConcurrentHashMap<Integer, Element>());
		
		Element prevVote = m.getOrDefault(request.getVoteId(), null);
		if (prevVote != null) {
			statusCode = 1;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("重复投票了!");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
			return MsgUtil.build(body.getCommand(), response.toByteArray());
		};
		
		
		
		

		Element gid = CacheUtil.gPairing.pairing(CacheUtil.gGeneratorG1,  hash_G_2);
		Element commonGyi = CacheUtil.gCommonKeys.getOrDefault(request.getVoteId(), null);
		
		
		// 如果没有计算过公钥
		if (commonGyi == null) {
			statusCode = 3;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("没有计算过公共公钥!");
			
			GoVoteProto.GoVoteResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
			
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
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
			return MsgUtil.build(body.getCommand(), response.toByteArray());
			
		}
		
		
		
		
		System.out.println("pass the test!");
		System.out.println("------------------------\n");
		
		
		//3 得到结果并 打印、保存
		// 3.1 保存到数据库
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ballot_info(voteId, selectionId, ballotInfo, voteTime)  values(?, ?, ?, now())");
		
		
		PreparedStatement psql = DBUtil.con.prepareStatement(sql.toString());
		psql.setInt(1, request.getVoteId()); 
		psql.setString(2, CacheUtil.gSelectionId);
		psql.setBytes(3, y.toBytes());
		
		
		
		int affected = psql.executeUpdate();
		System.out.println("\n------------------------");
		System.out.println("插入选票信息成功!" + "affected: " + affected);
		System.out.println("voteid: " + request.getVoteId() + " selectionId: " + CacheUtil.gSelectionId);
		System.out.println("ballotInfo: " + y.duplicate());
		System.out.println("------------------------\n");
		
		
		
		
		
		m.put(request.getVoteId(), y);
		CacheUtil.gVotes.put(CacheUtil.gSelectionId, m);
		
		
		
		
		
		//4 返回
		GoVoteProto.GoVoteResponse response = responseBuilder.build();
		return MsgUtil.build(body.getCommand(), response.toByteArray());
		
	}
	
	
	
	private MsgBody handleGetCommonParam(MsgBody body, Channel ch) throws InvalidProtocolBufferException, UnsupportedEncodingException  {
		//1 反序列化
		GetCommonParamProto.GetCommonParamRequest request = 
				GetCommonParamProto.GetCommonParamRequest.parseFrom(body.getContent().toByteArray());
		
		
		
		// 2 返回
		Builder b = GetCommonParamProto.GetCommonParamResponse.newBuilder();
		b.setStatusCode(0);
		
		
		
		
		//3 是不是没有登录
		if (!LoginUtil.hasLogin(ch)) {
			b.setStatusCode(1);
			b.setExtra("没有登录!");
			
			GetCommonParamProto.GetCommonParamResponse response = b.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
		}
		
		
		
		
		b.setGeneratorG1(ByteString.copyFrom(CacheUtil.gGeneratorG1.toBytes()));
		b.setSelectionId(ByteString.copyFrom(CacheUtil.gSelectionId.getBytes("iso-8859-1")));
		
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		b.setDeadLine(dateFormat.format(CacheUtil.gDeadLine));
		
		
		
		GetCommonParamProto.GetCommonParamResponse response = b.build();
		return MsgUtil.build(body.getCommand(), response.toByteArray());
		
		
//		//1  解析body
//		byte[] bodyBytes = body.getBytes("iso-8859-1");
//		
//		//2 处理body
//		//得到结构体， 返回结构体。
//		GetCommonParamProto.GetCommonParamRequest request = 
//				GetCommonParamProto.GetCommonParamRequest.parseFrom(bodyBytes);
//		
//		
//		
//		
//		
//		//3 返回
//		//status code : 0-ok
//		Builder b = GetCommonParamProto.GetCommonParamResponse.newBuilder();
//		b.setStatusCode(0);
//		b.setGeneratorG1(ByteString.copyFrom(CacheUtil.gGeneratorG1.toBytes()));
//		b.setSelectionId(ByteString.copyFrom(CacheUtil.gSelectionId.getBytes("iso-8859-1")));
//	
//		
//		GetCommonParamProto.GetCommonParamResponse response = b.build();
//		System.out.println("voteid: " + request.getVoteId() + " generatorG1: " 
//				+ CacheUtil.gGeneratorG1 + " selectionid: " + CacheUtil.gSelectionId);
//		
//		return new String(response.toByteArray(), "iso-8859-1");
		
	}
	
	
	private MsgBody handleCalCommonGyi(MsgBody body, Channel ch) throws Exception {

		
		//1 反序列化
		CalCommonKeyProto.CalCommonKeyRequest  request =  
				CalCommonKeyProto.CalCommonKeyRequest.parseFrom(body.getContent().toByteArray());
		
		
		//2 得到投票者id
		int voteId = request.getVoteId();
		CalCommonKeyProto.CalCommonKeyResponse.Builder responseBuilder = 
				CalCommonKeyProto.CalCommonKeyResponse.newBuilder();
		responseBuilder.setVoteId(voteId);
		responseBuilder.setStatusCode(0);
		
		
		
		
		//是不是没有登录
		if (!LoginUtil.hasLogin(ch)) {
			responseBuilder.setStatusCode(2);
			responseBuilder.setExtra("没有登录!");
			System.out.println("voteid: " + request.getVoteId() + " 没有登录！");
			
			CalCommonKeyProto.CalCommonKeyResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
		}
		
		
		
		
		
		//3 计算过了？
		Element commonKey = CacheUtil.gCommonKeys.getOrDefault(voteId, null);
		if (commonKey != null) {
			responseBuilder.setStatusCode(1);
			responseBuilder.setCommonKey(ByteString.copyFrom(commonKey.toBytes()));
			
			
			System.out.println(request.getVoteId() + " 已经计算过了common key");
			CalCommonKeyProto.CalCommonKeyResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
			
		}
		
		
		
		//当前的公钥是否传播了
		Element prevKey = CacheUtil.gPublickKeys.getOrDefault(request.getVoteId(), null);
		if (prevKey == null) {
			responseBuilder.setStatusCode(2);
			responseBuilder.setExtra("请先公布公钥!");
			
			
			System.out.println("voteid: " + request.getVoteId() + " 没有公布公钥！");
			CalCommonKeyProto.CalCommonKeyResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
		//4.1 保存到数据库
		//是不是已经存在了？
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from user_key_info where voteId=?");
		
		
		PreparedStatement psql = DBUtil.con.prepareStatement(sql.toString());
		psql.setInt(1, request.getVoteId()); 
		
		ResultSet rs = psql.executeQuery();
		
		int rowCount = 0;
		if (rs.next()) {
			rowCount= rs.getInt(1);    
		
		}
		
		
		if (rowCount == 0) {
					//没有这个记录
					//直接插入
			sql = new StringBuilder();
			sql.append("insert into user_key_info(voteId, commonKey) values(?, ?)");
			
			psql = DBUtil.con.prepareStatement(sql.toString());
			psql.setInt(1, request.getVoteId());
			psql.setBytes(2, commonKey.duplicate().toBytes());
			
			int affeted = psql.executeUpdate();
			System.out.println("保存公共公钥！ id: " + request.getVoteId() + " affeted: " + affeted);
			System.out.println(commonKey.duplicate());
			
		} else {
					sql = new StringBuilder();
					sql.append("update user_key_info set commonKey=? where voteId= ?");
					
					psql = DBUtil.con.prepareStatement(sql.toString());
					psql.setBytes(1, commonKey.duplicate().toBytes());
					psql.setInt(2, request.getVoteId());
					
					
					
					int affeted = psql.executeUpdate();
					System.out.println("更新公共公钥！ id: " + request.getVoteId() + " affeted: " + affeted);
					System.out.println(commonKey.duplicate());
					
		}
		
		
		
		System.out.println("voteid: " + voteId + " commonkey: " + commonKey);
		CacheUtil.gCommonKeys.put(voteId,  commonKey.duplicate());
		

		responseBuilder.setCommonKey(ByteString.copyFrom(commonKey.toBytes()));
		CalCommonKeyProto.CalCommonKeyResponse response = responseBuilder.build();
		return MsgUtil.build(body.getCommand(), response.toByteArray());
		
	}
	
	
	private MsgBody handleBoardcastPrivateKey(MsgBody body, Channel ch) throws Exception  {	
		
		PublicKeyProto.BoardcastPublicKeyResponse.Builder responseBuilder = 
				PublicKeyProto.BoardcastPublicKeyResponse.newBuilder();
				
		
		
		
		//1  解析body
		//1.1 投票者id
		//1.2 公钥
		BoardcastPublicKeyRequest requestCopy = 
				PublicKeyProto.BoardcastPublicKeyRequest.parseFrom(body.getContent().toByteArray());
		responseBuilder.setVoteId(requestCopy.getVoteId());
		responseBuilder.setStatusCode(0);
		
		
		int statusCode = 0;
		//是不是没有登录
		if (!LoginUtil.hasLogin(ch)) {
			statusCode = 4;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("没有登录!");
			
			System.out.println("voteid: " + requestCopy.getVoteId() + " 没有登录！");
			PublicKeyProto.BoardcastPublicKeyResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
		}
		
		
		
		
		
		// 是否之前传播了
		Element prevKey = CacheUtil.gPublickKeys.getOrDefault(requestCopy.getVoteId(), null);
		if (prevKey != null) {
			//之前已经广播了
			statusCode = 1;
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("重复公布公钥!");
			
			
			System.out.println("voteid: " + requestCopy.getVoteId() + " 已经公布公钥了！");
			PublicKeyProto.BoardcastPublicKeyResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
		}
		
		
		
		
		
		
		//2 得到参数，   验证零知识证明。
		Element pkElement = CacheUtil.gPairing.getG1().newRandomElement();
		pkElement.setFromBytes(requestCopy.getPublickey().toByteArray());
		
		
		// 得到gq  r   z
		// 先得到gq
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
			PublicKeyProto.BoardcastPublicKeyResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
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
			responseBuilder.setStatusCode(statusCode);
			responseBuilder.setExtra("零知识证明失败！");
			
			
			System.out.println("voteid: " + requestCopy.getVoteId() + " 零知识证明失败！");
			PublicKeyProto.BoardcastPublicKeyResponse response = responseBuilder.build();
			return MsgUtil.build(body.getCommand(), response.toByteArray());
			
		} else {
			System.out.println("pass the test!\n");
			
		}
		System.out.println("--------------------------\n");
		
		

		
//		
//		//4 保存
		//4.1 保存到数据库
		//是不是已经存在了？
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from user_key_info where voteId=?");
		
		
		PreparedStatement psql = DBUtil.con.prepareStatement(sql.toString());
		psql.setInt(1, requestCopy.getVoteId()); 
		
		ResultSet rs = psql.executeQuery();
		int rowCount = 0;
		if (rs.next()) {
			rowCount= rs.getInt(1);    
		 }
		
		if (rowCount == 0) {
			//没有这个记录
			//直接插入
			sql = new StringBuilder();
			sql.append("insert into user_key_info(voteId, publicKey) values(?, ?)");
			
			psql = DBUtil.con.prepareStatement(sql.toString());
			psql.setInt(1, requestCopy.getVoteId());
			psql.setBytes(2, pkElement.duplicate().toBytes());
			
			
			int affeted = psql.executeUpdate();
			System.out.println("保存公钥！ id: " + requestCopy.getVoteId() + " affeted: " + affeted);
			System.out.println(pkElement.duplicate());
			
			
		} else {
			sql = new StringBuilder();
			sql.append("update user_key_info set publicKey=? where voteId= ?");
			
			psql = DBUtil.con.prepareStatement(sql.toString());
			psql.setBytes(1, pkElement.duplicate().toBytes());
			psql.setInt(2, requestCopy.getVoteId());
			
			
			
			int affeted = psql.executeUpdate();
			System.out.println("更新公钥！ id: " + requestCopy.getVoteId() + " affeted: " + affeted);
			System.out.println(pkElement.duplicate());
			
		}
		
		
		
		
		//4.2 保存映射
		CacheUtil.gPublickKeys.put(requestCopy.getVoteId(), pkElement.duplicate());		
		System.out.println("Printall pks: ");
		for (Entry<Integer, Element> entry : CacheUtil.gPublickKeys.entrySet()) {
			  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			}		
		
		
		
		//5  返回
		PublicKeyProto.BoardcastPublicKeyResponse response = responseBuilder.build();
		return MsgUtil.build(body.getCommand(), response.toByteArray());
		
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
	
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//客户端下线。
		LoginUtil.markAsLogout(ctx.channel());
		System.out.println("客户端: " + ctx.channel().remoteAddress() + " 下线!");
		
		
        ctx.close();
		
//		ctx.writeAndFlush("客户端"+ InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
//		super.channelActive(ctx);
	}
	
	
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//这个也算客户端下线。
		LoginUtil.markAsLogout(ctx.channel());
		System.out.println("客户端: " + ctx.channel().remoteAddress() + " 异常下线!");
		
		
        ctx.close();
        System.out.println("异常信息： " + cause.getMessage());
    }
	
	
	
	
	
	
	
	
	
	
	
	
}

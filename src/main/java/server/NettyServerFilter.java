package server;

import com.test.proto.MsgInfo.MsgBody;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class NettyServerFilter extends ChannelInitializer<SocketChannel> {

	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline ph = ch.pipeline();
		
		NettyServerHandler handler = new NettyServerHandler();
		//处理http 的handler
//		ph.addLast("encoder", new HttpResponseEncoder());
//		ph.addLast("decoder", new HttpRequestDecoder());
//		ph.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
//		ph.addLast("handler",  handler);
		
		ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
		ch.pipeline().addLast(new ProtobufDecoder(MsgBody.getDefaultInstance()));
		ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
		ch.pipeline().addLast(new ProtobufEncoder());
        // 在管道中添加我们自己的接收数据实现方法
		ch.pipeline().addLast(handler);
	}
}

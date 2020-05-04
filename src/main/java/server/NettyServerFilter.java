package server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class NettyServerFilter extends ChannelInitializer<SocketChannel> {

	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline ph = ch.pipeline();
		
		NettyServerHandler handler = new NettyServerHandler();
		//处理http 的handler
		ph.addLast("encoder", new HttpResponseEncoder());
		ph.addLast("decoder", new HttpRequestDecoder());
		ph.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
		ph.addLast("handler",  handler);
	}
}

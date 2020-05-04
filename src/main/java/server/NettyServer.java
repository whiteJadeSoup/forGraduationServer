package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import util.CacheUtil;

public class NettyServer {
	// 监听端口
	private static final int port = 6889;
	private static EventLoopGroup bossGroup = new NioEventLoopGroup();
	private static EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	
	private static ServerBootstrap b = new ServerBootstrap();
	
	
	
	public static void main(String[] args) throws Exception  {
		//初始化
		CacheUtil.init();
		
		
		
		try {
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new NettyServerFilter());
			
			
			
			ChannelFuture f = b.bind(port).sync();
			System.out.println("服务端启动成功,端口是:" +port);
			
			
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}
	
}

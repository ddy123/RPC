package example.Server.server.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import example.Server.netty.nettyInitializer.NettyServerInitializer;
import example.Server.provider.ServiceProvider;
import example.Server.server.RpcServer;

@AllArgsConstructor
public class NettyRPCServer implements RpcServer {
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        NioEventLoopGroup bossGroup=new NioEventLoopGroup();
        NioEventLoopGroup workGroup=new NioEventLoopGroup();
        System.out.println("netty服务端启动了");
       try{
           //启动netty服务器
           ServerBootstrap serverBootstrap=new ServerBootstrap();
           //初始化
           serverBootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
                   //NettyClientInitializer这里 配置netty对消息的处理机制
                   .childHandler(new NettyServerInitializer(serviceProvider));
           //同步堵塞
           ChannelFuture channelFuture=serverBootstrap.bind(port).sync();
           //死循环监听
           channelFuture.channel().closeFuture().sync();
       }catch (InterruptedException e){
           e.printStackTrace();
       }finally {
           bossGroup.shutdownGracefully();
           workGroup.shutdownGracefully();
       }
    }

    @Override
    public void stop() {

    }
}

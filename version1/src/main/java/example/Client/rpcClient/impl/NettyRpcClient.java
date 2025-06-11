package example.Client.rpcClient.impl;

import example.Client.serviceCenter.ServiceCenter;
import example.Client.serviceCenter.ZKServiceCenter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import example.Client.netty.nettyInitializer.NettyClientInitializer;
import example.Client.rpcClient.RpcClient;
import example.common.Message.RpcRequest;
import example.common.Message.RpcResponse;

import java.net.InetSocketAddress;

public class NettyRpcClient implements RpcClient {

    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private ServiceCenter serviceCenter;
    public NettyRpcClient() throws InterruptedException {
        this.serviceCenter=new ZKServiceCenter();
    }
    static {
        eventLoopGroup=new NioEventLoopGroup();
        bootstrap=new Bootstrap();
        //这里不要导错NioSCTPServerChannel,也不要导错NioServerSocketChannel，注意这三者之间的区别
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                //NettyClientInitializer这里 配置netty对消息的处理机制
                //该方法接收一个handler，ChannelInitializer和SimpleChannelInboundHandler的顶级接口都是handler
                //不要弄错了
                .handler(new NettyClientInitializer());
    }
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        InetSocketAddress address=serviceCenter.serviceDiscovery(request.getInterfaceName());
        String host=address.getHostName();
        int port=address.getPort();
        try {
            //创建一个channelFuture对象，代表这一个操作事件，sync表示阻塞直到conneext完成
            ChannelFuture channelFuture=bootstrap.connect(host,port).sync();
            //channel表示一个连接的单位，类似socket
            Channel channel=channelFuture.channel();
            //发送数据
            channel.writeAndFlush(request);
            //sync()阻塞获取结果
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会有线程安全问题。
            // 当前场景下选择堵塞获取结果
            // 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...
            AttributeKey<RpcResponse> key=AttributeKey.valueOf("RPCResponse");
            RpcResponse response=channel.attr(key).get();
            //System.out.println(response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

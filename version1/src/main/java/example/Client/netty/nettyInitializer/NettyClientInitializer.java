package example.Client.netty.nettyInitializer;

import example.common.serializer.myCode.MyDecoder;
import example.common.serializer.myCode.MyEncoder;
import example.common.serializer.mySerializer.JsonSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import example.Client.netty.handler.NettyClientHandler;

/*用于初始化客户端的Channel和ChannelPipeline.在Netty中，Channel是网络通信的基本单元
* 而ChannelPipeline是一个用于处理消息的责任链，它包含了一系列的ChannelHandler，每个ChannelHandler
* 都负责处理不同的操作，如编解码，异常处理等
* */

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline=ch.pipeline();
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}

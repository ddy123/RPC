package example.Client.netty.nettyInitializer;

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
        //消息格式【长度】【消息体】，解决粘包问题
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        //计算当前待发送消息的长度，写入到前4个字节中
        pipeline.addLast(new LengthFieldPrepender(4));
        //编码器
        //使用java序列化方式，netty自带的解码编码支持传输这种结构
        pipeline.addLast(new ObjectEncoder());
        //解码器
        //使用了Netty中的ObjectDecoder，它用于将字节流解码为 Java 对象。
        //在ObjectDecoder的构造函数中传入了一个ClassResolver 对象，用于解析类名并加载相应的类。
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));
        pipeline.addLast(new NettyClientHandler());
    }
}

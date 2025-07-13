package commom.serializer.myCode;/*
 * @author ddy
 * @date 2025/6/10
 * */

import example.common.Message.MessageType;
import example.common.Message.RpcRequest;
import example.common.Message.RpcResponse;
import commom.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println(msg.getClass());
        //写入类型信息
        if(msg instanceof RpcRequest){
            out.writeShort(MessageType.REQUEST.getCode());
            //System.out.println("类型信息："+MessageType.REQUEST.getCode());
        }else if(msg instanceof RpcResponse){
            out.writeShort(MessageType.RESPONSE.getCode());
            //System.out.println("类型信息："+MessageType.RESPONSE.getCode());
        }
        //写入序列化方式
        out.writeShort(serializer.getType());
        //System.out.println("序列化方式："+serializer.getType());
        //得到序列化数组
        byte[] serializerBytes= serializer.serialize(msg);
        //写入长度
        out.writeInt(serializerBytes.length);
        //写入序列化数组
        out.writeBytes(serializerBytes);
        //System.out.println("序列化完成");

    }
}

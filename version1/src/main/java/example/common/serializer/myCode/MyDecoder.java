package example.common.serializer.myCode;/*
 * @author ddy
 * @date 2025/6/10
 * */

import example.common.Message.MessageType;
import example.common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        //读取消息类型
        short messageType=in.readShort();
        //System.out.println("我是数据类型："+messageType);
        //现在还只支持request和response请求
        if(messageType != MessageType.REQUEST.getCode()&&messageType!=MessageType.RESPONSE.getCode()){
            System.out.println("暂不支持此种数据");
            return;
        }
        //读取序列化的方式
        short serializerType=in.readShort();
        //System.out.println("我是序列化方式"+serializerType);
        Serializer serializer=Serializer.getSerializerByCode(serializerType);
        if(serializer==null){
            throw new RuntimeException("不存在对应的序列化器");
        }
        //读取序列化数组长度
        int length=in.readInt();
        //读取序列化数组
        byte[] bytes=new byte[length];
        in.readBytes(bytes);
        Object deserialize=serializer.deserialize(bytes,messageType);
        out.add(deserialize);
    }
}

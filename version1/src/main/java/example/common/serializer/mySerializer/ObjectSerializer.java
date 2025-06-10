package example.common.serializer.mySerializer;/*
 * @author ddy
 * @date 2025/6/10
 * */

import java.io.*;

public class ObjectSerializer implements Serializer{
    //利用Java io 对象->字节数组
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes=null;
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        try {
            //是一个对象输出流，用于将Java对象序列化为字节流，并将其连接到bos上
            ObjectOutputStream oos=new ObjectOutputStream(bos);
            oos.writeObject(obj);
            //刷新ObjectOutputStream，确保所有缓冲区的数据都被写入到底层流中
            oos.flush();
            //将bos其内部缓冲区中的数据转换为字节数组
            bytes= bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;

    }

    //原生序列化保留类型信息，虽然返回的是一个object，但此对象已经是实际类型了
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;

    }

    //0 代表Java原生序列器
    @Override
    public int getType() {
        return 0;
    }
}

package example.Server.server.work;

import lombok.AllArgsConstructor;
import example.Server.provider.ServiceProvider;
import example.common.Message.RpcRequest;
import example.common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

@AllArgsConstructor
public class WorkThread implements Runnable{
    private Socket socket;
    private ServiceProvider serviceProvider;
    @Override
    public void run() {
        try {
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest=(RpcRequest) ois.readObject();
            RpcResponse rpcResponse=getResponse(rpcRequest);
            oos.writeObject(rpcResponse);
            oos.flush();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private RpcResponse getResponse(RpcRequest rpcRequest){
        //得到服务名
        String interfaceName=rpcRequest.getInterfaceName();
        //得到相应的实现类
        Object service=serviceProvider.getService(interfaceName);
        Method method=null;
        try {
            //根据方法名和参数类型获取相应的方法
            method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamsType());
            //根据类和参数值执行相应的方法
            Object invoke=method.invoke(service,rpcRequest.getParams());
            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }

    }
}

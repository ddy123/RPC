package part1.Client.proxy;

import lombok.AllArgsConstructor;
import part1.Client.IOClient;
import part1.Client.rpcClient.RpcClient;
import part1.Client.rpcClient.impl.NettyRpcClient;
import part1.Client.rpcClient.impl.SimpleSocketRpcClient;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ClientProxy implements InvocationHandler {
   private RpcClient rpcClient;
   public ClientProxy(String host,int port,int choose){
       switch (choose){
           case 0:
               rpcClient=new NettyRpcClient(host,port);
               break;
           case 1:
               rpcClient=new SimpleSocketRpcClient(host,port);
       }
   }
   public ClientProxy(String host,int port){
       rpcClient=new NettyRpcClient(host,port);
   }
    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强(反射获取request对象，socket发送到服务端)
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request=RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramsType(method.getParameterTypes()).build();
        RpcResponse response= rpcClient.sendRequest(request);
        return response.getData();
    }
    public <T>T getProxy(Class clazz){
        Object o= Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
        return (T)o;
    }
}

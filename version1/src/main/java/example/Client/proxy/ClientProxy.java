package example.Client.proxy;

import example.Client.rpcClient.RpcClient;
import example.Client.rpcClient.impl.NettyRpcClient;
import example.Client.rpcClient.impl.SimpleSocketRpcClient;
import example.common.Message.RpcRequest;
import example.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ClientProxy implements InvocationHandler {
   private RpcClient rpcClient;
   public ClientProxy(){
               rpcClient=new NettyRpcClient();
   }

    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强(反射获取request对象，socket发送到服务端)
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //System.out.println("我是服务名称："+method.getDeclaringClass().getName());
        //System.out.println("我是方法名称:"+method.getName());
        //getDeclaringClass()返回声明了该字段方法的接口
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

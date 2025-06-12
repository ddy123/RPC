package example.Client.proxy;

import example.Client.circuitBreaker.CircuitBreaker;
import example.Client.circuitBreaker.CircuitBreakerProvider;
import example.Client.retry.guavaRetry;
import example.Client.rpcClient.RpcClient;
import example.Client.rpcClient.impl.NettyRpcClient;
import example.Client.rpcClient.impl.SimpleSocketRpcClient;
import example.Client.serviceCenter.ServiceCenter;
import example.Client.serviceCenter.ZKServiceCenter;
import example.common.Message.RpcRequest;
import example.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ClientProxy implements InvocationHandler {
   private RpcClient rpcClient;
   private ServiceCenter serviceCenter;
   private CircuitBreakerProvider circuitBreakerProvider;
   public ClientProxy() throws InterruptedException {
       rpcClient=new NettyRpcClient();
       serviceCenter=new ZKServiceCenter();
       circuitBreakerProvider=new CircuitBreakerProvider();
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
        //获取熔断器
        CircuitBreaker circuitBreaker=circuitBreakerProvider.getCircuitBreaker(method.getName());
        //判断熔断器是否允许请求经过
        if(!circuitBreaker.allowRequest()){
            //这里可以针对熔断器做特殊处理，返回特殊值
            return null;
        }

        //数据传输
        RpcResponse response;
        //后续添加逻辑，为保持幂等性，只对白名单上的服务进程重试
        if(serviceCenter.checkRetry(request.getInterfaceName())){
            //调用retry框架进行重试操作
            response=new guavaRetry().sendServiceWithRetry(request,rpcClient);
        }else {
            //只调用一次
            response=rpcClient.sendRequest(request);
        }
        //记录response的状态，上报给熔断器
        if(response.getCode()==200){
            circuitBreaker.recordSuccess();
        }
        if(response.getCode()==500){
            circuitBreaker.recordFailure();
        }
        return response.getData();
    }
    public <T>T getProxy(Class clazz){
        Object o= Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
        return (T)o;
    }
}

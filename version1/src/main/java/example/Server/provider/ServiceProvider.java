package example.Server.provider;

import example.Server.serviceRegister.ServiceRegister;
import example.Server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

//本地服务存放器,将接口名称与其实现类做个映射
public class ServiceProvider {
    //集合中存放服务的实例
    private Map<String,Object> interfaceProvider;
    private ServiceRegister serviceRegister;
    private int port;
    private String host;
    public ServiceProvider(String host,int port){
        //需要传入服务端自身的网络地址
        this.host=host;
        this.port=port;
        this.interfaceProvider=new HashMap<>();
        this.serviceRegister=new ZKServiceRegister();

    }

    //这里的service是个接口实现类
    public void providerServiceInterface(Object service){
        //获取类名
        String servicename=service.getClass().getName();
        System.out.println("我是类名称:"+servicename);
        //获取该类的接口
        Class<?>[] interfaceName=service.getClass().getInterfaces();
        for(Class<?> clazz:interfaceName){
            //本地映射  接口名：接口的具体实现类
            interfaceProvider.put(clazz.getName(),service);
            //在注册中心注册 接口名：ip：port
            serviceRegister.register(clazz.getName(),new InetSocketAddress(host,port));
        }
    }
    //根据接口名到注册中心查找服务的地址，访问具体的服务地址然后根据接口名找到具体的实例
    //通过接口名称获取服务实例
    public Object getService(String interfaceName){return  interfaceProvider.get(interfaceName);}
}

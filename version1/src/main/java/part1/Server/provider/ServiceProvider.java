package part1.Server.provider;

import java.util.HashMap;
import java.util.Map;

//本地服务存放器,将接口名称与其实现类做个映射
public class ServiceProvider {
    //集合中存放服务的实例
    private Map<String,Object> interfaceProvider;
    public ServiceProvider(){
        this.interfaceProvider=new HashMap<>();
    }
    public void providerServiceInterface(Object service){
        String servicename=service.getClass().getName();
        Class<?>[] interfaceName=service.getClass().getInterfaces();
        for(Class<?> clazz:interfaceName){
            interfaceProvider.put(clazz.getName(),service);
        }
    }
    //获取服务实例
    public Object getService(String interfaceName){return  interfaceProvider.get(interfaceName);}
}

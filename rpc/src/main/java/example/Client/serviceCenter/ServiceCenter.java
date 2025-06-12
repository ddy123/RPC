package example.Client.serviceCenter;

import java.net.InetSocketAddress;

public interface ServiceCenter {
    //查询：根据服务名查询地址
    InetSocketAddress serviceDiscovery(String serviceName);
    boolean checkRetry(String serviceName);
}

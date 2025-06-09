package example.Client.serviceCenter;/*
 * @author ddy
 * @date 2025/6/9
 * */

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceCenter implements ServiceCenter{
    //curator 提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper 根路径节点
    private static final String ROOT_PATH="MyRPC";
    //负责zookeeper客户端的初始化，并与zookeeper服务端进行连接
    public ZKServiceCenter(){
        //指重试时间
        RetryPolicy policy=new ExponentialBackoffRetry(1000,3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client= CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper连接成功");

    }
    //根据服务名返回地址
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> strings=client.getChildren().forPath("/"+serviceName);
            String string=strings.get(0);
            return parseAddress(string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private String getServiceAddress(InetSocketAddress serverAddress){
        return serverAddress.getHostName()+
                ":"+
                serverAddress.getPort();
    }
    //字符串解析为地址
    private InetSocketAddress parseAddress(String address){
        String[] result=address.split(":");
        return new InetSocketAddress(result[0],Integer.parseInt(result[1]));
    }
}

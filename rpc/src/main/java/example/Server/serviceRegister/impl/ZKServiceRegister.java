package example.Server.serviceRegister.impl;/*
 * @author ddy
 * @date 2025/6/9
 * */

import example.Server.serviceRegister.ServiceRegister;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;

public class ZKServiceRegister implements ServiceRegister {
    private CuratorFramework client;
    private static final String ROOT_PATH="MyRPC";

    private static final String RETRY="CanRetry";
    public ZKServiceRegister(){
        //指时间重试
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
    //注册到服务中心
    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress,boolean canRetry) {
        //System.out.println("我是路径"+serviceAddress);
        try {
            if(client.checkExists().forPath("/"+serviceName)==null){
                //serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/"+serviceName);
            }
            //路径地址，一个/代表一个节点
            String path="/"+serviceName+"/"+getServiceAddress(serviceAddress);
            //System.out.println("我是serviceName"+serviceName);
            //System.out.println("我是路径"+getServiceAddress(serviceAddress));
            //System.out.println("我是path:"+path);
            //临时节点，服务器下线就删除节点
            //即保留服务名
            //creatingParentsIfNeeded()如果父节点不存在则先创建父节点，这里是serviceName
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            if(canRetry){
                //RETRY是ROOT_PATH的子节点
                path="/"+RETRY+"/"+serviceName;
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            }
        } catch (Exception e) {
            System.out.println("此服务已存在");
        }
    }
    //地址->域名:port字符串
    private String getServiceAddress(InetSocketAddress serviceAddress){
        //在这里将ip转换为了域名+端口
        return serviceAddress.getHostName()+
        ":"+ serviceAddress.getPort();
    }
    //字符串解析为地址
    private InetSocketAddress parseAddress(String address){
        String[] result=address.split(":");
        return new InetSocketAddress(result[0],Integer.parseInt(result[1]));
    }
}

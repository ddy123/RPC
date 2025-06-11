package example.Client.serviceCenter.balance.impl;/*
 * @author ddy
 * @date 2025/6/11
 * */

import example.Client.serviceCenter.balance.LoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        Random random=new Random();
        int choose=random.nextInt(addressList.size());
        System.out.println("负载均衡选择了"+choose+"服务器");
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}

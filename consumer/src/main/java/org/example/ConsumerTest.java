package org.example;

import example.Client.proxy.ClientProxy;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.User;
import org.example.service.UserService;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ConsumerTest {
    private static final int THREAD_POOL_SIZE=20;
    private static final ExecutorService executorService= Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
            ClientProxy clientProxy=new ClientProxy();
            UserService proxy=clientProxy.getProxy(UserService.class);
            for(int i=0;i<120;i++){
                final Integer i1=1;
                if(i%30==0){
                    //每30个请求模拟延迟
                    Thread.sleep(10000);
                }
                executorService.submit(()->{
                  try{
                      User user=proxy.getUserByUserId(i1);
                      if (user != null) {
                          log.info("从服务端得到的user={}", user);
                      } else {
                          log.warn("获取的 user 为 null, userId={}", i1);
                      }
                      Integer id = proxy.insertUserId(User.builder()
                              .id(i1)
                              .userName("User" + i1)
                              .gender(true)
                              .build());

                      if (id != null) {
                          log.info("向服务端插入user的id={}", id);
                      } else {
                          log.warn("插入失败，返回的id为null, userId={}", i1);
                      }
                  }catch (Exception e){
                      log.error("调用服务时发生异常，userId={}", i1, e);
                  }
                });
            }
            executorService.shutdown();
            clientProxy.close();
    }
}

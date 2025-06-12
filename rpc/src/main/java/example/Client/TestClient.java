package example.Client;

import example.Client.proxy.ClientProxy;
import example.common.pojo.User;
import example.common.service.UserService;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class TestClient {

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        /*System.out.println(InetAddress.getLocalHost().getHostName());
        System.out.println(new InetSocketAddress("127.0.0.1",9999).getHostName());*/
        ClientProxy clientProxy=new ClientProxy();
        //ClientProxy clientProxy=new ClientProxy("127.0.0.1",9999,0);
        UserService proxy=clientProxy.getProxy(UserService.class);
        for(int i=0;i<120;i++){
            Integer i1=i;
            if(i%30==0){
                Thread.sleep(10000);
            }
            new Thread(()->{
              try {
                  User user=proxy.getUserByUserId(i1);
                  System.out.println("从服务端得到的user="+user.toString());
                  Integer id=proxy.insertUserId(User.builder().id(i1).userName("User"+i1.toString()).sex(true).build());
                  System.out.println("向服务端插入user的id"+id);
              }catch (NullPointerException e){
                  System.out.println("user为空");
                  e.printStackTrace();
              }
            }).start();
        }

       /* User user=proxy.getUserByUserId(1);
        System.out.println("从服务端获取的user="+user.toString());
        User u=User.builder().id(100).userName("ddy").sex(true).build();
        Integer id= proxy.insertUserId(u);
        System.out.println("向服务端插入的user id"+id);*/
    }
}

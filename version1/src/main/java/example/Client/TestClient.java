package example.Client;

import example.Client.proxy.ClientProxy;
import example.common.pojo.User;
import example.common.service.UserService;

public class TestClient {

    public static void main(String[] args){
        ClientProxy clientProxy=new ClientProxy();
        //ClientProxy clientProxy=new ClientProxy("127.0.0.1",9999,0);
        UserService proxy=clientProxy.getProxy(UserService.class);
        User user=proxy.getUserByUserId(1);
        System.out.println("从服务端获取的user="+user.toString());
        User u=User.builder().id(100).userName("ddy").sex(true).build();
        Integer id= proxy.insertUserId(u);
        System.out.println("向服务端插入的user id"+id);
    }
}

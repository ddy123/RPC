package example.common.service.Impl;

import example.common.pojo.User;
import example.common.service.UserService;

import java.util.Random;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询了"+id+"的用户");
        Random random=new Random();
        User user=User.builder().userName(UUID.randomUUID().toString()).id(id).sex(random.nextBoolean()).build();
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入数据成功"+user.getUserName());
        return user.getId();
    }
}

package example.common.service;

import example.common.pojo.User;

public interface UserService {
    User getUserByUserId(Integer id);
    Integer insertUserId(User user);
}

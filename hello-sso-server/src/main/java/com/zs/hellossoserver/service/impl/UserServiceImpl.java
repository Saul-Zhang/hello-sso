package com.zs.hellossoserver.service.impl;

import com.zs.hellossoserver.bean.Users;
import com.zs.hellossoserver.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author ZhangSong
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public Users findUser(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        Users users = new Users();
        users.setId(UUID.randomUUID().toString().replaceAll("-",""));
        users.setUsername(username);
        return users;
    }
}

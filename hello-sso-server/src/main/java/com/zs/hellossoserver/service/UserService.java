package com.zs.hellossoserver.service;


import com.zs.hellossoserver.bean.Users;

/**
 * @author ZhangSong
 */
public interface UserService {
    /**
     * 获取用户信息
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    Users findUser(String username, String password);
}

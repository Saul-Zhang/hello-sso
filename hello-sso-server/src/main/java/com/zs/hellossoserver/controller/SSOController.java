package com.zs.hellossoserver.controller;

import com.zs.hellossoserver.bean.Users;
import com.zs.hellossoserver.constant.Config;
import com.zs.hellossoserver.helper.CheckLoginHelper;
import com.zs.hellossoserver.service.UserService;
import com.zs.hellossoserver.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author ZhangSong
 */
@Controller
public class SSOController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private CheckLoginHelper checkLoginHelper;


    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        String ssoTicket = CookieUtils.getCookie(request, Config.COOKIE_SSO_TICKET);
        model.addAttribute("returnUrl", returnUrl);

        // 判断用户是否登录过了sso系统
        boolean isVerified = checkLoginHelper.isLogin(request);
        if (isVerified) {
            // 如果用户登录过，则产生一个临时票据重定向到业务系统
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }

        //用户没有登录过，那么要跳转到sso的统一登录页面
        return "login";
    }

    /**
     * 用户登录
     *
     * @param username  用户名
     * @param password  密码
     * @param returnUrl 调用方
     */
    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        String we = CookieUtils.getCookie(request, Config.COOKIE_SSO_TICKET);

        model.addAttribute("returnUrl", returnUrl);

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            model.addAttribute("errMsg", "用户名或密码不能为空");
            return "login";
        }

        Users userResult = userService.findUser(username, MD5Utils.getMD5Str(password));
        if (userResult == null) {
            model.addAttribute("errMsg", "用户名或密码不正确");
            return "login";
        }

        // 在redis中保存用户会话
        redisOperator.set(Config.REDIS_USER_SESSION + ":" + userResult.getId(), JsonUtils.objectToJson(userResult));

        // 生成sso_ticket，放在sso端的cookie中， 代表用户在sso登录过
        String ssoTicket = UUID.randomUUID().toString().trim();
        CookieUtils.setCookie(Config.COOKIE_SSO_TICKET, ssoTicket, response);

        // sso_ticket关联用户id，放入到redis中，代表这个用户有了全局票据，可以登录各个系统
        redisOperator.set(Config.REDIS_SSO_TICKET + ":" + ssoTicket, userResult.getId());

        // 生成临时票据，重定向到调用端网站
        String tmpTicket = createTmpTicket();
        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }


    /**
     * 检查用户的临时票据是否有效
     *
     * @param tmpTicket 临时票据
     */
    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public ResultT verifyTmpTicket(String tmpTicket,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        // 检查用户临时票据是否有效
        boolean flag = checkLoginHelper.verifyTmpTicket(tmpTicket, request);
        //如果有效，则返回用户信息
        if (flag) {
            String ssoTicket = CookieUtils.getCookie(request, Config.COOKIE_SSO_TICKET);
            String userId = redisOperator.get(Config.REDIS_SSO_TICKET + ":" + ssoTicket);
            String userRedis = redisOperator.get(Config.REDIS_USER_SESSION + ":" + userId);
            return ResultT.builder()
                    .code(ResultT.SUCCESS_CODE)
                    .data(JsonUtils.jsonToPojo(userRedis, Users.class))
                    .build();
        } else {
            return ResultT.builder()
                    .code(ResultT.FAIL_CODE)
                    .build();
        }

    }

    @PostMapping("/logout")
    @ResponseBody
    public ResultT logout(String userId,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        // 获取sso中的sso_ticket
        String ssoTicket = CookieUtils.getCookie(request, Config.COOKIE_SSO_TICKET);

        // 清除sso_ticket票据，redis/cookie
        CookieUtils.deleteCookie(Config.COOKIE_SSO_TICKET, response);
        redisOperator.del(Config.REDIS_SSO_TICKET + ":" + ssoTicket);

        // 清除用户会话
        redisOperator.del(Config.REDIS_USER_SESSION + ":" + userId);

        return ResultT.builder().code(ResultT.SUCCESS_CODE).build();
    }

    /**
     * 创建临时票据
     */
    private String createTmpTicket() {
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(Config.REDIS_TMP_TICKET + ":" + tmpTicket,
                    MD5Utils.getMD5Str(tmpTicket), 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }

    @ResponseBody
    @GetMapping("/sso")
    public String sso(HttpServletResponse response) {
        CookieUtils.setCookie("key1", "val1", response);
        return "sso";
    }


}

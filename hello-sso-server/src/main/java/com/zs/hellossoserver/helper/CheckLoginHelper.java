package com.zs.hellossoserver.helper;

import com.zs.hellossoserver.constant.Config;
import com.zs.hellossoserver.utils.CookieUtils;
import com.zs.hellossoserver.utils.MD5Utils;
import com.zs.hellossoserver.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangSong
 */
@Component
public class CheckLoginHelper {

    @Autowired
    private RedisOperator redisOperator;
    /**
     * 检查是否sso是否是登录状态
     */
    public boolean isLogin(HttpServletRequest request){
        // 从sso系统的cookie中获取sso_ticket
        String ssoTicket = CookieUtils.getCookie(request, Config.COOKIE_SSO_TICKET);

        // 如果获取到了sso_ticket,说明用户登录过了sso系统，校验这个ticket通过后,产生一个临时票据重定向到业务系统
        if (StringUtils.isBlank(ssoTicket)) {
            return false;
        }

        //验证sso票据是否有效
        String userId = redisOperator.get(Config.REDIS_SSO_TICKET + ":" + ssoTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }

        // 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(Config.REDIS_USER_SESSION + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }
        return true;
    }

    /**
     * 校验临时票据
     */
    public  boolean verifyTmpTicket(String tmpTicket,HttpServletRequest request) throws Exception {
        // 验证临时票据是否存在
        String tmpTicketValue = redisOperator.get(Config.REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
           return false;
        }

        // 验证临时票据是否正确
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
           return false;
        } else {
            // 销毁临时票据
            redisOperator.del(Config.REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        // 验证全局票据
        String ssoTicket = CookieUtils.getCookie(request, Config.COOKIE_SSO_TICKET);
        String userId = redisOperator.get(Config.REDIS_SSO_TICKET + ":" + ssoTicket);
        if (StringUtils.isBlank(userId)) {
           return false;
        }

        // 验证全局会话是否存在
        String userRedis = redisOperator.get(Config.REDIS_USER_SESSION + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }
        return true;
    }

    /**
     * 根据userId判断用户会话是否存在
     * @param userId
     * @return
     */
    public boolean verifySession(String userId){
        if(StringUtils.isBlank(userId)){
            return false;
        }
        String userRedis = redisOperator.get(Config.REDIS_USER_SESSION + ":" + userId);
        if(StringUtils.isBlank(userRedis)){
            return false;
        }
        return true;
    }
}

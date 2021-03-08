package com.zs.hellossoserver.utils;


import lombok.Builder;

/**
 * @author ZhangSong
 */
@Builder
public class ResultT {
    private int code;

    private String msg;

    private Object data;

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}

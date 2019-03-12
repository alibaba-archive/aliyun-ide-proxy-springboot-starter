package com.aliyun.dataworks.ide.proxy.common;

import java.io.Serializable;

/**
 * 记录一些类型值
 *
 * @author genxiaogu
 * @date 2019.03.12
 */
public enum Code implements Serializable {
    // 成功
    SUCCESS(200),
    // 失败
    ERROR(500)
    ;

    public int code;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private Code(int code) {
        this.code = code;
    }
}

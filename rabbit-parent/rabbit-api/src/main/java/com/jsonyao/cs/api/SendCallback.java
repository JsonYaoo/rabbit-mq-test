package com.jsonyao.cs.api;

/**
 * 回调函数处理
 */
public interface SendCallback {

    /**
     * 成功回调
     */
    void onSuccess();

    /**
     * 失败回调
     */
    void onFailure();
}

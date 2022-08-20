package com.xxxx.crm.utils;

import com.xxxx.crm.exceptions.ParamsException;

/**
 * 校验类
 * <p>
 * 乐字节：专注线上IT培训
 * 答疑老师微信：lezijie
 */
public class AssertUtil {


    /**
     * 判断条件是否满足
     * 如果条件满足，则抛出参数异常
     * <p>
     * <p>
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     *
     * @param flag
     * @param msg
     * @return void
     */
    // flag正确的话，就输出msg的信息
    public static void isTrue(Boolean flag, String msg) {
        if (flag) {
            throw new ParamsException(msg);
        }
    }

}

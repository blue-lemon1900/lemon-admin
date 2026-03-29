package org.lemon.commons.core.domain.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自定义状态码
     */
    private Integer code;

    /**
     * 提示内容，如果接口出错，则存放异常信息
     */
    private String msg;

    /**
     * 返回数据体
     */
    private T data;

    /**
     * 是否成功标记
     */
    private Boolean success;

    /**
     * 构造函数
     *
     * @param code    自定义状态码
     * @param msg     提示内容
     * @param data    返回数据体
     * @param success 是否成功标记
     */
    public R(Integer code, String msg, T data, Boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    /**
     * 请求成功返回
     * <p>public和返回值间的<T>指定的这是一个泛型方法，这样才可以在方法内使用T类型的变量</p>
     *
     * @param <T> 泛型
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> success() {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null, true);
    }

    /**
     * 请求成功返回
     * <p>public和返回值间的<T>指定的这是一个泛型方法，这样才可以在方法内使用T类型的变量</p>
     *
     * @param data 需要设置返回的对象
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> success(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data, true);
    }

    /**
     * 请求成功返回
     * <p>public和返回值间的<T>指定的这是一个泛型方法，这样才可以在方法内使用T类型的变量</p>
     *
     * @param msg 消息提示
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> success(String msg) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null, true);
    }

    /**
     * 请求成功返回
     * <p>public和返回值间的<T>指定的这是一个泛型方法，这样才可以在方法内使用T类型的变量</p>
     *
     * @param msg  消息提示
     * @param data 需要设置返回的对象
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> success(String msg, T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), msg, data, true);
    }

    /**
     * 请求失败返回
     *
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMsg(), null, false);
    }

    /**
     * 请求失败返回
     *
     * @param msg 需要设置返回的消息
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(ResultCode.FAILED.getCode(), msg, null, false);
    }

    /**
     * 请求失败返回
     *
     * @param code 自定义状态码
     * @param msg  需要设置返回的消息
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> fail(Integer code, String msg) {
        return new R<>(code, msg, null, false);
    }

    /**
     * 请求失败返回
     *
     * @param msg  需要设置返回的消息
     * @param data 需要设置返回的对象
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> fail(String msg, T data) {
        return new R<>(ResultCode.FAILED.getCode(), msg, data, false);
    }

    /**
     * 请求失败返回
     *
     * @param errorCode 需要设置返回的状态码表 {@link ResultCode}
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> fail(ResultCode errorCode) {
        return new R<>(errorCode.getCode(), errorCode.getMsg(), null, false);
    }

    /**
     * 请求失败返回
     *
     * @param errorCode 需要设置返回的状态码表 {@link ResultCode}
     * @param data      需要设置返回的对象
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> fail(ResultCode errorCode, T data) {
        return new R<>(errorCode.getCode(), errorCode.getMsg(), data, false);
    }

    /**
     * 请求警告返回
     *
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> warn() {
        return new R<>(ResultCode.WARN.getCode(), ResultCode.WARN.getMsg(), null, false);
    }


    /**
     * 请求警告返回
     *
     * @param msg 需要设置返回的消息
     * @return 返回对象 {@link R}
     */
    public static <T> R<T> warn(String msg) {
        return new R<>(ResultCode.WARN.getCode(), msg, null, false);
    }

}

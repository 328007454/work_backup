package com.cnksi.nari.utils;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/7/31 19:45
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ResultSet<T> {
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final int NEEDLOGIN = 3;
    public static final int NETWORK_ERROR = 4;

    private int status;
    private String desc;
    private T result;
    private Exception exception;

    public ResultSet() {
    }

    public ResultSet(int status, String desc, T result, Exception exception) {
        this.status = status;
        this.desc = desc;
        this.result = result;
        this.exception = exception;
    }

    public static <A> ResultSet<A> createINFO(String txt, A result) {
        return new ResultSet<>(SUCCESS, txt, result, null);
    }

    public static ResultSet createERROR(String txt, Exception e) {
        return new ResultSet(ERROR, txt, null, e);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setSuccess(T result, String desc) {
        this.setResult(result);
        this.setStatus(SUCCESS);
        this.setDesc(desc);
    }

    public void setError(Exception e, String desc) {
        this.setDesc(desc);
        this.setException(e);
        if (e != null && e.getClass().getName().startsWith("java.net")) {
            setStatus(NETWORK_ERROR);
        } else {
            setStatus(ERROR);
        }

    }

    public void setNeedlogin(String desc) {
        setDesc(desc);
        setStatus(NEEDLOGIN);
    }
}

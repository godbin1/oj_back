package com.hao.pojo;

/**
 * @author: haozhang
 * @Date: 2021/1/3 15:51
 */
public class CompileRequest {

    /**
     * 表示题目id
     */
    private int id;

    /**
     * 表示用户提交的代码
     */
    private String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

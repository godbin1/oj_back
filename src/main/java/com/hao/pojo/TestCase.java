package com.hao.pojo;

public class TestCase {
    private int id;
    private int problemId;
    private String test_in;
    private String test_out;

    // 默认构造函数
    public TestCase() {
    }

    // 构造函数
    public TestCase(int id, int problemId, String testIn, String testOut) {
        this.id = id;
        this.problemId = problemId;
        this.test_in = testIn;
        this.test_out = testOut;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getTestIn() {
        return test_in;
    }

    public void setTestIn(String testIn) {
        this.test_in = testIn;
    }

    public String getTestOut() {
        return test_out;
    }

    public void setTestOut(String testOut) {
        this.test_out = testOut;
    }
}

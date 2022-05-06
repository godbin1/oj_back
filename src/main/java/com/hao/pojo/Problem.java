package com.hao.pojo;

/**
 * @author: haozhang
 * @Date: 2020/12/21 17:46
 */
public class Problem {

    private Integer id;

    private String title;

    private String level;

    private String description;

    private String templateCode;

    private String testCode;

    public Problem() {
    }

    public Problem(Integer id, String title, String level, String description,
                   String templateCode, String testCode) {
        this.id = id;
        this.title = title;
        this.level = level;
        this.description = description;
        this.templateCode = templateCode;
        this.testCode = testCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                ", templateCode='" + templateCode + '\'' +
                ", testCode='" + testCode + '\'' +
                '}';
    }
}

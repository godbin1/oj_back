package com.hao.pojo;

public class Compare {

    public Compare(){}
    public boolean compareOutputs(String expectedOutput, String userOutput) {
        String[] expectedLines = expectedOutput.split("\\r?\\n");
        String[] userLines = userOutput.split("\\r?\\n");

        if (expectedLines.length != userLines.length) {
            return false;  // 行数不同，输出不一致
        }

        for (int i = 0; i < expectedLines.length; i++) {
            if (!expectedLines[i].equals(userLines[i])) {
                return false;  // 发现不一致的行
            }
        }

        return true;  // 所有行均一致
    }

}

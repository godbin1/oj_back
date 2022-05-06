package com.hao.controller;

import com.hao.pojo.Answer;
import com.hao.pojo.Question;
import com.hao.pojo.CompileRequest;
import com.hao.pojo.CompileResponse;
import com.hao.pojo.Problem;
import com.hao.service.ProblemService;
import com.hao.util.HttpBodyHandlerUtils;
import com.hao.util.ServerResponseUtil;
import com.hao.util.TaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author: haozhang
 * @Date: 2020/12/31 12:32
 */
@Controller
@RequestMapping("/oj")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @PostMapping("/all")
    @ResponseBody
    public List<Problem> queryAllList() {
        return problemService.queryAllList();
    }

    @RequestMapping("/toDo")
    public String toOne(Integer id) {
        return "forward:/html/doProblem.html";
    }

    @PostMapping("/one")
    @ResponseBody
    public String queryOneList(Integer id) {
        return HttpBodyHandlerUtils.pojoToString(problemService.queryOne(id));
    }

    @PostMapping("/compile")
    @ResponseBody
    public void compileAndRun(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //1.读取 请求 的 body 的所有数据
        String body = HttpBodyHandlerUtils.readBody(req);
        System.out.println("user_compile: \n" + body);

        if (!HttpBodyHandlerUtils.isCompileComplete(body)) {
            CompileResponse compileResponse = new CompileResponse();
            compileResponse.setOk(0);
            compileResponse.setReason("编译错误:您提交的代码无法完成编译");
            String jsonString = HttpBodyHandlerUtils.pojoToString(compileResponse);
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write(jsonString);
        } else {
            //2.按照API约定的格式来解析json数据，得到CompileRequest对象
            CompileRequest compileRequest = HttpBodyHandlerUtils.stringToPojo(body, CompileRequest.class);

            //3.按照 id 从数据库中读取出对应的测试用例代码

            Problem problem = problemService.queryOne(compileRequest.getId());

            //得到该题目的测试代码
            String testCode = problem.getTestCode();

            //得到改题目的用户输入的代码
            String requestCode = compileRequest.getCode();

            //4.把用户输入的代码和测试用例进行组装，组装成一个完整的可以运行编译的代码
            String finalCode = mergeCode(requestCode, testCode);
            System.out.println("compile_and_run_code: \n" + finalCode);


            //5.创建task对象对刚才拼装的代码进行编译运行
            Question question = new Question();
            question.setCode(finalCode);
            question.setStdin("");
            TaskUtil task = new TaskUtil();
            Answer answer = null;
            try {
                answer = task.compileAndRun(question);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

            //6.把运行结果构造成响应数据写回给客户端
            CompileResponse compileResponse = new CompileResponse();
            assert answer != null;
            compileResponse.setOk(answer.getError());
            compileResponse.setReason(answer.getReason());
            compileResponse.setStdout(answer.getStdout());
            String jsonString = HttpBodyHandlerUtils.pojoToString(compileResponse);
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write(jsonString);
        }
    }

    /**
     * 把testCode中的main方法内容嵌入到requestCode中
     * @param requestCode 用户的代码
     * @param testCode 测试用例代码
     * @return
     */
    private String mergeCode(String requestCode, String testCode) {
        //合并之前考虑清楚这两部分的代码都是什么样的
        //1.先找到requestCode中最后一个 }
        //2.把requestCode中最后一个 } 删除之后，再把testCode内容拼接上
        //3.拼接完之后再补一个 }
        int pos = requestCode.lastIndexOf('}');
        if (pos == -1) {
            return null;
        }

        return requestCode.substring(0, pos) + testCode + "\n}";
    }

}

package com.hao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.hao.pojo.*;
import com.hao.pojo.judge.Commit;
import com.hao.service.ProblemService;
import com.hao.util.HttpBodyHandlerUtils;
import com.hao.util.TaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: haozhang
 * @Date: 2020/12/31 12:32
 */
@Controller
@RequestMapping("/oj")
@CrossOrigin
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

    @RequestMapping("/one")
    @ResponseBody
    public String queryOneList(Integer id) {
        return HttpBodyHandlerUtils.pojoToString(problemService.queryOne(id));
    }

    @PostMapping("/compile")
    @ResponseBody
    public String compileAndRun(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1.读取请求的body的所有数据
        String body = HttpBodyHandlerUtils.readBody(req);
        System.out.println("user_compile: \n" + body);

        // 2. 按照API约定的格式来解析json数据，得到CompileRequest对象
        CompileRequest compileRequest = HttpBodyHandlerUtils.stringToPojo(body, CompileRequest.class);

        // 3. 按照id从数据库中读取出对应的测试用例代码
        Problem problem = problemService.queryOne(compileRequest.getId());
        System.out.println("body" + body);
        System.out.println("request:" + compileRequest);
        System.out.println("id:" + compileRequest.getId());
        System.out.println("problem:" + problem);

        // 4. 获取问题的所有测试样例
        List<TestCase> testcases = problemService.getTestCaseByProblemId(problem.getId());

        WebClient webClient = WebClient.create();

        List<String> tokens = new ArrayList<>();

        // 对每个测试样例进行评测
        for (TestCase testcase : testcases) {
            Commit commit = new Commit(compileRequest.getLanguage_id(), compileRequest.getCode(), testcase.getTestIn(), testcase.getTestOut(), 10, 4096);
            String requestBody = HttpBodyHandlerUtils.pojoToString(commit);
            System.out.println("testcase"+testcase.getTestIn());
            System.out.println("测试用例"+requestBody);
            System.out.println(requestBody);

            // 评测请求的URL
            String url = "http://localhost:2358/submissions";

            Mono<String> response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(String.class);

            response.flatMap(json -> {
                int start = json.indexOf("'");
                int end = json.length() - 2;
                String token = "";
                if (end > 10) {
                    token = json.substring(10, end);
                    tokens.add(token);
                }

                // 处理评测结果
                // ...

                return Mono.just(token);
            }).block(); // 阻塞并获取最终结果
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 创建一个 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();

// 创建一个 HttpHeaders 对象，设置请求头（可选）
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        int testindex = 0;
        ResponseEntity<Answer> lastAcceptedAnswer = null;
        for (String token : tokens) {
            ResponseEntity<Answer> response = restTemplate.exchange(
                "http://localhost:2358/submissions/" + token,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Answer.class,
                token
            );
            testindex++;

            Answer answer = response.getBody();
            if (answer != null && answer.getStatus() != null) {
                if (!"Accepted".equals(answer.getStatus().getDescription())) {
                    // 如果description不为"Accepted"，返回当前请求得到的结果
                    return HttpBodyHandlerUtils.pojoToString(answer);
                }
                System.out.println("请求："+response);
                System.out.println("body："+body);
                // 记录最后一个请求得到的结果
                lastAcceptedAnswer = response;
            }
        }

// 返回最后一个请求得到的结果（即使是"Accepted"）
        return lastAcceptedAnswer != null ? (HttpBodyHandlerUtils.pojoToString(lastAcceptedAnswer.getBody()) + ",\"nums\": "+testindex): null;
    }
// 延迟5秒钟后再发送请求

            // 处理获取的结果
            /*return getResultResponse.flatMap(result -> {
                // 在这里可以对获取的结果进行进一步处理
                System.out.println("result: \n");
                System.out.println(result + "\n");
                System.out.println("string;");
                System.out.println();
                try {
                    resp.setContentType("application/json; charset=utf-8");
                    // 将result以JSON形式传回前端
                    ObjectMapper objectMapper = new ObjectMapper();
                    String responseValue = objectMapper.writeValueAsString(result);
                    resp.getOutputStream().write(responseValue.getBytes(StandardCharsets.UTF_8));
                    return Mono.just(responseValue);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Mono.error(e);
                }
            });
        }).block(); */// 阻塞并获取最终结果









    //得到该题目的测试代码
            /*String testCode = problem.getTestCode();

            //得到改题目的用户输入的代码
            String requestCode = compileRequest.getCode();

            //4.把用户输入的代码和测试用例进行组装，组装成一个完整的可以运行编译的代码
            String finalCode = mergeCode(requestCode, testCode);
            System.out.println("compile_and_run_code: \n" + finalCode);*/


            //5.创建task对象对刚才拼装的代码进行编译运行
            /*Question question = new Question();
            *//*question.setCode(finalCode);*//*
            question.setStdin("");
            TaskUtil task = new TaskUtil();
            Answer answer = null;
            try {
                answer = task.compileAndRun(question);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            Compare compare = new Compare();
            boolean f = compare.compareOutputs("sss",answer.getStdout());

            //6.把运行结果构造成响应数据写回给客户端
            CompileResponse compileResponse = new CompileResponse();
            assert answer != null;
            if(f)
                compileResponse.setOk(answer.getError());
            else
                compileResponse.setOk(2);

            compileResponse.setReason(answer.getReason());
            compileResponse.setStdout(answer.getStdout());
            String jsonString = HttpBodyHandlerUtils.pojoToString(compileResponse);
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write(jsonString);*/



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

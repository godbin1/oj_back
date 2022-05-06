package com.hao.controller;

import com.hao.pojo.Problem;
import com.hao.service.AdminService;
import com.hao.service.ProblemService;
import com.hao.pojo.Admin;
import com.hao.util.HttpBodyHandlerUtils;
import com.hao.util.PojoToMapUtils;
import com.hao.util.ServerResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author: haozhang
 * @Date: 2021/1/4 13:14
 */
@Controller
public class AdminController {
    /**
     * ok = 1 表示密码正确
     */
    static class LoginResponse {
        String name;
        String reason;
        int ok;
    }

    @Autowired
    private ProblemService problemService;

    @Autowired
    private AdminService adminService;

    @RequestMapping("/manage")
    public String manage() {
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest request) {
        String body = HttpBodyHandlerUtils.readBody(request);
        System.out.println("login_body: \n" + body);
        Admin admin = HttpBodyHandlerUtils.stringToPojo(body, Admin.class);
        System.out.println("admin: \n" + admin);
        Admin daoAdmin = adminService.getAdminByName(admin.getUsername());
        if (daoAdmin == null || !daoAdmin.getPassword().equals(admin.getPassword())) {
            return ServerResponseUtil.serverResponse(0, "用户名或密码错误");
        } else {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", daoAdmin);
            session.setMaxInactiveInterval(120 * 60);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.ok = 1;
            loginResponse.reason = "";
            loginResponse.name = daoAdmin.getUsername();
            return HttpBodyHandlerUtils.pojoToString(loginResponse);
        }
    }

    @PostMapping("/checkLogin")
    @ResponseBody
    public String checkLogin(HttpServletRequest request) throws UnsupportedEncodingException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ServerResponseUtil.serverResponse(0, "您还未登录");
        } else {
            Admin admin = (Admin) session.getAttribute("user");
            if (admin == null) {
                return ServerResponseUtil.serverResponse(0, "您还未登录");
            } else {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.ok = 1;
                loginResponse.reason = "";
                loginResponse.name = admin.getUsername();
                return HttpBodyHandlerUtils.pojoToString(loginResponse);
            }
        }
    }

    @PostMapping("/add")
    @ResponseBody
    public String admin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ServerResponseUtil.serverResponse(0, "您还未登录");
        } else {
            Admin admin = (Admin) session.getAttribute("user");
            if (admin == null) {
                return ServerResponseUtil.serverResponse(0, "您还未登录");
            } else {
                String body = HttpBodyHandlerUtils.readBody(request);
                System.out.println("add_body: \n" + body);
                if (!HttpBodyHandlerUtils.isComplete(body)) {
                    return ServerResponseUtil.serverResponse(0, "请录入完整的Problem再提交");
                } else {
                    Problem problem = HttpBodyHandlerUtils.stringToPojo(body, Problem.class);
                    problem.setTestCode(problem.getTestCode().replaceAll("\\\\", ""));
                    System.out.println("add_problem: \n" + problem);
                    List<Problem> list = problemService.queryAllList();
                    if (problem.getId() <= list.size()) {
                        return ServerResponseUtil.serverResponse(0, "请录入正确的id值");
                    } else {
                        int i = problemService.insertProblem(problem);
                        System.out.println(i);
                        LoginResponse loginResponse = new LoginResponse();
                        loginResponse.ok = i;
                        loginResponse.reason = "";
                        loginResponse.name = admin.getUsername();
                        return HttpBodyHandlerUtils.pojoToString(loginResponse);
                    }
                }
            }
        }
    }

    @PostMapping("/all")
    @ResponseBody
    public String adminAll() {
        return HttpBodyHandlerUtils.pojoToString(problemService.queryAllList());
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deleteProblem(Integer id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ServerResponseUtil.serverResponse(0, "您还未登录");
        } else {
            Admin admin = (Admin) session.getAttribute("user");
            if (admin == null) {
                return ServerResponseUtil.serverResponse(0, "您还未登录");
            } else {
                int i = problemService.deleteProblem(id);
                if (i == 0) {
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.ok = i;
                    loginResponse.reason = "没有该题目";
                    loginResponse.name = admin.getUsername();
                    return HttpBodyHandlerUtils.pojoToString(loginResponse);
                } else {
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.ok = i;
                    loginResponse.reason = "";
                    loginResponse.name = admin.getUsername();
                    return HttpBodyHandlerUtils.pojoToString(loginResponse);
                }
            }
        }
    }

    @PostMapping("/toUpdate")
    @ResponseBody
    public String toUpdate(Integer id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ServerResponseUtil.serverResponse(0, "您还未登录");
        } else {
            Admin admin = (Admin) session.getAttribute("user");
            if (admin == null) {
                return ServerResponseUtil.serverResponse(0, "您还未登录");
            } else {
                Problem problem = problemService.queryOneProblemToAdmin(id);
                return HttpBodyHandlerUtils.pojoToString(problem);
            }
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateProblem(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ServerResponseUtil.serverResponse(0, "您还未登录");
        } else {
            Admin admin = (Admin) session.getAttribute("user");
            if (admin == null) {
                return ServerResponseUtil.serverResponse(0, "您还未登录");
            } else {
                String body = HttpBodyHandlerUtils.readBody(request);
                System.out.println("update_body: \n" + body);
                if (!HttpBodyHandlerUtils.isComplete(body)) {
                    return ServerResponseUtil.serverResponse(0, "请完整修改Problem再提交");
                } else {
                    Problem problem = HttpBodyHandlerUtils.stringToPojo(body, Problem.class);
                    problem.setTestCode(problem.getTestCode().replaceAll("\\\\", ""));
                    System.out.println("update_problem: \n" + problem);
                    Map<Object, Object> map = PojoToMapUtils.pojoToMap(problem);
                    int i = problemService.updateProblem(map);
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.ok = i;
                    loginResponse.reason = "";
                    loginResponse.name = admin.getUsername();
                    return HttpBodyHandlerUtils.pojoToString(loginResponse);
                }
            }
        }
    }
}

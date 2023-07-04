package com.hao.service;

import com.hao.mapper.ProblemMapper;
import com.hao.pojo.Problem;
import com.hao.pojo.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: haozhang
 * @Date: 2020/12/31 12:31
 */
@Service
public class ProblemServiceImpl implements ProblemService {



    @Autowired
    private ProblemMapper problemMapper;

    public void setProblemMapper(ProblemMapper problemMapper) {
        this.problemMapper = problemMapper;
    }

    @Override
    public List<Problem> queryAllList() {
        return problemMapper.queryAllList();
    }

    @Override
    public Problem queryOne(Integer id) {
        return problemMapper.queryOne(id);
    }

    @Override
    public int insertProblem(Problem problem) {
        return problemMapper.insertProblem(problem);
    }

    @Override
    public int deleteProblem(Integer id) {
        return problemMapper.deleteProblem(id);
    }

    @Override
    public int updateProblem(Map<Object, Object> map) {
        return problemMapper.updateProblem(map);
    }

    @Override
    public Problem queryOneProblemToAdmin(Integer id) {
        return problemMapper.queryOneProblemToAdmin(id);
    }

    @Override
    public List<TestCase> getTestCaseByProblemId(int id){
        return problemMapper.getTestCaseByProblemId(id);

    }

}

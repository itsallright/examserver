package com.hit.software.cloudserver.examserver

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.*

@Controller
@RequestMapping("/")
class MainController {

    // 数据库中表项索引
    private var userTestId = 0
    private var userAnswerId = 0

    // 随机选取N个题目返回
    private fun selectRandomProblems(Problems:Array<Problem>,N:Int):Array<Problem>{

        // 通过HashSet随机生成N个下标
        var labelSet = hashSetOf<Int>()
        while(labelSet.size<N && labelSet.size<Problems.size){
            labelSet.add(Random().nextInt(Problems.size))
        }

        // 返回下标对应的N个题目
        var ret = arrayOf<Problem>()
        labelSet.forEach { it -> ret += Problems[it] }
        return ret
    }

    // 查询数据库类
    @Autowired
    var mJdbcTemplate = JdbcTemplate()

    // 登录页面
    @RequestMapping("/")
    fun firstMethod():String{
        return "index"
    }

    // 安卓端登录
    @ResponseBody
    @PostMapping("/student/login")
    fun studentLoginMethod(@RequestBody login:Login):String{

        // 处理数据
        var hasResult = false
        val sql = "select * from user_info where user_name='${login.username}' and user_password='${login.userpassword}';"
        mJdbcTemplate.query(sql) {
            hasResult = true
        }

        return if(hasResult){
            "{\"code\":200}"
        }else{
            "{\"code\":201}"
        }
    }

    // 安卓端获取试卷列表
    @ResponseBody
    @GetMapping("/student/test")
    fun returnTests():String{
        var tests = arrayOf<Test>()
        val nowTime:String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        val sql = "select id,test_name,test_type from tests where start_time<'$nowTime' and end_time>'$nowTime';"
        mJdbcTemplate.query(sql) {
                tests += Test(it.getInt("id"),it.getString("test_name"),it.getString("test_type"))
            }

        return "{\"tests\":${Gson().toJson(tests)}}"
    }

    // 安卓端获取指定试卷
    @ResponseBody
    @PostMapping("/student/test")
    fun returnProblems(@RequestBody test:Test):String{

        // 获取测试中的问题
        var problems = arrayOf<Problem>()
        val sql = "select problems.id problem_id,problems.content problem_content,duration,problem_type from test_problem,problems where test_id = ${test.test_id};"
        mJdbcTemplate.query(sql){ it1: ResultSet ->
            val problemId:Int = it1.getInt("problem_id")

            // 获取问题的选项和正确答案
            val sql0 = "select id,content,correctness from options where problem_id = ${problemId};"
            var options = arrayOf<String>()
            var correctAnswer = ""
            mJdbcTemplate.query(sql0){
                options += it.getString("content")
                if(it.getInt("correctness")==1) {
                    correctAnswer += 'A'.plus(it.row-1) + "-"
                }
            }
            problems += Problem(problemId,
                    it1.getString("problem_type"),
                    it1.getInt("duration"),
                    it1.getString("problem_content"),
                    options,
                    correctAnswer.removeSuffix("-"))
        }

        return "{\"problems\":${Gson().toJson(problems)}}"
    }

    // 安卓端获取学生成绩
    @ResponseBody
    @GetMapping("/student/record")
    fun returnScores(@RequestBody user:Login):String{
        val sql = "select test_id,score from user_tests where user_name = '${user.username}';"
        var tests = arrayOf<Test>()
        mJdbcTemplate.query(sql){
            tests += Test(it.getInt("test_id"),Score = it.getInt("score"))
        }

        return "{\"tests\":${Gson().toJson(tests)}}"
    }

    // 安卓端提交学生做题情况
    @ResponseBody
    @PostMapping("/student/record")
    fun receiveStats(@RequestBody userStats:Stats):String{

        // 向user_tests表中添加数据
        var sql = "insert into user_tests value($userTestId,'${userStats.username}',${userStats.test_id},${userStats.score});"
        mJdbcTemplate.update(sql)
        sql = "select * from user_tests where id=$userTestId and user_name='${userStats.username}' and test_id=${userStats.test_id} and score=${userStats.score};"
        var hasResult = false
        mJdbcTemplate.query(sql){
            hasResult = true
        }
        if(!hasResult) {
            return "{\"code\":201}"
        }
        userTestId++

        // 向user_answers表中添加数据
        userStats.student_answers?.forEach{
            sql = "select id test_problem_id from test_problem where test_id=${userStats.test_id} and problem_id=${it.problem_id};"
            mJdbcTemplate.query(sql){it0 ->
                mJdbcTemplate.update("insert into user_answers value(${userAnswerId++},'${userStats.username}',${it0.getInt("test_problem_id")},'${it.student_answer}');")
            }
        }

        return "{\"code\":200}"
    }

    // 安卓端获取练习题目
    @ResponseBody
    @GetMapping("/student/practice")
    fun returnPracticeProblems(@RequestBody psf:ProblemSetFeature):String{
        var problems = arrayOf<Problem>()
        val sql = "select * from problems where problem_type = '${psf.problem_type}';"
        mJdbcTemplate.query(sql){ it1: ResultSet ->
            val problemId:Int = it1.getInt("problem_id")

            // 获取问题的选项和正确答案
            val sql0 = "select id,content,correctness from options where problem_id = ${problemId};"
            var options = arrayOf<String>()
            var correctAnswer = ""
            mJdbcTemplate.query(sql0){
                options += it.getString("content")
                if(it.getInt("correctness")==1) {
                    correctAnswer += 'A'.plus(it.row-1) + "-"
                }
            }
            problems += Problem(problemId,
                    it1.getString("problem_type"),
                    it1.getInt("duration"),
                    it1.getString("problem_content"),
                    options,
                    correctAnswer.removeSuffix("-"))
        }

        // 随机选择number个问题返回
        return "{\"problems\":${Gson().toJson(selectRandomProblems(problems,psf.number))}}"
    }

    // 网页端登录
    @ResponseBody
    @PostMapping("/teacher/login")
    fun teacherLoginMethod(@RequestBody login:Login):String{
        var hasResult = false
        val sql = "select * from teacher_info where teacher_name='${login.username}' and teacher_password='${login.userpassword}';"
        mJdbcTemplate.query(sql){
            hasResult = true
        }

        return if(hasResult){
            "{\"code\":200}"
        }else{
            "{\"code\":201}"
        }
    }

    // 网页端获取试卷列表
    @ResponseBody
    @GetMapping("/teacher/test")
    fun returnTests2():String{
        var tests = arrayOf<Test>()
        val sql = "select * from tests;"
        mJdbcTemplate.query(sql){
            tests += Test(
                    it.getInt("id"),
                    it.getString("test_name"),
                    it.getString("test_type"),
                    null,
                    it.getString("make_time"),
                    it.getString("start_time"),
                    it.getString("end_time"),
                    it.getString("maker")
            )
        }

        return "{\"tests\":${Gson().toJson(tests)}}"
    }

    // 网页端获取某一试卷所有学生做题情况
    @ResponseBody
    @PostMapping("/teacher/test")
    fun returnProblems2(@RequestBody test:Test):String{
        var studentStats = arrayOf<Stats>()
        mJdbcTemplate.query("select * from user_tests where id=${test.test_id};"){
            var studentAnswers = arrayOf<StudentAnswer>()
            mJdbcTemplate.query("select id, problem_id, correct_answer from test_problem where test_id=${test.test_id};"){it0 ->
                mJdbcTemplate.query("select answer from user_answers where user_name='${it.getString("user_name")}' and test_problem_id=${it0.getInt("id")};"){it1 ->
                    studentAnswers += StudentAnswer(it0.getInt("problem_id"),it0.getString("correct_answer"),it1.getString("answer"))
                }
            }
            studentStats += Stats(it.getString("user_name"),null,it.getInt("score"),studentAnswers)
        }

        return "{\"student_stats\":${Gson().toJson(studentStats)}}"
    }

    // 网页端增/改试卷
    @ResponseBody
    @PutMapping("/teacher/test")
    fun updateTests(@RequestBody test:Test):String{
        return ""
    }
}

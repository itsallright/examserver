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

    private var userTestId = 0
    // 查询数据库类
    @Autowired
    var mJdbcTemplate = JdbcTemplate()

    @RequestMapping("/")
    fun firstMethod():String{
        return "index"
    }

    @ResponseBody
    @PostMapping("/student/login")
    fun loginMethod(@RequestBody login:Login):String{
        // 处理数据
        var hasResult = false
        val sql = "select * from user_info where user_name='${login.username}' and user_password='${login.userpassword}';"
        mJdbcTemplate.query(sql) {
            hasResult = true
        }

        return if(hasResult){
            "{\"code\":\"200\"}"
        }else{
            "{\"code\":\"201\"}"
        }
    }

    @ResponseBody
    @GetMapping("/student/test")
    fun returnTests():String{
        var tests = arrayOf<TestName>()
        val nowTime:String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        val sql = "select id,test_name from tests where start_time<'$nowTime' and end_time>'$nowTime';"
        mJdbcTemplate.query(sql) {
                tests += TestName(it.getInt("id"),it.getString("test_name"))
            }

        return "{\"tests\":${Gson().toJson(tests)}}"
    }

    @ResponseBody
    @PostMapping("/student/test")
    fun returnProblems(@RequestBody test:String):String{

        // 获取测试中的问题
        var problems = arrayOf<Problem>()
        val sql = "select problems.id problem_id,problems.content problem_content,duration,problem_type from test_problem,problems where test_id = ${Gson().fromJson(test,Test::class.javaObjectType).test_id};"
        mJdbcTemplate.query(sql){ it1: ResultSet ->
            val problemId:Int = it1.getInt("problem_id")

            // 获取问题的选项和正确答案
            val sql0 = "select id,content,correctness from options where problem_id = ${problemId};"
            var options = arrayOf<Option>()
            var correctAnswer = ""
            mJdbcTemplate.query(sql0){
                options += Option(it.getInt("id"),it.getString("content"))
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

    @ResponseBody
    @GetMapping("/student/record")
    fun returnScores(@RequestBody user:Login):String{
        val sql = "select test_id,score from user_tests where user_name = '${user.username}';"
        var tests = arrayOf<TestScore>()
        mJdbcTemplate.query(sql){
            tests += TestScore(it.getInt("test_id"),it.getInt("score"))
        }

        return "{\"tests\":${Gson().toJson(tests)}}"
    }

    @ResponseBody
    @PostMapping("/student/record")
    fun receiveStats(@RequestBody userStats:Stats):String{
        val sql = "insert into user_tests value(${userTestId},'${userStats.username}',${userStats.test_id},${userStats.score});"
        mJdbcTemplate.query(sql){

        }
        return "{\"code\":\"200\"}"
    }
}

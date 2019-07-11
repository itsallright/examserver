package com.hit.software.cloudserver.examserver

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.*
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView


@Controller
@RequestMapping("/")
class MainController {

    // 查询数据库类
    @Autowired
    private var mJdbcTemplate = JdbcTemplate()

    // 随机选取N个题目返回
    private fun selectRandomProblems(Problems:Array<Problem>,N:Int):Array<Problem>{

        // 通过HashSet随机生成N个下标
        val labelSet = hashSetOf<Int>()
        while(labelSet.size<N && labelSet.size<Problems.size){
            labelSet.add(Random().nextInt(Problems.size))
        }

        // 返回下标对应的N个题目
        var ret = arrayOf<Problem>()
        labelSet.forEach { ret += Problems[it] }
        return ret
    }

    // 根据选项数组的下标和答案字符串判断某个选项是否正确
    private fun isCorrectAnswer(Index:Int,Answer:String?):Int{
        Answer?.split('-')?.forEach{
            if(Index == it[0].toInt()-'A'.toInt())
                return 1
        }
        return 0
    }

    // 登录页面
    @RequestMapping("/")
    fun firstMethod():String{ return "login" }

    @RequestMapping("/home")
    fun secondMethod(): String { return "home" }

    @RequestMapping("/create_newproblem")
    fun forthMethod(): String { return "create_newproblem" }

    @RequestMapping("/score")
    fun fifthMethod(): String { return "score" }

    @RequestMapping("/contact_us")
    fun sixMethod(): String { return "contact_us" }

    @RequestMapping("/make_paper")
    fun sevenMethod(): String { return "make_paper" }

//    @RequestMapping("/problem_pool")
//    fun eightMethod(): String { return "problem_pool" }

//    @RequestMapping("/papers")
//    fun nineMethod(): String { return "papers" }

    // 安卓端登录
    @ResponseBody
    @PostMapping("/student/login")
    fun studentLoginMethod(@RequestBody login:Login):String{
        var hasResult = false
        val sql = "select * from user_info where user_name='${login.username}' and user_password='${login.userpassword}';"
        mJdbcTemplate.query(sql) { hasResult = true }

        return if(hasResult) "{\"code\":200}" else "{\"code\":201}"
    }

    // 安卓端获取试卷列表
    @ResponseBody
    @GetMapping("/student/test")
    fun returnTests():String{
        var tests = arrayOf<TestInfo>()
        val nowTime:String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        val sql = "select id,test_name,test_type from tests where start_time<'$nowTime' and end_time>'$nowTime';"
        mJdbcTemplate.query(sql) {
                tests += TestInfo(it.getInt("id"),it.getString("test_name"),it.getString("test_type"))
            }

        return "{\"tests\":${Gson().toJson(tests)}}"
    }

    // 安卓端获取指定试卷
    @ResponseBody
    @PostMapping(value = ["/student/test","/teacher/test"])
    fun returnProblems(@RequestBody test:TestInfo):String{

        // 获取测试中的问题
        var problems = arrayOf<Problem>()
        val sql = "select problems.id problem_id,problems.content problem_content,duration,problem_type from test_problem,problems where test_id = ${test.test_id};"
        mJdbcTemplate.query(sql){ it1: ResultSet ->
            val problemId:Int = it1.getInt("problem_id")

            // 获取问题的选项和正确答案
            val sql0 = "select id,content,correctness from options where problem_id = $problemId;"
            var options = arrayOf<String>()
            var correctAnswer = ""
            mJdbcTemplate.query(sql0){
                options += it.getString("content")
                if(it.getInt("correctness")==1) {
                    correctAnswer += 'A'.plus(it.row-1) + "-"
                }
            }
            problems += Problem(
                    Id = problemId,
                    Type = it1.getString("problem_type"),
                    Duration = it1.getInt("duration"),
                    Content = it1.getString("problem_content"),
                    Options = options,
                    Correct = correctAnswer.removeSuffix("-"),
                    Maker = null)
        }

        return "{\"problems\":${Gson().toJson(problems)}}"
    }

    // 安卓端获取学生成绩
    @ResponseBody
    @PostMapping("/student/score")
    fun returnScores(@RequestBody user:Login):String{
        val sql = "select distinct test_name,score from user_tests,tests where user_name = '${user.username}';"
        var tests = arrayOf<TestInfo>()
        mJdbcTemplate.query(sql){
            tests += TestInfo(Name = it.getString("test_name"),Score = it.getInt("score"))
        }

        return "{\"tests\":${Gson().toJson(tests)}}"
    }

    // 安卓端提交学生做题情况
    @ResponseBody
    @PostMapping("/student/record")
    fun receiveStats(@RequestBody userStats:Stats):String{

        // 检查user_tests表中是否已经提交过成绩
        var hasScore = false
        mJdbcTemplate.query("select * from user_tests where user_name='${userStats.username}' and test_id=${userStats.test_id};"){ hasScore = true }
        if(hasScore) return "{\"code\":201}"

        // 向user_tests表中添加数据
        mJdbcTemplate.update("insert into user_tests value(default,'${userStats.username}',${userStats.test_id},${userStats.score});")

        // 向user_answers表中添加数据
        userStats.student_answers?.forEach{
            mJdbcTemplate.query("select id test_problem_id from test_problem where test_id=${userStats.test_id} and problem_id=${it.problem_id};"){it0 ->
                mJdbcTemplate.update("insert into user_answers value(default,'${userStats.username}',${it0.getInt("test_problem_id")},'${it.student_answer}');")
            }
        }

        return "{\"code\":200}"
    }

    // 安卓端获取练习题目
    @ResponseBody
    @PostMapping("/student/practice")
    fun returnPracticeProblems(@RequestBody psf:ProblemSetFeature):String{

        // 获取某一类型的所有题目
        var problems = arrayOf<Problem>()
        mJdbcTemplate.query("select * from problems where problem_type = '${psf.problem_type}';"){ it1 ->

            // 获取问题的选项和正确答案
            var options = arrayOf<String>()
            mJdbcTemplate.query("select id,content,correctness from options where problem_id = ${it1.getInt("id")};"){
                options += it.getString("content")
            }
            problems += Problem(
                    Id = it1.getInt("id"),
                    Type = it1.getString("problem_type"),
                    Duration = it1.getInt("duration"),
                    Content = it1.getString("content"),
                    Options = options,
                    Correct = it1.getString("correct_answer"),
                    Maker = null)
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
        mJdbcTemplate.query(sql){ hasResult = true }

        return if(hasResult) "{\"code\":200}" else "{\"code\":201}"
    }

    // 网页端获取某一试卷所有学生做题情况
    @ResponseBody
    @PostMapping("/teacher/record")
    fun returnProblems2(@RequestBody test:TestInfo):String{

        // 查询参加了某一试卷的所有学生
        var studentStats = arrayOf<Stats>()
        mJdbcTemplate.query("select * from user_tests where id=${test.test_id};"){

            // 查询某一学生此次测试的所有答案
            var studentAnswers = arrayOf<StudentAnswer>()
            mJdbcTemplate.query("select problem_id,correct_answer,answer from test_problem,problems,user_answers where test_id=${test.test_id} and user_name='${it.getString("user_name")}';"){it0 ->
                studentAnswers += StudentAnswer(
                        Id = it0.getInt("problem_id"),
                        CorrectAnswer = it0.getString("correct_answer"),
                        studentAnswer = it0.getString("answer")
                )
            }
            studentStats += Stats(
                    Name = it.getString("user_name"),
                    Id = null,
                    Score = it.getInt("score"),
                    StudentAnswers = studentAnswers
            )
        }

        return "{\"student_stats\":${Gson().toJson(studentStats)}}"
    }

    // 网页端获取试卷列表
    @ResponseBody
    @GetMapping("/teacher/test")
    fun returnTests2():ModelAndView{
        var tests = arrayOf<TestInfo>()
        val sql = "select * from tests;"
        mJdbcTemplate.query(sql){
            tests += TestInfo(
                    Id = it.getInt("id"),
                    Name = it.getString("test_name"),
                    Type = it.getString("test_type"),
                    Score = null,
                    StartTime = it.getString("start_time"),
                    EndTime = it.getString("end_time"),
                    Maker = it.getString("maker")
            )
        }

        return ModelAndView("papers","tests",tests)
    }

    // 网页端增/改试卷
    @ResponseBody
    @PutMapping("/teacher/test")
    fun updateTest(@RequestBody test:TestInfo):String{

        // 查询此试卷是否已经存在
        var hasTest = false
        mJdbcTemplate.query("select * from tests where id=${test.test_id?:-1};") { hasTest = true }

        // 删除原来试卷
        if(hasTest) mJdbcTemplate.execute("delete from tests where id=${test.test_id};")

        // 添加试卷
        mJdbcTemplate.update("insert into tests value(default,'${test.test_name}','${test.start_time}','${test.end_time}','${test.maker}','${test.test_type}');")
        test.problems?.forEach {
            mJdbcTemplate.update("insert into test_problem value(default,${test.test_id},$it);")
        }

        return "{\"code\":200}"
    }

    // 网页端删除试卷
    @ResponseBody
    @DeleteMapping("/teacher/test")
    fun deleteTest(@RequestBody test:TestInfo):String{
        mJdbcTemplate.execute("delete from tests where id=${test.test_id};")
        return "{\"code\":200}"
    }

    // 网页端获取题库
    @ResponseBody
    @GetMapping("/teacher/problems")
    fun returnAllProblems():ModelAndView{
        var problems = arrayOf<Problem>()
        mJdbcTemplate.query("select * from problems;"){

            // 获取问题选项
            var options = arrayOf<String>()
            mJdbcTemplate.query("select * from options where problem_id=${it.getInt("id")};"){it0->
                options += it0.getString("content")
            }
            problems += Problem(
                    Id = it.getInt("id"),
                    Content = it.getString("content"),
                    Duration = it.getInt("duration"),
                    Type = it.getString("problem_type"),
                    Maker = it.getString("maker"),
                    Options = options,
                    Correct = it.getString("correct_answer")
            )
        }

        return ModelAndView("problem_pool","problems",problems)
    }

    // 网页端增/改题目
    @ResponseBody
    @PostMapping("/teacher/problems")
    fun updateProblem(@RequestBody problem: Problem):String{

        // 判断题目是否已经存在
        var hasProblem = false
        mJdbcTemplate.query("select * from problems where id=${problem.problem_id?:-1};"){
            hasProblem = true
        }

        // 删除原题目
        if(hasProblem) mJdbcTemplate.execute("delete from problems where id=${problem.problem_id};")

        // 添加题目
        mJdbcTemplate.update("insert into problems value(default,'${problem.content}',${problem.duration},'${problem.problem_type}','${problem.maker}','${problem.correct_answer}');")

        // 获取problem ID
        var problemId = 0
        mJdbcTemplate.query("select LAST_INSERT_ID() id;"){
            problemId = it.getInt("id")
        }

        // 添加选项
        problem.options?.forEachIndexed { index, content ->
            mJdbcTemplate.update("insert into options value(default,$problemId,'$content',${isCorrectAnswer(index,problem.correct_answer)});")
        }
        return "{\"code\":200}"
    }

    // 网页端删除题目
    @ResponseBody
    @DeleteMapping("/teacher/problems")
    fun deleteProblem(@RequestBody problem:Problem):String{
        mJdbcTemplate.execute("delete from problems where id=${problem.problem_id};")
        return "{\"code\":200}"
    }
}

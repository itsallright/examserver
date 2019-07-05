package com.hit.software.cloudserver.examserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
class MainController {

    // 查询数据库类
    @Autowired
    var mJdbcTemplate: JdbcTemplate? = null

    @ResponseBody
    @RequestMapping("/login")
    fun firstMethod():String{
        println("one time")
        return "Hello world!"
    }
//
//    @ResponseBody
//    @PostMapping("/login")
//    fun loginMethod(@RequestBody login:login):String?{
//        println("POST")
//        // 处理数据
//        var hasResult = false
//        val sql = "select * from user_info where user_name='${login.username}' and user_password='${login.userpassword}';"
//        mJdbcTemplate?.query(sql) {
//            println(it.getString("user_name")+" "+it.getString("user_password"))
//            hasResult = true
//        }
//
//        return if(hasResult){
//            // 登录成功
//            "{\"code\":\"100\",\"result\":\"success\"}"
//        }else{
//            // 登录失败
//            "{\"code\":\"101\",\"result\":\"login failure\"}"
//        }
//    }
//
//    @ResponseBody
//    @GetMapping("/login")
//    fun login2Method(@RequestBody login:login):String?{
//        println("GET")
//        // 处理数据
//        var hasResult = false
//        val sql = "select * from user_info where user_name='${login.username}' and user_password='${login.userpassword}';"
//        mJdbcTemplate?.query(sql) {
//            println(it.getString("user_name")+" "+it.getString("user_password"))
//            hasResult = true
//        }
//
//        return if(hasResult){
//            // 登录成功
//            "{\"code\":\"100\",\"result\":\"success\"}"
//        }else{
//            // 登录失败
//            "{\"code\":\"101\",\"result\":\"login failure\"}"
//        }
//    }
}

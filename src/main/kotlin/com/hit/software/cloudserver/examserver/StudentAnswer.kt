package com.hit.software.cloudserver.examserver

class StudentAnswer(
        Id:Int?=null,
        CorrectAnswer:String?=null,
        studentAnswer:String?=null
) {
    var problem_id = Id
    var correct_answer = CorrectAnswer
    var student_answer = studentAnswer
}
package com.hit.software.cloudserver.examserver

class Stats(
        Name:String?= null,
        Id:Int?=null,
        Score:Int?=null,
        StudentAnswers:Array<StudentAnswer>?=null
) {
    var username = Name
    var test_id = Id
    var score = Score
    var student_answers = StudentAnswers
}
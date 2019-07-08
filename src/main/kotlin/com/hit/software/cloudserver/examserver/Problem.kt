package com.hit.software.cloudserver.examserver

class Problem(Id:Int,Type:String,Duration:Int,Content:String,Options:Array<Option>,Correct:String) {
    var problem_id:Int = Id
    var problem_type:String = Type
    var duration:Int = Duration
    var content:String = Content
    var options:Array<Option> = Options
    var correct_answer:String = Correct
}
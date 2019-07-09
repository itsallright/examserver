package com.hit.software.cloudserver.examserver

class Problem(
        Id:Int?=null,
        Type:String?=null,
        Duration:Int?=null,
        Content:String?=null,
        Maker:String?=null,
        Options:Array<String>?=null,
        Correct:String?=null,
        MakeTime:String?=null
) {
    var problem_id = Id
    var problem_type = Type
    var maker = Maker
    var duration = Duration
    var content = Content
    var options = Options
    var correct_answer = Correct
    var make_time = MakeTime
}
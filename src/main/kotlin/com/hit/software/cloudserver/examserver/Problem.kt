package com.hit.software.cloudserver.examserver

import com.google.gson.annotations.SerializedName

class Problem(Id:Int?,Type:String?,Duration:Int?,Content:String?,Options:Array<String>?,Correct:String?) {
    var problem_id = Id
    var problem_type = Type
    var duration = Duration
    var content = Content
    var options = Options
    var correct_answer = Correct
}
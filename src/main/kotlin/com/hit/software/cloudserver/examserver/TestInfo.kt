package com.hit.software.cloudserver.examserver

import java.text.SimpleDateFormat
import java.util.*

class TestInfo(
        Id:Int?=null,
        Name:String?=null,
        Type:String?=null,
        Score:Int?=null,
        StartTime:String?=null,
        EndTime:String?=null,
        Maker:String?=null,
        Problems:Array<Int>?=null
){
    var test_id = Id
    var test_name = Name
    var test_type = Type
    var score = Score
    var start_time = StartTime
    var end_time = EndTime
    var maker = Maker
    var problems = Problems
}
package com.hit.software.cloudserver.examserver

class TestInfo(
        Id:Int?,
        Name:String?=null,
        Type:String?=null,
        Score:Int?=null,
        MakeTime:String?=null,
        StartTime:String?=null,
        EndTime:String?=null,
        Maker:String?=null,
        Problems:Array<Int>?=null
){
    var test_id = Id
    var test_name = Name
    var test_type = Type
    var score = Score
    var make_time = MakeTime
    var start_time = StartTime
    var end_time = EndTime
    var maker = Maker
    var problems = Problems
}
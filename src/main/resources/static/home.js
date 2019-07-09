// 从服务端接收到返回的信息
// $(document).ready(function() {
//             $.ajax({
//                 url : "login.html",//后台请求的数据
//                 dataType : "json",//数据格式，这里需要服务端返回的是老师的名字（username）
//                 type : "post",//请求方式
//                 async : false,//是否异步请求
//                 success : function(data) {   //如果请求成功，返回数据。
//                 var html = "";
//                 for(var i=0;i<data.length;i++){    //遍历data数组
//                         var ls = data[i];     
//                         html +="<span>测试："+ls.name+"</span>";
//                     }
//                     $("#test").html(html); //在html页面id=test的标签里显示html内容
//                 },
//             })
//         })
         
         
 //    var problem_id = data.problem_id.value;
 //    var content = data.problem_content;
 //    var duration = data.problem_duration>;
 //    var problem_type = data.problem type;
 //            "maker": <problem maker>,
 //            "make_time": <problem make time>,
 //            "options": [<option content>],
 //            "correct_answer": <answer_string>
 //         

// document.getElementById('username').innerHTML = data.username;
 document.getElementById('username').innerHTML = "张三";


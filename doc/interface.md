student 为学生安卓端接口

teacher 为教师网页端接口

- Request 为向服务器发送的数据
- Response 为服务器返回的数据

### **数据取值**

status code:
- 200: success
- 201: failure

answer_string:
- single_answer: A|B|C|D|N
- multiple_answer: connect single answer character with '-'.

# /student/

## /student/login/

### POST
- Request
```
{
    "username": <student's name>,
    "userpassword": <student's password>
}
```

- Response
```
{
    "code": <status code>
}
```

## /student/test/

### GET
- Response
```
{
    "tests": [
        {
            "test_id": <test ID>,
            "test_name": <test name>,
            "test_type": <test type>
        }
    ]
}
```

### POST
- Request
```
{
    "test_id": <test ID>
}
```

- Response
```
{
    "problems": [
        {
            "problem_id": <problem ID>,
            "problem_type": <problem type>,
            "duration": <integer>,
            "content": <problem content>,
            "options": [<option content>],
            "correct_answer": <answer_string>
        }
    ]
}
```

## /student/score/

### POST
- Request
```
{
    "username": <student's name>,
}
```

- Response
```
{
    "tests": [
        {
            "test_name": <test name>,
            "score": <integer>
        }
    ]
}
```

## /student/record/

### POST
- Request
```
{
    "username": <student's name>,
    "test_id": <test ID>,
    "score": <student's score>,
    "student_answers": [
        {
            "problem_id": <problem ID>,
            "correct_answer": <answer_string>,
            "student_answer": <answer_string>
        }
    ]
}
```

- Response
```
{
    "code": 200
}
```

## /student/practice/

### POST
- Request
```
{
    "problem_type": <problem type>,
    "number": <problem number>
}
```

- Response
```
{
    "problems": [
        {
            "problem_id": <problem ID>,
            "problem_type": <problem type>,
            "duration": <integer>,
            "content": <problem content>,
            "options": [<option content>],
            "correct_answer": <answer_string>
        }
    ]
}
```

# /teacher/

## /teacher/login/

### POST
- Request
```
{
    "username": <teacher's name>,
    "userpassword": <teacher's password>
}
```

- Response
```
{
    "code": <status code>
}
```

## /teacher/record/

### POST
- Request
```
{
    "test_id": <test ID>
}
```

- Response
```
{
    "student_stats":[
        {
            "username": <student's name>,
            "score": <student's score>,
            "student_answers": [
                {
                    "problem_id": <problem ID>,
                    "correct_answer": <answer_string>,
                    "student_answer": <answer_string>
                }
            ]
        }
    ]
}
```

## /teacher/test/

### GET
- Response
```
{
    "tests":[
        {
            "test_id": <test ID>,
            "test_name": <test name>,
            "test_type": <test type>,
            "start_time": <YYYY-MM-DD HH:MM:SS>,
            "end_time": <YYY-MM-DD HH:MM:SS>,
            "maker": <teacher's name>
        }
    ]
}
```

### POST
- Request
```
{
    "test_id": <test ID>
}
```

- Response
```
{
    "problems": [
        {
            "problem_id": <problem ID>,
            "problem_type": <problem type>,
            "duration": <integer>,
            "content": <problem content>,
            "options": [<option content>],
            "correct_answer": <answer_string>
        }
    ]
}
```

### PUT
- Request
```
{
    "test_id": <test ID(if new test this is -1)>,
    "test_name": <test name>,
    "start_time": <YYYY-MM-DD HH:MM:SS>,
    "end_time": <YYYY-MM-DD HH:MM:SS>,
    "maker": <teacher name>,
    "test_type": <test type>,
    "problems": [<problem ID>]
}
```

- Response
```
{
    "code": <status code>
}
```

### DELETE
- Request
```
{
    "test_id": <test ID>
}
```

- Response
```
{
    "code": <status code>
}
```

## /teacher/problems/

### GET
- Response
```
{
    "problems": [
        {
            "problem_id": <problem ID>,
            "content": <problem content>,
            "duration": <problem duration>,
            "problem_type": <problem type>,
            "maker": <problem maker>,
            "options": [<option content>],
            "correct_answer": <answer_string>
        }
    ]
}
```

### POST
- Request
```
{
    "problem_id": <problem ID(if is a new problem, this is -1)>,
    "content": <problem content>,
    "duration": <problem duration>,
    "problem_type": <problem type>,
    "maker": <teacher's name>,
    "options": [<option content>],
    "correct_answer": <answer_string>
}
```

- Response
```
{
    "code": <status code>
}
```

### DELETE
- Request
```
{
    "problem_id": <problem ID>
}
```

- Response
```
{
    "code": <status code>
}
```

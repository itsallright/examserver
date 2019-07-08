create database TestData;
use TestData

-- 学生信息
create table user_info(
    user_name varchar(20),
    user_password varchar(15) not null,
    primary key(user_name)
);

-- 教师信息
create table teacher_info(
    teacher_name varchar(20),
    teacher_password varchar(15) not null,
    primary key(teacher_name)
);

-- 题目
create table problems(
    id integer,
    content text,
    duration integer,
    problem_type varchar(30),
    maker varchar(20),
    make_time datetime,
    primary key(id),
    foreign key(maker) references teacher_info(teacher_name) on delete cascade
);

-- 选项
create table options(
    id integer,
    problem_id integer,
    content text,
    correctness boolean,
    primary key(id),
    foreign key(problem_id) references problems(id) on delete cascade
);

-- 试卷
create table tests(
    id integer,
    test_name varchar(60),
    make_time datetime,
    start_time datetime,
    end_time datetime,
    maker varchar(20),
    test_type varchar(30),
    primary key(id),
    foreign key(maker) references teacher_info(teacher_name) on delete cascade
);

-- 试卷&题目关联表
create table test_problem(
    id integer,
    test_id integer,
    problem_id integer,
    primary key(id),
    foreign key(test_id) references tests(id) on delete cascade,
    foreign key(problem_id) references problems(id) on delete cascade
);

-- 用户&测试关联表
create table user_tests(
    id integer,
    user_name varchar(20),
    test_id integer,
    score integer,
    primary key(id),
    foreign key(user_name) references user_info(user_name) on delete cascade,
    foreign key(test_id) references tests(id) on delete cascade
);

-- 用户做题情况
create table user_answers(
    id integer,
    user_name varchar(20),
    test_problem_id integer,
    answer varchar(5),
    primary key(id),
    foreign key(user_name) references user_info(user_name) on delete cascade,
    foreign key(test_problem_id) references test_problem(id) on delete cascade
);

insert into Roles (role_Name, is_project_admin) VALUES ('admin', 1)
insert into Users (fullname, email, [password], role_id) VALUES ('Thang', 'doanvanthang4271@gmail.com', 'abc', 1)
insert into Milestones (milestone_Name, due_on, [project_id], [status], is_started, completed_on) VALUES ('Test milestone', getDate(), 1, 0, 1, GETDATE())
insert into TestPlan (plan_Name, project_id, milestone_id) VALUES ('plan name', 1, 1)
insert into Sections (section_Name, project_id, plan_id) VALUES ('section name', 1, 1)
insert into TestRun (run_ID, run_Name, created_on, milestone_id, user_id, project_id, is_completed, include_all, failed_count, passed_count, retest_count, untested_count) values (1, 'test run', getdate(), 1, 1, 1, 1, 1, 3, 4, 6, 3)
insert into Priorities(priority_name, priority_count, short_name, is_default, is_delete)
values('priority name', 2, 'short name', 1, 1)
insert into [Status] (status_Name) values ('PASSED')
select * from TestCase
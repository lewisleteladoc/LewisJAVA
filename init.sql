create database Health;

go

use Health;

go

drop table BloodPressure
go

create table BloodPressure(
id int identity(1,1),
systolic int not null,
diastolic int not null,
heartRate int  not null,
dateTaken date not null,
);
go
drop table UserBloodPressure
go

create table UserBloodPressure(
id int identity(1,1),
userId varchar(20)  not null,
bloodPressureId int  not null
)

go

select * from UserBloodPressure


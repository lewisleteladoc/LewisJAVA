use Health;
go

drop procedure sp_addbloodpressure
go

create procedure sp_addbloodpressure
	@systolic int,
	@diastolic int,
	@heartRate int,
	@userId varchar(20),
	@newId      INT OUTPUT
as

	insert into BloodPressure (systolic, diastolic, heartRate, dateTaken)
	values (@systolic, @diastolic, @heartRate, getDate())

	SET @newId = SCOPE_IDENTITY()  -- captures the new ID

	INSERT INTO UserBloodPressure (userId, bloodPressureId)
	VALUES (@userId, @newId)

go




go

DECLARE @insertedId INT

EXEC sp_addBloodPressure
    @systolic  = 120,
    @diastolic = 80,
    @heartRate = 72,
    @userId    = 'user1',
    @newId     = @insertedId OUTPUT

SELECT @insertedId AS NewId
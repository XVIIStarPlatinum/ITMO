SELECT COUNT(*) as AveragePerformingStudents FROM
    (SELECT ОЦЕНКА FROM Н_ВЕДОМОСТИ WHERE Н_ВЕДОМОСТИ.ОЦЕНКА = '3') A;

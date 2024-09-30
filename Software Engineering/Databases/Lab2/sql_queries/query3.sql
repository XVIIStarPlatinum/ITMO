SELECT COUNT(*) as UniqueNameCount FROM
    (SELECT ФАМИЛИЯ FROM Н_ЛЮДИ GROUP BY ФАМИЛИЯ) t;

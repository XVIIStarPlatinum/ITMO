CREATE OR REPLACE FUNCTION oldestCrewMember(crewID INT)
    RETURNS text
    LANGUAGE plpgsql as $func$
DECLARE
    OldestAge BIGINT;
    OldestCrewMember text;
BEGIN
    SELECT max(age) from crewMember WHERE ID
    IN(SELECT ID from crewMember WHERE ID
    IN(SELECT ID from crew where spaceshipID = crewID)) INTO OldestAge;
    SELECT fullName from crewMember where age = OldestAge INTO OldestCrewMember;
    RETURN OldestCrewMember;
END;
$func$;
-- Этот функция убивает всех членов экипажи при взрыве корабля.
CREATE OR REPLACE FUNCTION reflect_ship_condition()
    RETURNS TRIGGER LANGUAGE plpgsql AS $FUNC$
BEGIN
    DELETE FROM crewmember WHERE ID IN(
        SELECT ID FROM crewmember WHERE ID IN(
        SELECT ID FROM CREW WHERE spaceshipID IN(
        SELECT ID FROM spaceship WHERE spaceship.integrity ~* 'взорван|exploded')));
    DELETE FROM spaceship WHERE spaceship.integrity ~* 'взорван|exploded';
    RETURN NEW;
END;
$FUNC$;
-- Этот триггер активируется при взрыве космического корабля.
CREATE OR REPLACE TRIGGER EXPLODED
    AFTER UPDATE ON spaceship
    FOR EACH ROW EXECUTE FUNCTION reflect_ship_condition();
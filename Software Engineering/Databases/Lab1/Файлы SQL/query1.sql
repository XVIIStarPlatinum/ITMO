SELECT
    crewMember.ID,
    fullName,
    age,
    gender
FROM crewMember join sarcophagus s on crewMember.ID = s.ID
WHERE((s.state) = 'asleep');
 





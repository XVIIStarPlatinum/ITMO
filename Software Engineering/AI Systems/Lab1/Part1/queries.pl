% Простые запросы к базе знаний для поиска фактов:
% Какая молярная масса у молекулы неопентана?
?- molar_mass('Neopentane', X).
% Какие температуры переход фаз у кубана?
?- temperatures('Cubane', X, Y).
% Запросы, использующие логические операторы (и, или, не) для формулирования сложных условий:
% Какие молекулы либо являются жидкостью, либо является токсичными?
?- (temperatures(X, T1, T2), T1 < 25, T2 > 25; nfpa(X, H, _, _, _), H >= 3).
% Какие соединения являются газами без заданного давления насыщенного пара и только вызывают раздражение?
?- organic_compound(Compound), vapor_pressure(Compound, V), V = 'Empty', nfpa(Compound, H, _, _, _), H =< 1.
% Какие соединения не являются неограниченно растворимыми в воде?
organic_compound(Compound), \+ water_solubility(Compound, 'miscible')

% Запросы, использующие переменные для поиска объектов с определенными характеристиками: 
% Какие молекулы имеют молярную массу 72.151?
molar_mass(X, 72.151).
% Какие соединения могут детонировать?
nfpa(X, _, _, R, _), R >= 3.


% Запросы, которые требуют выполнения правил для получения результата:
% Какие молекулы являются удушаюшим по действии:
list_of_specials(X, 'SA')
% Какие соединения являются летучими и имеет среднее значение, равный 2, по NFPA 704?
is_volatile(X), nfpa_avg(X, 2)

% Запросы по всем правилам.
?- list_of_compounds(Compound).
?- is_isomeric('n-Butane', 'Isobutane').
?- is_lightweight('Molecule').
?- is_dense('Compound').
?- state_of_matter('Compound').
?- is_volatile('Benzene').
?= solubility('Benzaldehyde').
?- nfpa_hazard(X, 1, _, _).
?- nfpa_special_hazard('Acetic acid').
?- nfpa_avg('Aniline', X).
?- list_of_specials(X, 'COR').
?- nfpa_compare('Formaldehyde', 'Acetaldehyde').
?- list_of_volatiles(X).
# I/İTMO/SE/DB

---
> Мегафакультет КТУ, факультет программной инженерии и компьютерной техники в университете информационной технологии, механики и оптики в г. Санкт-Петербург

# Late night ~~drive~~ practices
<p align="center">
    <img src="https://media1.tenor.com/m/XhZOSshvCmQAAAAC/avril-lavigne-damn-cold-night.gif" alt="perfection"/>
</p>

| :exclamation: <b>Любое копирование материалов целиком или частично,<br>но без ссылки на автора, является кражей интеллектуальной собственности.<br>Это плагиат, за который из ИТМО отчисляют.</b> :exclamation:<br><sub><sup><i>(ещё получите 1-(2’’-гидроксилциклогексил)-3-[аминопропил]-4-[3’-аминопропил]пиперазин)-ы от нас)</sup></sub></b> |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Здесь представлены лабораторные, рубежные работы и экзамен по дисциплине **Базы данных**.

Если хотите что-то добавить в репозиторий, отправляйте **Pull request** :mailbox_with_mail:. После проверки информация будет добавлена.

Если я упустил кое-какой язык, то флаг в руки вам создать свою версию ридмишек и отправить **Pull Request**.

| [<strong>Русский</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/README.md) | [<strong>English</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/.docs/README_EN.md) | [<strong>Монгол</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/.docs/README_MN.md) | [<strong>Español</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/.docs/README_ES.md) | [<strong>中文</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/.docs/README_CN.md) | [<strong>Tiếng việt</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/.docs/README_VN.md) | [<strong><p dir="rtl" lang="ar">اَلْعَرَبِيَّةُ</p></strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/.docs/README_AR.md) | [<strong>हिन्दी</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/.docs/README_IN.md) | [<strong>Português</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Databases/.docs/README_PT.md) |
|-----------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
> Хорошая работа, малыш. Ты решил связаться с неправильным чуваком. Прямо сейчас я на расстоянии одного клика от использования атаки SQL-инъекцией, чтобы получить доступ к базе данных этого веб-сайта и получить ваши учетные данные для входа, а также ваш IP-адрес. Затем я продолжу утечку вашего IP-адреса на мои хакерские форумы, а затем, используя IP-локатор, я легко найду ваш дом и ваше имя. Благодаря этому я смогу получить доступ к вашим аккаунтам в социальных сетях и начать разрушать вашу жизнь. Думаешь, я блефую? Вы думаете, что только потому, что группа семилетних детей говорит, что они могут «взломать», но на самом деле не могут, я тоже не могу? Позвольте мне разрушить этот фасад для вас. С помощью моей команды хакеров мы сможем отследить, какие веб-сайты вы посещали и какие клавиши нажимали, и благодаря этому я смогу проникнуть на банковский счет ваших родителей. Я могу осушить его и сделать тебя нищим. Вы когда-нибудь чувствовали, что значит быть бездомным? Ну, ты собираешься это сделать. Я также могу использовать межсайтовый скриптинг, чтобы перенаправить ваш веб-браузер на один из моих частных сайтов, загрузить нелегальные файлы на ваш компьютер и запереть ваших родителей в тюрьме. Я твой худший кошмар, и я собираюсь заставить тебя пережить ад. Я нигде и везде одновременно. Развлекайся, сожалея о своем существовании, малыш.
---
**Лектор**: [Николаев Владимир Вячеславович](https://my.itmo.ru/persons/146060)

**Практик**: [Чупанов Аликылыч Алибекович](https://my.itmo.ru/persons/285317)

## Результаты

---
<s>Халява</s>
- II семестр: **4C** (${\color{yellow}}80.1$/100 баллов)

## Рубежка (от [@worthant](https://github.com/worthant))
0) Всем выдают предметную область - потом одновременно переворачиваете
1) По ней надо выделить 8 сущностей, одна из которых имеет минимум 6 атрибутов
   - описать инфологическую модель, сделать связь many to many
2) построить даталогическую диаграмму, описать органичения целостностей
3) создать три сущности используя DDL - вместе с ограничениями целостности
4) привести пример использоваться DML
5) создать представление, которое считает количество строк той самой таблицы, где минимум 6 атрибутов

Засады:
1) текст гавно (у кого как, но у меня больше 3 сущностей было не выделить - но за 30 минут можно додумать и сделать
   хорошую интересную диаграмку в целом. просто надо буквально всё выдумать)
2) слишком долго сидеть на 1 и 2 задании. Нет, в целом так и надо, просто советую сделать сразу же 4 и 5, ибо они изи и делаются за 2 минуты. А потом уже строить диаграммы и к ним на DDL создавать таблицы
## Экзамен (2023)
![exam](/img/charts/db_exam.jpg)

## Полезные ссылки <a name="links"></a>
| Ссылка                                                                                                                               | Описание                    |
|--------------------------------------------------------------------------------------------------------------------------------------|-----------------------------|
| https://vk.com/club84933635                                                                                                          | Паблик Николаева            |
| https://docs.google.com/document/d/1VlCpJtjxr4mZcazKUanbjqBC2RfhEM9D/edit?usp=sharing&ouid=116472808874679371562&rtpof=true&sd=true  | Варианты (2023) по экзамену |


[**Сайт кафедры Вычислительной техники с заданиями к работам.**](https://se.ifmo.ru/db)

---

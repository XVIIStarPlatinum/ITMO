# I/İTMO/SE/Distributed

---

# Алгосы 2: Electric boogaloo

<p align="center">
    <img src="https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExdXhmb2lyeDdoYmpoc2o4ZnJicHg4ZzVnb3M5ZXV1eHQ3dzMwM2dkNSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/VhGkDaTIMRCn4nrT42/giphy.gif" width="500" alt="tgpu without pu"/>
</p>

> Мегафакультет КТУ, факультет программной инженерии и компьютерной техники в университете информационной технологии, механики и оптики в г. Санкт-Петербург

| :exclamation: <b>Любое копирование материалов целиком или частично,<br>но без ссылки на автора, является кражей интеллектуальной собственности.<br>Это плагиат, за который из ИТМО отчисляют.</b> :exclamation:<br><sub><sup><i>(ещё получите 1-(2’’-гидроксилциклогексил)-3-[аминопропил]-4-[3’-аминопропил]пиперазин)-ы от нас)</sup></sub></b> |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Здесь представлены лабораторные по дисциплине **Распределенные вычисления**.

Если хотите что-то добавить в репозиторий, отправляйте **Pull request** :mailbox_with_mail:. После проверки информация будет добавлена.

Если я упустил кое-какой язык, то флаг в руки вам создать свою версию ридмишек и отправить **Pull Request**.

| [<strong>Русский</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/README.md) | [<strong>English</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/.docs/README_EN.md) | [<strong>Монгол</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/.docs/README_MN.md) | [<strong>Español</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/.docs/README_ES.md) | [<strong>中文</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/.docs/README_CN.md) | [<strong>Tiếng việt</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/.docs/README_VN.md) | [<strong><p dir="rtl" lang="ar">اَلْعَرَبِيَّةُ</p></strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/.docs/README_AR.md) | [<strong>हिन्दी</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/.docs/README_IN.md) | [<strong>Português</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Distributed%20Computing/.docs/README_PT.md) |
|-------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|

<p align="center">
    <img src="/img/memes/worst-so-far.png" alt="cloud ahh cloud" width="500"/>
</p>

**Лектор**: [Косяков Михаил Сергеевич](https://my.itmo.ru/persons/139799) ([VK](https://vk.com/id4561041))\
**Практик**: [Тараканов Денис Сергеевич](https://my.itmo.ru/persons/173960) ([VK](https://vk.com/id29936513))

**Партнер**:\
[Галлямов Камиль Рустемович](https://my.itmo.ru/persons/367149) ([@pro100kamil](https://github.com/pro100kamil))

## План занятия

> [!NOTE]
> В курсе будет 5 лабораторных задач.\
> Каждая работа вначале должна быть сдана
> боту [ifmo.distributedclass.bot@gmail.com](mailto:ifmo.distributedclass.bot@gmail.com) и одобрена им, т.е. от бота
> должен быть получен вердикт "**Passed**".\
> В случае успеха, возможна очная сдача одобренной ботом работы. Очная сдача является необязательной, и нужна только
> если вы планируете прийти на экзамен и повысить оценку. Очные защиты будут дистанционными, запись на защиту будет
> публиковаться в группе за сутки до занятия.\
> Всего 5 задач, при сдаче боту 15 баллов, при сдаче боту и очной защите 20 баллов. Т.е. для зачета достаточно либо 4
> задачи боту, либо 3 задачи с очной защитой.
> При случае с уличанием в плагиат итоговая оценка за работу будет уменьшена в 100 раз.

Решения написаны на **C**. Задачи компилируются через `Сlang-14.0.0-1ubuntu1.1` с аргументами
`-std=C99 -Wall -pedantic *.c`.

> [!TIP]
> Сетап для выполнения лабораторных была украдена у [@maxbarsukov](https://github.com/maxbarsukov).

> [!CAUTION]
> Лабораторная работа №1 была уличена в плагиате.

## Результаты

> ~~Моя реакция после того, как получил 12-ый дедлок после запуска кода, даже когда Камиль пытался всей силой меня
уговорить от того самого "изменения":~~
> <p align="center">
>     <img src="https://media1.tenor.com/m/5ddnhZIz29wAAAAd/higuruma-jujutsu-kaisen.gif" width="500" alt="oh-my-god-bruh">
> </p>

- VII семестр: **зачёт** (${\color{green}60.15}$/100 баллов)
---

## Полезные ссылки <a name="links"></a>

| Ссылка                                                                                                                                             | Описание                                        |
|----------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| https://drive.google.com/file/d/1daM08AIiOEepb9dS6YX6k-oruen0OYd6/view <br> https://drive.google.com/file/d/1pCUWhzrlzKaK5BZ528RmTA7BTQQMou61/view | Лекция №1: Введение в распределенные вычисления |
| https://drive.google.com/file/d/1_dT3OQzOpbjjba2K0XKr7JYq9hS9v0QN/view                                                                             | Лекция №2: Модель распределенной системы        |
| https://drive.google.com/file/d/1rteV7F1UE6cEbVdNRVJsgzXSRsgkNWc9/view                                                                             | Лекция №3: Причинно-следственный порядок        |
| https://drive.google.com/file/d/1RPp37BxnSq4OhfaZkA7ZAwQ3biyvkD-k/view                                                                             | Лекция №4: Часы Лэмпорта                        |
| https://drive.google.com/file/d/1v9YWKXbiw3i2Fg_uZZ7DTKiYU-zcUCOo/view                                                                             | Лекция №5: Взаимное исключение                  |
| https://drive.google.com/file/d/1s6aajcdt1x5HqBn3j4N4-GXLWdTkmUbV/view <br> https://drive.google.com/file/d/1m0LRSZnNFVHokvUSEsUtIZGAd1oui9cJ/view | Лекция №1 (маг): Обедающие философы             |
| https://drive.google.com/file/d/1CpwSZVKegaJI0vtzLXc6p0BHDXM9EABe/view <br> https://drive.google.com/file/d/1s0S0Zpzq_GoPVa7AL8ndX3NGgyhhJLwy/view | Лекция №2 (маг): Векторные часы                 |

---

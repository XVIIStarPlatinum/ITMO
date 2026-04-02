# I/İTMO/SE/CompMath

---
# I identify by kilometers per second because I'm going to km/s

> <p align="center">
>     <img src="/img/gifs/tissue-roll-hanging.gif" alt="noose" width="500"/>
> </p>

> Мегафакультет КТУ, факультет программной инженерии и компьютерной техники в университете информационной технологии, механики и оптики в г. Санкт-Петербург

| :exclamation: <b>Любое копирование материалов целиком или частично,<br>но без ссылки на автора, является кражей интеллектуальной собственности.<br>Это плагиат, за который из ИТМО отчисляют.</b> :exclamation:<br><sub><sup><i>(ещё получите 1-(2’’-гидроксилциклогексил)-3-[аминопропил]-4-[3’-аминопропил]пиперазин)-ы от нас)</sup></sub></b> |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Здесь представлены лабораторные и рубежные работы по дисциплине **Вычислительная математика**.

Если хотите что-то добавить в репозиторий, отправляйте **Pull request** :mailbox_with_mail:. После проверки информация будет добавлена.

Если я упустил кое-какой язык, то флаг в руки вам создать свою версию ридмишек и отправить **Pull Request**.

| [<strong>Русский</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/README.md) | [<strong>English</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Computational%20Mathematics/.docs/README_EN.md) | [<strong>Монгол</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Computational%20Mathematics/.docs/README_MN.md) | [<strong>Español</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Computational%20Mathematics/.docs/README_ES.md) | [<strong>中文</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Computational%20Mathematics/.docs/README_CN.md) | [<strong>Tiếng việt</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Computational%20Mathematics/.docs/README_VN.md) | [<strong><p dir="rtl" lang="ar">اَلْعَرَبِيَّةُ</p></strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Computational%20Mathematics/.docs/README_AR.md) | [<strong>हिन्दी</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Computational%20Mathematics/.docs/README_IN.md) | [<strong>Português</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Computational%20Mathematics/.docs/README_PT.md) |
|-------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|

> Рубежка: калькуляторы и задачи\
> Защита при Малышеве:

```c
float Q_rsqrt ( float number )
{
    long i;
    float x2, y;
    const float threehalfs = 1.5F;
    
    x2 = number * 0.5F;
    y  = number;
    i  = * ( long * ) &y;                       // evil floating point bit level hacking
    i  = 0x5f3759df - ( i >> 1 );               // what the fuck?
    y  = * ( float * ) &i;
    y  = y * ( threehalfs - ( x2 * y * y ) );   // 1st iteration
//  y  = y * ( threehalfs - ( x2 * y * y ) );   // 2nd iteration, this can be removed

    return y;
}
```

**Лектор/Практик**: [Малышева Татьяна Алексеевна](https://my.itmo.ru/persons/165275)

**Вариант**: 1

> <h3 align="center">
>     Нули после запятого в итоге вычислительных действий с float:
>     <img src="https://media1.tenor.com/m/xdOYn3spIHsAAAAd/infinite-dragon-dream-feet-loop.gif" alt="zeros" width="500"/>
> </h3>

## Результат
<s>ДУХОТАААААААААААА</s>
- IV семестр: **зачёт** (${\color{green}65.5}$/100 баллов)
---

## Полезные ссылки
| Ссылка                                                 | Описание                                                                             |
|--------------------------------------------------------|--------------------------------------------------------------------------------------|
| https://books.ifmo.ru/file/pdf/3229.pdf/               | Лабораторный практикум по вычислительной математике: учебно-методическое пособие.    |
| https://205826.github.io/T2P/T2P_EDITOR.html?id=299994 | Решатор рубежной работы №1. :bangbang: НЕ ГАРАНТИРУЮ КОРРЕКТНОСТЬ ОТВЕТОВ :bangbang: |

---

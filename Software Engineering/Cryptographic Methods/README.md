# I/İTMO/SE/Crypto

---

# Не криптовалюты
> 
> ![encrypt deez nutz](img/memes/crypto.png)

> Мегафакультет КТУ, факультет программной инженерии и компьютерной техники в университете информационной технологии, механики и оптики в г. Санкт-Петербург

| :exclamation: <b>Любое копирование материалов целиком или частично,<br>но без ссылки на автора, является кражей интеллектуальной собственности.<br>Это плагиат, за который из ИТМО отчисляют.</b> :exclamation:<br><sub><sup><i>(ещё получите 1-(2’’-гидроксилциклогексил)-3-[аминопропил]-4-[3’-аминопропил]пиперазин)-ы от нас)</sup></sub></b> |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Здесь представлены тесты по дисциплине **Методы криптографии** из модуля **Цифровая культура в профессиональной деятельности**.

Если хотите что-то добавить в репозиторий, отправляйте **Pull request** :mailbox_with_mail:. После проверки информация будет добавлена.

Если я упустил кое-какой язык, то флаг в руки вам создать свою версию ридмишек и отправить **Pull Request**.

| [<strong>Русский</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/README.md) | [<strong>English</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/.docs/README_EN.md) | [<strong>Монгол</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/.docs/README_MN.md) | [<strong>Español</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/.docs/README_ES.md) | [<strong>中文</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/.docs/README_CN.md) | [<strong>Tiếng việt</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/.docs/README_VN.md) | [<strong><p dir="rtl" lang="ar">اَلْعَرَبِيَّةُ</p></strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/.docs/README_AR.md) | [<strong>हिन्दी</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/.docs/README_IN.md) | [<strong>Português</strong>](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/.docs/README_PT.md) |
|-------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|

---
> https://en.wikipedia.org/wiki/Alice_and_Bob#Cryptographic_systems
> ![A whole telenovela lol](img/memes/why-is-it-always-you-three.png)
---
## Методы
| Название метода         | Упражнение                                                                                                                    |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| Шифр Виженера           | [Упражнение №1](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/Vigenere) |
| Сеть Фейстеля. DES      | [Упражнение №2](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/DES)      |
| Рейндель. AES           | [Упражнение №3](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/Rijndael) |
| РСЛОС. RC4              | [Упражнение №4](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/RC4)      |
| RSA                     | [Упражнение №5](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/RSA)      |
| Цифровые подписи. DSA   | [Упражнение №6](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/DSA)      |
| RSA. Контрольная работа | [Контрольная](https://github.com/XVIIStarPlatinum/itmo/blob/master/Software%20Engineering/Cryptographic%20Methods/Final)      |

> [!WARNING]
> Эти реализации были произведены мной на Rust для учебной цели. Абсолютно не стоит использовать их на проде (потому что реализации криптоалгоритмов из учебника в реальном мире это как малолетний ребёнок в Дидди-вечеринках) или как пример того, как надо реализовать эти алгоритмы шифрования.
---
## Результаты
<s>Моя любимая нация — прокрастинация</s>
- VI семестр: **зачёт** (${\color{green}100}$/100 баллов)
---

> Диффи-Хеллман момент\
> ![dhm](img/memes/dhm.png)

## Полезные ссылки <a name="links"></a>
| Ссылка                                                                                                                                                         | Описание                                                  |
|----------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| https://gravityfalls.fandom.com/wiki/List_of_cryptograms/Episodes#Vigen%C3%A8re_cipher<br>https://www.dcode.fr/vigenere-cipher                                 | Для шифра Виженера                                        |
| https://ru.wikipedia.org/wiki/Сеть%20Фейстеля                                                                                                                  | Для сети Фейстеля                                         |
| https://en.wikipedia.org/wiki/Advanced_Encryption_Standard<br>https://youtu.be/O4xNJsjtN6E<br>https://youtu.be/nz2LeXwJOyI<br>https://youtu.be/gP4PqVGudtg     | Для AES                                                   |
| https://www.geeksforgeeks.org/computer-networks/rc4-encryption-algorithm/<br>https://www.geeksforgeeks.org/digital-logic/linear-feedback-shift-registers-lfsr/ | Для RC4 и LFSR (РСЛОС)                                    |
| https://youtu.be/JD72Ry60eP4<br>https://youtu.be/-ShwJqAalOk<br>https://youtu.be/M7kEpw1tn50<br>https://youtu.be/-UrdExQW0cs                                   | Для RSA                                                   |
| https://gmpy2.readthedocs.io/en/latest/<br>https://www.simplilearn.com/tutorials/cryptography-tutorial/digital-signature-algorithm                             | Для DSA                                                   |
| https://youtu.be/nz2LeXwJOyI<br>https://youtu.be/jrOMooH-kjs<br>https://youtu.be/veIy1pJJ4Ow<br>https://youtu.be/1PWRA9JSyko                                   | Видосы про tominecon.7z или почему сложно сломать AES-256 |

---
# Руководство по выполнению Работы №1

1. Выберите удобный под вами техстек. Разрешено использовать один из трех языков при
   разработке, и технологию для создания приложения на его основе:
    - `Java` (`Spring`, `JAX-RS`, ...)
    - `Python` (`Flask`, `Django`, ...)
    - `JavaScript` (`Node.js + Express`, ...)\
      Также выберите удобную базу данных, которая будет хранить данные. Может быть `PostgreSQL`, может быть и
      нереляционная БД (`MongoDB`, `Cassandra`), а может быть даже простой файл/массив в памяти, если это учебный
      пример.

2. Создавать новый проект либо через менеджера пакетов (`npm`, `pip`, `gradle`), либо через графического интерфейса
   `IDE`.
3. Архитектура приложения может быть любым, если она удовлетворяет следующим условиям REST:
    - Наличие эндпоинтов;
    - Запросы отправляются поверх HTTP;
    - Наличие бизнес-логики приложения;
    - Наличие слоя представления (контроллера);
    - Наличие слоя хранения данных (БД, файл, массив в памяти).\
      Считается хорошим тоном следить принципам REST: отсутствие состояния (statelessness), кэшируемость, однородность
      интерфейса и т.д.

4. Реализовать 3 эндпоинтов, способные принимать следующие виды запросов по следующим адресам:
    - `POST /auth/login` — метод для аутентификации пользователя (принимает логин и пароль).
    - `GET /api/data` — метод для получения каких-либо данных (например, список пользователей или постов). Доступ должен
      быть только у аутентифицированных пользователей.
    - Любое другое.

5. Реализовать механизмы безопасности, направленных для устранения следующих известных уязвимостей:
    - **SQLi** (`SQL`-инъекций): Используйте параметризованные запросы (`Prepared Statements`) или `ORM` (например,
      `Sequelize`, `SQLAlchemy`, `Hibernate`). Не используйте конкатенацию строк для формирования `SQL`-запросов.\
      Пример на Spring:

    ```java
    @Repository
    public interface FooRepository extends JpaRepository<Foo, Long> {}
    ```

    - **XSS**: Санитизируйте (экранируйте) все пользовательские данные, которые возвращаются в ответах API. Используйте
      встроенные функции фреймворка (например, `escape()` в `Express`).\
      Пример на Spring:
    ```java
        .headers(headers ->
            headers.xssProtection(
                    xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
            ).contentSecurityPolicy(
                    cps -> cps.policyDirectives("script-src 'self'")
            )
        );
    ```

   Должны устанавливаться следующие заголовки в запросе:
    ```
    X-XSS-Protection: 1; mode=block
    Content-Security-Policy: script-src 'self'
   ```

    - **Broken Authentication**:
        - Реализуйте выдачу JWT-токена upon successful login. Также устанавливайте обоснованный срок завершения
          валидности токена.
        - Напишите middleware, которое будет проверять JWT-токен на всех защищенных эндпоинтах.
        - Пароли ни в коем случае не должны храниться в открытом виде. Обязательно хэшируйте их с помощью алгоритмов
          типа bcrypt, scrypt или Argon2.\
          Пример на `Spring`:
    ```java
   http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/magic-items").authenticated()
                .anyRequest().permitAll()
   )
   // ...
   http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 
   ```

6. Настраивать CI/CD в `workflow.yml` / `.gitlab-ci.yml` так, чтобы сначала собралось приложение, а потом работали SAST
   и SCA последовательно.
   Необходимо, чтобы после работы сгенерировались отчеты в виде `HTML`, так как они являются важными в самом конце
   отчета.
   Пример:

```yaml

- name: Run SpotBugs
  run: ./gradlew spotbugsMain spotbugsTest

- name: Upload SpotBugs XML report
  uses: actions/upload-artifact@v4
  with:
    name: spotbugs-report
    path: reports/spotbugs.xml
    overwrite: true
- name: Upload SpotBugs HTML report
  uses: actions/upload-artifact@v4
  with:
    name: spotbugs-report
    path: reports/spotbugs.html
    overwrite: true
- name: Run OWASP Dependency-Check
  env:
    NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
    GRADLE_OPTS: -Xmx4g -Xms1g
  run: ./gradlew dependencyCheckAnalyze --console=plain

- name: Upload Dependency-Check report
  uses: actions/upload-artifact@v4
  with:
    name: dependency-check-report
    path: reports/dependency-check-report.html
    overwrite: true
```

7. Написать README.md с описанием проекта, доступными методами API, реализованными мерами защиты против SQLi и XSS,
   механизм аутентификации и отчетов SAST/SCA.

---

## Лицензия

Проект доступен с открытым исходным кодом на условиях [Лицензии MIT](https://opensource.org/licenses/MIT). \
*Авторские права 2026 Ariguun Bolorbold*

Поставьте звезду :star:, если вы нашли этот проект полезным.
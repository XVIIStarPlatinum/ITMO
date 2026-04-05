# Практическое задание со SpotBugs

**Цель:** Внедрить SAST-анализатор SpotBugs, найти уязвимости, проанализировать результат.
**Инструменты:** VS Code, Maven.

**Предварительный этап:**
Скачивание проекта
Это самый важный шаг.

1. Скачайте проект **DockerTraining
   **: [Скачать ZIP](https://github.com/Miroyulia/DockerTraining/archive/refs/heads/main.zip).
2. Распакуйте его в удобную папку (например, `Documents/DockerTraining`).
3. Откройте **VS Code**.
4. Меню **File** -> **Open Folder...** -> Выберите папку `DockerTraining`. (Нажмите "Yes, I trust..." если спросят).
5. В верхнем меню выберите **Terminal** -> **New Terminal**.
6. Вставьте и запустите команду (она скачает пол-интернета, это нормально):

```powershell
C:\apache-maven-3.9.11\bin\mvn clean install -DskipTests
```

---

## Этап 1: Подключение анализатора

1. В **VS Code** в левой панели найдите файл **`pom.xml`** (в самом низу списка). Кликните, чтобы открыть его.
2. Нажмите `Ctrl + F` и найдите слово `<build>`. Чуть ниже будет тег `<plugins>`.
3. **ВНИМАНИЕ:** Вставьте следующий блок кода **ВНУТРЬ** тега `<plugins> ... </plugins>`. (Следите, чтобы не вставить
   его внутрь другого `<plugin>`).

```xml

<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.8.2.0</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Low</threshold>
        <xmlOutput>false</xmlOutput>
        <htmlOutput>true</htmlOutput>
        <plugins>
            <plugin>
                <groupId>com.h3xstream.findsecbugs</groupId>
                <artifactId>findsecbugs-plugin</artifactId>
                <version>1.12.0</version>
            </plugin>
        </plugins>
    </configuration>
</plugin>
```

4. Нажмите `Ctrl + S`, чтобы сохранить файл.

---

## Этап 2: Запуск анализа

1. В верхнем меню выберите **Terminal** -> **New Terminal**.
2. Вставьте команду для запуска анализа:

```powershell
C:\apache-maven-3.9.11\bin\mvn spotbugs:spotbugs
```

*(Если у вас другая версия Maven, скорректируйте путь)*.

3. Дождитесь завершения. Если видите `BUILD SUCCESS` — отлично.
    * *Примечание:* Если видите `BUILD FAILURE` и ошибки зависимостей — позовите преподавателя. Возможно, нужно сначала
      запустить `mvn install`.

4. Найдите отчет:
    * В левой панели VS Code раскройте папку **`target`**.
    * Найдите файл **`spotbugs.html`**.

---

## Этап 3: Анализ и исправление

1. В отчете в браузере ищите заголовки **Security**.
2. Проанализируйте результаты отчетов блока **Security** по каждому из компонентов.
3. По результатам анализа сформируйте отчёт, в котором будет информация:
    * Файл, в котором найдена уязвимость;
    * Строка
    * Тип уязвимости
    * Сценарий возможной атаки
    * Как можно исправить
    * False Positive? 

# Предварительная подготовка (SpotBugs)

**Инструменты:** Java 17, Maven, VS Code.

---

## Шаг 1. Установка Java 17 (Обязательно)

Нам строго нужна **версия 17**.

1. Скачайте установщик **Eclipse Temurin JDK 17 (LTS)** для Windows x64:
   *
   *[Скачать .msi файл](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_x64_windows_hotspot_17.0.13_11.msi)
   **
   Или по ссылке найдите версию, подходящую для вашей
   ОС https://adoptium.net/temurin/releases/?version=17&os=windows&arch=x64
2. Запустите установку.
3. **ВАЖНО:** На этапе "Custom Setup" найдите пункт **Set JAVA_HOME variable**. Нажмите на красный крестик и выберите *
   *"Will be installed on local hard drive"**.

## Шаг 2. Установка Maven (Сборщик проектов)

Так как в VS Code встроенный Maven иногда работает со сбоями, мы будем использовать вариант ручной установки. Если будет
работать встроенный Maven, шаг можно пропустить

1. Скачайте **Apache Maven** (Binary zip archive):
   **[Скачать apache-maven-3.9.11-bin.zip](https://maven.apache.org/download.cgi)**
2. Распакуйте архив.
3. Переместите папку `apache-maven-3.9.11` на диск **C:\** (в корень), чтобы путь был коротким.
    * Итоговый путь должен быть: `C:\apache-maven-3.9.11`

## Шаг 3. Установка VS Code и плагинов

1. Установите **Visual Studio Code**: [Скачать](https://code.visualstudio.com/).
2. Скачайте файл плагина **Extension Pack for Java**:
   [Прямая ссылка (.vsix)](https://marketplace.visualstudio.com/_apis/public/gallery/publishers/vscjava/vsextensions/vscode-java-pack/latest/vspackage)**
3. Откройте VS Code -> Вкладка **Extensions** (квадратики слева) -> Три точки вверху -> **Install from VSIX...** ->
   Выберите скачанный файл.


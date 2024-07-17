1. Описание решения - мной был реализован REST API микросервис на Java(openjdk-22)+Spring Boot, предоставляющий возможность управлять (хранить, добавлять, получать) файлы посредством HTTP запросов.
   Проект реализуется через 3 основных класса (не считая основного класса приложения) -
   - Класс File представляет собой сущность файла, имеет поля id, file, title, creation_date и description, а также геттеры и сеттеры к ним.
   - Класс FileController принимает HTTP запросы, имеет методы testcasePost (принимает POST запрос с JSON, преобразует его в объект File и вызывает метод addFile у экземпляра класса FileRepository) и testcaseGet (принимает GET запрос с параметром id и вызывает метод getFileById у экземпляра класса FileRepository).
   - Класс FileRepository необходим для взаимодействия с базой данных (PostgreSQL), имеет методы addFile (добавляет файл в базу данных и возвращает его id) и getFileById (возвращает файл по id).

   При запуске приложения, в базе данных инициализируется таблица, если её не существует. В таблице у столбцов title и description установлены ограничения по длине - 100 и 500 символов соответственно. Base64 файл преобразуется в бинарный     и хранится в базе данных как bytea.

   Приложение реализует обработку следующих ошибок со стороны клиента - Указан id не существующего файла (404 Not Found), файл передан в формате отличном от base64 (400 Bad Request), title превышает 100 или description 500 символов (400      Bad Request).
  
   Запросы к api осуществляются по адресу http://localhost:8080/testcase
  
   Также микросервис был обёрнут в контейнер Docker и настроен для работы с базой данных через Docker Compose.

2. Для начала необходимо указать данные для доступа к базе данных в файле application.properties, при запуске через Docker в файле compose.yaml можно при желании поменять данные для базы данных внутри контейнеров.

   Для запуска прилоложения нужно сначала собрать его:
   <pre>
   mvn clean package
   </pre>
   В случае если проект планируется запускать через Docker или база данных ещё не подключена, следует также добавить ключ -DskipTests, т.к. тест не пройдёт на этапе инициализации несуществующей базы данных.
  
   Далее запустить jar файл:
   <pre>
   java -jar target/testcase-0.0.1-SNAPSHOT.jar
   </pre>
  
   Либо через Docker compose:
   <pre>
   docker-compose build
   docker-compose up
   </pre>


3. Примеры запросов к api:

   Запрос для добавления файла -  
   <pre>
   POST /testcase  
   Host: localhost  
   Content-Type: application/json  
  
   {  
     "file": "<base64-encoded file>",  
     "title": "New file",  
     "description": "File description"  
   }  
   </pre>
   Ожидаемый результат -  
   <pre>
   201 Created  
   Content-Type: application/json  
  
   11  
   </pre>
  
   Запрос для получения файла по id -  
   <pre>
   GET /testcase?id=11  
   Host: localhost  
   </pre>
     
   Ожидаемый результат -  
   <pre>
   200 OK  
   Content-Type: application/json  
  
   {  
     "file": "<base64-encoded file>",  
     "title": "New file",  
     "creation_date": "2024-07-17T12:00:00Z",  
     "description": "File description"  
   }  
   </pre>

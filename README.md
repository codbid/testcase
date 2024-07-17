1. Описание решения - мной был реализован REST API микросервис на Java(openjdk-22)+Spring Boot, предоставляющий возможность управлять (хранить, добавлять, получать) файлы посредством HTTP запросов.
   Проект реализуется через 3 основных класса (не считая основного класса приложения) -
   - Класс File представляет собой сущность файла, имеет поля id, file, title, creation_date и description, а также геттеры и сеттеры к ним.
   - Класс FileController принимает HTTP запросы, имеет методы testcasePost (принимает POST запрос с JSON, преобразует его в объект File и вызывает метод addFile у экземпляра класса FileRepository), testcaseGet (принимает GET запрос с 
     параметром id и вызывает метод getFileById у экземпляра класса FileRepository) и testcaseGetAll (принимает GET запрос с параметрами size и page и вызывает метод getAllFiles у экземпляра класса FileRepository, size - количество 
     файлов на странице, page - номер страницы, страницы нумеруются с 0).
   - Класс FileRepository необходим для взаимодействия с базой данных (PostgreSQL), имеет методы addFile (добавляет файл в базу данных и возвращает его id), getFileById (возвращает файл по id) и getAllFiles (возвращает список всех файлов      и их атрибутов с сортировкой по дате создания и с пагинацией).

   При запуске приложения, в базе данных инициализируется таблица, если её не существует. В таблице у столбцов title и description установлены ограничения по длине - 100 и 500 символов соответственно. Base64 файл преобразуется в 
   бинарный     и хранится в базе данных как bytea.

   Приложение реализует обработку следующих ошибок со стороны клиента - Указан id не существующего файла (404 Not Found), файл передан в формате отличном от base64 (400 Bad Request), title превышает 100 или description 500 символов (400     Bad Request).
  
   Запросы к api осуществляются по адресу http://localhost:8080/testcase
   Для получения списка всех файлов (первое доп. требование) http://localhost:8080/testcase/all  
  
   Также микросервис был обёрнут в контейнер Docker и настроен для работы с базой данных через Docker Compose.

3. Для начала необходимо указать данные для доступа к базе данных в файле application.properties, при запуске через Docker в файле compose.yaml можно при желании поменять данные для базы данных внутри контейнеров.

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


4. Примеры запросов к api:

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
     "creation_date": "2024-07-18 01:11:45.317668",  
     "description": "File description"  
   }  
   </pre>

   Запрос для получения всех файлов с пагинацией -  
   <pre>
   GET /testcase/all?size=3&page=1 
   Host: localhost  
   </pre>
     
   Ожидаемый результат -  
   <pre>
   200 OK  
   Content-Type: application/json  

   [  
      {  
        "file": "<base64-encoded file>",  
        "title": "New file3",  
        "creation_date": "2024-07-18 01:11:45.317668",  
        "description": "File3 description"  
      }  
      {  
        "file": "<base64-encoded file>",  
        "title": "New file2",  
        "creation_date": "2024-07-18 00:49:27.002648",  
        "description": "File2 description"  
      }  
      {  
        "file": "<base64-encoded file>",  
        "title": "New file1",  
        "creation_date": "2024-07-18 00:02:58.48922",  
        "description": "File1 description"  
      }  
   ]  
   </pre>


Небольшая пометка - в ТЗ указано, что все методы должны "принимать/отдавать запросы/ответы в формате JSON", не совсем понял в каком виде лучше сделать ответ от метода создания файла {11} или {"id": 11}, реализовал первый вариант, просто число, но тоже в JSON. В методе для получения всех файлов реализовал как и сказано в ТЗ сортировку по дате создания, но в порядке убывания.

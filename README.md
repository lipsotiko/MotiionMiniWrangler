#Motiion: Development take-home test, Mini-wrangler

##Overview
- Customers may utilize this web application via an HTTP POST 
- The contents of CSV file should be placed in the body of the request as raw text
- The response will contain a message with the total number of records that exist in the Customer Orders database table
- Transformations are configurable via the application.yml file; settings may be overridden with environment variables

##Architectural Overview & Technology Choices
- Spring Boot Web Application written in Kotlin backed by an H2 in-memory Database
- Request response architecture; a later assessment of the volume of data that will need to be injected may
    dictate a different architecture, but for now, this is simple
- Data translation is done via the Translator package / interface
- Within the translator the CSV file is parsed, formatted based on the DSL Config, and converted to as JSON
- The Jackson ObjectMapper is used to convert the JSON data into the Motiion CustomerOrder entity and persisted

##Domain Specific Language Configuration
In the application.yml, config properties may be found here: motiion.domain-specific-language.field-config-parameters.
- initial-field: The name of the column header in the csv file
- field-type: The target field data type; STRING, DATE, INTEGER, BIGDECIMAL
- destination-field: The name of the destination field in the Motiion CustomerOrder entity; 
    - orderId: Long
    - orderDate: LocalDate
    - productId: String
    - productName: String
    - quantity: BigDecimal
    - unit: String

##How to Run The Code
1. Check out the repo from here: https://github.com/lipsotiko/mini-wrangler
1. From the root of the repo, run the command: './gradlew bootRun'
1. Use a tool such as PostMan to interface with the api: localhost:8080/api/import-orders-csv via an HTTP POST
1. The body of the request must include a CSV file as raw text; a sample set of data may be found here: 
    src/test/resources/fixtures/orders.csv
1. To run the test suite: './gradlew test'

##Assumptions
- Customers will POST over an HTTP request a reasonable about of data that won't cause the system to crash
- The CSV file will be posted as a single string in the body of the HTTP request
- An in memory database is used for automated tests, and for running the application locally; 
- Production configuration will be needed to connect to a real database
- The first row of the csv file will always have column headers
- Quoted values are assumed to be quoted because they contain a comma; and since a comma is used as a delimiter
the quoted values will be formatted by removing commas within the quotes, then removing the quotes

##Next Steps
- Add additional tests with larger sets of diverse data to ensure the code is resilient and not error prone
- See if the DSL can be standardized across multiple customers so that a high level of configurability is not needed
- Investigate the potential throughput needed for this API to be efficient in a production environment and test it 
- If customers are generating files from various systems; we may need to make the end-of-line character 
configurable to accept newline characters from other file types.
- If data from multiple customers needs to be stored in the same system, we'll need to find a way to segregate the data
- When an error occurs with the translation of an import, find a way for the data to be corrected or re-imported
- Create a production configuration that will allow the application to connect to a real database

##Resources
- https://www.martinfowler.com/articles/codeGenDsl.html
- https://github.com/s1monw1/hibernateOnKotlin/tree/master/src/main/kotlin/com/kotlinexpertise/hibernatedemo
- https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html

#Motiion: Development take-home test, Mini-wrangler

#Introduction 
This problem documentation is not intending to be sufficient to deliver a “production grade product” - 
it is deliberately unspecific. In a real development situation, you would have the opportunity to ask 
clarifying questions about requirements - you will not be able to ask such questions in this case. 
Instead, make assumptions, document and deliver them as part of the solution. 

#System overview 
We are implementing a small system for (row-wise) wrangling of text data. The typical use case is 
taking a delimited data file from a customer and massaging it to fit with a standardized schema by 
applying a sequence of column transforms. 

For example, we could have a target format defined as following (in JVM types): 
- Column name (Type)
- OrderID (Integer)
- OrderDate (Date)
- ProductId (String)
- ProductName (String proper cased)
- Quantity (BigDecimal)
- Unit (String)

Let’s say we get a CSV with the following fields (example available as a Gist): 
Column name String format pseudo-regex or number parsing format 

- Order Number d 
- Year YYYY 
- Month MM 
- Day dd 
- Product Number A-Z - 
- Product Name A-Z 
- Count #,## # 
- Extra Col -- 
- Extra Col -- 

This source data is missing the Unit field, but we know that all products are measured in kg. 
Transforming from the source to the target could be described with the following steps: 
- Rename Order Number → OrderID and parse as Integer __
- Add a new column, concatenating Year , Month , Day → OrderDate and parse as DateTime 
- Rename Product Number → ProductId and parse as String 
- Proper case Product Name , rename it → ProductName and parse as String 
- Rename Count → Quantity and parse as BigDecimal 
- Add a new column with the fixed value "kg" → Unit and parse as String 
- Keep only our 6 reference columns 

#Requirements 
The transformations should be configurable with an external DSL (like a configuration file) 
The functionality should be implemented as a library, without (significant) external dependencies 
Invalid rows should be collected, with errors describing why they are invalid (logging them is fine for now) 
The data tables can have a very large number of rows In order to make it easier to assess the results, 
please implement the project in a language that most Motiion developers are familiar with - 
Java, Kotlin, Scala, Javascript, C# or Typescript 

#Out of scope 
Other file formats that CSV 
No need to build a CSV parser unless you really want to - feel free to use a pre- built CSV parser for 
the basic splitting and escaping 

#Deliverables:
Running code and test suite provided through online code repo or in a tar-ball 
Instructions on how to build and run the code with example data Short architectural overview and technology 
choices made (Basic) documentation, unless it’s completely self-documenting (to a fellow software developer) 
List of assumptions or simplifications made. List of the next steps you would want to do if this were a real project 

##Hints to what we’re looking for: 
Try balancing some common sense foresight with the simplest thing that could work. A large chunk of this assignment 
is very straightforward, but there are also some aspects of both API design and implementation that could be solved 
in a wide range of ways. Pick a reasonable approach and be prepared to speak to alternatives in the review. 
This aside, these are the other areas we’re scoring during the review: A working system Easy to use API and easy to 
understand configuration DSL Well structured, readable and maintainable code Tests Quality of documentation 

##Submission Format
Please zip (or tar) up the code and documentation and share it with the recruiter via 
Dropbox, Google Drive or any other file sharing service you prefer 

#Assumptions
- Customers will POST over an HTTP request a reasonable about of data that won't cause the system to crash
- The CSV file will be posted as a single string in the body of the HTTP request
- An in memory database is used for automated tests, and for running the application locally
- The first row of the csv file will always have column headers
- Adding a new derived column such as the "kg" string for the Unit field will only work if there are exiting 
    records in the csv file
- Quoted values are assumed to be quoted because they contain a comma; and since a comma is used as a delimiter
the quoted values will be formatted by removing commas within the quotes, then removing the quotes

#Next Steps
- See if the DSL can be standardized across multiple customers so that a high level of configurability is not needed
- Investigate the potential throughput needed for this API to be efficient in a production environment and test it 
- If customers are generating files from various system types; we may need to make the end-of-line character configurable 
to accept newline characters from other file types.
- If multiple customers data needs to be stored in the same system, find a way to segregate the data
- When an error occurs with the translation of an import, find a way for the data to be corrected or re-imported
- Create a production configuration that will allow the application to connect to a real database

#Resources
- https://www.martinfowler.com/articles/codeGenDsl.html
- https://github.com/s1monw1/hibernateOnKotlin/tree/master/src/main/kotlin/com/kotlinexpertise/hibernatedemo

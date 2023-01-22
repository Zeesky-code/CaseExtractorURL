#README
This repository contains a Java program that can scrape metadata and html content of the judgements which are published on the website of The Constitutional Court of The Republic Turkey (https://kararlarbilgibankasi.anayasa.gov.tr/).

The program uses Jsoup to crawl the website page by page, parse the metadata for each result item, and extract the html content from the url. The program then stores the metadata, url, and extracted html content to a MySQL database.

> **Warning**
>Due to the large amount of data being crawled, the program might take a while to run depending on Internet speed and other factors. On average, it takes about 30 minutes to crawl and store 11k court judgements.

## Getting Started
### Prerequisites
- Java 8 or later
- Maven
- MySQL

## Running the Program
1. Clone the repository to your local machine using:

```
git clone https://github.com/Zeesky-code/CaseExtractorURL.git
```

2. Add your database url, username, and password in a .env file in the root directory.
Example:
```
DB_URL = "jdbc:mysql://mydb"
USER = "testuser"
PASS = "testpassword"
```

3. Open a terminal and navigate to the root of the cloned repository.
Run the command `mvn clean install package` to build the project.

4. Execute the program using:
```
java -jar target\CaseExtractor.jar
```




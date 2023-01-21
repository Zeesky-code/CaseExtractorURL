#README
This repository contains a Java program that can scrape metadata and html content of the judgements which are published on the website of The Constitutional Court of The Republic Turkey (https://kararlarbilgibankasi.anayasa.gov.tr/).

The program uses Jsoup to crawl the website page by page, parse the metadata for each result item, and extract the html content from the url. The program then stores the metadata, url, and extracted html content in batches of 1000 to a MySQL database.

## Getting Started
Prerequisites
Java 8 or later
Maven
MySQL

##Installation
Clone the repository to your local machine using:

```
git clone https://github.com/Zeesky-code/CaseExtractorURL.git
```

Add your database url, username, and password in a .env file.
Running the program
Open a terminal and navigate to the root of the cloned repository.
Run the command mvn clean install to build the project.
Execute the program using:
```
java -jar target\CaseExtractor.jar
```

Note
If you're using SQLite, please provide the correct path of the database in the .env file.
If you're using MySQL, please provide the correct url, username and password in the .env file.
In case of any issues, please check the logs generated in the target folder.
If you have any questions or issues, please create a new issue in the repository.
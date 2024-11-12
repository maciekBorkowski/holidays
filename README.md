# Find common holiday app

## Contents
- [Description](#description)
- [Requirements](#requirements)
- [Installation](#installation)
- [Running app](#running-app)
- [Running tests](#running-tests)
- [Simple API example](#simple-api-example)

## Description

This web app provides information about common holidays in the world.

## Requirements
In order to run the project, you need to have the following software installed:

- **JDK 19** or newer
- **Git**

## Installation
In order to start working with the project, follow these steps:

1. Clone repository on your local environment:
   ```bash
   git clone https://github.com/TwojaNazwaUzytkownika/nazwa-projektu.git
2. Go to the project directory:
   ```bash
   cd holiday
3. Install required dependencies:
    ```bash
    ./gradlew build
4. Setup api key:
    ```bash
    export $HOLIDAYS_API_KEY=your_api_key
   ```
   or use a default api key which is stored in the application.properties file.

## Running app
```bash
./gradlew bootRun
```
 
## Running tests
```bash
./gradlew test
```    

## Simple API example
   ```bash
   curl --location 'localhost:8080/holidays?countryCodes=DE,PL&date=2016-03-01' \
    --header 'x-api-key: your_api_key'
   ```
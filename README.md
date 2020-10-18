# Holiday Information Service 

Spring Boot application returns next holiday after the given date that will happen on the 
same day in both countries.

### App startup
The application can be started by issuing the following command in the command line:

on mac/linux:
```bash
./gradlew clean bootRun
```
on windows:
```bash
.\gradlew clean bootRun
```
- Ensure you have correct JAVA_HOME path.

- You can also run application in Intellij IDEA.

## Stopping
To stop the service (when it is running with `gradle bootRun`) use Control-C.

## APPLICATION API
- **`GET: <HOST>:8080/api/holidays/next-holiday`**

Endpoint returns next holiday after the given 
date that will happen on the same day in both countries. If the first country and second country 
code are the same just return next holiday in the country. If in the country with this date exists 
a few holidays then merge holiday names. Checks the current year and when holiday not matches 
in countries checks also next year holidays, when again not match then stopped checking.

**Required params:**
- `date` - Date with a format `yyyy-MM-dd`. 
- `country1` - Two letters first country code.
- `country2` - Two letters second country code.

Example URL: `localhost:8080/api/holidays/next-holiday?date=2020-10-10&country1=PR&country2=PL` 

## HOLIDAY API PROVIDER 
- `https://date.nager.at/`

## Test runs
Tests can be run by the following command in the command line:

on mac/linux:
```bash
./gradlew clean test
```
on windows:
```bash
.\gradlew clean test
```
Ensure you have correct JAVA_HOME path.

## IntelliJ Idea development
1. Install Lombok plugins under File -> Settings -> Plugins -> Browse repositories... search for the Lombok and install.
2. Check the Enable annotation processing checkbox under File -> Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors.

## Development stack
- Java 11 
- Spring Boot 2.3.4 

# Requirement Specification

## Project Introduction

- **Background**
  With the rapid development of the Internet, the amount of information and related service has been increasing considerably. It is becoming more and more important to figure out an efficient method of extracting the target information precisely among the overwhelming amount of resources on the World-Wide Web. The requirement for web crawling is so common that even some people with little programming knowledge are looking for a way to extract data from websites.

- **Purpose of this Project**

  The purpose of this project is to implement a scraping tool that allows users to easily extract data from the websites visually with no coding required.

- **Target Users**

  The users with little knowledge of developing web crawlers who want to scrape some information from websites efficiently and conviently.

- **Boundary of this Project**

  This project can help to crawl information from websites, but it cannot help to process of the data crawled afterwards. The project aims to simplify the process of scraping data from websites, instead of the analysis on the data, such as emotion analysis of an article or data mining among the large amount of data scraped.

## Rquirement Analysis

Using a KAOS diagram to decompose the project form high-level goals to user scenarios.

![KAOS](images/requirement_specification/KAOS.png)

## Use Case Analysis

![Use Case](images/requirement_specification/Use_Case_Diagram.png)

### Use Case: _Login_

#### Description

Login with the registered username and the password.

#### Participants

User

#### Pre-condition (Optional)

The user has registered the username in the system.

#### Process Flow

1. Enter the username.
2. Enter the password.

#### Exceptions (Optional)

If the username does not exist in the system or the password does not match the username, the login action will fail.

### Use Case: _Create New Projects and Tasks_

#### Description

Create new projects and tasks of web crawling.

#### Participants

User

#### Process Flow

Linear process flow:

1. Enter the name of the project.
2. Enter the name of the task.

#### Exceptions

If either the name is empty, the task will not be created.

### Use Case: _Edit Configurations of Tasks_

#### Description

Edit the configurations of a specificied task, including the basic rules, assistant rules, persistence rules, creep rules and executation rules of this task.

#### Participants

User

#### Pre-condition

- The task truly exists and has been selected by the current user.
- The format of the configuration file should be the same as the exported configuration file (JSON).

#### Process Flow

Linear process flow:
1. Edit the basic rules.
2. Edit the assitant rules.
3. Edit the persistence rules.
4. Edit the creep rules.
5. Edit the executation rules.
6. Save the modifications.

### Use Case: _Import Configurations_

#### Description

Import the configurations of a task from a configuration file stored locally on the user's computer.

#### Participants

User

#### Pre-condition

There is a configuration file stored locally on the user's computer.

#### Process Flow

Linear process flow:
1. Select the configuration file stored locally on the user's computer.
2. Upload the local file.
3. Optional: import the configurations into the current selected task.

#### Exceptions

If the format is not consistent with the exported configuration file, the import action will be unsuccessful and the user will not be able to import the configuration into the current selected task.

### Use Case: _Export Configurations_

#### Description

Export the configurations of a task and store them into a configuration file stored locally on the user's computer.

#### Participants

User

#### Process Flow

Linear process flow:
1. Select a task from the list of the existing tasks.
2. Export the configuration file onto the user's computer.

#### Exceptions

If the user has not selected a task, the content of the exported configuration file will be empty.

### Use Case: _Run Tasks_

#### Description

Run a task of web crawling.

#### Participants

User

#### Pre-condition

- The task exists and has been selected by the user.
- The configuration of the task has been completed.

#### Process Flow

1. Select a task from an existing project.
2. Check the integrity of the configuration of the selected task.
3. Run the crawling task.

#### Exceptions

If the task does not exist or the configuration of the task has not completed yet, the executation of the task will not be successful.

### Use Case: _Monitor Tasks_

#### Description

Monitor the status of the executation of a task.

#### Participants

User

#### Pre-condition

- The task exists and has been selected by the user.
- The configuration of the task has been completed.
- The task has started running before now.

#### Process Flow

1. Select a task from an existing project.
2. Select the information of the status of the executation of a task.

#### Exceptions

If the task does not exist or the configuration of the task has not completed yet, or the task has not started running, there will be no information about the status of the executation of this task.

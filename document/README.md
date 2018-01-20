# Big Research

## Introduction

- **Background**

  With the rapid development of the Internet, the amount of information and related service has been increasing considerably. It is becoming more and more important to figure out an efficient method of extracting the target information precisely among the overwhelming amount of resources on the World-Wide Web. The requirement for web crawling is so common that even some people with little programming knowledge are looking for a way to extract data from websites.

- **Purpose of this Project**

  The purpose of this project is to implement a scraping tool that allows users to easily extract data from the websites visually with no coding required.

- **Target Users**

  The users with little knowledge of developing web crawlers who want to scrape some information from websites efficiently and conveniently.

- **Boundary of this Project**

  This project can help to crawl information from websites, but it cannot help to process of the data crawled afterwards. The project aims to simplify the process of scraping data from websites, instead of the analysis on the data, such as emotion analysis of an article or data mining among the large amount of data scraped.

## How to Build

### Environment Requirements

- java 1.8 or later from `https://www.java.com/en/`
- IntelliJ IDEA from `https://www.jetbrains.com/idea/`

### Notable Technologies

- Back End: Java 8 with Spring Boot
- Front End: AngularJS + BootStrap
- Database: MySQL
- ORM: MyBatis
- Security: Spring Security
- Restful API

### Get the Project

- Using the command line interface of Git

  ```
  git clone https://github.com/XLab-Tongji/bigresearch.git
  ```

- Use a GUI client for Git ([SourceTree](https://www.sourcetreeapp.com/) for example)

  ![sourcetree](./images/readme/sourcetree.png)


- Just download it from [GitHub Repository](https://github.com/XLab-Tongji/bigresearch/tree/spider-dev)

### Import the Project into your IDE

As this project was developed using IntelliJ IDEA, so we strongly recommend you to import this project using IntelliJ IDEA. Of course, you can also use other IDE.

- Open the IntelliJ IDEA and click `import project`

  ![open_idea](./images/readme/open_idea.png)

- Select the `pom.xml` file and click `open`![select_pom](./images/readme/select_pom.png)

## How to Run

- Set up you java sdk

  ![set_sdk](./images/readme/set_sdk.png)

- Run the spring boot application in IDEA

  ![run](./images/readme/run.png)

## How to Use

- 创建项目

  ![创建项目](./images/readme/创建项目.png)

- 创建任务

  ![创建任务](./images/readme/创建任务.png)

- 配置基本信息

  ![配置基本信息](./images/readme/配置基本信息.png)

- 配置辅助规则

  ![配置辅助规则](./images/readme/配置辅助规则.png)

- 配置URL规则

  ![配置URL规则](./images/readme/配置URL规则.png)

- 单页

  ![单页](./images/readme/单页.png)

  ![单页输入](./images/readme/单页输入.png)

  ![列表](./images/readme/列表.png)

  ![导入](./images/readme/导入.png)

- 配置采集规则

  ![添加模式](./images/readme/添加模式.png)

  ![输入项目](./images/readme/输入项目.png)

  ![选取xpath](./images/readme/选取xpath.png)

  ![模式添加成功](./images/readme/模式添加成功.png)

- 配置持久化规则

  ![配置持久化规则](./images/readme/配置持久化规则.png)

- 配置执行计划

  ![配置执行计划](./images/readme/配置执行计划.png)

## Code Structure

The whole project structure as following, 

```
├── java
│   ├── com
│   │   └── monetware
│   │       ├── config
│   │       ├── controller
│   │       ├── mapper
│   │       │   ├── analysis
│   │       │   ├── collect
│   │       │   ├── common
│   │       │   └── search
│   │       ├── model
│   │       │   ├── analysis
│   │       │   ├── collect
│   │       │   ├── common
│   │       │   └── search
│   │       ├── service
│   │       │   ├── analyze
│   │       │   ├── collect
│   │       │   ├── common
│   │       │   └── search
│   │       └── util
│   └── org
├── resources
│   ├── dataFile
│   ├── mybatis
│   │   ├── analysis
│   │   ├── collect
│   │   ├── common
│   │   └── search
│   └── templates
└── webapp
    ├── WEB-INF
    ├── assets
    └── view
        ├── css
        ├── js
        │   ├── controllers
        │   │   ├── analysis
        │   │   ├── collect
        │   │   └── search
        │   ├── scripts
        │   └── services
        │       ├── analysis
        │       ├── collect
        │       ├── search
        │       └── user
        ├── tpl
        └── views
            ├── analysis
            ├── collect
            ├── profile
            └── search
```

|           Folder            |               Description                |
| :-------------------------: | :--------------------------------------: |
| /java/com/monetware/config  | The folder of spring boot config classes |
| /java/com/monetware/mapper  |     The folder of DAO layer classes      |
|  /java/com/monetware/model  |     The folder of data model classes     |
| /java/com/monetware/service |  The folder of business process classes  |
|  /java/com/monetware/util   |     The folder of some utility class     |
|     /resources/mybatis      |    The folder of Mybatis config files    |
|     /resources/dataFile     |         The folder of data files         |
|      /webapp/view/css       |         The folder of css files          |
|       /webapp/view/js       |          The folder of js files          |
|      /webapp/view/tpl       |    The folder of html template files     |
|     /webapp/view/views      |         The folder of html files         |


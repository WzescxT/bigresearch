# Big Research

## w - Introduction

> 

## How to Build

### Environment Requirements

- java 1.8 or later from `https://www.java.com/en/`
- IntelliJ IDEA from `https://www.jetbrains.com/idea/`

### Notable Technologies

- Backend: Java 8 with Sprint Boot
- Frontend: Angular.js + Bootstrap
- Database: MySQL
- ORM: Mybatis
- Security: Spring Security
- Restful API

### Get the Project

- Use the `git` command

  ```
  git clone https://github.com/XLab-Tongji/bigresearch.git
  ```

- Use Sourcetree `https://www.sourcetreeapp.com`or other tools

  ![sourcetree](./images/readme/sourcetree.png)

  ​

- Just download from `https://github.com/XLab-Tongji/bigresearch/tree/spider-dev`

### Import the Project to IDE

We develop this project in IntelliJ IDEA, so we strongly suggest that you import this project using  IntelliJ IDEA. Of course, you can also  use other IDE. 

- Open the IntelliJ IDEA and click `import project`

  ![open_idea](./images/readme/open_idea.png)

- Select the `pom.xml` file and click `open`![select_pom](./images/readme/select_pom.png)

## How to Run

- Set up you java sdk

  ![set_sdk](./images/readme/set_sdk.png)

- Run the spring boot application in IDEA

  ![run](./images/readme/run.png)

## w - How to Use

> 

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


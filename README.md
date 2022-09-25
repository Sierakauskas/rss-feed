# RSS Feed App

Rss feed app is an application to parse rss feeds from provided url

## Installation

1. install mysql: https://dev.mysql.com/doc/refman/8.0/en/windows-installation.html
2. create schema and tables with provided sql script through MySql command line:
```bash
source C:/.../database-sql/feeds.sql
```
3. maven clean-compile-package-install
4. Add tomcat run configuration with created **.war** file

## Usage

Run application with tomcat.  Open browser and go to ```http://localhost:8080``` (default deploy url). On rss feed page provide  *url*  and *feed name*. Press **Add feed**. On table below newly added feed appears with **View** button. Press view button and you will be redirected to feed view page, where you can find feed main information and newest 5 articles
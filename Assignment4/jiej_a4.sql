DROP TABLE IF EXISTS `jeopardy`.`users`;
DROP SCHEMA IF EXISTS `jeopardy`;
CREATE SCHEMA `jeopardy` ;
CREATE TABLE `jeopardy`.`users` (
  `username` VARCHAR(20) NOT NULL,
  `password` VARCHAR(20) NULL,
  PRIMARY KEY (`username`));

CREATE DATABASE IF NOT EXISTS `pdtp2021`;
USE `pdtp2021`;

DROP TABLE IF EXISTS `pd_message`;
DROP TABLE IF EXISTS `pd_channel`;
DROP TABLE IF EXISTS `pd_user`;

CREATE TABLE IF NOT EXISTS `pd_user` (
  `userId` INT NOT NULL AUTO_INCREMENT,
  `userName` VARCHAR(25) NOT NULL,
  `userPassword` VARCHAR(15) NOT NULL,
  `userPhotoPath` TEXT NOT NULL,
  PRIMARY KEY (`userId`))
ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `pd_channel` (
  `channelId` INT NOT NULL AUTO_INCREMENT,
  `channelName` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`channelId`))
ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `pd_message` (
  `messageId` INT NOT NULL,
  `messageText` TEXT NOT NULL,
  `messageDateTime` DATETIME NOT NULL,
  `userId` INT NOT NULL,
  `channelId` INT NOT NULL,
  `isRestMsg` INT NOT NULL,
  PRIMARY KEY (`messageId`),
  INDEX `fk_pd_message_pd_channel1_idx` (`channelId` ASC),
  INDEX `fk_pd_message_pd_user1_idx` (`userId` ASC),
  CONSTRAINT `fk_pd_message_pd_channel1`
    FOREIGN KEY (`channelId`)
    REFERENCES `pd_channel` (`channelId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pd_message_pd_user1`
    FOREIGN KEY (`userId`)
    REFERENCES `pd_user` (`userId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE USER `pdtpuser`@`%` IDENTIFIED BY 'pd_tp_password_123';
GRANT SELECT, INSERT, UPDATE, DELETE ON `pdtp2021`.* TO `pdtpuser`@`%`;

show databases;
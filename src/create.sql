DROP DATABASE IF EXISTS `browserprint`;
CREATE DATABASE `browserprint`;
USE `browserprint`;

/*
 * REMEMBER TO INCREMENT THE DEFAULT VAULE OF `BrowserprintVersion` IF YOU CHANGE THIS.
 */
CREATE TABLE `Samples` (
  `BrowserprintVersion` SMALLINT UNSIGNED NOT NULL DEFAULT 6,
  `IP` TEXT NOT NULL,
  `TimeStamp` DATETIME NOT NULL,
  `SampleUUID` CHAR(36) NOT NULL,
  `SampleID` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `UserAgent` TEXT,
  `AcceptHeaders` TEXT,
  `Platform` TEXT,
  `PlatformFlash` TEXT,
  `PluginDetails` TEXT,
  `TimeZone` TEXT,
  `ScreenDetails` TEXT,
  `ScreenDetailsFlash` TEXT,
  `LanguageFlash` TEXT,
  `Fonts` TEXT,
  `CharSizes` TEXT,
  `CookiesEnabled` BOOL NOT NULL,
  `SuperCookieLocalStorage` BOOL,
  `SuperCookieSessionStorage` BOOL,
  `SuperCookieUserData` BOOL,
  `DoNotTrack` TEXT,
  `ClockDifference` BIGINT,
  `DateTime` TEXT,
  `MathTan` TEXT,
  `UsingTor` BOOL NOT NULL,
  `TbbVersion` TEXT,
  `AdsBlocked` BOOL,
  `Canvas` TEXT,
  `WebGLVendor` TEXT,
  `WebGLRenderer` TEXT,
  `ColourVision` INTEGER NOT NULL,
  PRIMARY KEY(`SampleUUID`),
  KEY(`SampleID`)
)
ENGINE=InnoDB;

CREATE TABLE `SampleStatistics` (
  `SampleID` BIGINT UNSIGNED NOT NULL,
  `BrowserGroup` TEXT,
  `BrowserVersion` TEXT,
  `OSGroup` TEXT,
  `OSName` TEXT,
  FOREIGN KEY(`SampleID`) REFERENCES `Samples`(`SampleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY(`SampleID`)
)
ENGINE=InnoDB;

CREATE TABLE `SampleSets` (
  `SampleSetID` CHAR(36) NOT NULL,
  `SampleID` BIGINT UNSIGNED NOT NULL,
  FOREIGN KEY(`SampleID`) REFERENCES `Samples`(`SampleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY(`SampleSetID`,`SampleID`)
)
ENGINE=InnoDB;
DROP DATABASE IF EXISTS `browserprint`;
CREATE DATABASE `browserprint`;
USE `browserprint`;

/*
 * REMEMBER TO INCREMENT THE DEFAULT VAULE OF `BrowserprintVersion` IF YOU CHANGE THIS.
 */
CREATE TABLE `Samples` (
  `BrowserprintVersion` SMALLINT UNSIGNED NOT NULL DEFAULT 21,
  `IP` TEXT NOT NULL,
  `TimeStamp` DATETIME NOT NULL,
  `FingerprintHash` TEXT,
  `AllHeaders` TEXT NOT NULL,
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
  `ScreenDetailsCSS` TEXT,
  `LanguageFlash` TEXT,
  `Fonts` TEXT,
  `FontsJS_CSS` TEXT,
  `FontsCSS` TEXT,
  `CharSizes` TEXT,
  `CookiesEnabled` BOOL NOT NULL,
  `SuperCookieLocalStorage` BOOL,
  `SuperCookieSessionStorage` BOOL,
  `SuperCookieUserData` BOOL,
  `HstsEnabled` BOOL,
  `IndexedDBEnabled` BOOL,
  `DoNotTrack` TEXT,
  `ClockDifference` BIGINT,
  `DateTime` TEXT,
  `MathTan` TEXT,
  `UsingTor` BOOL NOT NULL,
  `TbbVersion` TEXT,
  `AdsBlockedGoogle` BOOL,
  `AdsBlockedBanner` BOOL,
  `AdsBlockedScript` BOOL,
  `LikeShareFacebook` INTEGER,
  `LikeShareTwitter` INTEGER,
  `LikeShareReddit` INTEGER,
  `Canvas` TEXT,
  `WebGLVendor` TEXT,
  `WebGLRenderer` TEXT,
  `ContrastLevel` INTEGER NOT NULL,
  `TouchPoints` INTEGER,
  `TouchEvent` BOOL,
  `TouchStart` BOOL,
  `AudioFingerprintPXI` TEXT,
  `AudioFingerprintPXIFullBuffer` TEXT,
  `AudioFingerprintNtVc` TEXT,
  `AudioFingerprintCC` TEXT,
  `AudioFingerprintHybrid` TEXT,
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

CREATE TABLE `SuperSampleSets` (
  `SampleSuperSetID` CHAR(36) NOT NULL,
  `SampleID` BIGINT UNSIGNED NOT NULL,
  FOREIGN KEY(`SampleID`) REFERENCES `Samples`(`SampleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY(`SampleSuperSetID`, `SampleID`)
)
ENGINE=InnoDB;

CREATE TABLE `SampleQuestionnaire` (
  `SampleID` BIGINT UNSIGNED NOT NULL,
  `usingProxy` TEXT,
  `isSpoofing` TEXT,
  `whatBrowser` TEXT,
  `whatOS` TEXT,
  FOREIGN KEY(`SampleID`) REFERENCES `Samples`(`SampleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY(`SampleID`)
)
ENGINE=InnoDB;

ALTER TABLE `Samples` ADD INDEX `FingerprintHash`(`FingerprintHash`(28)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `UserAgent`(`UserAgent`(200)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `AcceptHeaders` (`AcceptHeaders`(200)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `Platform` (`Platform`(40)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `PlatformFlash` (`PlatformFlash`(40)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `PluginDetails` (`PluginDetails`(800)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `TimeZone` (`TimeZone`(10)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `ScreenDetails` (`ScreenDetails`(20)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `ScreenDetailsFlash` (`ScreenDetailsFlash`(20)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `ScreenDetailsCSS` (`ScreenDetailsCSS`(20)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `LanguageFlash` (`LanguageFlash`(20)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `Fonts` (`Fonts`(2000)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `FontsJS_CSS` (`FontsJS_CSS`(500)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `FontsCSS` (`FontsCSS`(500)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `CharSizes` (`CharSizes`(4000)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `ClockDifference` (`ClockDifference`) USING HASH;
ALTER TABLE `Samples` ADD INDEX `DateTime` (`DateTime`(50)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `MathTan` (`MathTan`(20)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `TbbVersion` (`TbbVersion`(5)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `AdsBlocked` (`AdsBlockedGoogle`,`AdsBlockedBanner`,`AdsBlockedScript`) USING HASH;
ALTER TABLE `Samples` ADD INDEX `LikeShareDetails` (`LikeShareFacebook`,`LikeShareTwitter`,`LikeShareReddit`) USING HASH;
ALTER TABLE `Samples` ADD INDEX `Canvas` (`Canvas`(15000)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `WebGLVendor` (`WebGLVendor`(15)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `WebGLRenderer` (`WebGLRenderer`(30)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `TouchDetails` (`TouchPoints`,`TouchEvent`,`TouchStart`) USING HASH;
ALTER TABLE `Samples` ADD INDEX `AudioFingerprintPXI` (`AudioFingerprintPXI`(20)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `AudioFingerprintPXIFullBuffer` (`AudioFingerprintPXIFullBuffer`(40)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `AudioFingerprintNtVc` (`AudioFingerprintNtVc`(600)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `AudioFingerprintCC` (`AudioFingerprintCC`(600)) USING HASH;
ALTER TABLE `Samples` ADD INDEX `AudioFingerprintHybrid` (`AudioFingerprintHybrid`(600)) USING HASH;
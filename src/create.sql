DROP DATABASE IF EXISTS `browserprint`;
CREATE DATABASE `browserprint`;
USE `browserprint`;

/*
 * REMEMBER TO INCREMENT THE DEFAULT VAULE OF `BrowserprintVersion` IF YOU CHANGE THIS.
 */
CREATE TABLE `Samples` (
  `BrowserprintVersion` SMALLINT UNSIGNED NOT NULL DEFAULT 19,
  `IP` TEXT NOT NULL,
  `TimeStamp` DATETIME NOT NULL,
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
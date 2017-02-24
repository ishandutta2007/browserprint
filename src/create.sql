DROP DATABASE IF EXISTS `browserprint`;
CREATE DATABASE `browserprint`;
USE `browserprint`;

/*
 * REMEMBER TO INCREMENT THE DEFAULT VAULE OF `BrowserprintVersion` IF YOU CHANGE THIS.
 */
CREATE TABLE `Samples` (
  `BrowserprintVersion` SMALLINT UNSIGNED NOT NULL DEFAULT 21,
  `IP` BLOB NOT NULL,
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

--Stuff to keep track of version counts for efficiency's sake
CREATE TABLE `CountBrowserprintVersion` (
	`BrowserprintVersion` INT NOT NULL,
	`Count` BIGINT UNSIGNED NOT NULL DEFAULT 0,
	PRIMARY KEY(`BrowserprintVersion`)
)
ENGINE=InnoDB;

--Stuff for keep track of occurrence counts for efficiency's sake
--Note: This doesn't keep track of composite yet
CREATE TABLE `CountFingerprintHash` (
    `FingerprintHash` VARCHAR(28),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`FingerprintHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountUserAgent` (
    `UserAgentHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`UserAgentHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountAcceptHeaders` (
    `AcceptHeadersHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`AcceptHeadersHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountPlatform` (
    `PlatformHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`PlatformHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountPlatformFlash` (
    `PlatformFlashHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`PlatformFlashHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountPluginDetails` (
    `PluginDetailsHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`PluginDetailsHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountTimeZone` (
    `TimeZoneHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`TimeZoneHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountScreenDetails` (
    `ScreenDetailsHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`ScreenDetailsHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountScreenDetailsFlash` (
    `ScreenDetailsFlashHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`ScreenDetailsFlashHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountScreenDetailsCSS` (
    `ScreenDetailsCSSHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`ScreenDetailsCSSHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountLanguageFlash` (
    `LanguageFlashHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`LanguageFlashHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountFonts` (
    `FontsHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`FontsHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountFontsJS_CSS` (
    `FontsJS_CSSHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`FontsJS_CSSHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountFontsCSS` (
    `FontsCSSHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`FontsCSSHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountCharSizes` (
    `CharSizesHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`CharSizesHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountDoNotTrack` (
    `DoNotTrackHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`DoNotTrackHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountDateTime` (
    `DateTimeHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`DateTimeHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountMathTan` (
    `MathTanHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`MathTanHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountTbbVersion` (
    `TbbVersionHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`TbbVersionHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountAdsBlocked` (
    `AdsBlockedGoogle` BOOL,
    `AdsBlockedBanner` BOOL,
    `AdsBlockedScript` BOOL,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    UNIQUE(`AdsBlockedGoogle`, `AdsBlockedBanner`, `AdsBlockedScript`)
)
ENGINE=InnoDB;
CREATE TABLE `CountLikeShare` (
    `LikeShareFacebook` INTEGER,
    `LikeShareTwitter` INTEGER,
    `LikeShareReddit` INTEGER,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    UNIQUE(`LikeShareFacebook`, `LikeShareTwitter`, `LikeShareReddit`)
)
ENGINE=InnoDB;
CREATE TABLE `CountCanvas` (
    `CanvasHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`CanvasHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountWebGLVendor` (
    `WebGLVendorHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`WebGLVendorHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountWebGLRenderer` (
    `WebGLRendererHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`WebGLRendererHash`)
)
ENGINE=InnoDB;
CREATE TABLE `CountCookiesEnabled` (
    `CookiesEnabled` BOOL UNIQUE,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0
)
ENGINE=InnoDB;
CREATE TABLE `CountSuperCookie` (
    `SuperCookieLocalStorage` BOOL,
    `SuperCookieSessionStorage` BOOL,
    `SuperCookieUserData` BOOL,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    UNIQUE(`SuperCookieLocalStorage`, `SuperCookieSessionStorage`, `SuperCookieUserData`)
)
ENGINE=InnoDB;
CREATE TABLE `CountHstsEnabled` (
    `HstsEnabled` BOOL UNIQUE,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0
)
ENGINE=InnoDB;
CREATE TABLE `CountIndexedDBEnabled` (
    `IndexedDBEnabled` BOOL UNIQUE,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0
)
ENGINE=InnoDB;
CREATE TABLE `CountClockDifference` (
    `ClockDifference` BIGINT UNIQUE,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0
)
ENGINE=InnoDB;
CREATE TABLE `CountUsingTor` (
    `UsingTor` BOOL UNIQUE,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0
)
ENGINE=InnoDB;
CREATE TABLE `CountContrastLevel` (
    `ContrastLevel` INT UNIQUE,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0
)
ENGINE=InnoDB;
CREATE TABLE `CountTouchDetails` (
    `TouchPoints` INTEGER,
    `TouchEvent` BOOL,
    `TouchStart` BOOL,
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    UNIQUE(`TouchPoints`, `TouchEvent`, `TouchStart`)
)
ENGINE=InnoDB;
CREATE TABLE `CountAudioFingerprint` (
    `AudioFingerprintHash` VARCHAR(64),
    `Count` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY(`AudioFingerprintHash`)
)
ENGINE=InnoDB;

DELIMITER $$
CREATE TRIGGER `CountProperties` AFTER INSERT ON `Samples`
FOR EACH ROW BEGIN
    INSERT INTO `CountBrowserprintVersion` (`BrowserprintVersion`, `Count`) VALUES(`NEW`.`BrowserprintVersion`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountFingerprintHash` (`FingerprintHash`, `Count`) VALUES(`NEW`.`FingerprintHash`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountUserAgent` (`UserAgentHash`, `Count`) VALUES(IF(`NEW`.`UserAgent` IS NULL, '', SHA2(`NEW`.`UserAgent`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountAcceptHeaders` (`AcceptHeadersHash`, `Count`) VALUES(IF(`NEW`.`AcceptHeaders` IS NULL, '', SHA2(`NEW`.`AcceptHeaders`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountPlatform` (`PlatformHash`, `Count`) VALUES(IF(`NEW`.`Platform` IS NULL, '', SHA2(`NEW`.`Platform`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountPlatformFlash` (`PlatformFlashHash`, `Count`) VALUES(IF(`NEW`.`PlatformFlash` IS NULL, '', SHA2(`NEW`.`PlatformFlash`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountPluginDetails` (`PluginDetailsHash`, `Count`) VALUES(IF(`NEW`.`PluginDetails` IS NULL, '', SHA2(`NEW`.`PluginDetails`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountTimeZone` (`TimeZoneHash`, `Count`) VALUES(IF(`NEW`.`TimeZone` IS NULL, '', SHA2(`NEW`.`TimeZone`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountScreenDetails` (`ScreenDetailsHash`, `Count`) VALUES(IF(`NEW`.`ScreenDetails` IS NULL, '', SHA2(`NEW`.`ScreenDetails`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountScreenDetailsFlash` (`ScreenDetailsFlashHash`, `Count`) VALUES(IF(`NEW`.`ScreenDetailsFlash` IS NULL, '', SHA2(`NEW`.`ScreenDetailsFlash`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountScreenDetailsCSS` (`ScreenDetailsCSSHash`, `Count`) VALUES(IF(`NEW`.`ScreenDetailsCSS` IS NULL, '', SHA2(`NEW`.`ScreenDetailsCSS`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountLanguageFlash` (`LanguageFlashHash`, `Count`) VALUES(IF(`NEW`.`LanguageFlash` IS NULL, '', SHA2(`NEW`.`LanguageFlash`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountFonts` (`FontsHash`, `Count`) VALUES(IF(`NEW`.`Fonts` IS NULL, '', SHA2(`NEW`.`Fonts`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountFontsJS_CSS` (`FontsJS_CSSHash`, `Count`) VALUES(IF(`NEW`.`FontsJS_CSS` IS NULL, '', SHA2(`NEW`.`FontsJS_CSS`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountFontsCSS` (`FontsCSSHash`, `Count`) VALUES(IF(`NEW`.`FontsCSS` IS NULL, '', SHA2(`NEW`.`FontsCSS`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountCharSizes` (`CharSizesHash`, `Count`) VALUES(IF(`NEW`.`CharSizes` IS NULL, '', SHA2(`NEW`.`CharSizes`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountDoNotTrack` (`DoNotTrackHash`, `Count`) VALUES(IF(`NEW`.`DoNotTrack` IS NULL, '', SHA2(`NEW`.`DoNotTrack`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountDateTime` (`DateTimeHash`, `Count`) VALUES(IF(`NEW`.`DateTime` IS NULL, '', SHA2(`NEW`.`DateTime`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountMathTan` (`MathTanHash`, `Count`) VALUES(IF(`NEW`.`MathTan` IS NULL, '', SHA2(`NEW`.`MathTan`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountTbbVersion` (`TbbVersionHash`, `Count`) VALUES(IF(`NEW`.`TbbVersion` IS NULL, '', SHA2(`NEW`.`TbbVersion`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountAdsBlocked` (`AdsBlockedGoogle`, `AdsBlockedBanner`, `AdsBlockedScript`, `Count`) VALUES(`NEW`.`AdsBlockedGoogle`, `NEW`.`AdsBlockedBanner`, `NEW`.`AdsBlockedScript`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountLikeShare` (`LikeShareFacebook`, `LikeShareTwitter`, `LikeShareReddit`, `Count`) VALUES(`NEW`.`LikeShareFacebook`, `NEW`.`LikeShareTwitter`, `NEW`.`LikeShareReddit`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountCanvas` (`CanvasHash`, `Count`) VALUES(IF(`NEW`.`Canvas` IS NULL, '', SHA2(`NEW`.`Canvas`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountWebGLVendor` (`WebGLVendorHash`, `Count`) VALUES(IF(`NEW`.`WebGLVendor` IS NULL, '', SHA2(`NEW`.`WebGLVendor`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountWebGLRenderer` (`WebGLRendererHash`, `Count`) VALUES(IF(`NEW`.`WebGLRenderer` IS NULL, '', SHA2(`NEW`.`WebGLRenderer`, 256)), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountTouchDetails` (`TouchPoints`, `TouchEvent`, `TouchStart`, `Count`) VALUES(`NEW`.`TouchPoints`, `NEW`.`TouchEvent`, `NEW`.`TouchStart`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountAudioFingerprint` (`AudioFingerprintHash`, `Count`) VALUES(SHA2(CONCAT_WS('', `NEW`.`AudioFingerprintPXI`, `NEW`.`AudioFingerprintPXIFullBuffer`, `NEW`.`AudioFingerprintNtVc`, `NEW`.`AudioFingerprintCC`, `NEW`.`AudioFingerprintHybrid`), 256), 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;    
    INSERT INTO `CountCookiesEnabled` (`CookiesEnabled`, `Count`) VALUES(`NEW`.`CookiesEnabled`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountSuperCookie` (`SuperCookieLocalStorage`, `SuperCookieSessionStorage`, `SuperCookieUserData`, `Count`) VALUES(`NEW`.`SuperCookieLocalStorage`, `NEW`.`SuperCookieSessionStorage`, `NEW`.`SuperCookieUserData`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountHstsEnabled` (`HstsEnabled`, `Count`) VALUES(`NEW`.`HstsEnabled`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountIndexedDBEnabled` (`IndexedDBEnabled`, `Count`) VALUES(`NEW`.`IndexedDBEnabled`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountClockDifference` (`ClockDifference`, `Count`) VALUES(`NEW`.`ClockDifference`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountUsingTor` (`UsingTor`, `Count`) VALUES(`NEW`.`UsingTor`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
    INSERT INTO `CountContrastLevel` (`ContrastLevel`, `Count`) VALUES(`NEW`.`ContrastLevel`, 1) ON DUPLICATE KEY UPDATE `Count` = `Count` + 1;
END$$
DELIMITER ;
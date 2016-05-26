# Browserprint
An open-source browser fingerprinting suite, inspired by the EFF's Panopticlick.

## Supported Tests
Browserprint implements the following tests:

### Monitor Contrast Level
A rough measure of the level of contrast of the monitor the browser is being displayed on.
Detected using a CAPTCHA with a couple of light grey letters that disappear when a monitor's contrast is sufficiently high.

### Colour Vision
[Test removed for ethics reasons. Last in commit 5858dd7]
Whether you have any issues seeing colour (note: this isn't necessarily correct and should be taken with a grain of salt).
Detected using a CAPTCHA with colour vision test plates (people with certain types of colour blindness see different numbers).

### User Agent
The User-Agent header sent with the HTTP request for the page.

### HTTP\_ACCEPT Headers
The concatenation of three headers from the HTTP request:
the Accept request header, the Accept-Encoding request header, and the Accept-Language request header.

### Platform (JavaScript)
The name of the platform the browser is running on, detected using JavaScript.

### Platform (Flash)
The name of the platform the browser is running on, detected using Flash.

### Browser Plugin Details
A list of the browsers installed plugins as detected using JavaScript.

### Time Zone
The time-zone configured on the client's machine.

### Screen Size and Color Depth
The screen size and colour depth of the monitor displaying the client's web browser.

### Screen Size (Flash)
The resolution of the client's monitor(s).
Different from the other screen size test in that this number can be the cumulative resolution of the monitors in multiple monitor set ups.

### Language (Flash)
The language of the client's browser, as detected using Flash.

### System Fonts
The fonts installed on the client's machine, detected using Flash.

### Character Sizes
The height and width of a set of Unicode characters rendered at 2200pt with a variety of styles applied to them (e.g. sans-serif).
Different systems render these characters differently and this is one way to detect that without using a canvas.

### Are Cookies Enabled?
Whether cookies are enabled.

### Limited supercookie test
Three tests of whether DOM storage is supported (and enabled) in the client's web browser.
Tests for localStorage, sessionStorage, and Internet Explorer's userData.

### IndexedDB Enabled Test
Detects whether the browser supports IndexedDB, a database embedded within the browser.

### Do Not Track header
The value of the DNT (Do Not Track) header from the HTTP request.

### Client/server time difference (minutes)
The approximate amount of difference between the time on the client's computer and the clock on the server.
i.e., the clock on the client's computer is 5 minutes ahead of the clock on the server.

### Date/Time format test
When the JavaScript function toLocaleString() is called on a date it can reveal information about the language of the browser via the names of days and months.
For instance the output 'Thursday January 01, 10:30:00 GMT+1030 1970' reveals that English is our configured language because 'Thursday' is English.
Additionally different browsers tend to return differently formatted results.
For instance Opera returns the above whereas Firefox returns '1/1/1970 9:30:00 am' for the same date (UNIX epoch).
Additionally timezone information may be revealed.
For instance the above were taken on a computer configured for CST (+9:30), which is why the times shown aren't midnight.

### Math / Tan function
The same math functions run on different platforms and browsers can produce different results.
In particular we are interested in the output of Math.tan(-1e300), which has been observed to produce different values depending on operating system.
For instance on a 64bit Linux machine it produces the value -1.4214488238747245 and on a Windows machine it produces the value -4.987183803371025.

### Using Tor?
Checks whether a client's request came from a Tor exit node, and hence whether they're using Tor.
It does so by performing a TorDNSEL request for each client.

### Blocking Google Ads?
Checks whether ad blocking software is installed.
It does so by attempting to display an ad and checking whether it was successful.
May also be affected by tracker blocking software.

### Canvas
Rendering of a specific picture with the HTML5 Canvas element following a fixed set of instructions.
The picture presents some slight noticeable variations depending on the OS and the browser used.

### WebGL Vendor
Name of the WebGL Vendor. Some browsers give the full name of the underlying graphics card used by the device.

### WebGL Renderer
Name of the WebGL Renderer. Some browsers give the full name of the underlying graphics driver.

### Touch Support
Primative touch screen detection.

## Installation
Here we give a quick overview of one way the project can be deployed.
You need to have Apache Tomcat and MariaDB installed for this to work.

  * Start MariaDB.
  * (optional but recommended) [Configure your database to support UTF-8](http://stackoverflow.com/a/3513812)
  * Run the `create.sql` file found in the `src` directory of the repository through MariaDB.
  * Delete (or move) everything from the Tomcat `webapps` directory.
  * Download the file `browserprint.war` from the repository or build it yourself for a (possibly) more update version (we build it using the Eclipse IDE and a project consisting of the `src` and `WebContent` directories of the repository).
  * Copy the file `browserprint.war` to your `<TomcatDirectory>/webapps` directory.
  * Add the line `<Context path="" docBase="browserprint" debug="0" reloadable="true"></Context>` inside the Host tags to `<TomcatDirectory>/conf/server.xml`.
  * Start Tomcat (`<TomcatDirectory>/bin/startup.sh`). The `.war` file will be automatically unpacked by Tomcat.
  * Stop Tomcat (`<TomcatDirectory>/bin/shutdown.sh`).
  * Delete the file `browserprint.war` from your `webapps` directory.
  * Edit `<TomcatDirectory>/webapps/browserprint/META-INFO/context.xml` to fit your database.
  * Configure the server's public IP address as serversPublicIP in `browserprint/WEB-INF/web.xml`.
  * Check `web.xml.README` for other configurable options you may (and probably should) want to use in `browserprint/WEB-INF/web.xml`.
  * Start Tomcat.

The page can now be accessed at http://localhost:8080/ (assuming default Tomcat settings).

This project was created using the Eclipse IDE configured to use Tomcat and can be easily set up and modified using them.

### Optional
* Remove or replace the Creative Commons license on the output of the web application in `WebContent/WEB-INF/footer.jsp`.
  You are only required to retain the MIT license on the code, not the CC license on the web application output.

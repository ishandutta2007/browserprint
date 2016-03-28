function getPlatform() {
	return window.navigator.platform;
}

function getPlatformFlash(flash) {
	return flash.getOS();
}

function getPluginDetails() {
	try {
		var plugins = "";

		/*
		 * This method said to not be supported by IE.
		 */
		for (var i = 0; i < navigator.plugins.length; ++i) {
			var plugin = navigator.plugins[i];
			plugins += "Plugin " + i + ": " + plugin.name + "; "
					+ plugin.description + "; " + plugin.filename + ";";

			for (var j = 0; j < plugin.length; ++j) {
				plugins += " (" + plugin[j].description + "; " + plugin[j].type
						+ "; " + plugin[j].suffixes + ")";
			}
			plugins += ". ";
		}

		if (plugins == "") {
			/*
			 * Try the method that works with IE. Uses an MIT licensed script,
			 * PluginDetect.
			 */
			var plugin_names = [ "QuickTime", "Java", "DevalVR", "Flash",
					"Shockwave", "WindowsMediaPlayer", "Silverlight", "VLC",
					"AdobeReader", "PDFReader", "RealPlayer", "IEcomponent",
					"ActiveX", "PDFjs" ]
			for (var i = 0; i < plugin_names.length; ++i) {
				var version = PluginDetect.getVersion(plugin_names[i]);
				if (version) {
					plugins += plugin_names[i] + " " + version + "; ";
				}
			}
		}

		if (plugins == "") {
			plugins = "No plugins detected";
		}
	} catch (e) {
		return "Error";
	}

	return plugins;
}

function getScreenDetails() {
	try {
		return screen.width + "x" + screen.height + "x" + screen.colorDepth;
	} catch (e) {
		return "Error";
	}
}

function getScreenDetailsFlash(flash) {
	return flash.getResolution().join("x");
}

function getFontsFlash(flash) {
	return flash.getFonts().join(", ");
}

function getFontsJavaScriptCSS() {
	try {
		/*
		 * Function's code from:
		 * Fingerprintjs2 1.1.2 - Modern & flexible browser fingerprint library v2
		 * https://github.com/Valve/fingerprintjs2
		 * Copyright (c) 2015 Valentin Vasilyev (valentin.vasilyev@outlook.com)
		 * Licensed under the MIT (http://www.opensource.org/licenses/mit-license.php) license.
		 *
		 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
		 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
		 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
		 * ARE DISCLAIMED. IN NO EVENT SHALL VALENTIN VASILYEV BE LIABLE FOR ANY
		 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
		 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
		 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
		 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
		 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
		 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
		 */
		var extendedJsFonts = 1;

		// a font will be compared against all the three default fonts.
		// and if it doesn't match all 3 then that font is not available.
		var baseFonts = ["monospace", "sans-serif", "serif"];

		//we use m or w because these two characters take up the maximum width.
		// And we use a LLi so that the same matching fonts can get separated
		var testString = "mmmmmmmmmmlli";

		//we test using 72px font size, we may use any size. I guess larger the better.
		var testSize = "72px";

		var h = document.getElementsByTagName("body")[0];

		// create a SPAN in the document to get the width of the text we use to test
		var s = document.createElement("span");
		s.style.fontSize = testSize;
		s.innerHTML = testString;
		var defaultWidth = {};
		var defaultHeight = {};
		for (var index = 0, length = baseFonts.length; index < length; index++) {
		    //get the default width for the three base fonts
		    s.style.fontFamily = baseFonts[index];
		    h.appendChild(s);
		    defaultWidth[baseFonts[index]] = s.offsetWidth; //width for the default font
		    defaultHeight[baseFonts[index]] = s.offsetHeight; //height for the defualt font
		    h.removeChild(s);
		}
		var detect = function (font) {
		    var detected = false;
		    for (var index = 0, l = baseFonts.length; index < l; index++) {
		        s.style.fontFamily = font + "," + baseFonts[index]; // name of the font along with the base font for fallback.
		        h.appendChild(s);
		        var matched = (s.offsetWidth !== defaultWidth[baseFonts[index]] || s.offsetHeight !== defaultHeight[baseFonts[index]]);
		        h.removeChild(s);
		        detected = detected || matched;
		    }
		    return detected;
		};
		var fontList = [
		  "Andale Mono", "Arial", "Arial Black", "Arial Hebrew", "Arial MT", "Arial Narrow", "Arial Rounded MT Bold", "Arial Unicode MS",
		  "Bitstream Vera Sans Mono", "Book Antiqua", "Bookman Old Style",
		  "Calibri", "Cambria", "Cambria Math", "Century", "Century Gothic", "Century Schoolbook", "Comic Sans", "Comic Sans MS", "Consolas", "Courier", "Courier New",
		  "Garamond", "Geneva", "Georgia",
		  "Helvetica", "Helvetica Neue",
		  "Impact",
		  "Lucida Bright", "Lucida Calligraphy", "Lucida Console", "Lucida Fax", "LUCIDA GRANDE", "Lucida Handwriting", "Lucida Sans", "Lucida Sans Typewriter", "Lucida Sans Unicode",
		  "Microsoft Sans Serif", "Monaco", "Monotype Corsiva", "MS Gothic", "MS Outlook", "MS PGothic", "MS Reference Sans Serif", "MS Sans Serif", "MS Serif", "MYRIAD", "MYRIAD PRO",
		  "Palatino", "Palatino Linotype",
		  "Segoe Print", "Segoe Script", "Segoe UI", "Segoe UI Light", "Segoe UI Semibold", "Segoe UI Symbol",
		  "Tahoma", "Times", "Times New Roman", "Times New Roman PS", "Trebuchet MS",
		  "Verdana", "Wingdings", "Wingdings 2", "Wingdings 3"
		];
		var extendedFontList = [
		  "Abadi MT Condensed Light", "Academy Engraved LET", "ADOBE CASLON PRO", "Adobe Garamond", "ADOBE GARAMOND PRO", "Agency FB", "Aharoni", "Albertus Extra Bold", "Albertus Medium", "Algerian", "Amazone BT", "American Typewriter",
		  "American Typewriter Condensed", "AmerType Md BT", "Andalus", "Angsana New", "AngsanaUPC", "Antique Olive", "Aparajita", "Apple Chancery", "Apple Color Emoji", "Apple SD Gothic Neo", "Arabic Typesetting", "ARCHER",
		   "ARNO PRO", "Arrus BT", "Aurora Cn BT", "AvantGarde Bk BT", "AvantGarde Md BT", "AVENIR", "Ayuthaya", "Bandy", "Bangla Sangam MN", "Bank Gothic", "BankGothic Md BT", "Baskerville",
		  "Baskerville Old Face", "Batang", "BatangChe", "Bauer Bodoni", "Bauhaus 93", "Bazooka", "Bell MT", "Bembo", "Benguiat Bk BT", "Berlin Sans FB", "Berlin Sans FB Demi", "Bernard MT Condensed", "BernhardFashion BT", "BernhardMod BT", "Big Caslon", "BinnerD",
		  "Blackadder ITC", "BlairMdITC TT", "Bodoni 72", "Bodoni 72 Oldstyle", "Bodoni 72 Smallcaps", "Bodoni MT", "Bodoni MT Black", "Bodoni MT Condensed", "Bodoni MT Poster Compressed",
		  "Bookshelf Symbol 7", "Boulder", "Bradley Hand", "Bradley Hand ITC", "Bremen Bd BT", "Britannic Bold", "Broadway", "Browallia New", "BrowalliaUPC", "Brush Script MT", "Californian FB", "Calisto MT", "Calligrapher", "Candara",
		  "CaslonOpnface BT", "Castellar", "Centaur", "Cezanne", "CG Omega", "CG Times", "Chalkboard", "Chalkboard SE", "Chalkduster", "Charlesworth", "Charter Bd BT", "Charter BT", "Chaucer",
		  "ChelthmITC Bk BT", "Chiller", "Clarendon", "Clarendon Condensed", "CloisterBlack BT", "Cochin", "Colonna MT", "Constantia", "Cooper Black", "Copperplate", "Copperplate Gothic", "Copperplate Gothic Bold",
		  "Copperplate Gothic Light", "CopperplGoth Bd BT", "Corbel", "Cordia New", "CordiaUPC", "Cornerstone", "Coronet", "Cuckoo", "Curlz MT", "DaunPenh", "Dauphin", "David", "DB LCD Temp", "DELICIOUS", "Denmark",
		  "DFKai-SB", "Didot", "DilleniaUPC", "DIN", "DokChampa", "Dotum", "DotumChe", "Ebrima", "Edwardian Script ITC", "Elephant", "English 111 Vivace BT", "Engravers MT", "EngraversGothic BT", "Eras Bold ITC", "Eras Demi ITC", "Eras Light ITC", "Eras Medium ITC",
		  "EucrosiaUPC", "Euphemia", "Euphemia UCAS", "EUROSTILE", "Exotc350 Bd BT", "FangSong", "Felix Titling", "Fixedsys", "FONTIN", "Footlight MT Light", "Forte",
		  "FrankRuehl", "Fransiscan", "Freefrm721 Blk BT", "FreesiaUPC", "Freestyle Script", "French Script MT", "FrnkGothITC Bk BT", "Fruitger", "FRUTIGER",
		  "Futura", "Futura Bk BT", "Futura Lt BT", "Futura Md BT", "Futura ZBlk BT", "FuturaBlack BT", "Gabriola", "Galliard BT", "Gautami", "Geeza Pro", "Geometr231 BT", "Geometr231 Hv BT", "Geometr231 Lt BT", "GeoSlab 703 Lt BT",
		  "GeoSlab 703 XBd BT", "Gigi", "Gill Sans", "Gill Sans MT", "Gill Sans MT Condensed", "Gill Sans MT Ext Condensed Bold", "Gill Sans Ultra Bold", "Gill Sans Ultra Bold Condensed", "Gisha", "Gloucester MT Extra Condensed", "GOTHAM", "GOTHAM BOLD",
		  "Goudy Old Style", "Goudy Stout", "GoudyHandtooled BT", "GoudyOLSt BT", "Gujarati Sangam MN", "Gulim", "GulimChe", "Gungsuh", "GungsuhChe", "Gurmukhi MN", "Haettenschweiler", "Harlow Solid Italic", "Harrington", "Heather", "Heiti SC", "Heiti TC", "HELV",
		  "Herald", "High Tower Text", "Hiragino Kaku Gothic ProN", "Hiragino Mincho ProN", "Hoefler Text", "Humanst 521 Cn BT", "Humanst521 BT", "Humanst521 Lt BT", "Imprint MT Shadow", "Incised901 Bd BT", "Incised901 BT",
		  "Incised901 Lt BT", "INCONSOLATA", "Informal Roman", "Informal011 BT", "INTERSTATE", "IrisUPC", "Iskoola Pota", "JasmineUPC", "Jazz LET", "Jenson", "Jester", "Jokerman", "Juice ITC", "Kabel Bk BT", "Kabel Ult BT", "Kailasa", "KaiTi", "Kalinga", "Kannada Sangam MN",
		  "Kartika", "Kaufmann Bd BT", "Kaufmann BT", "Khmer UI", "KodchiangUPC", "Kokila", "Korinna BT", "Kristen ITC", "Krungthep", "Kunstler Script", "Lao UI", "Latha", "Leelawadee", "Letter Gothic", "Levenim MT", "LilyUPC", "Lithograph", "Lithograph Light", "Long Island",
		  "Lydian BT", "Magneto", "Maiandra GD", "Malayalam Sangam MN", "Malgun Gothic",
		  "Mangal", "Marigold", "Marion", "Marker Felt", "Market", "Marlett", "Matisse ITC", "Matura MT Script Capitals", "Meiryo", "Meiryo UI", "Microsoft Himalaya", "Microsoft JhengHei", "Microsoft New Tai Lue", "Microsoft PhagsPa", "Microsoft Tai Le",
		  "Microsoft Uighur", "Microsoft YaHei", "Microsoft Yi Baiti", "MingLiU", "MingLiU_HKSCS", "MingLiU_HKSCS-ExtB", "MingLiU-ExtB", "Minion", "Minion Pro", "Miriam", "Miriam Fixed", "Mistral", "Modern", "Modern No. 20", "Mona Lisa Solid ITC TT", "Mongolian Baiti",
		  "MONO", "MoolBoran", "Mrs Eaves", "MS LineDraw", "MS Mincho", "MS PMincho", "MS Reference Specialty", "MS UI Gothic", "MT Extra", "MUSEO", "MV Boli",
		  "Nadeem", "Narkisim", "NEVIS", "News Gothic", "News GothicMT", "NewsGoth BT", "Niagara Engraved", "Niagara Solid", "Noteworthy", "NSimSun", "Nyala", "OCR A Extended", "Old Century", "Old English Text MT", "Onyx", "Onyx BT", "OPTIMA", "Oriya Sangam MN",
		  "OSAKA", "OzHandicraft BT", "Palace Script MT", "Papyrus", "Parchment", "Party LET", "Pegasus", "Perpetua", "Perpetua Titling MT", "PetitaBold", "Pickwick", "Plantagenet Cherokee", "Playbill", "PMingLiU", "PMingLiU-ExtB",
		  "Poor Richard", "Poster", "PosterBodoni BT", "PRINCETOWN LET", "Pristina", "PTBarnum BT", "Pythagoras", "Raavi", "Rage Italic", "Ravie", "Ribbon131 Bd BT", "Rockwell", "Rockwell Condensed", "Rockwell Extra Bold", "Rod", "Roman", "Sakkal Majalla",
		  "Santa Fe LET", "Savoye LET", "Sceptre", "Script", "Script MT Bold", "SCRIPTINA", "Serifa", "Serifa BT", "Serifa Th BT", "ShelleyVolante BT", "Sherwood",
		  "Shonar Bangla", "Showcard Gothic", "Shruti", "Signboard", "SILKSCREEN", "SimHei", "Simplified Arabic", "Simplified Arabic Fixed", "SimSun", "SimSun-ExtB", "Sinhala Sangam MN", "Sketch Rockwell", "Skia", "Small Fonts", "Snap ITC", "Snell Roundhand", "Socket",
		  "Souvenir Lt BT", "Staccato222 BT", "Steamer", "Stencil", "Storybook", "Styllo", "Subway", "Swis721 BlkEx BT", "Swiss911 XCm BT", "Sylfaen", "Synchro LET", "System", "Tamil Sangam MN", "Technical", "Teletype", "Telugu Sangam MN", "Tempus Sans ITC",
		  "Terminal", "Thonburi", "Traditional Arabic", "Trajan", "TRAJAN PRO", "Tristan", "Tubular", "Tunga", "Tw Cen MT", "Tw Cen MT Condensed", "Tw Cen MT Condensed Extra Bold",
		  "TypoUpright BT", "Unicorn", "Univers", "Univers CE 55 Medium", "Univers Condensed", "Utsaah", "Vagabond", "Vani", "Vijaya", "Viner Hand ITC", "VisualUI", "Vivaldi", "Vladimir Script", "Vrinda", "Westminster", "WHITNEY", "Wide Latin",
		  "ZapfEllipt BT", "ZapfHumnst BT", "ZapfHumnst Dm BT", "Zapfino", "Zurich BlkEx BT", "Zurich Ex BT", "ZWAdobeF"];

		if(extendedJsFonts) {
		  fontList = fontList.concat(extendedFontList);
		}
		var available = [];
		for (var i = 0, l = fontList.length; i < l; i++) {
		  if(detect(fontList[i])) {
		    available.push(fontList[i]);
		  }
		}
		return available.join(", ");
	} catch (e) {
		return "Error";
	}
}

function getTimeZone() {
	try {
		return new Date().getTimezoneOffset();
	} catch (e) {
		return "Error";
	}
}

function getSuperCookieLocalStorage() {
	try {
		if ('localStorage' in window && window['localStorage'] !== null) {
			return "1";
		} else {
			return "0";
		}
	} catch (ex) {
		return "0";
	}
}

function getSuperCookieSessionStorage() {
	try {
		if ('sessionStorage' in window && window['sessionStorage'] !== null) {
			return "1";
		} else {
			return "0";
		}
	} catch (ex) {
		return "0";
	}

	return test;
}

function getSuperCookieUserData() {
	try {
		var persistDiv = $('<div id="tmpDiv" style="behavior:url(#default#userdata)"></div>');
		persistDiv.appendTo(document.body);
		tmpDiv.setAttribute("remember", "original value");
		tmpDiv.save("oXMLStore");
		tmpDiv.setAttribute("remember", "overwritten");
		tmpDiv.load("oXMLStore");
		if ((tmpDiv.getAttribute("remember")) == "original value") {
			return "1";
		} else {
			return "0";
		}
	} catch (ex) {
		return "0";
	}
}

function getIndexedDBEnabled() {
	try {
		if (!!window.indexedDB) {
			return "1";
		} else {
			return "0";
		}
	} catch (ex) {
		return "0";
	}

	return test;
}

function getTime() {
	try {
		var time = new Date().getTime();
		return time;
	} catch (e) {
		return "Error";
	}
}

function getDateTime() {
	try {
		var d = new Date(0);
		return d.toLocaleString();
	} catch (ex) {
		return "Error";
	}
}

function getMathTan() {
	try {
		return Math.tan(-1e300);
	} catch (e) {
		return "Error";
	}
}

function getAdsBlocked() {
	try {
		if ($('#ad').height() == 0) {
			// Ads are blocked.
			return 1;
		} else {
			// Ads are not blocked.
			return 0;
		}
	} catch (e) {
		return "Error";
	}
}

function getCanvas() {
	try {
		/*
		 * Function's code taken from from Am I Unique?
		 * https://amiunique.org
		 * 
		 * The MIT License (MIT)
		 *
		 * Copyright (c) 2014 Pierre Laperdrix
		 *
		 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
		 *  to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
		 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
		 *
		 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
		 *
		 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
		 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
		 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
		 */
		canvas = document.createElement("canvas");
		canvas.height = 60;
		canvas.width = 400;
		canvasContext = canvas.getContext("2d");
		canvas.style.display = "inline";
		canvasContext.textBaseline = "alphabetic";
		canvasContext.fillStyle = "#f60";
		canvasContext.fillRect(125, 1, 62, 20);
		canvasContext.fillStyle = "#069";
		canvasContext.font = "11pt no-real-font-123";
		canvasContext.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03",
				2, 15);
		canvasContext.fillStyle = "rgba(102, 204, 0, 0.7)";
		canvasContext.font = "18pt Arial";
		canvasContext.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03",
				4, 45);
		return canvas.toDataURL();
	} catch (e) {
		return "Not supported";
	}
}

function getWebGLVendor() {
	try {
		var canvas = document.createElement('canvas');
		var ctx = canvas.getContext("webgl")
				|| canvas.getContext("experimental-webgl");
		if (ctx.getSupportedExtensions().indexOf("WEBGL_debug_renderer_info") >= 0) {
			return ctx
					.getParameter(ctx.getExtension('WEBGL_debug_renderer_info').UNMASKED_VENDOR_WEBGL);
		} else {
			return "Not supported";
		}
	} catch (e) {
		return "Not supported";
	}
}

function getWebGLRenderer() {
	try {
		var canvas = document.createElement('canvas');
		var ctx = canvas.getContext("webgl")
				|| canvas.getContext("experimental-webgl");
		if (ctx.getSupportedExtensions().indexOf("WEBGL_debug_renderer_info") >= 0) {
			return ctx
					.getParameter(ctx.getExtension('WEBGL_debug_renderer_info').UNMASKED_RENDERER_WEBGL);
		} else {
			return "Not supported";
		}
	} catch (e) {
		return "Not supported";
	}
}

function getLanguageFlash(flash) {
	return flash.getLanguage();
}

/*
 * Some code taken from public domain materials here: https://www.bamsoftware.com/talks/fc15-fontfp/fontfp.html#demo
 */
function getCharacterSizes(){
	var CODEPOINTS = [0x20B9, 0x2581, 0x20BA, 0xA73D, 0xFFFD, 0x20B8, 0x05C6, 0x1E9E, 0x097F, 0xF003, 0x1CDA, 0x17DD, 0x23AE, 0x0D02, 0x0B82, 0x115A, 0x2425, 0x302E, 0xA830, 0x2B06, 0x21E4, 0x20BD, 0x2C7B, 0x20B0, 0xFBEE, 0xF810, 0xFFFF, 0x007F, 0x10A0, 0x1D790, 0x0700, 0x1950, 0x3095, 0x532D, 0x061C, 0x20E3, 0xFFF9, 0x0218, 0x058F, 0x08E4, 0x09B3, 0x1C50, 0x2619];
	var font_families = ["default", "sans-serif", "serif", "monospace", "cursive", "fantasy"];
	
	var span = $("<span>");
	var div = $('<div>');
	div.attr("style", "font-size:2200pt; visibility:hidden;");
	span.appendTo(div);
	div.appendTo(document.body);
	
	var sizesStr = "";
	for(var i = 0; i < CODEPOINTS.length; ++i){
		var chr;
		if (CODEPOINTS[i] <= 0xffff) {
			chr = String.fromCharCode(CODEPOINTS[i]);
		} else {
			CODEPOINTS[i] -= 0x10000;
			chr = String.fromCharCode(0xd800 + (CODEPOINTS[i] >> 10), 0xdc00 + (CODEPOINTS[i] % 0x400));
		}
		
		span.text(chr);
		
		for(var j = 0; j < font_families.length; ++j){
			span.attr("style", "font-family:" + font_families[j]);
			if(i != 0 || j != 0){
				sizesStr += " ";
			}
			sizesStr += span.width() + "x" + div.height();				
		}
	}
	span.text("");
	
	return sizesStr;
}
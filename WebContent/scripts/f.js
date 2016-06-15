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

function getAdsBlockedGoogle() {
	try {
		if ($('#ad').height() == 0) {
			// Ad was blocked.
			return 1;
		} else {
			// Ad was not blocked.
			return 0;
		}
	} catch (e) {
		return "Error";
	}
}

function getAdsBlockedBanner() {
	try {
		if($("#banner468x60").height() == 0){
			// Ad was blocked.
			return 1;
		}
		else{
			// Ad was not blocked.
			return 0;
		}
	} catch (e) {
		return "Error";
	}
}

function getAdsBlockedScript() {
	try {
		return testscript_blockedFunction();
	} catch (e) {
		// Ad script was blocked.
		return 1;
	}
}

function getFacebookSocialButton() {
	try {
		var likeButton = $("#likeButton");
		if(likeButton.length == 0){
			//Privacy Badger or similar must have replaced the like button div.
			return 1;
		}
		else{
			if(likeButton.children().length == 0){
				//NoScript, uMatrix.
				return 2;
			}
			else{
				var retval = 0; //0 = no blocking.
				likeButton.children().each(function(){
					if($(this).height() == 0 && $(this).width() == 0){
						//Adblock Plus Anti-Social.
						retval = 3;
					}
				});
				return retval;
			}
		}
	} catch (ex) {
		return "Error";
	}
}

function getTwitterButton(){
	try{
		if($("#twitterLink").length == 0){
			if($("#twitter-widget-0").length != 0){
				//Not blocked.
				return 0;
			}
			else{
				//Blocked by Privacy Badger.
				return 1;
			}
		}
		else{
			//Blocked by anything that blocks scripts.
			return 2;
		}
	}
	catch(ex){
		return "Error";
	}
}

function getRedditButton(){
	try{
		var redditButtonDiv = $("#redditButtonDiv");
		var retval = 2;//2 = blocked by anything that blocks scripts.
		redditButtonDiv.children().each(function(){
			if($(this).is("iframe")){
				if($(this).height() == 0 && $(this).width() == 0){
					//Something happened, was it blocked by ... something?
					retval = 4;
					return;
				}
				else{
					//Not blocked.
					retval = 0;
					return;
				}
			}
		});
		return retval;
	}
	catch(ex){
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
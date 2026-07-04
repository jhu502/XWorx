"use strict";

var Module = {
	'locateFile': function(name) {
		return ThingView.modulePath + name;
	},
	onRuntimeInitialized: function() {
		ThingView.loaded = true;
		if (!(ThingView.initCB == undefined)) {
			ThingView._completeInit();
			ThingView._setResourcePath(ThingView.resourcePath);
			ThingView.LoadPreferences(function(jsonObj, defaultPrefs) {
				if (jsonObj !== undefined) {
					ThingView.StorePreferences(jsonObj, defaultPrefs);
					_addPreferenceEvents();
				}
				if (ThingView.initCB) {
					ThingView.initCB();
				}
			});
		}
	}
};

function FailedLoad() {
	window.alert("In FailedLoad");
}
var ThingView = (function() {
	var id = 0;
	var thingView;
	var isUpdated = false;
	var _currentSession = null;
	var _nextCanvasId = 0;
	var resourcePath = null;
	var loadedPreferences = {};
	var defaultPreferences = {};
	var s_fileversion = "0.19.176.0";
	var s_productversion = "0.19.176-LEXGYP";
	var s_productname = "ThingView 0.19";
	var __PDF_DOC = null;
	var __CANVAS = null;
	var __CANVAS_CTX = null;
	var __CURRENT_PAGE = 0;
	var __TOTAL_PAGES = 0;
	var __ZOOMSCALE = 1;
	var doCapture = false;
	var captureWrapper;
	var requestID = null;
	var iOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
	var edge = /Edge\/\d+/.test(navigator.userAgent);

	// Preference names
	var s_pref_nav_navmode = "Nav.NavMode";
	var s_pref_gen_filecache = "Gen.FileCache";
	var s_pref_gen_filecachesize = "Gen.FileCacheSize";

	var returnObj = {
		init: function(path, initCB) {
			ThingView.resourcePath = path;
			ThingView.initCB = initCB;
			if (ThingView.loaded) {
				ThingView._completeInit();
				ThingView.LoadPreferences(function(jsonObj, defaultPrefs) {
					if (jsonObj !== undefined) {
						ThingView.StorePreferences(jsonObj, defaultPrefs);
						_addPreferenceEvents();
					}
					if (ThingView.initCB) {
						ThingView.initCB();
					}
				});
			}
			else {
				var head = document.getElementsByTagName('head').item(0);
				ThingView.id = document.createElement("SCRIPT");
				var loaderLib;
				if ((typeof (WebAssembly) == "undefined") || (iOS == true) || (edge == true))
					loaderLib = "libthingview.js";
				else {
					loaderLib = "libthingview_wasm.js";
					ThingView.id.onerror = this.failedWasmLoad;
				}

				if (path) {
					var idx = path.lastIndexOf('/');
					if ((idx == -1) || (idx < path.length - 1))
						path += "/";
					loaderLib = path + loaderLib;
					ThingView.modulePath = path;
				}
				ThingView.id.src = loaderLib;
				head.appendChild(ThingView.id);

			}
		},
		failedWasmLoad: function() {
			console.log("Failed loading wasm so try asmjs");
			var head = document.getElementsByTagName('head').item(0);

			var id = document.createElement("SCRIPT");
			id.src = ThingView.modulePath + "libthingview.js";
			head.appendChild(id);

		},
		GetVersion: function() {
			return s_version;
		},

		GetDateCode: function() {
			return thingView.GetDateCode();
		},
		GetFileVersion: function() {
			return s_fileversion;
		},
		_completeInit: function() {
			thingView = Module.ThingView.GetThingView();
			if (requestID == null)
				requestID = requestAnimationFrame(_DoRender);
		},
		_setResourcePath: function(path) {
			thingView.SetResourcePath(path);
		},
		SetInitFlags: function(flags) {
			thingView.SetInitFlags(flags);
		},
		LoadImage: function(imagename) {
			thingView.LoadImage(imagename);
		},
		CreateSession: function(parentCanvasId) {
			var session = _createSession(parentCanvasId);
			if (ThingView.loadedPreferences) {
				if (Object.keys(ThingView.loadedPreferences).length > 0) {
					_applyPreferences(session, ThingView.loadedPreferences);
				}
			}
			return session;
		},
		SetHighMemoryUsageValue: function(megaBytes) {
			thingView.SetHighMemoryUsageValue(megaBytes);
		},
		CreateDocumentSession: function(parentCanvasId, callback) {
			return _createDocumentSession(parentCanvasId, callback);
		},
		IsDocumentSession: function(session) {
			return _IsDocumentSession(session);
		},
		ClearCanvas: function() {
			_ClearCanvas();
		},
		LoadDocument: function(val, documentName, callback) {
			_LoadDocument(val, documentName, callback);
		},
		LoadPrevPage: function(callback) {
			_LoadPrevPage(callback);
		},
		LoadNextPage: function(callback) {
			_LoadNextPage(callback);
		},
		LoadPage: function(callback, pageNo) {
			_LoadPage(callback, pageNo);
		},
		EnableSession: function(session) {
			_enableSession(session);
		},
		DeleteSession: function(session) {
			_deleteSession(session);
		},
		OpenPreferencesDialog: function() {
			window.open(ThingView.modulePath + "preferences.html", "ThingView Preferences", "width=500, height=240, status=no, toolbar=no, menubar=no, location=no");
		},
		StorePreferences: function(jsonObj, defaultPrefs) {
			try {
				if (!(jsonObj == undefined)) {
					ThingView.loadedPreferences = jsonObj;
				}
				if (!(defaultPrefs == undefined)) {
					ThingView.defaultPreferences = defaultPrefs;
				}
			} catch (e) {
				console.log("StorePreferences, exception: " + e);
			}
		},
		LoadPreferences: function(callbackFunc) {
			_loadPreferences(function(jsonObj, defaultPrefs) {
				callbackFunc(jsonObj, defaultPrefs);
			});
		},
		ApplyPreferences: function(jsonObj) {
			_applyPreferences(_currentSession, jsonObj);
		},
		SavePreferences: function(jsonObj) {
		},
		GetLoadedPreferences: function() {
			return _getLoadedPreferences();
		},
		CaptureCanvas: function(captureFunc) {
			doCapture = true;
			captureWrapper = captureFunc;
		}
	};
	return returnObj;// End of public functions

	function _DoRender(timeStamp) {
		var doRender = true;
		try {
			if ((doCapture === true) && (captureWrapper !== undefined) && (captureWrapper instanceof Function)) {
				doCapture = false;
				captureWrapper(function() {
					thingView.DoRender(timeStamp);
				});
			} else {
				thingView.DoRender(timeStamp);
			}
		} catch (err) {
			console.log("Javascript caught exception " + err);
			doRender = false;
		}
		if (doRender)
			requestID = requestAnimationFrame(_DoRender);
	}

	function _createSession(parentCanvasId) {
		var sessionCanvas = document.createElement("canvas");
		var parent = document.getElementById(parentCanvasId);
		sessionCanvas.id = parentCanvasId + "_CreoViewCanvas" + _nextCanvasId;
		_nextCanvasId++;
		sessionCanvas.setAttribute('style', "position: relative; width: 100%; height: 100%");

		var width = parent.clientWidth;
		var height = parent.clientHeight;

		sessionCanvas.width = width;
		sessionCanvas.height = height;
		parent.insertBefore(sessionCanvas, parent.childNodes[0]);

		sessionCanvas.oncontextmenu = function(e) {
			e.preventDefault();
			return false;
		};

		_currentSession = thingView.CreateSession(sessionCanvas.id);
		return _currentSession;
	}

	function _createDocumentSession(parentCanvasId, callback) {
		var head = document.getElementsByTagName('head').item(0);
		if (!document.getElementById("pdfjs")) {
			var script_pdf = document.createElement("SCRIPT");
			script_pdf.src = ThingView.modulePath + "pdfjs/pdf.js";
			script_pdf.id = "pdfjs";
			script_pdf.async = false;
			head.appendChild(script_pdf);

			script_pdf.onload = function() {
				var sessionCanvas = document.createElement("canvas");
				var context = sessionCanvas.getContext('2d');
				var parent = document.getElementById(parentCanvasId);
				sessionCanvas.id = parentCanvasId + "_CreoViewDocumentCanvas" + _nextCanvasId;
				_nextCanvasId++;
				sessionCanvas.setAttribute('style', "position: relative; width: 100%; height: 100%");
				var width = parent.clientWidth;
				var height = parent.clientHeight;
				sessionCanvas.width = width;
				sessionCanvas.height = height;
				parent.insertBefore(sessionCanvas, parent.childNodes[0]);
				sessionCanvas.oncontextmenu = function(e) {
					e.preventDefault();
					return false;
				};
				_currentSession = thingView.CreateSession(sessionCanvas.id);
				if (requestID)
					cancelAnimationFrame(requestID);
				callback(_currentSession);
			}
			return;
		}

		var sessionCanvas = document.createElement("canvas");
		var context = sessionCanvas.getContext('2d');
		var parent = document.getElementById(parentCanvasId);
		sessionCanvas.id = parentCanvasId + "_CreoViewDocumentCanvas" + _nextCanvasId;
		_nextCanvasId++;
		sessionCanvas.setAttribute('style', "position: relative; width: 100%; height: 100%");
		var width = parent.clientWidth;
		var height = parent.clientHeight;
		sessionCanvas.width = width;
		sessionCanvas.height = height;
		parent.insertBefore(sessionCanvas, parent.childNodes[0]);
		sessionCanvas.oncontextmenu = function(e) {
			e.preventDefault();
			return false;
		};
		_currentSession = thingView.CreateSession(sessionCanvas.id);
		if (requestID)
			cancelAnimationFrame(requestID);
		callback(_currentSession);
		return;
	}

	function _IsDocumentSession(session) {
		var retVal = false;
		if (_currentSession == session) {
			var session_html = Module.castToSession_html(session);
			var canvasId = session_html.GetCanvasName();
			retVal = canvasId.includes("_CreoViewDocumentCanvas");
		}
		return retVal;
	}
	function _ClearCanvas() {
		if (_IsDocumentSession(_currentSession)) {
			var session_html = Module.castToSession_html(_currentSession);
			var canvasId = session_html.GetCanvasName();
			var canvas = document.getElementById(canvasId);
			var context = canvas.getContext('2d');
			if (context) {
				context.clearRect(0, 0, canvas.width, canvas.height);
			}
		}
	}
	function _LoadDocument(val, documentName, callback) {
		if (_IsDocumentSession(_currentSession) && val) {
			var session_html = Module.castToSession_html(_currentSession);
			var canvasId = session_html.GetCanvasName();
			var canvas = document.getElementById(canvasId);
			var context = canvas.getContext('2d');
			if (context) {
				__CANVAS = canvas;
				__CANVAS_CTX = context;
				if (val) {
					showPDF(val, documentName, callback);
				}
			}
		}
	}
	function showPDF(val, documentName, callback) {
		PDFJS.getDocument({ data: val }).then(function(pdf_doc) {
			__PDF_DOC = pdf_doc;
			__TOTAL_PAGES = __PDF_DOC.numPages;
			// Show the first page
			showPage(1, documentName, callback);
		}).catch(function(error) {
			console.log("Javascript caught exception in showPDF : " + error.message);
			if (typeof callback === "function") callback(false, null);
		});
	}
	function showPage(page_no, documentName, callback) {
		__CURRENT_PAGE = page_no;
		// Fetch the page
		__PDF_DOC.getPage(page_no).then(function(page) {
			// As the canvas is of a fixed width we need to set the scale of the viewport accordingly
			var scale_required = __CANVAS.width / page.getViewport(1).width;
			//scale_required = scale_required * __ZOOMSCALE;
			// Get viewport of the page at required scale
			var viewport = page.getViewport(scale_required);
			// Set canvas height
			__CANVAS.height = viewport.height;
			var renderContext = {
				canvasContext: __CANVAS_CTX,
				viewport: viewport
			};
			console.log("Canvas Height/Widht Reset: " + __CANVAS.height + " / " + __CANVAS.width);
			var renderTask = page.render(renderContext);
			// Render the page contents in the canvas
			renderTask.promise.then(function() {
				if (typeof callback === "function") callback(true, documentName, __CURRENT_PAGE, __TOTAL_PAGES);
			}).catch(function(error) {
				console.log("Javascript caught exception in showPage : " + error.message);
				if (typeof callback === "function") callback(false, documentName, 0, 0);
			});
		});
	}
	function _LoadPrevPage(callback) {
		if (__CURRENT_PAGE != 1)
			showPage(--__CURRENT_PAGE, null, callback);
	}
	function _LoadNextPage(callback) {
		if (__CURRENT_PAGE != __TOTAL_PAGES)
			showPage(++__CURRENT_PAGE, null, callback);
	}
	function _LoadPage(callback, pageNo) {
		if ((pageNo > 0) && (pageNo <= __TOTAL_PAGES))
			showPage(pageNo, null, callback);
	}
	function _enableSession(session) {
		if (_currentSession != null) {
			_currentSession.Disable();
		}
		session.Enable();
		_currentSession = session;
	}

	function _deleteSession(session) {
		if (_currentSession == session) {
			_currentSession = null;
		}
		var session_html = Module.castToSession_html(session);
		var canvasId = session_html.GetCanvasName();
		var canvas = document.getElementById(canvasId);
		session.delete();
		session_html.delete();
		if (canvas != null && canvas.parentElement != null)
			canvas.parentElement.removeChild(canvas);

	}

	function _loadPreferences(callback) {
		callback();
	}

	function _applyPreferences(session, jsonObj) {
		try {
			if (session == null)
				return;
			if (jsonObj !== undefined) {
				for (key in jsonObj) {
					if (ThingView.loadedPreferences === undefined)
						ThingView.loadedPreferences = {};
					ThingView.loadedPreferences[key] = jsonObj[key];

					var fileCacheEnabled = false;
					var fileCacheSize = 0;

					if (key == s_pref_nav_navmode) {
						if (jsonObj[key] == "CREO_VIEW") {
							session.SetNavigationMode(Module.NavMode.CREO_VIEW);
							session.SetOrthographicProjection(1.0);
						}
						else if (jsonObj[key] == "CREO") {
							session.SetNavigationMode(Module.NavMode.CREO);
							session.SetOrthographicProjection(1.0);
						}
						else if (jsonObj[key] == "CATIA") {
							session.SetNavigationMode(Module.NavMode.CATIA);
							session.SetOrthographicProjection(1.0);
						}
						else if (jsonObj[key] == "EXPLORE") {
							session.SetNavigationMode(Module.NavMode.EXPLORE);
							session.SetPerspectiveProjection(60.0);
						}
						else if (jsonObj[key] == "MOCKUP")
							session.SetNavigationMode(Module.NavMode.MOCKUP);
						else if (jsonObj[key] == "VUFORIA")
							session.SetNavigationMode(Module.NavMode.VUFORIA);
						else if (jsonObj[key] == "VUFORIA_NOPICK")
							session.SetNavigationMode(Module.NavMode.VUFORIA_NOPICK);
					} else if (key == s_pref_gen_filecache) {
						if (jsonObj[key] === true)
							fileCacheEnabled = true;
					} else if (key == s_pref_gen_filecachesize) {
						fileCacheSize = jsonObj[key];
					}
					//if (fileCacheEnabled)
					//  session.EnableFileCache(fileCacheSize);
				}
			}
		} catch (e) {
			console.log(e);
		}
	}

	function _getLoadedPreferences() {
		if (ThingView.loadedPreferences) {
			if (Object.keys(ThingView.loadedPreferences).length > 0) {
				return ThingView.loadedPreferences;
			}
		}
		return {};
	}

})();

function _addPreferenceEvents() {
	var re = new RegExp("version\\/(\\d+).+?safari", "i");
	var match = navigator.userAgent.match(re);
	if (!match) {
		document.addEventListener("keydown", function(event) {
			if (event.shiftKey && event.keyCode == 80 /*P*/) {
				ThingView.OpenPreferencesDialog();
			}
		}, false);
	}

	window.addEventListener("storage", function(event) {
		if (event.key == 'msgPref') {
			if (event.newValue) {
				var message = JSON.parse(event.newValue);
				ThingView.ApplyPreferences(message);
				ThingView.SavePreferences(ThingView.GetLoadedPreferences());
			}
		} else if (event.key == 'resetPref') {
			if (event.newValue && event.newValue == 'true') {
				ThingView.loadedPreferences = {};
				ThingView.ApplyPreferences(ThingView.defaultPreferences);
				ThingView.SavePreferences(ThingView.GetLoadedPreferences());
			}
		} else if (event.key == 'msgReady') {
			if (event.newValue && event.newValue == 'true') {
				localStorage.setItem('msgCurPref', JSON.stringify(ThingView.loadedPreferences));
				localStorage.removeItem('msgCurPref');
				localStorage.setItem('msgDefPref', JSON.stringify(ThingView.defaultPreferences));
				localStorage.removeItem('msgDefPref');
			}
		}
	}, false);
}

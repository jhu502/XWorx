var StringPrimitive = {
	getBaseType: function() {
		return "STRING";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'STRING:' + _E(v);
	}
};

var NumberPrimitive = {
	getBaseType: function() {
		return "NUMBER";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'NUMBER:' + _E(v);
	}
};

var IntegerPrimitive = {
	getBaseType: function() {
		return "INTEGER";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'INTEGER:' + _E(v);
	}
};

var LongPrimitive = {
	getBaseType: function() {
		return "LONG";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'LONG:' + _E(v);
	}
};

var BooleanPrimitive = {
	getBaseType: function() {
		return "BOOLEAN";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'BOOLEAN:' + _E(v);
	}
};

var DataTimePrimitive = {
	getBaseType: function() {
		return "DATATIME";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'DATATIME:' + _E(v);
	}
};

var JsonPrimitive = {
	getBaseType: function() {
		return "JSON";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'JSON:' + _E(v);
	}
};

var PasswordPrimitive = {
	getBaseType: function() {
		return "PASSWORD";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'PASSWORD:' + _E(v);
	}
};

var InfoTablePrimitive = {
	getBaseType: function() {
		return "INFOTABLE";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'INFOTABLE:' + _E(v);
	}
};

var NoThingPrimitive = {
	getBaseType: function() {
		return "NOTHING";
	},
	getDecode: function(v) {
		return _D(v);
	},
	getEncode: function(v) {
		return 'NOTHING:' + _E(v);
	}
};

var BaseType = {
	'STRING': StringPrimitive,
	'NUMBER': NumberPrimitive,
	'INTEGER': IntegerPrimitive,
	'LONG': LongPrimitive,
	'BOOLEAN': BooleanPrimitive,
	'DATETIME': DataTimePrimitive,
	'JSON': JsonPrimitive,
	'PASSWORD': PasswordPrimitive,
	'INFOTABLE': InfoTablePrimitive,
	'NOTHING': NoThingPrimitive,

	// STRING:SGVsbG86OQ==
	decode: function(v) {
		if (isBlank(v)) {
			return '';
		}
		console.log(v);
		let x = v.indexOf(':');
		let t = v.substring(0, x);
		let _v = v.substring(x + 1);
		return BaseType[t].getDecode(_v);
	},
	encode: function(t, v) {
		return BaseType[t].getEncode(v);
	}
};

/**
 * FlameThingClient
 */
var FlameThingClient = FlameThingClient || class {
	constructor() {
		this.socket = {};
		this.bindMap = new HashMap();
		this.callback = null;
		this.onOpen = function(event) {
			console.log("Connection is open.");
		};
		this.onMessage = function(message) {
			try {
				let receive = this.parseMessage(message);
				console.log(receive);
				if (!isBlank(receive)) {
					if (!isBlank(receive.method)) {
						let localThing = this.bindMap.get(receive.identity);
						let _result = localThing[receive.method](receive.params);
						this.rfcResult(receive.id, receive.jsonrpc, _result);
					} else if (!isBlank(receive.error)) {
						alert(_D(receive.error.message));
					} else {
						let result = _D(receive.result);
						if (this.callback != null) {
							(this.callback)(result);
						}
					}
				}
			} catch (e) {
				alert(e);
			}
		};
		this.onClose = function() {
			console.log("Connection is close.");
		};
		this.onError = function() {
			console.log("Connection error.");
		};
	}
	connectServer(url) {
		let $this = this;
		this.id = 0;
		this.socket = new WebSocket(url);

		this.socket.onopen = function() {
			$this.onOpen();
		};

		this.socket.onmessage = function(message) {
			$this.onMessage(message);
		};

		this.socket.onclose = function() {
			$this.onClose();
		};

		this.socket.onerror = function() {
			$this.onError();
		}
	}
	binding(thing, object) {
		this.invoke("Flamethrower:Platform", 'binding', { 'identity': BaseType.encode('STRING', thing) });
		this.bindMap.put(thing, object);
	}
	invoke(thing, method, params, func) {
		this.callback = func;

		let content = {
			'id': this.id++,
			'jsonrpc': '2.0',
			'method': thing + '.' + method,
			'params': params
		};
		let message = JSON.stringify(content);
		this.socket.send(message);
	}
	rfcResult(id, version, result) {
		if (result == null) {
			result = '';
		}
		let content = {
			'id': id,
			'jsonrpc': version,
			'result': _E(result)
		};
		let message = JSON.stringify(content);
		this.socket.send(message);
	}
	parseMessage(message) {
		if (isBlank(message) || isBlank(message.data))
			return;
		let o = JSON.parse(message.data);
		if (!isBlank(o.method)) {
			let i = o.method.lastIndexOf('.');
			let params = [];
			for (var a in o.params) {
				var v = o.params[a];
				params.push(BaseType.decode(v));
			}
			
			return {
				id: o.id,
				jsonrpc: o.jsonrpc,
				identity: o.method.substring(0, i),
				method: o.method.substring(i + 1),
				params: params
			}
		} else {
			if (!isBlank(o.error)) {
				return {
					id: o.id,
					jsonrpc: o.jsonrpc,
					error: {
						code: o.error.code,
						message: o.error.message
					}
				}
			} else {
				return {
					id: o.id,
					jsonrpc: o.jsonrpc,
					result: o.result
				}
			}
		}
	}
}

/**
 * HashMap
 */
var HashMap = HashMap || class {
	constructor() {
		this.map = {};
	}
	put(key, value) {// 向Map中增加元素（key, value)
		this.map[key] = value;
	}
	get(key) { // 获取指定Key的元素值Value，失败返回Null
		if (this.map.hasOwnProperty(key)) {
			return this.map[key];
		}
		return null;
	}
	remove(key) { // 删除指定Key的元素，成功返回True，失败返回False
		if (this.map.hasOwnProperty(key)) {
			return delete this.map[key];
		}
		return false;
	}
	removeAll() { // 清空HashMap所有元素
		this.map = {};
	}
	keySet() { // 获取Map中所有KEY的数组（Array）
		var _keys = [];
		for (var i in this.map) {
			_keys.push(i);
		}
		return _keys;
	}
}

function getQueryParam(param) {
	let query = window.location.search.substring(1);
	let vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == param) {
			return pair[1];
		}
	}
	return (false);
};

function isBlank(o) {
	if (o == null || o == "" || typeof (o) == "undefined") {
		return true;
	} else if (o instanceof Object) {
		return false;
	} else if (typeof o == 'string') {
		let s = o.replace(/^\s+/g, "").replace(/\s+$/g, "");
		if (s == "") {
			return true;
		}
	} else {
		return false;
	}
};

String.prototype.startWith = String.prototype.startWith || function(s) {
	if (s == null || s == "" || this.length == 0 || s.length > this.length)
		return false;
	if (this.substr(0, s.length) == s)
		return true;
	return false;
}
String.prototype.endWith = String.prototype.endWith || function(s) {
	if (s == null || s == "" || this.length == 0 || s.length > this.length)
		return false;
	if (this.substring(this.length - s.length) == s)
		return true;
	else
		return false;
}
String.prototype.trim = String.prototype.trim || function() {
	return this.replace(/^\s\s*/, "").replace(/\s\s*$/, "");
};

/** 对字符串进行Base64解码，为了减少代码量，对$.base64.decode()进行包装
 * @param o
 * @returns
 */
function _D(o) {
	if (isBlank(o)) {
		return '';
	}
	return $.base64.decode(o);
};
/** 对字符串进行Base64编码，为了减少代码量，对$.base64.encode()进行包装
 * @param o
 * @returns
 */
function _E(o) {
	return $.base64.encode(o);
};

jQuery.base64 = (function($) {
	var _PADCHAR = "=", _ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", _VERSION = "1.1";
	function _getbyte64(s, i) {
		var idx = _ALPHA.indexOf(s.charAt(i));

		if (idx === -1) {
			throw "Cannot decode base64";
		}

		return idx;
	}

	function _decode_chars(y, x) {
		while (y.length > 0) {
			var ch = y[0];
			if (ch < 0x80) {
				y.shift();
				x.push(String.fromCharCode(ch));
			} else if ((ch & 0x80) == 0xc0) {
				if (y.length < 2)
					break;
				ch = y.shift();
				var ch1 = y.shift();
				x.push(String.fromCharCode(((ch & 0x1f) << 6) + (ch1 & 0x3f)));
			} else {
				if (y.length < 3)
					break;
				ch = y.shift();
				var ch1 = y.shift();
				var ch2 = y.shift();
				x.push(String.fromCharCode(((ch & 0x0f) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f)));
			}
		}
	}

	function _decode(s) {
		var pads = 0, i, b10, imax = s.length, x = [], y = [];

		s = String(s);

		if (imax === 0) {
			return s;
		}

		if (imax % 4 !== 0) {
			throw "Cannot decode base64";
		}

		if (s.charAt(imax - 1) === _PADCHAR) {
			pads = 1;

			if (s.charAt(imax - 2) === _PADCHAR) {
				pads = 2;
			}

			// either way, we want to ignore this last block
			imax -= 4;
		}

		for (i = 0; i < imax; i += 4) {
			b10 = (_getbyte64(s, i) << 18) | (_getbyte64(s, i + 1) << 12) | (_getbyte64(s, i + 2) << 6) | _getbyte64(s, i + 3);
			y.push(b10 >> 16);
			y.push((b10 >> 8) & 0xff);
			y.push(b10 & 0xff);
			_decode_chars(y, x);
		}
		switch (pads) {
			case 1:
				b10 = (_getbyte64(s, i) << 18) | (_getbyte64(s, i + 1) << 12) | (_getbyte64(s, i + 2) << 6);
				y.push(b10 >> 16);
				y.push((b10 >> 8) & 0xff);
				break;

			case 2:
				b10 = (_getbyte64(s, i) << 18) | (_getbyte64(s, i + 1) << 12);
				y.push(b10 >> 16);
				break;
		}
		_decode_chars(y, x);
		if (y.length > 0)
			throw "Cannot decode base64";
		return x.join("");
	}

	function _get_chars(ch, y) {
		if (ch < 0x80)
			y.push(ch);
		else if (ch < 0x800) {
			y.push(0xc0 + ((ch >> 6) & 0x1f));
			y.push(0x80 + (ch & 0x3f));
		} else {
			y.push(0xe0 + ((ch >> 12) & 0xf));
			y.push(0x80 + ((ch >> 6) & 0x3f));
			y.push(0x80 + (ch & 0x3f));
		}
	}

	function _encode(s) {
		if (arguments.length !== 1) {
			throw "SyntaxError: exactly one argument required";
		}

		s = String(s);
		if (s.length === 0) {
			return s;
		}

		// s = _encode_utf8(s);
		var i, b10, y = [], x = [], len = s.length;
		i = 0;
		while (i < len) {
			_get_chars(s.charCodeAt(i), y);
			while (y.length >= 3) {
				var ch1 = y.shift();
				var ch2 = y.shift();
				var ch3 = y.shift();
				b10 = (ch1 << 16) | (ch2 << 8) | ch3;
				x.push(_ALPHA.charAt(b10 >> 18));
				x.push(_ALPHA.charAt((b10 >> 12) & 0x3F));
				x.push(_ALPHA.charAt((b10 >> 6) & 0x3f));
				x.push(_ALPHA.charAt(b10 & 0x3f));
			}
			i++;
		}

		switch (y.length) {
			case 1:
				var ch = y.shift();
				b10 = ch << 16;
				x.push(_ALPHA.charAt(b10 >> 18) + _ALPHA.charAt((b10 >> 12) & 0x3F) + _PADCHAR + _PADCHAR);
				break;

			case 2:
				var ch1 = y.shift();
				var ch2 = y.shift();
				b10 = (ch1 << 16) | (ch2 << 8);
				x.push(_ALPHA.charAt(b10 >> 18) + _ALPHA.charAt((b10 >> 12) & 0x3F) + _ALPHA.charAt((b10 >> 6) & 0x3f) + _PADCHAR);
				break;
		}

		return x.join("");
	}

	return {
		decode: _decode,
		encode: _encode,
		VERSION: _VERSION
	};

}(jQuery));

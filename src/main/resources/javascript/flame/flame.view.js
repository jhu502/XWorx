var flame = flame || {};
flame.view = flame.view || {};

/**
 * ThingView执行环境主要有4个文件：
 * 1.libthingview.js: 是一下开源工具的打包，例如：Promise、WebAssembly、
 */
flame.view.Thingview = flame.view.Thingview || class {
	constructor(baseUrl, divId) {
		this.baseUrl = baseUrl;
		this.divId = divId;
		this.logger = console;
		this.modelMap = new Map();
	}
	/**
	 * 用为header创建script元素的方式，加载thingview.js到当前页面
	 */
	loadThingViewJS = function() {
		return new Promise((resolve, reject) => {
			const thingview_js = document.getElementById("thingview.js");
			if (!thingview_js) {
				const script = document.createElement('script');
				script.id = 'thingview.js';
				script.type = 'text/javascript';
				script.src = `${this.baseUrl}/thingview.js`;
				script.onload = () => resolve();
				script.onerror = () => reject();
				document.getElementsByTagName('head')[0].appendChild(script);
			} else {
				resolve();
			}
		});
	}
	/**
	 * 初始化ThingView执行环境：
	 * 1.调用loadThingViewJS去加载thingview.js;
	 * 2.ThingView加载libthingview.js、libthingview_wasm.js进页面；
	 * 3.libthingview_wasm.js加载libthingview_wasm.wasm进浏览器，并为其生成js调用接口；
	 * 4.创建ThingView的Session；
	 */
	initializeThingView() {
		return new Promise((resolve, reject) => {
			this.loadThingViewJS().then(() => {
				if (this.successInit === true) {
					resolve();
					return;
				}
				if (this.failedInit === true) {
					reject();
				}
				try {
					ThingView.init(this.baseUrl, () => {
						this.successInit = true;
						this.createThingViewSession();
						resolve();
					});
				} catch (e) {
					this.failedInit = false;
					reject();
					this.logger.error('error starting ThingView', e);
				}
			}, () => {
				reject();
			});
		});
	}
	createThingViewSession() {
		ThingView.SetDefaultSystemPreferences(window.Module.ApplicationType.THINGVIEW);
		this.app = ThingView.CreateCVApplication(this.divId);
		this.session = this.app.GetSession();
		this.shapeScene = this.session.MakeShapeScene(true);
		/** this.shapeScene.AddInstanceSelectionObserver(this.selectionObserver); */
		/** this.shapeScene.SetSelectionFilter(window.Module.SelectionFilter.DISABLED, window.Module.SelectionList.PRESELECTION); */
		/** this.shapeScene.SetSelectionFilter(window.Module.SelectionFilter.DISABLED, window.Module.SelectionList.PRIMARYSELECTION); */
		const element = this.querySelector(document, `#${this.divId}`);
		const canvas = this.querySelector(element, 'canvas');
		this.shapeView = this.shapeScene.MakeShapeView(canvas.id, true);
		this.shapeView.ShowGnomon(true);
		this.shapeView.ShowSpinCenter(true);
		this.shapeView.SetSelectionHighlightStyle(window.Module.SelectionList.PRIMARYSELECTION, window.Module.HighlightStyle.FILL);
		this.shapeView.SetSelectionHighlightStyle(window.Module.SelectionList.PRESELECTION, window.Module.HighlightStyle.FILL);
	}
	loadStructureNode(uri, cad3d, p) {
		if (!cad3d)
			return p;

		const location = [];
		if (p) {
			location[0] = p[0] + cad3d.translation[0];
			location[1] = p[1] + cad3d.translation[1];
			location[2] = p[2] + cad3d.translation[2];
		} else {
			location[0] = cad3d.translation[0];
			location[1] = cad3d.translation[1];
			location[2] = cad3d.translation[2];
		}
		if (!cad3d.filename)
			return location;

		const srcURI = uri + "/" + cad3d.filename;
		if (!this.modelMap.has(srcURI)) {
			this.session.LoadStructNodeWithURL(srcURI, true, (node, errors) => {
				if (node) {
					const model = this.shapeScene.MakeModel();
					model.LoadStructNode(node, Module.AutoloadBehaviour.CREATE_GEOMETRY, true, (success, isStructure, errorStack) => {
						if (isStructure) {
							this.modelMap.set(model.GetSourceURL(), model);
						}
					});

					const rotation = this.matrixToEulerAngles(cad3d.rotation);
					model.SetOrientation(rotation[0], rotation[1], rotation[2]); //使用欧拉角去旋转模型
					model.SetPosition(location[0], location[1], location[2]); //设置平移位置
					/** model.SetScaleXYZ(1,1,1); //设置缩放比例，(1,1,1)不进行缩放 */
					return location;
				} else {
					this.logger.error("Failed to load structNode");
				}
			});
		}
	}
	load3dModelNode(srcURI) {
		if (this.modelMap.has(srcURI))
			return;
			
		this.session.LoadStructNodeWithURL(srcURI, true, (node, errors) => {
			if (node) {
				const model = this.shapeScene.MakeModel();
				model.LoadStructNode(node, Module.AutoloadBehaviour.CREATE_GEOMETRY, true, (success, isStructure, errorStack) => {
					if (isStructure) {
						this.modelMap.set(model.GetSourceURL(), model);
					}
				});
				return model;
			} else {
				this.logger.error("Failed to load structNode");
			}
		});
	}
	reload3dModelNode(srcURI) {
		this.unload3dModelNode(srcURI);
		this.load3dModelNode(srcURI);
	}
	unloadAll3dModel() {
		if (!this.modelMap) 
			return;
			
		const $this = this;
		this.modelMap.forEach(function(value, key) {
			value.Unload();
			$this.modelMap.delete(key);
		});
	}
	unload3dModelNode(srcURL) {
		if (this.modelMap.has(srcURL)) {
			this.modelMap.get(srcURL).Unload();
			this.modelMap.delete(srcURL);
		}
	}
	logErrorStack(errorStack) {
		for (let i = 0; i < errorStack.size(); i += 1) {
			const error = errorStack.get(i);
			this.logger.error(`ThingView: ${error.number} ${error.name} ${error.message}`);
		}
	}
	clearSession() {
		if (this.session) {
			if (this.session.HasProgress()) {
				this.session.CancelPendingDownloads();
			}
			if (this.model) {
				this.model.delete();
				this.model = undefined;
			}
			if (this.shapeScene) {
				this.shapeScene.ShowProgress(false);
				this.shapeScene.ShowTicker(false);
				this.shapeScene.DeleteAllLocatorMarkers();
				if (this.shapeScene.RemoveAllModels)
					this.shapeScene.RemoveAllModels();
			}
			this.session.RemoveAllLoadSources();
			if (this.session.RemoveAllModels)
				this.session.RemoveAllModels(false);
		}
		this.csysItems = [];
		ThingView.ClearCanvas();
	}
	matrixToEulerAngles(matrix) {
		if (!matrix)
			return [0, 0, 0];

		const R00 = matrix[0], R01 = matrix[1], R02 = matrix[2];
		const R10 = matrix[3], R11 = matrix[4], R12 = matrix[5];
		const R20 = matrix[6], R21 = matrix[7], R22 = matrix[8];

		const SY = Math.sqrt(R00 * R00 + R10 * R10);

		const singular = SY < 1e-6;

		let x, y, z;
		if (!singular) {
			x = Math.atan2(R21, R22);
			y = Math.atan2(-R20, SY);
			z = Math.atan2(R10, R00);
		} else {
			x = Math.atan2(-R12, R11);
			y = Math.atan2(-R20, SY);
			z = 0;
		}
		x = x * 180.0 / Math.PI;
		y = y * 180.0 / Math.PI;
		z = z * 180.0 / Math.PI;

		return [x, y, z];
	}
	eulerAnglesToMatrix(x, y, z) {
		const cx = Math.cos(x);
		const sx = Math.sin(x);
		const cy = Math.cos(y);
		const sy = Math.sin(y);
		const cz = Math.cos(z);
		const sz = Math.sin(z);

		return [cy * cx, cy * sx * sz - sy * cz, sy * sz + cy * cz * sx, sy * cx, cy * cz + sy * sz * sx, sx * sy * cz - cy * sz, -sx, cx * sz, cx * cz];
	}
	querySelector(s, t) {
		const r = s.querySelector(t);
		if (!r)
			throw "Element:" + t + " is not found.";

		return r;
	}
}

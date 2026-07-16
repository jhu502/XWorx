class y extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"});const o=this.constructor.componentTemplate;o&&(this.shadowRoot.innerHTML=o);const t=Object.keys(Object.getPrototypeOf(this)).filter(e=>e!=="constructor"&&typeof this[e]=="function");for(const e of t){const s=this[e];s&&(this[e]=s.bind(this))}}static get componentTemplate(){return""}static mount(o,t){customElements.get(o)===void 0&&customElements.define(o,this),customElements.whenDefined(o).then(()=>{});const s=document.querySelector(o);if(s&&t&&typeof t=="object")for(const i of Object.keys(t))typeof t[i]=="function"?s[i]=t[i].bind(s):s[i]=t[i];return s}}class x extends y{constructor(){super();const o=this;this.vueApp=Vue.createApp({setup(){typeof o.onSetup=="function"&&o.onSetup(this)},beforeCreate(){typeof o.onBeforeCreate=="function"&&o.onBeforeCreate(this)},created(){const e=Object.keys(this).filter(s=>typeof this[s]=="function").map(s=>this[s]);if(e){for(const s of e)if(s.name.startsWith("bound ")){const i=s.name.substr(6);o[i]==null&&(o[i]=this[i])}}typeof o.onCreated=="function"&&o.onCreated(this)},mounted(){typeof o.onSuccess=="function"&&o.onSuccess(this)},data(){return o.properties===void 0?{self:o}:o.properties},methods:o.methods===void 0?{}:o.methods,...o.setting===void 0?{}:o.setting},this.vueConfig);const t=this.shadowRoot.querySelector("xui-root");t?this.vueApp.mount(t):console.error("XWX Component must take <xui-root> as root element.")}get properties(){return{}}get methods(){return{}}get vueConfig(){return{compilerOptions:{isCustomElement:o=>o.startsWith("xui-")}}}get setting(){return{shadowRoot:!0}}getVueApp(){return this.vueApp}}class m{constructor(o){this.pallet={},this.movable=!1,this.offset={x:0,y:0},o&&Object.assign(this,o),this.sharp=document.createElement("div"),this.sharp.style.cursor="hand",this.sharp.style.position="absolute",this.sharp.style.fontSize="12px",this.sharp.style.border="1px solid whitesmoke",this.sharp.style.backgroundColor="#78c6eb",this.sharp.style.display="none",this.sharp.style.padding="2px",this.sharp.onselectstart=()=>!1,document.body.appendChild(this.sharp)}getPallet(){return this.pallet}pickPallet(o){}onDraggingStart(o){if(this.pallet=this.pickPallet(o),this.pallet){const t=this.getPosition(o.target?.parentNode);this.sharp.style.display="block",this.sharp.style.left=t.x+"px",this.sharp.style.top=t.y+"px",this.sharp.style.minWidth="135px",this.sharp.style.height="22px",this.sharp.style.background="linear-gradient(90deg,rgba(220,137,255,1)0%,rgba(162,152,255,1)52%,rgba(83,160,253,1)100%)",this.pallet.display?(this.sharp.innerHTML=this.pallet.display,this.sharp.setAttribute("name",this.pallet.id+"~"+this.pallet.name)):(this.sharp.innerHTML=this.pallet.name,this.sharp.setAttribute("name",this.pallet.id+"~"+this.pallet.name)),this.offset.x=o.clientX-t.x,this.offset.y=o.clientY-t.y,this.movable=!0}}onDraggingMove(o){if(!this.movable)return;this.sharp.style.top=o.clientY-this.offset.y+"px",this.sharp.style.left=o.clientX-this.offset.x+2+"px";const t=document.querySelectorAll("td:hover");for(const e of t)e&&e.classList.contains("cell")&&(e.style.backgroundColor="#ffccff")}onDraggingPlace(o){this.movable&&(this.movable=!1,this.sharp.style.display="none",typeof this.placeDragging=="function"&&this.placeDragging(o))}getPosition(o){if(!o)return{x:0,y:0};let t=0,e=0;if(o.offsetParent)for(;o.offsetParent;)t+=o.offsetLeft,e+=o.offsetTop,o=o.offsetParent;else o.x?t+=o.x:o.y&&(e+=o.y);return{x:t,y:e}}}const g=class g extends x{static get observedAttributes(){return[]}constructor(){super(),this.meshDrag=new m,this.meshDrag.placeDragging=o=>{const t=this.meshDrag.getPallet();if(!t)return;const e=this.shadowRoot.elementFromPoint(o.clientX,o.clientY);if(!e)return;const s=e.classList.contains("cell")?e:e.closest("span.cell");if(!s)return;const i=s.getAttribute("idx"),l=s.getAttribute("row"),r=s.getAttribute("col"),n=this.getGrid(i);if(!n||n.rows.some(u=>Object.keys(u).some(h=>u[h]!==void 0&&u[h].value===t.name)))return;const c=n.rows[parseInt(l)];if(!c)return;const p=c[r];p&&(p.value=t.name,p.display=t.display)}}static get componentTemplate(){return`<style>
	:host{position:relative;display:inline-block}
    .meshLayout{font-size:12px;border:1px solid lightgrey;border-radius:5px;border-spacing:0px;border-collapse:separate;}
    .meshLayout span.focus{background-color:blanchedalmond;}
    .meshLayout span.selected{background-color:#b3d9ff;box-shadow:inset 0 0 0 2px #1a73e8;}
    .meshLayout span:hover{background-color:blanchedalmond;}
    .meshLayout span.cell{display:block;padding-left:2px;width:100%;height:20px;}
    .meshLayout td{height:20px;font-size:12px;padding-left:2px;border-bottom:1px solid lightgrey;border-right:1px solid lightgrey;cursor:default;}
    .meshLayout tbody tr{display:flex;}
    .meshLayout .col-slot{flex:1;min-width:80px;}
    .meshLayout .row-num{width:24px;flex:none;}
    .meshLayout .row-tools{width:36px;flex:none;}
    .meshLayout .row-tools button{width:14px;height:18px;padding:0;text-align:center;line-height:16px;border:1px solid lightgrey;border-radius:3px;background-color:aliceblue;}
    .meshLayout .row-tools button:hover{border:1px solid #03638d;}
    .meshLayout .toolbar .tool-btn{width:16px;height:16px;vertical-align:middle;cursor:pointer;border:1px solid #ccc;border-radius:3px;padding:2px;background:aliceblue;}
    .meshLayout .toolbar .tool-btn:hover{border:1px solid #03638d;}
    .meshLayout .toolbar{background-color:#80cefc;border-bottom:1px solid lightgrey !important;}
    .meshLayout th{font-size:12px;font-weight:normal;border-bottom:2px solid black;background-color:#80cefc;padding:2px 4px;}
</style>${g.TEMPLATE}`}get properties(){return{self:this,movable:!1,movedDiv:null,meshLayout:null,propertyMap:new Map,layout:{grids:[{head:[],rows:[]}]},cellSelection:{},i18n:window.i18n||{}}}get methods(){const o=this;return{loadLayoutTable(t){t&&(this.layout=t)},saveLayoutEdit(){const t=JSON.stringify(this.layout);typeof o.onSaveLayout=="function"&&o.onSaveLayout(JSON.parse(t))},setGridCellValue(t,e){if(!t)return;const s=t.split("-"),i=this.getGrid(s[0]);if(i){const l=i.rows[s[1]];if(l){const r=l[parseInt(s[2])];r.value=e,r.display=e}}},cleanBackColor(){const t=this.getMeshLayout().find("td.cell");for(const e of t)e.style.backgroundColor=null,e.style.cursor=null},getPropertyRow(t){if(!o.isDom(t))return;let e=null;if(t.classList.contains("datagrid-cell")&&(e=t.parentNode?.parentNode),e){const s=e.childNodes,i={oid:s[0].innerText,display:s[1].innerText,name:s[2].innerText};return this.propertyMap.set(i.oid,i),i}},isMeshLayout(t){return null},getMeshLayout(){return this.meshLayout==null&&(this.meshLayout=jQuery("table[name='meshLayout']")),this.meshLayout},delLayoutRow(t){if(t){const e=t.split("-"),s=this.getGrid(e[0]);s&&s.rows.splice(parseInt(e[1]),1)}},addLayoutRow(t){if(t){const e=t.split("-"),s=this.getGrid(e[0]);s&&s.rows.splice(parseInt(e[1])+1,0,[{colspan:1,clazz:"",style:"",rowCount:1,value:"",display:""},{colspan:1,clazz:"",style:"",rowCount:1,value:"",display:""},{colspan:1,clazz:"",style:"",rowCount:1,value:"",display:""},{colspan:1,clazz:"",style:"",rowCount:1,value:"",display:""}])}},editStyle(t){const e=this.getGrid(t);if(!e)return;if(!e.selected?.length){alert("未选中任何单元格");return}const[s,i]=e.selected[0].split("-").map(Number),l=e.rows[s]?.[i];if(!l)return;const r=document.createElement("x-window");r.id="xui-cell-dlg",r.title="编辑单元格",r.setAttribute("x-options","park:'center',minimize:false,maximize:false,resize:false,destroy:true"),r.style.cssText="width:420px;height:360px",r.content(`
                    <div style="padding:12px 16px 16px;">
                        <label style="font-size:13px;color:#555;display:block;">Cell 类型：</label>
                        <select id="xui-uitype-select" onchange="((el)=>{
                            const ri=el.querySelector('#xui-rows-input');
                            ri.disabled=this.value==='Widget';
                            if(this.value==='Widget') {
                                ri.value=1;
                            }
                        })(this.closest('div'))" style="width:100%;height:28px;margin-top:4px;padding:4px 8px;border:1px solid #ccc;border-radius:3px;box-sizing:border-box;">
                            <option value="Widget" ${l.uiType!=="Tablet"?"selected":""}>Widget</option>
                            <option value="Tablet" ${l.uiType==="Tablet"?"selected":""}>Tablet</option>
                        </select>
                        <label style="font-size:13px;color:#555;display:block;margin-top:10px;">Cell 内容（属性名 或 表格类名）：</label>
                        <input id="xui-value-input" value="${l.value||""}" placeholder="属性字段名 或 xw.auths.builder.GroupListTableBuilder" style="width:100%;height:28px;margin-top:4px;padding:4px 8px;border:1px solid #ccc;border-radius:3px;box-sizing:border-box;"/>
                        <label style="font-size:13px;color:#555;display:block;margin-top:10px;">CSS 样式（应用到单元格）：</label>
                        <input id="xui-style-input" value="${l.style||""}" placeholder="color:red;font-weight:bold;" style="width:100%;height:28px;margin-top:4px;padding:4px 8px;border:1px solid #ccc;border-radius:3px;box-sizing:border-box;"/>
                        <label style="font-size:13px;color:#555;display:block;margin-top:10px;">行数设置（1: 单行input，>1: 多行textarea）：</label>
                        <input id="xui-rows-input" type="number" min="1" value="${l.rowCount||1}" ${l.uiType=="Tablet"?"disabled":""} style="width:100%;height:28px;margin-top:4px;padding:4px 8px;border:1px solid #ccc;border-radius:3px;box-sizing:border-box;"/>
                        <div style="display:flex;justify-content:flex-end;gap:8px;margin-top:14px;">
                            <button style="padding:6px 18px;border:1px solid #ccc;border-radius:3px;background:#f5f5f5;cursor:pointer;" onclick="this.closest('x-window')?.close()">取消</button>
                            <button style="padding:6px 18px;border:1px solid #03638d;border-radius:3px;background:#03638d;color:#fff;cursor:pointer;" onclick="
                                (function(el){
                                    const w=el.closest('x-window');
                                    const us=w.querySelector('#xui-uitype-select');
                                    const vi=w.querySelector('#xui-value-input');
                                    const si=w.querySelector('#xui-style-input');
                                    const ri=w.querySelector('#xui-rows-input');
                                    const val=vi.value.trim(), d=w.data;
                                    d.cell.value=val||'';
                                    if(us.value === 'Tablet'){
                                        d.cell.display=val.substring(val.lastIndexOf('.')+1)||val;
                                        d.cell.uiType='Tablet';
                                    }else{
                                        d.cell.display=val||'';
                                        d.cell.uiType=undefined;
                                    }
                                    d.cell.style=si.value.trim()||null;
                                    d.cell.rowCount=parseInt(ri.value)||1;
                                    const row=d.grid.rows[d.hRow];
                                    if(row) row[d.hCol]=d.cell;
                                    w.close();
                                })(this)
                            ">确认</button>
                        </div>
                    </div>`,{cell:l,grid:e,hRow:s,hCol:i}),r.open()},addGrid(){const t=this.layout.grids[0];if(!t){alert("暂无参考 Grid");return}const e=JSON.parse(JSON.stringify(t));for(const l of e.head)l.value="",Object.prototype.hasOwnProperty.call(e,l.name)&&(e[l.name]="");for(const l of e.rows)for(const r of l)r.value="",r.display="";let s="";for(let l=0;l<t.head.length;l++){const r=t.head[l];s+=`
                        <label style="font-size:13px;color:#555;display:block;margin-top:${l===0?"0":"10px"};">${r.label}</label>
                        <input id="xui-grid-${r.name}" style="width:100%;height:28px;margin-top:4px;padding:4px 8px;border:1px solid #ccc;border-radius:3px;box-sizing:border-box;"/>`}const i=document.createElement("x-window");i.id="xui-add-grid-dlg",i.title="新增 Grid",i.setAttribute("x-options","park:'center',minimize:false,maximize:false,resize:false,destroy:true"),i.style.cssText="width:400px;height:360px",i.content(`
                    <div style="padding:12px 16px 16px;">
                        ${s}
                        <div style="display:flex;justify-content:flex-end;gap:8px;margin-top:12px;">
                            <button style="padding:6px 18px;border:1px solid #ccc;border-radius:3px;background:#f5f5f5;cursor:pointer;" onclick="this.closest('x-window')?.close()">取消</button>
                            <button style="padding:6px 18px;border:1px solid #03638d;border-radius:3px;background:#03638d;color:#fff;cursor:pointer;" onclick="
                                (function(el){
                                    const w=el.closest('x-window'),d=w.data;
                                    const clone=d.clone;
                                    for(const h of clone.head){
                                        const input=w.querySelector('#xui-grid-'+h.name);
                                        if(input) h.value=input.value.trim();
                                        if(Object.prototype.hasOwnProperty.call(clone, h.name)) clone[h.name]=h.value;
                                    }
                                    d.self.layout.grids.push(clone);
                                    w.close();
                                })(this)
                            ">确认</button>
                        </div>
                    </div>`,{self:this,clone:e}),i.open()},angleUp(t){const e=this.getGrid(t),s=e?.selected?.[0];if(e&&s){const i=this.getCell(t,s),l=s.split("-"),r=parseInt(l[0]),n=parseInt(l[1]),a=r-1;if(a>-1){const c=a+"-"+n,p=this.getCell(t,c);this.setCell(t,s,p),this.setCell(t,c,i),this.cellSelection[t+"-"+r+"-"+n]=!1,this.cellSelection[t+"-"+a+"-"+n]=!0,e.selected[0]=c}}},angleDown(t){const e=this.getGrid(t),s=e?.selected?.[0];if(e&&s){const i=this.getCell(t,s),l=s.split("-"),r=parseInt(l[0]),n=parseInt(l[1]),a=r+1;if(a>-1){const c=a+"-"+n,p=this.getCell(t,c);this.setCell(t,s,p),this.setCell(t,c,i),this.cellSelection[t+"-"+r+"-"+n]=!1,this.cellSelection[t+"-"+a+"-"+n]=!0,e.selected[0]=c}}},angleLeft(t){const e=this.getGrid(t),s=e?.selected?.[0];if(e&&s){const i=this.getCell(t,s),l=s.split("-"),r=parseInt(l[0]),n=parseInt(l[1]),a=n-1;if(a>-1){const c=r+"-"+a,p=this.getCell(t,c);this.setCell(t,s,p),this.setCell(t,c,i),this.cellSelection[t+"-"+r+"-"+n]=!1,this.cellSelection[t+"-"+r+"-"+a]=!0,e.selected[0]=c}}},angleRight(t){const e=this.getGrid(t),s=e?.selected?.[0];if(e&&s){const i=this.getCell(t,s),l=s.split("-"),r=parseInt(l[0]),n=parseInt(l[1]),a=n+1;if(a<4){const c=r+"-"+a,p=this.getCell(t,c);this.setCell(t,s,p),this.setCell(t,c,i),this.cellSelection[t+"-"+r+"-"+n]=!1,this.cellSelection[t+"-"+r+"-"+a]=!0,e.selected[0]=c}}},angleClose(t){this.layout.grids.splice(t,1)},toggleSelect(t,e,s){const i=t+"-"+e+"-"+s,l=this.getGrid(t),r=e+"-"+s;if(this.cellSelection[i])this.cellSelection[i]=!1,l?.selected&&(l.selected=l.selected.filter(n=>n!==r));else{for(const n of Object.keys(this.cellSelection))if(n.startsWith(t+"-")&&n.split("-")[1]!==String(e)&&(this.cellSelection[n]=!1,l?.selected)){const a=n.split("-").slice(1).join("-");l.selected=l.selected.filter(c=>c!==a)}this.cellSelection[i]=!0,l&&(l.selected||(l.selected=[]),l.selected.includes(r)||l.selected.push(r))}},isSelected(t,e,s){return!!this.cellSelection[t+"-"+e+"-"+s]},isCellCovered(t,e,s){const i=this.getGrid(t);if(!i||!i.rows[e])return!1;if(i.rows[e][s].colspan===0)return!0;for(let l=0;l<s;l++)if(l+i.rows[e][l].colspan>s)return!0;return!1},mergeSplit(t){const e=this.getGrid(t);if(!e)return;const s=e.selected?.[0];if(!s)return;const[i,l]=s.split("-").map(Number),r=e.rows[i]?.[l],n=(e.selected||[]).filter(a=>a.startsWith(i+"-")).map(a=>parseInt(a.split("-")[1])).sort((a,c)=>a-c);if(n.length>=2){let a=0;for(const c of n)a+=e.rows[i][c].colspan;e.rows[i][n[0]].colspan=a;for(let c=1;c<n.length;c++){const p=n[c],u=e.rows[i][p];u.colspan=0,u.value="",u.display="",this.cellSelection[t+"-"+i+"-"+p]=!1,e.selected&&(e.selected=e.selected.filter(h=>h!==i+"-"+p))}this.cellSelection[t+"-"+i+"-"+n[0]]=!1,e.selected&&(e.selected=e.selected.filter(c=>c!==i+"-"+n[0]))}else r&&r.colspan>1&&(r.colspan-=1)},getGrid(t){return this.layout.grids[t]},getCell(t,e){const s=this.getGrid(t);if(s){const i=e.split("-"),l=s.rows[i[0]];if(l)return l[parseInt(i[1])]}},setCell(t,e,s){const i=this.getGrid(t);if(i){const l=e.split("-"),r=i.rows[l[0]];r&&(r[l[1]]=s)}},onCellDragStart(t,e,s,i){const l=this.getCell(e,s+"-"+i);if(!l||!l.value){t.preventDefault();return}t.dataTransfer.setData("text/plain",JSON.stringify({idx:e,row:s,col:i})),t.dataTransfer.effectAllowed="move"},onCellDrop(t,e,s,i){t.preventDefault();const l=t.dataTransfer.getData("text/plain");if(!l)return;const r=JSON.parse(l);if(r.idx===e&&r.row===s&&r.col===i)return;const n=this.getCell(r.idx,r.row+"-"+r.col),a=this.getCell(e,s+"-"+i);!n||!a||n.value&&this.getGrid(e).rows.some(u=>Object.keys(u).some(h=>u[h]!==n&&u[h]!==a&&u[h].value===n.value))||(a.value=n.value,a.display=n.display,n.value="",n.display="")},onCellDragEnd(t,e,s,i){if(t.dataTransfer.dropEffect==="none"){const l=this.getCell(e,s+"-"+i);l&&(l.value||l.display)&&(l.value="",l.display="")}}}}getXUIMeshDrag(){return this.meshDrag}connectedCallback(){document.addEventListener("mousemove",this.meshDrag.onDraggingMove.bind(this.meshDrag)),document.addEventListener("mouseup",this.meshDrag.onDraggingPlace.bind(this.meshDrag))}disconnectedCallback(){document.removeEventListener("mousemove",this.meshDrag.onDraggingMove),document.removeEventListener("mouseup",this.meshDrag.onDraggingPlace)}attributeChangedCallback(o,t,e){}initXUIMeshLayout(o){this.loadLayoutTable?.(o)}isDom(o){return o===void 0?!1:typeof HTMLElement=="object"?o instanceof HTMLElement:o&&typeof o=="object"&&o.nodeType===1&&typeof o.nodeName=="string"}};g.TEMPLATE=`
<xui-root>
    <div :title="grid.display" v-for="(grid, idx) in layout.grids" style="display:flex;justify-content:space-between;margin-bottom:5px;">
        <span style="flex:1;margin-right:10px;">
            <table class="meshLayout" :id="'meshLayout-'+grid.name" name="meshLayoutInfo" style="width:100%;">
                <thead style="display:table;width:100%;table-layout:fixed;">
                    <!-- ===== 表头：Grid 元信息 + 工具栏 ===== -->
                    <tr>
                        <th style="display:flex;align-items:center;">
                            <span v-for="(row, index) in grid.head" style="margin-left:20px;">
                                <label style="font-weight:bold;">{{row.label}}</label>
                                <span>{{row.value}}</span>
                            </span>
                            <!-- 工具栏按钮组：编辑样式、合并/拆分、上下左右移动、删除 Grid -->
                            <span class="toolbar" style="font-size:18px;display:flex;align-items:center;margin-left:auto;">
                                <img src="images/svg/build.svg" :alt="i18n.editCell || 'Edit Cell'" @click="editStyle(idx);" class="tool-btn">
                                <img src="images/svg/mixer.svg" :alt="i18n.mergeSplit || 'Merge/Split'" @click="mergeSplit(idx);" class="tool-btn">
                                <img src="images/svg/arrow-up.svg" :alt="i18n.moveUp || 'Move Up'" @click="angleUp(idx);" class="tool-btn">
                                <img src="images/svg/arrow-down.svg" :alt="i18n.moveDown || 'Move Down'" @click="angleDown(idx);" class="tool-btn">
                                <img src="images/svg/arrow-back.svg" :alt="i18n.moveLeft || 'Move Left'" @click="angleLeft(idx);" class="tool-btn">
                                <img src="images/svg/arrow-forward.svg" :alt="i18n.moveRight || 'Move Right'" @click="angleRight(idx);" class="tool-btn">
                                <img v-if="idx > 0" src="images/svg/close.svg" :alt="i18n.delete || 'Delete'" @click="angleClose(idx);" class="tool-btn">
                            </span>
                        </th>
                    </tr>
                </thead>
                <!-- ===== 表体：可拖拽的单元格网格（4 列 × N 行） ===== -->
                <tbody style="display:block;overflow-y:scroll;height:206px;">
                    <tr v-for="(row, index) in grid.rows" :key="index" style="display:table;width:100%;table-layout:fixed;">
                        <!-- 行号列 -->
                        <td class="row-num" style="text-align:center;background:aliceblue">{{index}}</td>
                        <!-- 4 列单元格：colspan>1 的单元格会覆盖相邻列，覆被盖列由 isCellCovered 跳过 -->
                        <template v-for="col in [0,1,2,3]" :key="col">
                            <td class="col-slot" v-if="!isCellCovered(idx, index, col)" :colspan="row[col].colspan" @dragover.prevent @drop="onCellDrop($event, idx, index, col)">
                                <!-- Cell：flex 布局，display 靠左 + uiType 灰色靠右 -->
                                <span class="cell" :class="{ selected: isSelected(idx, index, col) }" draggable="true" :idx="idx" :row="index" :col="col" :field="row[col].value"
                                    @click="toggleSelect(idx, index, col)" @dragstart="onCellDragStart($event, idx, index, col)" @dragend="onCellDragEnd($event, idx, index, col)" style="display:flex;justify-content:space-between;align-items:center;">
                                    <span>{{row[col].display}}</span> <span v-if="row[col].display" style="color:#C5C5C5;font-size:9px;margin-right:2px">{{row[col].uiType}}</span>
                                </span>
                            </td>
                        </template>
                        <!-- 行操作按钮：删除（首行不显示）、新增 -->
                        <td class="row-tools">
                            <button type="button" @click="delLayoutRow(idx+'-'+index)" v-if="index>0">-</button>
                            <button type="button" @click="addLayoutRow(idx+'-'+index)">+</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </span>
    </div>
    <!-- 底部：新增 Grid 按钮 -->
    <div style="display:flex;justify-content:center;margin-top:8px;">
        <img src="images/svg/add.svg" alt="新增Grid" @click="addGrid();" class="tool-btn" style="width:20px;height:20px;cursor:pointer;">
    </div>
</xui-root>`;let f=g;String.prototype.startWith=String.prototype.startWith||function(d){return d==null||d==""||this.length==0||d.length>this.length?!1:this.substr(0,d.length)==d};String.prototype.endWith=String.prototype.endWith||function(d){return d==null||d==""||this.length==0||d.length>this.length?!1:this.substring(this.length-d.length)==d};String.prototype.trim=String.prototype.trim||function(){return this.replace(/^\s\s*/,"").replace(/\s\s*$/,"")};String.format=String.format||function(){let d=arguments[0];for(let o=0;o<arguments.length-1;o++)d=d.replace(new RegExp("\\{"+o+"\\}","gm"),arguments[o+1]);return d};Object.assign(window,{XUIComponent:y,VUEComponent:x,XUIMeshLayout:f,XUIMeshDrag:m});
//# sourceMappingURL=layout.xui.js.map

export{y as XUIComponent,x as VUEComponent,f as XUIMeshLayout,m as XUIMeshDrag };
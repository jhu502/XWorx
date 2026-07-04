package plm.dynamic.engine.cvm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import plm.part.XPart;
import plm.dynamic.engine.mdb.CalChoice;
import plm.dynamic.engine.mdb.CalParam;

public class XWorxModel {
	private String id;
	private String oid;
	private XPart xpart;
	private String partType;
	private String moduleType;
	private boolean polymorphic;
	private String simulator;
	private String number;
	private String name;
	private String detailName;
	private String view;
	private String version;
	private String iteration;
	private String referenceId;
	private String componentId;
	private long usageLinkId;
	private double quantity;
	private boolean dynamic;
	private boolean phantom;
	private boolean absenceAllowed;
	private Map<String, XParam> parameters = new LinkedHashMap<>();
	private List<XWorxModel> childrens = new ArrayList<XWorxModel>();

	public static class XParam {
		protected String uuid;
		protected String name;
		protected String displayName;
		protected String decription;
		protected String globalId;
		protected Object value;
		protected Boolean readonly;
		protected Boolean required;
		protected Boolean hideDriven;
		protected Boolean display;
		protected String source;
		protected String optionType;
		protected String valueType;
		protected String mapAttribute;

		protected Map<Object, XValue> options = new LinkedHashMap<Object, XValue>();
		
		public static XParam newXParam(CalParam calParam) {
			XParam xparam = new XParam();
			xparam.uuid = calParam.getUUID();
			xparam.name = calParam.getName();
			xparam.displayName = calParam.getDisplayName();
			xparam.decription = calParam.getDescription();
			xparam.globalId = calParam.getGlobalId();
			xparam.value = calParam.getValue();
			xparam.source = calParam.getSource().toDisplay();
			xparam.readonly = calParam.isReadonly();
			xparam.required = calParam.isRequired();
			xparam.optionType = calParam.getOptionMode().toString();
			xparam.valueType = calParam.getBaseType().getName();
			xparam.mapAttribute = calParam.getMapAttribute();
			for (CalChoice choice : calParam.getChoices()) {
				XValue xvalue = new XValue();
				xvalue.value = choice.value();
				xvalue.prompt = choice.getPrompt();
				xvalue.status = choice.getStatus().toString();

				xparam.options.put(xvalue.value, xvalue);
			}
			return xparam;			
		}

		public String getUUID() {
			return uuid;
		}

		public String getName() {
			return name;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getDecription() {
			return decription;
		}

		public String getGlobalId() {
			return globalId;
		}

		public String getSource() {
			return source;
		}

		public Object getValue() {
			return value;
		}

		public boolean isReadonly() {
			return readonly;
		}
		
		public boolean isRequired() {
			return required;
		}

		public String getOptionType() {
			return optionType;
		}

		public String getValueType() {
			return valueType;
		}
		
		public String getMapAttribute() {
			return this.mapAttribute;
		}
		
		public Boolean getHideDriven() {
			return this.hideDriven;
		}
		
		public Boolean getDisplay() {
			return this.display;
		}

		public Map<Object, XValue> getOptions() {
			return options;
		}
	}

	public static class XValue {
		protected Object value;
		protected String prompt;
		protected String status;

		public Object getValue() {
			return value;
		}

		public String getPrompt() {
			return prompt;
		}

		public String getStatus() {
			return status;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public XPart getXpart() {
		return xpart;
	}
	
	public String getPartType() {
		return this.partType;
	}
	
	public String getModuleType() {
		return this.moduleType;
	}
	
	public boolean isPolymorphic() {
		return polymorphic;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getSimulator() {
		return simulator;
	}

	public void setSimulator(String simulator) {
		this.simulator = simulator;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
	
	public String getPartVersion() {
		return this.version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIteration() {
		return iteration;
	}

	public void setIteration(String iteration) {
		this.iteration = iteration;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	
	public long getUsageLinkId() {
		return this.usageLinkId;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isPhantom() {
		return phantom;
	}

	public void setPhantom(boolean phantom) {
		this.phantom = phantom;
	}

	public boolean isAbsenceAllowed() {
		return absenceAllowed;
	}

	public void setAbsenceAllowed(boolean absenceAllowed) {
		this.absenceAllowed = absenceAllowed;
	}

	public Map<String, XParam> getParameters() {
		return parameters;
	}

	public void addParameters(String name, XParam value) {
		this.parameters.put(name, value);
	}

	public List<XWorxModel> getChildrens() {
		return childrens;
	}

	public void addChildModel(XWorxModel xmodel) {
		this.childrens.add(xmodel);
	}
}

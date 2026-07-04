package plm.dynamic.engine.rule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalChoice;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalRow;
import com.flame.util.XException;

/**
 * 
 * @author hujin
 * @version 1.1
 * 
 */
public class CaseTableRule extends AbstractRule {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(CaseTableRule.class);
	private static final Object ALL = new Object();
	private CalCharacter[] columns;
	private CalTableType type = CalTableType.FLATTABLE;
	private Map<String, CalRow> rows = new HashMap<>();

	public enum CalTableType {
		FLATTABLE, BLOCKTABLE
	}

	public CaseTableRule(String name, CalCharacter[] columns) {
		this.name = name;
		this.columns = columns;
	}

	public CalTableType getType() {
		return this.type;
	}

	public void setType(CalTableType type) {
		this.type = type;
	}

	public CalCharacter[] getColumns() {
		return columns;
	}

	public void setColumns(CalCharacter[] columns) {
		this.columns = columns;
	}

	public CalCharacter getColumn(int i) {
		return this.columns[i];
	}

	public int getNumColumns() {
		return this.columns.length;
	}

	public void addRow(CalRow row) {
		this.rows.put(Integer.toString(this.rows.size()), row);
	}

	public Collection<CalRow> getRows() {
		return this.rows.values();
	}

	public Map<String, CalRow> getRowMap() {
		return Collections.unmodifiableMap(this.rows);
	}

	public void remove(String key) {
		this.rows.remove(key);
	}

	public int getNumRows() {
		return this.rows.size();
	}

	public Set<CalCharacter> performRule() {
		Set<CalCharacter> recurseSet = new HashSet<>();

		CalTableType tableType = this.getType();
		logger.debug("CalTable name: " + this.getName() + "    Count of row: " + this.getNumRows());
		int colnum = this.getNumColumns();

		Map<Integer, Collection<Object>> tableFilterMap = new HashMap<>(colnum);

		for (CalRow row : this.getRows()) {
			Map<Integer, Object> rowFilterMap = new HashMap<>(colnum);
			for (int i = 0; i < colnum; i++) {
				Object value = row.getCell(i).getValue();
				CalCharacter option = this.getColumn(i);

				if (value == null || "".equals(value)) {
					rowFilterMap.put(i, null);
				} else {
					if (CalTableType.FLATTABLE.equals(tableType)) {
						if (option.hasEnabledChoice(value)) {
							rowFilterMap.put(i, value);
						} else {
							rowFilterMap.clear();
							break;
						}
					} else if (CalTableType.BLOCKTABLE.equals(tableType)) {
						Set<?> values = (Set<?>) value;
						if (values == null || values.isEmpty()) {
							rowFilterMap.put(i, null);
						} else {
							Collection<?> interset = CollectionUtils.intersection(option.getEnabledChoices(), values);
							if (!interset.isEmpty()) {
								rowFilterMap.put(i, interset);
							} else {
								rowFilterMap.clear();
								break;
							}
						}
					}
				}
			}
			// Combine rowFileMap into tableFilteMap.
			if (!rowFilterMap.isEmpty()) {
				for (Entry<Integer, Object> entry : rowFilterMap.entrySet()) {
					Integer col = entry.getKey();
					Object value = entry.getValue(); // String / Set
					Collection<Object> _options = tableFilterMap.get(col);
					if (_options == null) {
						_options = new HashSet<>();
						tableFilterMap.put(col, _options);
					}
					if (CalTableType.FLATTABLE.equals(this.getType())) {
						_options.add(value);
					} else if (CalTableType.BLOCKTABLE.equals(this.getType())) {
						_options.addAll((Collection<?>) value);
					}
				}
			}
		}

		/**
		 * Check availability
		 */
		int cnum = this.getNumColumns();
		for (int i = 0; i < cnum; i++) {
			CalCharacter column = this.getColumn(i);
			CalCharacter option = this.getColumn(i);
			if (!tableFilterMap.containsKey(i)) {
				option.setPrompt("C101~选配器初始化时，参数:“" + column.getName() + "”的所有选项被案例表:“" + this.getName() + "”禁用.");
			}
		}

		// Merge tableFilteMap and PV list.
		if (!tableFilterMap.isEmpty()) {
			for (Entry<Integer, Collection<Object>> entry : tableFilterMap.entrySet()) {
				Integer col = entry.getKey();
				Collection<Object> values = entry.getValue();
				if (!values.contains(null)) {
					CalCharacter caloption = this.getColumn(col);

					boolean bool = false;
					for (Object object : caloption.getEnabledChoiceVals().toArray()) {
						if (!values.contains(object)) {
							String prompt = "C102~选配器初始化时，参数值:“" + object + "”被案例表:“" + this.getName() + "”禁用.";
							caloption.disableChoice(object, prompt);
							bool = true;
							logger.trace(prompt);
						}
					}

					if (bool) {
						recurseSet.add(caloption);
					}
				}
			}
		} else {
			for (CalCharacter calOption : this.getColumns()) {
				for (CalChoice choice : calOption.getEnabledChoices().toArray(new CalChoice[0])) {
					String prompt = "C103~选配器初始化时，参数值:“" + choice.value() + "”被案例表:“" + this.getName() + "”禁用.";
					choice.addPrompt(prompt);
					calOption.disableChoice(choice);
				}
				logger.trace("All options of parameter " + calOption.getName() + " were cleared by CaseTable " + this.getName() + " during initialization.");
			}
		}

		return recurseSet;
	}

	@Override
	public Set<CalParam> performRule(Map<String, CalParam> metabolic, CalParam inParam) {
		Set<CalParam> recurseSet = new HashSet<>();

		int colNum = this.getNumColumns();

		Map<String, Collection<Object>> tableFilteMap = this.crossOptionsCaseTable(metabolic);
		/**
		 * Check availability
		 */
		int cnum = this.getNumColumns();
		for (int i = 0; i < cnum; i++) {
			CalCharacter column = this.getColumn(i);
			CalParam param = metabolic.get(column.getUUID());
			if (!tableFilteMap.containsKey(column.getUUID())) {
				if (inParam == null) {
					param.setPrompt("C104~参数:“" + column.getDetailName() + "”的所有选项都被案例表:“" + this.getDetailName() + "”禁用.");
				} else {
					param.setPrompt("C104" + this.showParamInfo(inParam) + "时，参数:“" + column.getDetailName() + "”的所有选项都被案例表:“" + this.getDetailName() + "”禁用.");
				}
			}
		}

		// Merge tableFilteMap to metabolic.
		if (!tableFilteMap.isEmpty()) {
			for (Entry<String, Collection<Object>> entry : tableFilteMap.entrySet()) {
				String pname = entry.getKey();
				Collection<Object> values = entry.getValue();
				CalParam param = metabolic.get(pname);

				boolean _flag = false;
				Object[] objs = param.getEnabledChoiceVals().toArray();
				for (Object _option : objs) {
					if (!values.contains(_option)) {
						if (inParam == null) {
							param.disableChoice(_option, "C105~" + this.showParamInfo(inParam) + "时，参数值:“" + _option + "”被案例表:“" + this.getDetailName() + "”禁用.");
						} else {
							param.disableChoice(_option, "C105~参数值:“" + _option + "”被案例表:“" + this.getDetailName() + "”禁用.");
						}
						_flag = true;
					}
				}

				if (_flag) {
					recurseSet.add(param);
				}
			}
		} else {
			boolean flag = false;
			StringBuilder strbuf = new StringBuilder();
			if (inParam != null)
				strbuf.append("C105~").append(this.showParamInfo(inParam)).append("时，");
			for (int i = 0; i < colNum; i++) {
				CalParam param = metabolic.get(this.getColumn(i).getUUID());
				if (i == 0) {
					strbuf.append("参数:").append(param.getName());
				} else {
					strbuf.append(",").append(param.getName());
				}
				if (param.isRequired()) {
					flag = false;
				}
			}
			if (flag) {
				strbuf.append("违反案例表:“").append(this.getDetailName()).append("”约束.");
				throw new XException("C201", strbuf.toString());
			}
		}

		return recurseSet;
	}

	/**
	 * 基于一个CaseTable约束，对相关的参数进行约束运算，并将违反约束的参数值禁用；
	 * 
	 * @param metabolic
	 * @param caltable
	 * @return
	 */
	public Map<String, Collection<Object>> crossOptionsCaseTable(Map<String, CalParam> metabolic) {
		int colNum = this.getNumColumns();
		int rowNum = this.getNumRows();
		CalTableType tabType = this.getType();
		logger.trace("CalTable name: " + this.getDetailName() + "  Type: " + tabType + "   Row Count: " + rowNum + "   Column Count: " + colNum);

		Map<String, Collection<Object>> result = new HashMap<>(colNum);

		for (CalRow row : this.getRows()) {
			Map<String, Object> rowFilterMap = new HashMap<>(colNum); // Map<String, String> or Map<String, Collection<String>>
			Map<String, Object> rowInputMap = new HashMap<>(colNum); // 用了计算已经选择了值的参数的其他可能会启用的值
			/**
			 * 记录在CalTable中对某行进行匹配时，不满足条件的列的数量
			 */
			int violateCount = 0;

			for (int i = 0; i < colNum; i++) {
				String uuidOption = this.getColumn(i).getUUID();
				Object value = row.getCell(i).getValue();

				if (value == null || "".equals(value)) {
					rowFilterMap.put(uuidOption, ALL);
				} else {
					CalParam param = metabolic.get(uuidOption);

					if (CalTableType.FLATTABLE.equals(tabType)) {
						if (param != null) {
							if (CalParam.Source.INPUT.equals(param.getSource()) || CalParam.Source.DEFAULT.equals(param.getSource()) || CalParam.Source.DRIVEN.equals(param.getSource())
									|| CalParam.Source.CASCADE.equals(param.getSource())) {
								if (param.hasValue(value)) {
									rowFilterMap.put(uuidOption, value);
								} else {
									rowInputMap.put(uuidOption, value); //记录Input了值的参数的Enabled值.
									violateCount = violateCount + 1;
								}
							} else {
								if (param.hasEnabledChoice(value)) {
									rowFilterMap.put(uuidOption, value);
								} else {
									violateCount = violateCount + 1;
								}
							}
						}
					} else if (CalTableType.BLOCKTABLE.equals(tabType)) {
						Set<?> values = (Set<?>) value;
						if (param != null) {
							if (CalParam.Source.INPUT.equals(param.getSource()) || CalParam.Source.DEFAULT.equals(param.getSource()) || CalParam.Source.DRIVEN.equals(param.getSource())
									|| CalParam.Source.CASCADE.equals(param.getSource())) {
								Collection<?> interset = param.crossOptions(values);
								if (param.hasValue(values)) {
									rowFilterMap.put(uuidOption, interset);
								} else {
									rowInputMap.put(uuidOption, interset);
									violateCount = violateCount + 1;
								}
							} else {
								Collection<?> interset = param.crossOptions(values);
								if (!interset.isEmpty()) {
									rowFilterMap.put(uuidOption, interset);
								} else {
									violateCount = violateCount + 1;
								}
							}
						}
					}
				}
			}

			/**
			 * 如果clearCount>0，清理rowFilteMap的所有值，
			 * 如果clearCount=1，说明当前行不满足条件因为一个Input参数导致，需要其启用该Input参数的能启用的值
			 * 如果clearCount>1，说明多个参数都会导致CaseTable行不满足条件
			 */
			if (violateCount > 0) {
				rowFilterMap.clear();
			}

			// Collection result from CalTable row.
			if (!rowFilterMap.isEmpty()) {
				for (Entry<String, Object> entry : rowFilterMap.entrySet()) {
					String uuid = entry.getKey();
					Object value = entry.getValue();
					if (ALL.equals(value)) {
						Collection<Object> options = result.get(uuid);
						if (options == null) {
							options = new HashSet<>();
							result.put(uuid, options);
						}
						CalParam param = metabolic.get(uuid);
						options.addAll(param.getEnabledChoiceVals());
					} else {
						Collection<Object> _options = result.get(uuid);
						if (_options == null) {
							_options = new HashSet<>();
							result.put(uuid, _options);
						}
						if (CalTableType.FLATTABLE.equals(tabType)) {
							_options.add(value);
						} else if (CalTableType.BLOCKTABLE.equals(tabType)) {
							_options.addAll((Collection<?>) value);
						}
					}
				}
			}
		}

		return result;
	}
}

package plm.dynamic.engine.rule;

import com.flame.util.XException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalChoice;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalRow;

import java.util.*;
import java.util.Map.Entry;

public class UsageLinkRule extends AbstractRule {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(UsageLinkRule.class);
	private static final Object ALL = new Object();
	private Map<String, CalRow> rows = new HashMap<>();
	private CalCharacter[] columns;

	public static UsageLinkRule newInstance(Emulator emulator, CalCharacter dlist) {
		CalCharacter[] columns = new CalCharacter[dlist.getChildCharacts().size() + 1];
		columns[0] = dlist;
		int i = 1;
		for (CalCharacter charact : dlist.getChildCharacts()) {
			columns[i++] = charact;
		}

		return new UsageLinkRule(emulator.getNumber() + "-" + dlist.getName(), columns);
	}

	public UsageLinkRule(String name, CalCharacter[] columns) {
		this.name = name;
		this.columns = columns;
	}

	public void setRows(Map<String, CalRow> rows) {
		this.rows = rows;
	}

	public void addRow(CalRow row) {
		this.rows.put(Integer.toString(this.rows.size()), row);
	}

	public Collection<CalRow> getRows() {
		return this.rows.values();
	}

	public CalCharacter[] getColumns() {
		return columns;
	}

	public CalCharacter getColumn(int i) {
		return this.columns[i];
	}

	public void setColumns(CalCharacter[] columns) {
		this.columns = columns;
	}

	public int getNumColumns() {
		return this.columns.length;
	}

	public int getNumRows() {
		return this.rows.size();
	}

	public Set<CalCharacter> performRule() {
		Set<CalCharacter> recurseSet = new HashSet<>();

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
					if (option.hasEnabledChoice(value)) {
						rowFilterMap.put(i, value);
					} else {
						rowFilterMap.clear();
						break;
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
					_options.add(value);
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
				option.setPrompt(S("C101~选配器初始化时，参数:“%s”的所有选项被案例表:“%s”禁用.", column.getName(), this.getName()));
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
							caloption.disableChoice(object, S("C102~选配器初始化时，参数值:“%s”被案例表:“%s”禁用.", object, this.getName()));
							bool = true;
							logger.trace(S("C102~选配器初始化时，参数值:“%s”被案例表:“%s”禁用.", object, this.getName()));
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
					choice.addPrompt(S("C103~选配器初始化时，参数值:“%s”被案例表:“%s”禁用.", choice.value(), this.getName()));
					calOption.disableChoice(choice);
				}
				logger.trace(S("All options of parameter %s were cleared by CaseTable %s during initialization.", calOption.getName(), this.getName()));
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
					param.setPrompt(S("C104~参数:“%s”的所有选项都被案例表:“%s”禁用.", column.getDetailName(), this.getDetailName()));
				} else {
					param.setPrompt(S("C104~%s时，参数:“%s”的所有选项都被案例表:“%s”禁用.", this.showParamInfo(inParam), column.getDetailName(), this.getDetailName()));
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
							param.disableChoice(_option, S("C105~%s时，参数值:“%s”被案例表:“%s”禁用.", this.showParamInfo(inParam), _option, this.getDetailName()));
						} else {
							param.disableChoice(_option, S("C105~参数值:“%s”被案例表:“%s”禁用.", _option, this.getDetailName()));
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
	 * @return
	 */
	public Map<String, Collection<Object>> crossOptionsCaseTable(Map<String, CalParam> metabolic) {
		int colNum = this.getNumColumns();
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
					if (param != null) {
						if (CalParam.Source.INPUT.equals(param.getSource()) || CalParam.Source.DEFAULT.equals(param.getSource()) || CalParam.Source.DRIVEN.equals(param.getSource()) || CalParam.Source.CASCADE.equals(param.getSource())) {
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
						_options.add(value);
					}
				}
			}
		}

		return result;
	}
}

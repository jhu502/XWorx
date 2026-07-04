package plm.dynamic.engine.mdb;

public class CalItem implements Comparable<CalItem> {
	private String key;
	private String number;
	private double qty;
	private String unit;
	private int ser;

	@SuppressWarnings("unused")
	private CalItem() {
	}

	public CalItem(String number, double qty, String unit) {
		this.number = number;
		this.qty = qty;
		this.unit = unit;
		this.ser = 0;
		this.key = this.number + "_" + qty;
	}

	public void ascendingKey() {
		this.ser = this.ser + 1;
		this.key = this.number + "_" + qty;
	}

	public String getKey() {
		return this.key;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
		this.key = this.number + "_" + qty;
	}

	public double getQuantity() {
		return this.qty;
	}

	public void setQuantity(double qty) {
		this.qty = qty;
		this.key = this.number + "_" + qty;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public int compareTo(CalItem o) {
		return this.getKey().compareTo(o.getKey());
	}

	public String toString() {
		return this.getKey();
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof CalItem) {
			if (this.getKey() == null)
				return this.getKey() == ((CalItem) obj).getKey();
			else
				return this.getKey().equals(((CalItem) obj).getKey());
		}
		return false;
	}

	public int hashCode() {
		return (this.getKey() + "-" + String.format("%04d", this.ser)).hashCode();
	}

	public static void main(String[] args) {
		System.out.println(String.format("%04d", 0));
	}
}

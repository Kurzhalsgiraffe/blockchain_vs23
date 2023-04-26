package dao;

public class BlockValidator {
	private boolean isValid;
	private int index;

	public BlockValidator(boolean isValid, int index) {
		this.isValid = isValid;
		this.index = index;
	}

	public boolean isValid() {
		return isValid;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "BlockValidator [isValid=" + isValid + ", index=" + index + "]";
	}

}

package blue.endless.jarser.ast;

public class AstLeafNode {
	protected String value;
	
	public String getValue() {
		return value;
	}
	
	public int asInteger() throws NumberFormatException {
		return Integer.parseInt(value);
	}
	
	public double asDouble() throws NumberFormatException {
		return Double.parseDouble(value);
	}
	
	public long asLong() throws NumberFormatException {
		return Long.parseLong(value);
	}
}

package gov.usgs.cida.retrofit;

public class Transform {
	public String obj;
	public String func;
	public String[] args;
	public boolean isValid;

	public Transform(String transformExpression) {
		if (transformExpression == null || transformExpression.length() == 0) return;
		String[] parts = transformExpression.split("<-");
		this.obj = parts[0];
		
		int parentLocation = parts[1].indexOf("(");
		this.func = parts[1].substring(0, parentLocation);
		String argList = parts[1].substring(parentLocation + 1, parts[1].length() - 1);
		this.args = argList.split(",");
		for (int i=0; i<args.length; i++) {
			args[i] = args[i].trim();
		}
		this.isValid = true;
	}
}

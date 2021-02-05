package edu.cust;

public class FloatConvertTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "0.e+00,0.e+00,0.e+00,0.e+00,8.655467e+00,0.e+00,5.0560384e+00,6.92544e-0";
		s = "0.e+00,0.e+00,0.e+00,0.e+00,1.8339832e+01,0.e+00,1.0555315e+01,2.3655358e-0";
		String[] arr = s.split(",");
		for (String string : arr) {
			double d = Double.parseDouble(string);
			System.out.println(d);
		}
	}

}

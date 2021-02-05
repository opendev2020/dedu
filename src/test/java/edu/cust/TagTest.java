package edu.cust;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class TagTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ast = "0.e+00,0.e+00,0.e+00,0.e+00,8.655467e+00,0.e+00,5.0560384e+00,6.92544e-0";
		String fpt = "02b8aec45b072323354575705473b4328ce159054d56517527609005a21290f50e6515eb461a6c21f28d0acdd80ff2e3d4a03b9379dc3f80d4ae6104be7baa193b9cd640739c159e8169165842d5f52283e33f26897fef40f0107ece42228f38b1e445bb14c1fe5c5a8531e4df77389aef227b110dc05bd40872f592cf12ba713b0335ce70f0a1e9c178f8a8147552bde2809dc00b1df38051fac63d88d3eb35b95ab963071d1329faba9888e99b64f3dff51a9d04c4d94a67f7e31d2bab603e85d30fd4b744d2d7f2c9ab18e625d1b2f26b672403436da6f6501de49a59677cfe0fb12d56849b41a5eea51ec85b902c2d53742c68f8cf0fb6be80d657c44421";
		Pairing pairing = PairingUtil.getPairing("src\\main\\resources\\a.properties");
		try {
			Tag tag = Tag.prepareTag(ast, fpt, pairing.getG1());
			//byte[] md5 = Hex.decodeHex("f939c670835299373a5f638209e6c861".toCharArray());
			//byte[] md5 = Hex.decodeHex("f939c670835299373a5f638209e6c862".toCharArray());
			Element g = PairingUtil.getGenerator(pairing, "26d1c1f0a9bd45ff6eab842e4d4adcdae7440526562156742945a349cc64edb8788b293703d50dc456994e38ed3cc908e4dbb86243d05681fcc2508cdf43b05f1a910b435778f9b8f46aea06d054d8630a21c99a04f28ad52b03d7da0000577f336b7b7549fec318c85931e261199c751d17bc5deeb0f08cbe425ce371367a02");
			//Tag sample = Tag.prepareSample(ast, md5, pairing.getZr(), g);
			Tag sample = Tag.prepareSample("test_sample_origin", ast, pairing.getZr(), g);
			Element e1 = pairing.pairing(tag.t1, sample.t2);
			Element e2 = pairing.pairing(sample.t1, tag.t2);
			System.out.println(e1.isEqual(e2));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}

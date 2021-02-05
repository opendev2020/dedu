package edu.cust;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class PairingTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String curvePath = "src\\main\\resources\\a.properties";
		Pairing pairing = PairingUtil.getPairing(curvePath);
		Field<?> zr = pairing.getZr();
		Element h = zr.newRandomElement().getImmutable();
		//Element h2 = h1.duplicate();
		//BigInteger h = new BigInteger("123456789");
		Element a = zr.newRandomElement();
		//BigInteger a = new BigInteger("123456789");
		System.out.println("a:" + a.toString());
		//Element ha = a.duplicate();
		Element h1 = h.mulZn(a);
		Element b = zr.newRandomElement();
		//BigInteger b = new BigInteger("789");
		//Element hb = b.duplicate();
		Element h2 = h.mulZn(b);
		Element g = pairing.getG1().newRandomElement();
		byte[] gb = g.toBytes();
		String gbh = Hex.encodeHexString(gb);
		System.out.println("gbh:" + gbh);
		try {
			gb = Hex.decodeHex(gbh.toCharArray());
		}catch(Exception ex) {
			gb = null;
		}
		g = pairing.getG1().newElementFromBytes(gb).getImmutable();
		System.out.println(g.powZn(b).getLengthInBytes());
		System.out.println(Hex.encodeHexString(g.powZn(b).toBytes()));
		//Element h = pairing.getG2().newElement().setToRandom();
		Element x = g.powZn(b);
		Element y = g.powZn(h1);
		long s = System.currentTimeMillis();
		Element el = pairing.pairing(x, y);
		long i = System.currentTimeMillis() - s;
		System.out.println("time:" + i);
		double[] da = new double[8];
		double[] db = new double[8];
		Arrays.fill(da, 1);
		for (int j = 0; j < db.length; j++) {
			db[j] = Math.random();
		}
		s = System.nanoTime();
		
		double dis = 0;
		for (int j = da.length - 1; j >= 0; j--) {
			double dbj = db[j] - da[j];
			dis += dbj * dbj;
		}
		i = System.nanoTime() - s;
		System.out.println("time:" + i);
		System.out.println("dis:" + dis);
		Element er = pairing.pairing(g.powZn(a), g.powZn(h2));
		System.out.println(el.isEqual(er));
		System.out.println(el);
		System.out.println(er);
	}

}

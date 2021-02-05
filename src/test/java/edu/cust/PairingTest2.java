package edu.cust;

import java.math.BigInteger;

import org.apache.commons.codec.binary.Hex;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class PairingTest2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String curvePath = "src\\main\\resources\\a.properties";
		Pairing pairing = PairingUtil.getPairing(curvePath);
		Field<?> zr = pairing.getZr();
		
		Element g = pairing.getG1().newRandomElement().getImmutable();
		BigInteger bi = new BigInteger("1234567897653", 16);
		Element a = zr.newRandomElement().getImmutable();
		Element b = zr.newRandomElement().getImmutable();
		Element h = zr.newElement(bi);
		long s = System.nanoTime();
		Element ah = a.mulZn(h);
		long t = System.nanoTime() - s;
		System.out.println("time:" + t);
		Element bh = b.mulZn(zr.newElement(bi));
		String t1 = Hex.encodeHexString(g.powZn(a).toBytes());
		String t2 = Hex.encodeHexString(g.powZn(ah).toBytes());
		//String t3 = Hex.encodeHexString(g.powZn(b).toBytes());
		//String t4 = Hex.encodeHexString(g.powZn(bh).toBytes());
		System.out.println(t1);
		System.out.println(t1.length());
		System.out.println(t2);
		System.out.println(t2.length());
		Element e1 = pairing.pairing(g.powZn(b), g.powZn(ah));
		Element e2 = pairing.pairing(g.powZn(a), g.powZn(bh));
		System.out.println(e1.isEqual(e2));
		System.out.println(pairing.pairing(g.powZn(a), g.powZn(ah)));
	}

}

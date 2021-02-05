package edu.cust;

import org.apache.commons.codec.binary.Hex;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class PairingUtil {
	
	static PairingParameters getParameters(String curve) {
		return PairingFactory.getInstance().loadParameters(curve);
	}
	
	public static Pairing getPairing(String curve) {
		return PairingFactory.getPairing(getParameters(curve));
	}
	
	public static Element getGenerator(Pairing pairing, String hexG) throws Exception {
		byte[] gb = Hex.decodeHex(hexG.toCharArray());
		return pairing.getG1().newElementFromBytes(gb).getImmutable();
	}

}

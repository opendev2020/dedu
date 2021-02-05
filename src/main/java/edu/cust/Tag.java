package edu.cust;

import java.io.DataInputStream;
import java.io.FileInputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class Tag {
	
	double[] at;
	Element t1;
	Element t2;
	
	public void setAt(String ast) {
		String[] astArr = ast.split(",");
		if(astArr.length != 8) {
			log.debug("astArr.length:{}", astArr.length);
		}
		double[] at = new double[astArr.length];
		for (int i = 0; i < at.length; i++) {
			at[i] = Double.parseDouble(astArr[i]);
		}
		this.at = at;
	}
	
	public static Tag prepareSample(String samplePath, String ast, Field<?> zr, Element g) throws Exception {
		FileInputStream in = new FileInputStream(samplePath);
		byte[] buf = new byte[in.available()];
		new DataInputStream(in).readFully(buf);
		in.close();
		byte[] md5 = DigestUtils.md5(buf);
		log.debug("sample md5: {}", Hex.encodeHexString(md5));
		return prepareSample(ast, md5, zr, g);
	}
	
	public static Tag prepareSample(String ast, byte[] fp, Field<?> zr, Element g) {
		//byte[] md5 = Hex.decodeHex(fp.toCharArray());
		Element h = zr.newElementFromBytes(fp);
		Tag sample = new Tag();
		sample.setAt(ast);
		Element a = zr.newRandomElement().getImmutable();
		sample.t1 = g.powZn(a);
		sample.t2 = g.powZn(a.mulZn(h));
		return sample;
	}
	
	public static Tag prepareTag(String ast, String fpt, Field<?> g) throws Exception {
		Tag sample = new Tag();
		sample.setAt(ast);
		
		String fpt1 = fpt.substring(0, 256);
		String fpt2 = fpt.substring(256);
		Element t1 = g.newElementFromBytes(Hex.decodeHex(fpt1.toCharArray()));
		Element t2 = g.newElementFromBytes(Hex.decodeHex(fpt2.toCharArray()));
		sample.t1 = t1;
		sample.t2 = t2;
		return sample;
	}

}

package edu.cust;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class FpCalculate implements CommandLineRunner {
	
	@Autowired
	private Env env;
	@Autowired
	private JdbcTemplate jt;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		if(env.isFpCalculated()) {
			log.debug("fp_t is calculated");
			return;
		}
		Pairing pairing = PairingUtil.getPairing(env.getCurvePath());
		Element g = PairingUtil.getGenerator(pairing, env.getGenerator());
		Field<?> zr = pairing.getZr();
		
		List<Map<String, Object>> list = jt.queryForList("select c_id id,c_fp fp from c_chunk");
		for(Map<String, Object> row : list) {
			String id = (String)row.get("id");
			String fp = (String)row.get("fp");
			BigInteger fpb = new BigInteger(fp, 16);
			Element a = zr.newRandomElement().getImmutable();
			Element ah = a.mulZn(zr.newElement(fpb));
			String t1 = Hex.encodeHexString(g.powZn(a).toBytes());
			String t2 = Hex.encodeHexString(g.powZn(ah).toBytes());
			if(t1.length() != 256 || t2.length() != 256) {
				log.debug("t1.length:{}, t2.length:{}", t1.length(), t2.length());
			}
			jt.update("update c_chunk set c_fp_t=? where c_id=?", t1+t2, id);
		}
	}

}

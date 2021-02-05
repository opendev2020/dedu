package edu.cust;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
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
public class RefreshFp implements CommandLineRunner {
	
	@Autowired
	private JdbcTemplate jt;
	@Autowired
	private Env env;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		//jt.update("update c_chunk set c_fp=null");
		int c = 0;
		Pairing pairing = PairingUtil.getPairing(env.getCurvePath());
		Element g = PairingUtil.getGenerator(pairing, env.getGenerator());
		Field<?> zr = pairing.getZr();
		do {
			c = jt.query("select c_id,c_content from c_chunk where c_fp is null limit 10", (rs)->{
				int count = 0;
				while(rs.next()) {
					String id = rs.getString(1);
					try(InputStream in = rs.getBlob(2).getBinaryStream()){
						byte[] buf = new byte[4096];
						int n;
						MessageDigest md = DigestUtils.getMd5Digest();
						while((n = in.read(buf)) != -1) {
							md.update(buf, 0, n);
						}
						String fp = Hex.encodeHexString(md.digest());
						
						BigInteger fpb = new BigInteger(fp, 16);
						Element a = zr.newRandomElement().getImmutable();
						Element ah = a.mulZn(zr.newElement(fpb));
						String t1 = Hex.encodeHexString(g.powZn(a).toBytes());
						String t2 = Hex.encodeHexString(g.powZn(ah).toBytes());
						if(t1.length() != 256 || t2.length() != 256) {
							log.debug("t1.length:{}, t2.length:{}", t1.length(), t2.length());
						}
						jt.update("update c_chunk set c_fp=?,c_fp_t=? where c_id=?", fp, t1+t2, id);
					}catch(IOException ex) {
						log.warn(ex.getMessage(), ex);
					}
					count++;
				}
				return count;
			});
		}while(c > 0);
	}

}

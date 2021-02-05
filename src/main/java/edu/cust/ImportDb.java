package edu.cust;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.polynomial.Polynomial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImportDb implements CommandLineRunner {
	
	@Autowired
	private Env env;
	
	@Autowired
	private JdbcTemplate jt;
	
	private long totalSize;
	
	private final static int MAX_CHUNK_SIZE = 8192;
	private final static int MIN_CHUNK_SIZE = 32;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		if(env.isDbImported()) {
			log.debug("db is imported");
			return;
		}
		log.debug("dir:{}", env.getDir());
		File dir = new File(env.getDir());
		if(!dir.exists()) {
			log.warn("{} does not exist", dir.getAbsolutePath());
			return;
		}
		Polynomial polynomial = Polynomial.createFromLong(10923124345206883L);

		// Create a windowed fingerprint object with a window size of 48 bytes.
		iterateDir(dir, polynomial);
		log.info("total size is {}", totalSize);
		//splitFile(new File("C:\\eclipse-workspace\\secdedu\\src\\main\\java\\edu\\cust\\util\\DAOTemplate.java"), polynomial);
	}
	
	private void iterateDir(File f, Polynomial polynomial) {
		if(f.isHidden() || f.getName().startsWith(".")) {
			return;
		}
		if(f.isFile()) {
			String chunkList = splitFile(f, polynomial);
			insertFile(f, chunkList);
			return;
		}
		for(File file : f.listFiles()) {
			iterateDir(file, polynomial);
		}
	}
	
	private String splitFile(File f, Polynomial polynomial) {
		RabinFingerprintLongWindowed window = new RabinFingerprintLongWindowed(polynomial, 48);
		StringBuffer sb = new StringBuffer();
		byte[] chunk = new byte[MAX_CHUNK_SIZE];
		int chunkSize = 0;
		//bb.p
		try(InputStream in = new FileInputStream(f)) {
			int n;
			byte[] buf = new byte[4096];
			while((n = in.read(buf)) != -1) {
				for (int i = 0; i < n; i++) {
					if(chunkSize == MAX_CHUNK_SIZE) {
						//log.debug("chunk size is {}", chunkSize);
						sb.append(insertChunk(chunk, chunkSize));
						window.reset();
						chunkSize = 0;
					}
					byte b = buf[i];
					chunk[chunkSize++] = b;
					window.pushByte(b);
					long rfp = window.getFingerprintLong();
					if ((rfp & 0x3ff) == 0 && chunkSize >= MIN_CHUNK_SIZE) {
						//log.debug("chunk size is {}", chunkSize);
						sb.append(insertChunk(chunk, chunkSize));
						window.reset();
						chunkSize = 0;
					}
				}
			}
			if(chunkSize > 0) {
				//log.debug("chunk size is {}", chunkSize);
				sb.append(insertChunk(chunk, chunkSize));
			}
		} catch (IOException ex) {
			log.warn(ex.getMessage(), ex);
		}
		return sb.toString();
	}
	
	private void insertFile(File f, String chunkList) {
		List<Map<String, Object>> list = jt.queryForList("select c_id id from c_file where c_path=?", f.getAbsoluteFile());
		if(list.isEmpty()) {
			jt.update("insert into c_file (c_id,c_path,c_cid_list)values(?,?,?)",
					UUID.randomUUID().toString().replaceAll("-", ""),
					f.getAbsolutePath(), chunkList);
		}else {
			Object id = list.get(0).get("id");
			jt.update("update c_file set c_cid_list=? where c_id=?",
					chunkList, id);
		}
	}
	
	private String insertChunk(byte[] chunk, int chunkSize) {
		totalSize += chunkSize;
		MessageDigest md = DigestUtils.getMd5Digest();
		md.update(chunk, 0, chunkSize);
		String fp = Hex.encodeHexString(md.digest());
		//log.debug("fp:{}", fp);
		List<Map<String, Object>> list = jt.queryForList("select c_length len from c_chunk where c_fp=?", fp);
		if(list.isEmpty() || (Integer)list.get(0).get("len") != chunkSize) {
			jt.update("insert into c_chunk (c_id,c_content,c_length,c_fp)values(?,?,?,?)", (ps)->{
				ps.setString(1, UUID.randomUUID().toString().replaceAll("-", ""));
				ps.setBlob(2, new ByteArrayInputStream(chunk, 0, chunkSize));
				ps.setInt(3, chunkSize);
				ps.setString(4, fp);
			});
		}
		return fp;
	}

}

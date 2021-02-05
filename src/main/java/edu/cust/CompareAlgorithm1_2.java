package edu.cust;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
public class CompareAlgorithm1_2 implements CommandLineRunner {
	@Autowired
	private Env env;
	@Autowired
	private JdbcTemplate jt;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		Pairing pairing = PairingUtil.getPairing(env.getCurvePath());
		Field<?> gf = pairing.getG1();
		List<Tag> ds = prepareDataset(gf);
		
		Field<?> zr = pairing.getZr();
		Element g = PairingUtil.getGenerator(pairing, env.getGenerator());
		Tag sample = Tag.prepareSample(env.getTestSamplePath(), env.getTestSampleAst(), zr, g);
		long t1 = algorithm1(ds, sample, pairing);
		long t2_01 = algorithm2(ds, sample, pairing, 0.1);
		long t2_001 = algorithm2(ds, sample, pairing, 0.01);
		long t2_0001 = algorithm2(ds, sample, pairing, 0.001);
		log.debug("the time of algorithm1 is {}", t1);
		log.debug("the time of algorithm2_01 is {}", t2_01);
		log.debug("the time of algorithm2_001 is {}", t2_001);
		log.debug("the time of algorithm2_0001 is {}", t2_0001);
	}
	
	long algorithm1(List<Tag> ds, Tag obj, Pairing pairing) {
		long s = System.currentTimeMillis();
		ds.parallelStream().forEach((tag)->{
			compareTag(obj, tag, pairing);
		});
		/*for (Tag tag : ds) {
			if(compareTag(obj, tag, pairing)) {
				break;
			}
		}*/
		long t = System.currentTimeMillis() - s;
		return t;
	}
	
	long algorithm2(List<Tag> ds, Tag obj, Pairing pairing, double ratio) {
		long s = System.currentTimeMillis();
		TreeMap<Double, Tag> tm = calculateSimilar(ds, obj);
		int range = (int)(tm.size() * ratio);
		int i = 0;
		ArrayList<Tag> subset = new ArrayList<>();
		for (Iterator<Tag> it = tm.values().iterator(); it.hasNext() && i < range; i++) {
			Tag tag = it.next();
			subset.add(tag);
		}
		subset.parallelStream().forEach((tag)->{
			compareTag(tag, obj, pairing);
		});
		/*for (Iterator<Tag> it = tm.values().iterator(); it.hasNext() && i < range; i++) {
			Tag tag = it.next();
			if(compareTag(tag, obj, pairing)) {
				break;
			}
		}*/
		long t = System.currentTimeMillis() - s;
		return t;
	}
	
	TreeMap<Double, Tag> calculateSimilar(List<Tag> ds, Tag obj){
		TreeMap<Double, Tag> tm = new TreeMap<>();
		for (Tag tag : ds) {
			double dis = 0;
			double[] at1 = tag.at;
			double[] at2 = obj.at;
			for(int i = at1.length - 1; i >= 0; i--) {
				double d = at1[i] - at2[i];
				dis += d * d;
			}
			tm.put(dis, tag);
		}
		return tm;
	}
	
	boolean compareTag(Tag t1, Tag t2, Pairing pairing) {
		Element e1 = pairing.pairing(t1.t1, t2.t2);
		Element e2 = pairing.pairing(t2.t1, t1.t2);
		return e1.isEqual(e2);
	}
	
	List<Tag> prepareDataset(Field<?> g) throws Exception {
		List<Tag> tags = new ArrayList<>();
		List<Map<String, Object>> list = jt.queryForList("select c_as_t ast, c_fp_t fpt from c_chunk");
		for (Map<String, Object> row : list) {
			String ast = (String)row.get("ast");
			String fpt = (String)row.get("fpt");
			Tag t = Tag.prepareTag(ast, fpt, g);
			tags.add(t);
		}
		return tags;
	}
}

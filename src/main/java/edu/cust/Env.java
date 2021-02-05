package edu.cust;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties("env")
@Component
@Data
public class Env {
	
	private boolean dbImported;
	
	private boolean fpCalculated;
	
	private String generator;
	
	private String curvePath;
	
	private String dir;
	
	private String testSamplePath;
	
	private String testSampleAst;

}

package org.miabis.converter.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@ContextConfiguration(locations={"/spring/batch/config/config.xml", "/spring/batch/jobs/job-csv-index.xml"})
public class ConverterCli {
	
	private static Options options; 
	private static String format = "tab";
	private static String clustersNodes = "10.133.0.29:9300";

	public static void main(String[] args) throws ParseException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
	
		Option indexOpt = Option.builder("i")
				.argName("input file")
				.longOpt("index")
				.hasArg()
				.required()
				.desc("Index a file")
				.build();
		
		/*Option formatOpt = Option.builder("f")
				.argName("xml, tab")
				.longOpt("format")
				.hasArg()
				.desc("with -i: input file format")
				.build();*/
		
		Option clustersNodesOpt = Option.builder("c")
				.argName("elastic search cluster")
				.longOpt("cluster")
				.hasArg()
				.desc("with -i: elastic search cluster group. It defaults to "+clustersNodes)
				.build();
		
		Option helpOpt = Option.builder("h")
				.longOpt("help")
				.desc("print this message")
				.build();
		
		options = new Options();
		options.addOption(indexOpt);
		//options.addOption(formatOpt);
		options.addOption(clustersNodesOpt);
		options.addOption(helpOpt);
		
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		
		try{
			cmd = parser.parse( options, args);
		}catch(ParseException exp){
			System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
		}
		
		if(cmd == null || cmd.hasOption("h")){
			printHelp();
			return;
		}
		
		format = cmd.hasOption("f") ? cmd.getOptionValue('f') : format;
		clustersNodes = cmd.hasOption("c") ? cmd.getOptionValue('c') : clustersNodes;
		
		String inputFile = cmd.getOptionValue("i");
		
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"/spring/batch/config/config.xml", "/spring/batch/jobs/job-csv-index.xml"});
		ctx.registerShutdownHook();
		
		Job job = (Job) ctx.getBean("job1");
		JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
		
		JobParametersBuilder pb = new JobParametersBuilder();
		pb.addString("tab.input", "file:"+inputFile);
		pb.addString("clusters.nodes", clustersNodes);
		
		jobLauncher.run(job, pb.toJobParameters());
	}
	
	private static void printHelp(){
		new HelpFormatter().printHelp("miabis-converter", options);
	}

}

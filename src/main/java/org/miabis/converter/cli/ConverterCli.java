 package org.miabis.converter.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.miabis.converter.batch.util.Util;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConverterCli {
	
	private static Options options;
	private static String clusterNodes = "localhost:9300";
	private static String clusterName = "elasticsearch";
	private static String delimiter = Util.DELIMITER_TAB;

	public static void main(String[] args) throws ParseException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
	
		Option indexOpt = Option.builder("i")
				.argName("input file(s)")
				.longOpt("index")
				.hasArgs()
				.desc("indexes a set of files. If only one file is supplied it asumes is a MIABIS TAB file, else five files must be supplied (sample, biobank, saple collection, study, contact information). The list of files must be separated by a space.")
				.build();
		
		Option clusterNodesOpt = Option.builder("c")
				.argName("elastic search cluster")
				.longOpt("cluster")
				.hasArg()
				.desc("with -i: elastic search cluster group. It defaults to "+clusterNodes)
				.build();
		
		Option clusterNameOpt = Option.builder("z")
				.argName("elastic search cluster name")
				.longOpt("cname")
				.hasArg()
				.desc("with -i: elastic search cluster name. It defaults to "+clusterName)
				.build();
		
		Option transformOpt = Option.builder("t")
				.argName("input files")
				.hasArgs()
				.longOpt("transform")
				.desc("transforms a set of files to MIABIS TAB. Five files must be supplied (sample, biobank, saple collection, study, contact information). The list of files must be separated by a space.")
				.build();
		
		Option delimiterOpt = Option.builder("d")
				.argName("column delimiter")
				.longOpt("delimiter")
				.hasArg()
				.desc("with -t, -i: column delimiter. It defaults to TAB")
				.build();
		
		Option mapOpt = Option.builder("m")
				.argName("map file")
				.longOpt("map")
				.hasArg()
				.desc("with -t, -i: miabis mapping file.")
				.build();
		
		Option helpOpt = Option.builder("h")
				.longOpt("help")
				.desc("print this message")
				.build();
		
		Option nameOpt = Option.builder("n")
				.argName("index name")
				.longOpt("name")
				.hasArg()
				.desc("with -i: index name. Biobank name is recomended.")
				.build();
		
		options = new Options();
		
		options.addOption(indexOpt);
		options.addOption(clusterNodesOpt);
		options.addOption(clusterNameOpt);
		options.addOption(helpOpt);
		
		options.addOption(transformOpt);
		options.addOption(delimiterOpt);
		options.addOption(mapOpt);
		options.addOption(nameOpt);
		
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
		
		if(cmd.hasOption("i")){
			
			if(!cmd.hasOption("n")){
				System.err.println( "index name unspecified");
				printHelp();
				return;
			}
			
			clusterNodes = cmd.hasOption("c") ? cmd.getOptionValue("c") : clusterNodes;
			clusterName = cmd.hasOption("z") ? cmd.getOptionValue("z") : clusterName;
			
			String[] files = cmd.getOptionValues("i");
			
			if(files.length == 1){
				AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"classpath*:**/config.xml", "classpath*:**/job-csv-index.xml"});
				ctx.registerShutdownHook();
				
				Job job = (Job) ctx.getBean("job1");
				JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
				
				JobParametersBuilder pb = new JobParametersBuilder();
				pb.addString("tab.input", "file:" + files[0]);
				pb.addString("cluster.nodes", clusterNodes);
				pb.addString("cluster.name", clusterName);
				pb.addString("columns", Util.COLUMNS);
				pb.addString("index.name", cmd.getOptionValue("n").toLowerCase());
				
				jobLauncher.run(job, pb.toJobParameters());
			}else if(files.length == 5){
				
				if(!cmd.hasOption("m")){
					System.out.println("No mapping file defined.");
					return;
				} 
				
				String map = cmd.getOptionValue("m");
				delimiter = cmd.hasOption("d") ? cmd.getOptionValue('d') : delimiter;
				
				AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"classpath*:**/database.xml", "classpath*:**/job-csv-db-index.xml"});
				ctx.registerShutdownHook();
				
				JobParametersBuilder pb = new JobParametersBuilder();
				pb.addString("sample", "file:" + files[0]);
				pb.addString("biobank",  "file:" + files[1]);
				pb.addString("sampleCollection", "file:" + files[2]);
				pb.addString("study", "file:" + files[3]);
				pb.addString("contactInfo", "file:" + files[4]);
				
				pb.addString("cluster.nodes", clusterNodes);
				pb.addString("cluster.name", clusterName);
				pb.addString("index.name", cmd.getOptionValue("n").toLowerCase());
				
				//Map
				pb.addString("map", map);
				
				JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
				Job job = (Job) ctx.getBean("job1");
				
				jobLauncher.run(job, pb.toJobParameters());
			}else{
				printHelp();
			}
			
			
			
		}else if(cmd.hasOption("t")){
			
			if(!cmd.hasOption("m")){
				System.out.println("No mapping file defined.");
				return;
			} 
			
			String map = cmd.getOptionValue("m");
			delimiter = cmd.hasOption("d") ? cmd.getOptionValue('d') : delimiter;
			
			String[] files = cmd.getOptionValues("t");
			
			AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"classpath*:**/database.xml", "classpath*:**/job-csv-db.xml"});
			ctx.registerShutdownHook();
			
			JobParametersBuilder pb = new JobParametersBuilder();
			pb.addString("sample", "file:" + files[0]);
			pb.addString("biobank",  "file:" + files[1]);
			pb.addString("sampleCollection", "file:" + files[2]);
			pb.addString("study", "file:" + files[3]);
			pb.addString("contactInfo", "file:" + files[4]);
			
			//Map
			pb.addString("map", map);
			
			//Output file
			pb.addString("tab.output", "file:Miabis.tab");
			
			JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
			Job job = (Job) ctx.getBean("job1");
			
			jobLauncher.run(job, pb.toJobParameters());
			
		}else{
			printHelp();
		}	

		
	}
	
	private static void printHelp(){
		new HelpFormatter().printHelp("miabis-converter", options);
	}

}

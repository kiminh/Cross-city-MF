package main_output;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import algorithms.ItemPopularity;
import algorithms.baseline_LCE;
import algorithms.baseline_LCE;


public class crosscity_baseline_lce_kout extends main.main_crosscity {
	public static void main(String argv[]) throws IOException {
		String method = "none";
		double w0 = 0.1;
		boolean showProgress = true;
		boolean showLoss = true;
		int factors = 10;
		int maxIter = 500;
		double reg = 0.01;
		double alpha = 0;

		double lr = 0.001; 
		boolean adaptive = false;
		String datafile = "data/cross_part";
		String catefile = "data/categoty_info";
		int showbound = 100;
		int showcount = 10;
		int nativecity = 1100;
		int tourcity = 5401;
		double bigalpha=0.8;
		double bigbeta=100;

		if (argv.length > 0) {
			//dataset_name = argv[0];
			//method = argv[1];
			w0 = Double.parseDouble(argv[2]);
			showProgress = Boolean.parseBoolean(argv[3]);
			showLoss = Boolean.parseBoolean(argv[4]);
			factors = Integer.parseInt(argv[5]);
			maxIter = Integer.parseInt(argv[6]);
			reg = Double.parseDouble(argv[7]);
			if (argv.length > 8) alpha = Double.parseDouble(argv[8]);
			datafile = argv[9];
			showbound = Integer.parseInt(argv[10]);
			showcount = Integer.parseInt(argv[11]);

			nativecity = Integer.parseInt(argv[12]);
			tourcity = Integer.parseInt(argv[13]);
			bigalpha = Double.parseDouble(argv[14]);
			bigbeta = Double.parseDouble(argv[15]);
		}
	
		ReadRatings_GlobalSplit_notimestamp_forLCE(datafile, 0.2,nativecity,tourcity);
		System.out.printf("%s: showProgress=%s, factors=%d, maxIter=%d, reg=%.6f, w0=%.6f, alpha=%.4f\n",
				method, showProgress, factors, maxIter, reg, w0, alpha);
		System.out.println("this algorithm is LCE baseline  for new user");
		System.out.printf("new para:  bigalpha=%f bigbeta=%f\n",bigalpha,bigbeta);
		System.out.println("====================================================");
		Long start = System.currentTimeMillis();
		ItemPopularity popularity = new ItemPopularity(trainMatrix, testRatings, topK, threadNum);
		popularity.evaluatefor82crosscity(testRatings,start,nativeUsers,tourPois);
		popularity.evaluatefor82crosscity_showFactnum(testRatings,nativeUsers,tourPois);
		double init_mean = 0;
		double init_stdev = 0.01;
	
		if (true) {
			baseline_LCE als = new baseline_LCE(trainMatrix, testRatings, topK, threadNum,
					factors, maxIter, w0, reg, init_mean, init_stdev, showProgress, showLoss,showbound,showcount);
			als.sethashset(nativeUsers,tourPois);
			als.setbigw(new double [] {bigalpha,bigbeta});

			als.initialize();
			als.buildcrosscityModel();
			als.evaluatefor82crosscity(testRatings,start,nativeUsers,tourPois);
			
			String datafile_write = datafile.replace('/', '_');
			String outpath = "Kout/lce_"+datafile_write+"_w0_"+w0+"_reg_"+reg+
					"_K_"+factors+"_city"+nativecity;
			File outfile = new File("Kout/");
			if (!outfile.exists()) {
				outfile.mkdir();
			}	
			als.evaluatefororder_output(testRatings,start,nativeUsers,tourPois,outpath);
			System.out.printf("finish write in file: %s \n",outpath);
		}
	} // end main
}

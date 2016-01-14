package stem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import masoncsc.util.Stats;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import stem.tuning.MersenneTwisterFastApache;

import jsc.goodnessfit.KolmogorovTest;
import jsc.independentsamples.SmirnovTest;

public class KSTest
{
	
	public static double[][] readFile(File f) {
		double[][] a = new double[2][140];
		
		try {
			BufferedReader in = new BufferedReader(new FileReader (f));
			
			int index = 0;
			String line = null;
			while ((line = in.readLine()) != null)
			{
				String[] tokens = line.split(",");
				a[0][index] = Double.parseDouble(tokens[0]);
				a[1][index] = Double.parseDouble(tokens[1]);
				index++;
				
			}
		}
		catch (Exception e) {}
		
		return a;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String path = "/Users/jharrison/research/stem/exp/badks/";
		File[] files = new File(path).listFiles();
		
		SmirnovTest ksTest;
		KolmogorovSmirnovTest apacheKS = new KolmogorovSmirnovTest();
		
		int count = 0;
		
		for (File f : files) {
			if (!f.getName().startsWith("part_"))
				continue;
		
			// skip the duplicates
			if (++count % 2 == 0)
				continue;
			
			double [][]a = readFile(f);

			double sp=0, ks=0, ksA=0, areaBetween=0;

			areaBetween = Stats.calcAreaBetweenECDFs(a[0], a[1], true);

			ksA = apacheKS.kolmogorovSmirnovStatistic(a[0], a[1]);
			
			try {
				ksTest = new SmirnovTest(a[0], a[1]);
//				sp = ksTest.getSP();
				ks = ksTest.getTestStatistic();
			}
			catch(Exception e) {
//				ks = apacheKS.kolmogorovSmirnovStatistic(a[0], a[1]);
			}
			
			System.out.format("%s -- KS: %.3f, KSa: %.3f, AreaBetween: %.3f\n", f.toString(), ks, ksA, areaBetween);
			
			// read the file
		}
	}

}

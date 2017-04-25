package util.browserPrediction;

import java.util.ArrayList;

import beans.PredictionBean;
import datastructures.Fingerprint;
import eu.bitwalker.useragentutils.UserAgent;
import fingerprintVector.BrowserOsGuessFingerprintNumericRepresentation;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * Class for classifying browsers without using user-agent strings.
 * Needs to be initialised before being run.
 */
public class Predictor {
	private static Attribute browserClassAttribute;
	private static Attribute osClassAttribute;
	private static ArrayList<Attribute> browserAttributes;
	private static ArrayList<Attribute> osAttributes;
	private static Classifier browserClassifier;
	private static Classifier osClassifier;
	
	private static final ArrayList<String> browserGroupsWeCareAbout;
	static{
		browserGroupsWeCareAbout = new ArrayList<String>();
		browserGroupsWeCareAbout.add("CHROME");
		browserGroupsWeCareAbout.add("EDGE");
		browserGroupsWeCareAbout.add("FIREFOX");
		browserGroupsWeCareAbout.add("IE");
		browserGroupsWeCareAbout.add("OPERA");
		browserGroupsWeCareAbout.add("SAFARI");
	}
	
	private static final ArrayList<String> osGroupsWeCareAbout;
	static{
		osGroupsWeCareAbout = new ArrayList<String>();
		osGroupsWeCareAbout.add("WINDOWS");
		osGroupsWeCareAbout.add("IOS");
		osGroupsWeCareAbout.add("LINUX");
		osGroupsWeCareAbout.add("MAC_OS_X");
		osGroupsWeCareAbout.add("ANDROID");
	}
	
	public static void initialise(String browserModelFilePath, String osModelFilePath, String fontsPath) throws Exception{
		browserAttributes = new ArrayList<Attribute>();
		osAttributes = new ArrayList<Attribute>();
		browserClassAttribute = new Attribute("className", browserGroupsWeCareAbout);
		osClassAttribute = new Attribute("className", osGroupsWeCareAbout);
		browserAttributes.add(browserClassAttribute);
		osAttributes.add(osClassAttribute);
		for(int i = 1; i <= 5300; ++i){
			browserAttributes.add(new Attribute(Integer.toString(i)));
			osAttributes.add(new Attribute(Integer.toString(i)));
		}
		
		browserClassifier = (Classifier) SerializationHelper.read(browserModelFilePath);
		osClassifier = (Classifier) SerializationHelper.read(osModelFilePath);
		
		BrowserOsGuessFingerprintNumericRepresentation.initialise(fontsPath);
	}
	
	public static String browserClassify(double classifyMeArray[]) throws Exception{
		Instance classifyMe = new DenseInstance(1.0, classifyMeArray);
		Instances classifyMeDataSet = new Instances("testingDataset", browserAttributes, 0);
		classifyMeDataSet.setClass(browserClassAttribute);
		classifyMe.setDataset(classifyMeDataSet);
		classifyMe.setClassMissing();
		
		double prediction = browserClassifier.classifyInstance(classifyMe);
		return browserClassAttribute.value((int)Math.ceil(prediction));
	}
	
	public static String osClassify(double classifyMeArray[]) throws Exception{
		Instance classifyMe = new DenseInstance(1.0, classifyMeArray);
		Instances classifyMeDataSet = new Instances("testingDataset", osAttributes, 0);
		classifyMeDataSet.setClass(osClassAttribute);
		classifyMe.setDataset(classifyMeDataSet);
		classifyMe.setClassMissing();
		
		double prediction = osClassifier.classifyInstance(classifyMe);
		return osClassAttribute.value((int)Math.ceil(prediction));
	}
	
	public static PredictionBean getPredictionBean(Fingerprint fp) throws Exception{
		PredictionBean predictionBean = new PredictionBean();
		UserAgent ua = new UserAgent(fp.getUser_agent());
		
		BrowserOsGuessFingerprintNumericRepresentation fnr = new BrowserOsGuessFingerprintNumericRepresentation(
				fp.getAllHeaders(), fp.getFontsJS_CSS(), 
				fp.getSuperCookieLocalStorage(), fp.getSuperCookieSessionStorage(), fp.getSuperCookieUserData(),
				fp.getHstsEnabled(), fp.getIndexedDBEnabled(), fp.getMathTan(), fp.isUsingTor(), fp.getTbbVersion(),
				fp.getTouchPoints(), fp.getTouchEvent(), fp.getTouchStart());
		double fingerprintArray[];
		{
			double fingerprintArrayRaw[] = fnr.getFingerprintArray();
			fingerprintArray = new double[fingerprintArrayRaw.length + 1];
			fingerprintArray[0] = 0;
			for(int i = 0; i < fingerprintArrayRaw.length; ++i){
				fingerprintArray[i + 1] = fingerprintArrayRaw[i];
			}
		}
		
		predictionBean.setUseragentSpecifiedBrowser(ua.getBrowser().getGroup().toString());
		predictionBean.setBrowserPrediction(browserClassify(fingerprintArray));
		predictionBean.setUseragentSpecifiedOs(ua.getOperatingSystem().getGroup().toString());
		predictionBean.setOsPrediction(osClassify(fingerprintArray));
		
		return predictionBean;
	}
}

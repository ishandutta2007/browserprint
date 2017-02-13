package util.browserPrediction;

import java.util.ArrayList;

import beans.BrowserPredictionBean;
import datastructures.Fingerprint;
import eu.bitwalker.useragentutils.UserAgent;
import fingerprintVector.BrowserGuessFingerprintNumericRepresentation;
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
public class BrowserPredictor {
	private static Attribute classAttribute;
	private static ArrayList<Attribute> attributes;
	private static Classifier classifier;
	
	private static final ArrayList<String> browserGroupsWeCareAbout;
	static{
		browserGroupsWeCareAbout = new ArrayList<String>();
		browserGroupsWeCareAbout.add("FIREFOX");
		browserGroupsWeCareAbout.add("CHROME");
		browserGroupsWeCareAbout.add("SAFARI");
		browserGroupsWeCareAbout.add("OPERA");
		browserGroupsWeCareAbout.add("EDGE");
		browserGroupsWeCareAbout.add("IE");
	}
	
	public static void initialise(String modelFilePath, String fontsPath) throws Exception{
		attributes = new ArrayList<Attribute>();
		classAttribute = new Attribute("className", browserGroupsWeCareAbout);
		attributes.add(classAttribute);
		for(int i = 1; i <= 5300; ++i){
			attributes.add(new Attribute(Integer.toString(i)));
		}
		
		classifier = (Classifier) SerializationHelper.read(modelFilePath);
		
		BrowserGuessFingerprintNumericRepresentation.initialise(fontsPath);
	}
	
	public static String classify(Fingerprint fp) throws Exception{
		BrowserGuessFingerprintNumericRepresentation fnr = new BrowserGuessFingerprintNumericRepresentation(fp.getAllHeaders(), fp.getFontsJS_CSS(), fp.getSuperCookieLocalStorage(), fp.getSuperCookieSessionStorage(), fp.getSuperCookieUserData(), fp.getHstsEnabled(), fp.getIndexedDBEnabled(), fp.getMathTan(), fp.isUsingTor(), fp.getTbbVersion(), fp.getTouchPoints(), fp.getTouchEvent(), fp.getTouchStart());
		
		double fingerprintArrayRaw[] = fnr.getFingerprintArray();
		double fingerprintArray[] = new double[fingerprintArrayRaw.length + 1];
		fingerprintArray[0] = 0;
		for(int i = 0; i < fingerprintArrayRaw.length; ++i){
			fingerprintArray[i + 1] = fingerprintArrayRaw[i];
		}
		return classify(fingerprintArray);
	}
	
	public static String classify(double classifyMeArray[]) throws Exception{
		Instance classifyMe = new DenseInstance(1.0, classifyMeArray);
		Instances classifyMeDataSet = new Instances("testingDataset", attributes, 0);
		classifyMeDataSet.setClass(classAttribute);
		classifyMe.setDataset(classifyMeDataSet);
		classifyMe.setClassMissing();
		
		return classAttribute.value((int)Math.ceil(classifier.classifyInstance(classifyMe)));
	}
	
	public static BrowserPredictionBean getPredictionBean(Fingerprint fp) throws Exception{
		BrowserPredictionBean predictionBean = new BrowserPredictionBean();
		predictionBean.setBrowserPrediction(classify(fp));
		
		UserAgent ua = new UserAgent(fp.getUser_agent());
		predictionBean.setUseragentSpecifiedBrowser(ua.getBrowser().getGroup().toString());
		
		return predictionBean;
	}
}

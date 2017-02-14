package util.browserPrediction;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class PredictorInitialiser implements ServletContextListener {
	@Override
    public void contextInitialized(ServletContextEvent event) {
		//Do stuff when the server starts up.
		//Initialise directory for browser guesser
		ServletContext context = event.getServletContext();
		String browserModelPath = context.getRealPath("/WEB-INF/browserOsGuessFiles/browserGuess.randomForest.model");
		String osModelPath = context.getRealPath("/WEB-INF/browserOsGuessFiles/osGuess.randomForest.model");
		String fontsPath = context.getRealPath("/WEB-INF/browserOsGuessFiles/fontsIndices.fontsJS_CSS");
		try {
			Predictor.initialise(browserModelPath, osModelPath, fontsPath);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Do stuff during server shutdown.
    }
}

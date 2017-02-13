package util.browserPrediction;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class BrowserPredictorInitialiser implements ServletContextListener {
	@Override
    public void contextInitialized(ServletContextEvent event) {
		//Do stuff when the server starts up.
		//Initialise directory for browser guesser
		ServletContext context = event.getServletContext();
		String modelPath = context.getRealPath("/WEB-INF/browserGuessFiles/browserGuess.randomForest.model");
		String fontsPath = context.getRealPath("/WEB-INF/browserGuessFiles/fontsIndices.fontsJS_CSS");
		try {
			BrowserPredictor.initialise(modelPath, fontsPath);
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

package servlets.hstsSuperCookie;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HSTSServlet
 */
public class HstsSuperCookieExistingIDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Pattern pathRegexPattern;

	private static final int ID_IMAGE_WIDTH_PER_CHAR = 19;
	private static final int ID_IMAGE_HEIGHT = 30;
	private static final int ID_IMAGE_FONT_SIZE = 30;
	private static final int ID_IMAGE_TEXT_X_POSITION = 0;
	private static final int ID_IMAGE_TEXT_Y_POSITION = ID_IMAGE_HEIGHT - 5;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HstsSuperCookieExistingIDServlet() {
		super();
		
		this.pathRegexPattern = Pattern.compile("^/hstsSuperCookie/existingID/([10]{" + (HstsSuperCookieStartServlet.ID_LENGTH - 1) + "})$");
	}

	/**
	 * Serves requests for hsts[1 to ID_LENGTH].browserprint.info/hstsSuperCookie/existingID/*
	 * This script extracts the ID from the client one bit at a time.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int extractedBit;
		if(request.getServerPort() == 80){
			extractedBit = 0;
		}
		else if(request.getServerPort() == 443){
			extractedBit = 1;
		}
		else{
			System.err.println("HstsSuperCookieExistingIDServlet: Unexpected protocol.");
			response.sendError(404);
			return;
		}

		int subdomainNumber;
		{
			String subdomain = request.getServerName();
			Matcher domainRegexMatcher = Pattern.compile("^hsts(\\d+)\\..*$").matcher(subdomain);
			if(domainRegexMatcher.matches() == false){
				System.err.println("HstsSuperCookieExistingIDServlet: Invalid subdomain <" + subdomain + ">.");
				response.sendError(404);
				return;
			}
			subdomainNumber = Integer.parseInt(domainRegexMatcher.group(1));
		}
		int subdomainGroupIndex = (subdomainNumber - 1) % HstsSuperCookieStartServlet.ID_LENGTH + 1;//1 = 1; 2 = 2; 3 = 3; 4 = 4; 5 = 1
		
		if(subdomainGroupIndex < HstsSuperCookieStartServlet.ID_LENGTH){
			//Redirect the client to the next subdomain in the chain.
			response.sendRedirect("http://hsts" + (subdomainNumber + 1) + "." + getServletContext().getInitParameter("websiteBaseURL") + request.getRequestURI() + extractedBit);
		}
		else{//Must be == ID_LENGTH
			//This is the last subdomain in the ID extraction chain.
			
			//First, get the full ID.
			Matcher pathRegexMatcher = pathRegexPattern.matcher(request.getRequestURI());
			if(pathRegexMatcher.matches() == false){
				System.err.println("HstsSuperCookieExistingIDServlet: Invalid path. Must contain valid ID of length " + HstsSuperCookieStartServlet.ID_LENGTH + ". Path = <" + request.getRequestURI() + ">.");
				response.sendError(404);
				return;
			}
			String id = pathRegexMatcher.group(1) + extractedBit;
			
			//Output the ID.
			if(id.length() % 4 == 0){
				//We've got a multiple of 4 bits so we can convert the ID bit string to something more compact, a hex.
				id = Integer.toHexString(Integer.parseInt(id, 2));
			}
			
			int image_width = id.length() * ID_IMAGE_WIDTH_PER_CHAR;
			BufferedImage bImage = new BufferedImage(image_width, ID_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bImage.createGraphics();
			g2d.setBackground(Color.WHITE);
			g2d.clearRect(0, 0, image_width, ID_IMAGE_HEIGHT);
			g2d.setColor(Color.black);
			g2d.setFont(new Font("Liberation Mono", Font.PLAIN, ID_IMAGE_FONT_SIZE));
			g2d.drawString(id, ID_IMAGE_TEXT_X_POSITION, ID_IMAGE_TEXT_Y_POSITION);
			
			response.setContentType("image/png");
			ImageIO.write(bImage, "png", response.getOutputStream());
			
			//response.sendRedirect("http://dummyimage.com/50x30/fff/000&text=" + id);
			
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
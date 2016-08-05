package servlets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import datastructures.ContrastCaptcha;

/**
 * Servlet implementation class CaptchaServlet
 */
public class CaptchaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CaptchaServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		
		/*
		 * Create CAPTCHA.
		 */
		BufferedImage bImage;
		ContrastCaptcha captchaSolution;
		{
			final int MIN_CAPTCHA_LENGTH = 6;
			final int CAPTCHA_LENGTH_VARIABILITY = 0;
			//final int MIN_SPACES = 0;
			//final int MAX_SPACES = 0;
			final int FONT_SIZE = 20;
			final int CHARACTER_WIDTH = 13;
			final int CHARACTER_HEIGHT = 14;
			final int LEFT_PADDING = CHARACTER_WIDTH;
			final int RIGHT_PADDING = LEFT_PADDING;
			final int CAPTCHA_TOP_PADDING = CHARACTER_HEIGHT;
			final int CAPTCHA_BOTTOM_PADDING = CAPTCHA_TOP_PADDING;
			final int CAPTCHA_WIDTH = LEFT_PADDING + CHARACTER_WIDTH * (MIN_CAPTCHA_LENGTH + CAPTCHA_LENGTH_VARIABILITY) + RIGHT_PADDING;
			final int CAPTCHA_HEIGHT = CHARACTER_HEIGHT + CAPTCHA_TOP_PADDING + CAPTCHA_BOTTOM_PADDING;
			final int MIN_CHAR_HEIGHT = CHARACTER_HEIGHT;
			final int MAX_CHAR_HEIGHT = CAPTCHA_HEIGHT - CHARACTER_HEIGHT - 4;
			final int MIN_NUM_LINES = 3;
			final int NUM_LINES_VARIABILITY = 1;
			final int LINES_MAX_X = CAPTCHA_WIDTH;
			final int LINES_MAX_Y = CAPTCHA_HEIGHT;
			
			/*
			 * Create Graphics2D object.
			 */
			bImage = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bImage.createGraphics();
			
			/*
			 * Draw the image.
			 */
			final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			Random rand = new SecureRandom();
			
			char captchaChars[] = new char[MIN_CAPTCHA_LENGTH + rand.nextInt(CAPTCHA_LENGTH_VARIABILITY + 1)];
			for(int i = 0; i < captchaChars.length; ++i){
				captchaChars[i] = ALPHABET.charAt(rand.nextInt(ALPHABET.length()));
			}
			
			/*
			 * We want to add spaces, but they can't be the first or last character.
			 * NOTE: We don't actually want spaces because it messes up the colours.
			 */
			/*{
				int spacesIndices[] = new int[captchaChars.length - 2];
				for(int i = 0; i < spacesIndices.length; ++i){
					spacesIndices[i] = i + 1;
				}
				//Partial Fisher-Yates shuffle.
				int num_spaces = rand.nextInt(MAX_SPACES - MIN_SPACES + 1) + MIN_SPACES;
				for(int i = 0; i < num_spaces; ++i){
					int j = rand.nextInt(spacesIndices.length - i);
					
					//Swap i and j.
					int tmp = spacesIndices[i];
					spacesIndices[i] = spacesIndices[j];
					spacesIndices[j] = tmp;
				}
				for(int i = 0; i < num_spaces; ++i){
					captchaChars[spacesIndices[i]] = ' ';
				}
			}*/
			
			//We want to add spaces, but they can't be the first or last character.
			Color charColours[] = new Color[captchaChars.length];
			ContrastCaptcha.CaptchaColours charColoursForExport[] = new ContrastCaptcha.CaptchaColours[captchaChars.length];//For checking the colours of the CAPTCHA.
			{
				//Initialise array.
				for(int i = 0; i < charColours.length; ++i){
					charColours[i] = Color.BLACK;
					charColoursForExport[i] = ContrastCaptcha.CaptchaColours.UNIMPORTANT;
				}
				
				//Choose one char that's 0.1%, 10%, and 40%
				charColours[1] = new Color(0.85f, 0.85f, 0.85f, 1f);
				charColoursForExport[1] = ContrastCaptcha.CaptchaColours.LIGHTEST_GREY;
				charColours[2] = new Color(0.8f, 0.8f, 0.8f, 1f);
				charColoursForExport[2] = ContrastCaptcha.CaptchaColours.LIGHT_GREY;
				charColours[3] = new Color(0.6f, 0.6f, 0.6f, 1f);
				//charColoursForExport[3] = CaptchaColours.DARK_GREY;
				for(int i = charColours.length - 2; i > 1; --i){
					int j = rand.nextInt(i + 1) + 1;
					
					//Swap i and j.
					{
						Color tmp = charColours[i];
						charColours[i] = charColours[j];
						charColours[j] = tmp;
					}
					{
						ContrastCaptcha.CaptchaColours tmp = charColoursForExport[i];
						charColoursForExport[i] = charColoursForExport[j];
						charColoursForExport[j] = tmp;
					}
				}
			}
			captchaSolution = new ContrastCaptcha(captchaChars, charColoursForExport);
			
			g2d.setBackground(Color.WHITE);
			g2d.clearRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
			g2d.setColor(Color.black);
			g2d.setFont(new Font("Liberation Mono", Font.PLAIN, FONT_SIZE));
			
			/*
			 * Draw the characters.
			 */
			int x_position = LEFT_PADDING + (((MIN_CAPTCHA_LENGTH + CAPTCHA_LENGTH_VARIABILITY) - captchaChars.length) / 2) * CHARACTER_WIDTH;
			for(int i = 0; i < captchaChars.length; ++i){
				g2d.setColor(charColours[i]);
				int y_position = rand.nextInt(MAX_CHAR_HEIGHT + 1) + MIN_CHAR_HEIGHT;
				g2d.drawString(Character.toString(captchaChars[i]), x_position, y_position);
				x_position += CHARACTER_WIDTH;
			}
			
			/*
			 * Draw lines.
			 */
			int num_lines = rand.nextInt(NUM_LINES_VARIABILITY + 1) + MIN_NUM_LINES;
			for(int i = 0; i < num_lines; ++i){
				int x1 = rand.nextInt(LINES_MAX_X + 1);
				int y1 = rand.nextInt(LINES_MAX_Y + 1);
				int x2 = rand.nextInt(LINES_MAX_X + 1);
				int y2 = rand.nextInt(LINES_MAX_Y + 1);
				g2d.drawLine(x1, y1, x2, y2);
			}
		}

		session.setMaxInactiveInterval(240);//Session invalidates after 240 seconds.
		session.setAttribute("captcha", captchaSolution);
		
		//Prevent from caching
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		
		//Return image
		response.setContentType("image/png");
		ImageIO.write(bImage, "png", response.getOutputStream());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

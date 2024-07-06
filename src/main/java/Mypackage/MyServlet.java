package Mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import javax.print.DocFlavor.INPUT_STREAM;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String inputdata= request.getParameter("userInput");  // it gets the user input from input feild
		
		//API Setup
		String apikey = "69d67446ecbae8493170c9f6a01d2152";  // api id created on myweatherapi website
		
		//Get the city from the input
		String city= request.getParameter("city");
		
		//Create the URL for the OpenWeatherMap API Request
		String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apikey;
		try {
		// API Integration
		URL url= new URL(apiURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
		// Reading the data from nerwork
		InputStream inputstream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputstream);
		
		// Want to store in string we use string builder
		StringBuilder responseContent = new StringBuilder();
		
		// Input lene ke liye from the reader, will create scanner object
		Scanner sc = new Scanner(reader);
		while(sc.hasNext()) {
			responseContent.append(sc.nextLine());
		}
		sc.close();
		//System.out.println(responseContent);
		
		//TypeCasting = Parsing the data into Json
		Gson gson = new Gson();
		JsonObject jsonobject = gson.fromJson(responseContent.toString(),JsonObject.class);
		//System.out.println(jsonobject);
		
		//Date And time
		long datetimestamp = jsonobject.get("dt").getAsLong() * 1000;
		String date = new Date(datetimestamp).toString();
		
		//Tempreature
		double tempreature = jsonobject.getAsJsonObject("main").get("temp").getAsDouble();
		int tempreaturecelsius = (int) (tempreature - 273.15);
		
		//Humidity
		int humidity = jsonobject.getAsJsonObject("main").get("humidity").getAsInt();
		
		//Wind Speed
		double windspeed = jsonobject.getAsJsonObject("wind").get("speed").getAsDouble();
		
		//Weather Conditioned
		String weathercondition = jsonobject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		
		// Set the data as request attribute (for sending to the jsp page)
		request.setAttribute("date", date);
		request.setAttribute("city", city);
		request.setAttribute("tempreature", tempreaturecelsius);
		request.setAttribute("weathercondition",weathercondition);
		request.setAttribute("humidity", humidity);
		request.setAttribute("windspeed", windspeed);
		request.setAttribute("weatherData",responseContent.toString());
		
		connection.disconnect();
	} catch (IOException e) {
		e.printStackTrace();
		}

		// Forward the request to the weather.jsp page for rendering
		request.getRequestDispatcher("index.jsp").forward(request, response);
}
}

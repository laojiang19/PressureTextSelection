
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;


@SuppressWarnings("serial")
public class MainSketch extends PApplet {
	//public TouchClient client;
	public PFont pFont;
	public Vector<float[]> circles;
	boolean showFrameRate = true;
	float textSize;
	Socket socket;
	BufferedReader input;
	SocketReader reader;

	public void setup() {
		size(screenWidth, screenHeight, P3D);
		//client = new TouchClient(this, true, false);
		//client.setDrawTouchPoints(false);
		//client.applyZonesMatrix(true);
		//client.setDrawTouchPointsSize(getHeight()/30);
		pFont = loadFont("Aharoni-Bold-48.vlw");
		textSize = getHeight()/20;
		circles = new Vector<float[]>();
		float[] empty = {0, 0, 0};
		for(int i = 0; i < 5; i++){
			circles.add(empty);
		}
		
		try {
			socket = new Socket(InetAddress.getLocalHost(), 3333); //127.0.0.1, 3333);
			//socket.setSoTimeout(100);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("Connected to Server.");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reader = new SocketReader(input, this);
		reader.start();



	}

	public void draw(){
		background(255);

		if(showFrameRate){
			textFont(pFont, textSize);
			textAlign(PConstants.LEFT, PConstants.BOTTOM);
			fill(0);
			text("FPS:\n" + Integer.toString((int)frameRate), 0, 2*getHeight()/3);
		}
		
		if(!circles.isEmpty()){
			for(float[] touch: circles){
				this.color(0);
				this.fill(0);
				this.ellipse(touch[0]*this.getWidth(), touch[1]*this.getHeight(), touch[2], touch[2]);
			}

		}

	}

	public static void main(String[] args) {
		PApplet.main(new String[]{"--present", "MainSketch"});
	}
}

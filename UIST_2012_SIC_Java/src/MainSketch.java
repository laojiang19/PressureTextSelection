
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import arb.soundcipher.SoundCipher;

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
	SoundCipher sound;
	float[] pitchSet = {57, 60, 60, 60, 62, 64, 67, 67, 69, 72, 72, 72, 74, 76, 79};
	float setSize = pitchSet.length;
	int[] instruments;

	
	public void setup() {
		size(screenWidth, screenHeight, P3D);
		//client = new TouchClient(this, true, false);
		//client.setDrawTouchPoints(false);
		//client.applyZonesMatrix(true);
		//client.setDrawTouchPointsSize(getHeight()/30);
		pFont = loadFont("Aharoni-Bold-48.vlw");
		textSize = getHeight()/20;
		circles = new Vector<float[]>();
		sound = new SoundCipher(this);
		instruments = new int[]{(int) random(128), (int) random(128), (int) random(128), (int) random(128)};

		
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
			int i = 0;
			for(float[] touch: circles){
				if(touch[2] > 10){
					this.color(0);
					this.fill(0);
					float x = touch[0]*this.getWidth();
					float y = touch[1]*this.getHeight();
					
					this.ellipse(x, y, touch[2], touch[2]);
					
					sound.instrument(instruments[i]);
					sound.playNote(x, y, touch[2]);
					//sound.playNote(pitchSet[(int)random(setSize)], touch[2], 1);
					i++;
				}
			}
		}

	}

	public static void main(String[] args) {
		PApplet.main(new String[]{"--present", "MainSketch"});
	}
}

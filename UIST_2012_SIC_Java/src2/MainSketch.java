
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import beads.*;

//import arb.soundcipher.SoundCipher;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PVector;


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
	//SoundCipher sound;
	float[] pitchSet = {57, 60, 60, 60, 62, 64, 67, 67, 69, 72, 72, 72, 74, 76, 79};
	float setSize = pitchSet.length;
	int[] instruments;
	
	Glide carrierFreq, modFreqRatio;
	
	PVector sCenter;
	float c = (float) 22.5;
	float alphaAng = (float) 130.0;
	float betaAng  = (float) 130.0;
	
	public void setup() {
		size(screenWidth, screenHeight, P3D);
		sCenter= new PVector(screenWidth/2,screenHeight/2);
		//client = new TouchClient(this, true, false);
		//client.setDrawTouchPoints(false);
		//client.applyZonesMatrix(true);
		//client.setDrawTouchPointsSize(getHeight()/30);
		pFont = loadFont("Aharoni-Bold-48.vlw");
		textSize = getHeight()/20;
		circles = new Vector<float[]>();
		//sound = new SoundCipher(this);
		//instruments = new int[]{(int) SoundCipher.OCARINA, (int) SoundCipher.VOICE, (int) SoundCipher.PIANO, (int) SoundCipher.BANJO};
		AudioContext ac= new AudioContext();
		carrierFreq = new Glide(ac, 500);
		  modFreqRatio = new Glide(ac, 1);
		  Function modFreq = new Function(carrierFreq, modFreqRatio) {
		    public float calculate() {
		      return x[0] * x[1];
		    }
		  };
		  WavePlayer freqModulator = new WavePlayer(ac, modFreq, Buffer.SINE);
		  Function carrierMod = new Function(freqModulator, carrierFreq) {
		    public float calculate() {
		      return (float) (x[0] * 400.0 + x[1]);    
		    }
		  };
		  WavePlayer wp = new WavePlayer(ac, carrierMod, Buffer.SINE);
				
		  Gain g = new Gain(ac, 1, (float) 0.1);
		  g.addInput(wp);
		  ac.out.addInput(g);
		  ac.start();
		  
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
		}/* catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		reader = new SocketReader(input, this);
		reader.start();


		frameRate(4);
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
			background(255,255,255);
			  alphaAng += 0.005;
			  betaAng  += 0.004;
			   
			  for (int n=0; n < 200; n++) {
			     float r = c * sqrt( n );
			     float phi = n*alphaAng;
			     
			     float coordx = r*cos( (float) (phi * 3.14/180.0));
			     float coordy = r*sin( (float) (phi * 3.14/180.0));
			     float coordx2 = r*cos( (float) (n*-betaAng * 3.14/180.0));
			     float coordy2 = r*sin( (float) (n*-betaAng * 3.14/180.0));
			     
			     stroke(80,(float) (255*(n/200.0)),(float) (200*(n/200.0)), 200);
			     strokeWeight( sqrt(sqrt(n)) );
			     ellipse( sCenter.x + coordx2, sCenter.y + coordy2, 5, 5);
			     fill((float)(255*(n/200.0)), 128, (float)(128*(n/200.0)), 50);
			     stroke(255, 255, 255, 255);
			     strokeWeight(0);
			     ellipse( sCenter.x + coordx, sCenter.y + coordy, 1+100-abs(100-n), 1+100-abs(100-n));
			  }
			int i = 0;
			for(float[] touch: circles){
				if(touch[2] > 10){
					this.color(0);
					this.fill(0);
					float x = touch[0]*this.getWidth();
					float y = (1-touch[1])*this.getHeight();
					
					this.ellipse(x, y, touch[2], touch[2]);
					
					//sound.instrument(instruments[i]);
					//sound.playNote(0,i,instruments[i],touch[1]*64+30, touch[2]/5+54, touch[0]*10,0.8,64);
					//sound.playNote(pitchSet[(int)random(setSize)], touch[2], 1);
					i++;
					//touch[2]-=20;
					carrierFreq.setValue(touch[0] * 1000 + 50);
					  modFreqRatio.setValue((float) ((1 - touch[2]) * 10 + 0.1));
				}
			}
		}
	}

	public static void main(String[] args) {
		PApplet.main(new String[]{"--present", "MainSketch"});
	}
}

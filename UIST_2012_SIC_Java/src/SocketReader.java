import java.io.BufferedReader;
import java.io.IOException;

import processing.core.PApplet;


public class SocketReader extends Thread{
	BufferedReader input;
	MainSketch sketch;

	public SocketReader(BufferedReader input, MainSketch sketch){
		this.input = input;
		this.sketch = sketch;
	}

	public void run(){
		while(true){
			String forcePad;

			try {
				forcePad = input.readLine();
				if(forcePad != null){
					System.out.println("Server: " + forcePad);

					String[] data = forcePad.split("\\|");

					int pressure;
					if(data[3].equalsIgnoreCase("")){
						pressure = 0;
					}else {
						pressure = PApplet.abs(Integer.parseInt(data[3]));
					}

					float[] touch = {Float.parseFloat(data[1]), Float.parseFloat(data[2]), pressure};
					sketch.circles.set(Integer.parseInt(data[0]), touch); 
					//sketch.color(0);
					//sketch.fill(0);
					//sketch.ellipse(Float.parseFloat(data[1])*sketch.getWidth(), Float.parseFloat(data[2])*sketch.getHeight(), pressure, pressure);

				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}

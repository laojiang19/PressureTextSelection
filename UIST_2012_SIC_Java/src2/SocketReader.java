import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;


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
				/*System.out.println("Restarting server");
				Thread device = new DeviceThread();
				device.start();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					sketch.socket = new Socket(InetAddress.getLocalHost(), 3333);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} //127.0.0.1, 3333);
				//socket.setSoTimeout(100);
				try {
					input = new BufferedReader(new InputStreamReader(sketch.socket.getInputStream()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				e.printStackTrace();
			}
		}

	}
}

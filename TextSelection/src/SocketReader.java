import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;



public class SocketReader extends Thread{
	BufferedReader input;
	TextSelectionSketch sketch;
	long time = 0;
	long prevTime = 0;
	int buttonLeft = 0;
	int buttonTop = 0;
	int buttonRight = 0;
	int buttonBottom = 0;
	long diff = 0;
	final int LIGHT = 200;
	final int MEDIUM = 400;

	public SocketReader(BufferedReader input, TextSelectionSketch sketch){
		this.input = input;
		this.sketch = sketch;
	}


	public void run(){
		while(true){
			String forcePad;

			try {
				forcePad = input.readLine();
				if(forcePad != null){
					
					String[] data = forcePad.split("\\|");
					int touchNum = Integer.parseInt(data[0]);
					if (touchNum == 0){
						prevTime = time;
						time = System.currentTimeMillis();
						diff=time-prevTime;
						
						if(diff > 50){
							System.out.println();
							System.out.println("Server: " + forcePad + "| Time Difference: " + diff);
							System.out.println();
						} else {
							System.out.println("Server: " + forcePad + "| Time Difference: " + diff);
						}
					} else {
						System.out.println("Server: " + forcePad);
					}
					float x = Float.parseFloat(data[1]);
					float y = Float.parseFloat(data[2]);

					int pressure;
					if(data[3].equalsIgnoreCase("") || Integer.parseInt(data[3]) < 0){
						pressure = 0;
					} else if (Integer.parseInt(data[3]) > 1000) {
						pressure = 1000;
					} else {
						pressure = Integer.parseInt(data[3]);
					}

					float[] touch = {x, y, pressure, -1};
					
					if(touchNum == 0){
						if(pressure <= 50){
							sketch.pressureState = -1;
						} else if(pressure < 150){
							sketch.pressureState = 0;
						} else if (pressure < 350){
							sketch.pressureState = 1;
						} else {
							sketch.pressureState = 2;
						}
						sketch.pressure = pressure;
						
						
						int newX = (int) (touch[0]*sketch.getWidth());
						int newY = (int) ((1-touch[1])*sketch.getHeight());
						int relChangeX = 0;
						int relChangeY = 0;
						
						
						if(diff < 50){
							
							
							relChangeX = (newX - sketch.prevX);
							relChangeY = (newY - sketch.prevY);
						} else {
							
							relChangeX = 0;
							relChangeY = 0;
						}
						
						sketch.x = sketch.x + relChangeX;
						sketch.y = sketch.y + relChangeY;
						
						if(sketch.x < 0){
							sketch.x = 0;
						} else if (sketch.x > sketch.displayWidth){
							sketch.x = sketch.displayWidth;
						}
						
						if(sketch.y < 0){
							sketch.y = 0;
						} else if (sketch.y > sketch.displayHeight){
							sketch.y = sketch.displayHeight;
						}
						
						sketch.prevX = newX;
						sketch.prevY = newY;
					}
					
					
					


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

import java.io.BufferedReader;
import java.io.IOException;



public class SocketReader extends Thread{
	BufferedReader input;
	TextSelectionSketch sketch;

	long[] time = {0, 0, 0, 0, 0};
	long[] prevTime = {0, 0, 0, 0, 0};
	long[] diff = {0, 0, 0, 0, 0};
	int[] maxPressure = {-1, -1, -1, -1, -1};

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

					prevTime[touchNum] = time[touchNum];
					time[touchNum] = System.currentTimeMillis();
					diff[touchNum] = time[touchNum] - prevTime[touchNum];

					if(diff[touchNum] > 50){
						maxPressure[touchNum] = -1;
						System.out.println();
						System.out.println("Server: " + forcePad + "| Time Difference: " + diff[touchNum]);
						System.out.println();
					} else {
						System.out.println("Server: " + forcePad + "| Time Difference: " + diff[touchNum]);
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

					maxPressure[touchNum] = Math.max(maxPressure[touchNum], pressure);

					if(maxPressure[touchNum] <= 75){
						sketch.pressureState[touchNum] = -1;
						
					} else if(maxPressure[touchNum] < 150){
						sketch.pressureState[touchNum] = 0;
						
					} else if (maxPressure[touchNum] < 350){
						sketch.pressureState[touchNum] = 1;
						
					} else {
						sketch.pressureState[touchNum] = 2;
						
					}
					sketch.pressure[touchNum] = pressure;


					int newX = (int) (touch[0]*sketch.getWidth());
					int newY = (int) ((1-touch[1])*sketch.getHeight());
					int relChangeX = 0;
					int relChangeY = 0;


					if(diff[touchNum] < 50){

						relChangeX = (newX - sketch.prevX[touchNum]);
						relChangeY = (newY - sketch.prevY[touchNum]);
					} else {

						relChangeX = 0;
						relChangeY = 0;
					}

					sketch.x[touchNum] = sketch.x[touchNum] + relChangeX;
					sketch.y[touchNum] = sketch.y[touchNum] + relChangeY;

					if(sketch.x[touchNum] < 0){
						sketch.x[touchNum] = 0;
					} else if (sketch.x[touchNum] > sketch.displayWidth){
						sketch.x[touchNum] = sketch.displayWidth;
					}

					if(sketch.y[touchNum] < 0){
						sketch.y[touchNum] = 0;
					} else if (sketch.y[touchNum] > sketch.displayHeight){
						sketch.y[touchNum] = sketch.displayHeight;
					}

					sketch.prevX[touchNum] = newX;
					sketch.prevY[touchNum] = newY;
				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

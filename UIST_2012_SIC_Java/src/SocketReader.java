import java.io.BufferedReader;
import java.io.IOException;



public class SocketReader extends Thread{
	BufferedReader input;
	MainSketch sketch;

	int buttonLeft = 0;
	int buttonTop = 0;
	int buttonRight = 0;
	int buttonBottom = 0;

	final int LIGHT = 200;
	final int MEDIUM = 400;

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
					int touchNum = Integer.parseInt(data[0]);
					float x = Float.parseFloat(data[1]);
					float y = Float.parseFloat(data[2]);

					int pressure;
					if(data[3].equalsIgnoreCase("") || Integer.parseInt(data[3]) < 0){
						pressure = 0;
					}else {
						pressure = Integer.parseInt(data[3]);
					}

					float[] touch = {x, y, pressure, -1};

					//Defines the areas
					float Ax=0;
					float Ay=1;
					float Bx=1;
					float By=0;
					float Cx=x;
					float Cy=y;

					if(y> x){

						if(((Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax)) <0){
							//Left Button
							if(pressure < LIGHT){
								touch[3] = 0;
								buttonLeft = 1;
							} else if (pressure < MEDIUM){
								touch[3] = 1;
								buttonLeft = 2;
							} else if (pressure >= MEDIUM){
								touch[3] = 2;
								buttonLeft = 3;
							}
							

						}else{
							//Top Button
							if(pressure < LIGHT){
								touch[3] = 0;
								buttonTop = 1;
							} else if (pressure < MEDIUM){
								touch[3] = 1;
								buttonTop = 2;
							} else if (pressure >= MEDIUM){
								touch[3] = 2;
								buttonTop = 3;
							}
						}
					}else{

						if(((Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax)) <0){
							//Bottom Button
							if(pressure < LIGHT){
								touch[3] = 0;
								buttonBottom = 1;
							} else if (pressure < MEDIUM){
								touch[3] = 1;
								buttonBottom = 2;
							} else if (pressure >= MEDIUM){
								touch[3] = 2;
								buttonBottom = 3;
							}

						}else{
							//Right Button
							if(pressure < LIGHT){
								touch[3] = 0;
								buttonRight = 1;
							} else if (pressure < MEDIUM){
								touch[3] = 1;
								buttonRight = 2;
							} else if (pressure >= MEDIUM){
								touch[3] = 2;
								buttonRight = 3;
							}
						}
					}
					sketch.circles.set(touchNum, touch);

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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import processing.core.PApplet;
import processing.core.PGraphics;


@SuppressWarnings("serial")
public class TextSelectionSketch extends PApplet {

	boolean showFrameRate = true;
	Socket socket;
	BufferedReader input;
	public int x = 0;
	public int y = 0;
	public int prevX = 0;
	public int prevY = 0;
	SocketReader reader;
	float textSize;
	int pressure = 0;

	int pressureState = -1;

	PGraphics pg[] =new PGraphics[3];
	String textSample = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam id adipiscing tortor. Praesent aliquet elementum sodales. Proin leo ante, congue non ultricies vel, pellentesque a enim. Sed nec hendrerit magna. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aenean vel erat quis sem bibendum placerat. \n\n" +
			"Mauris vel lacus vitae nunc luctus accumsan at at tellus. Pellentesque mauris dolor, lobortis nec adipiscing sed, vestibulum ut ipsum. Suspendisse pellentesque laoreet ultrices. Aenean placerat lacus non enim pellentesque quis tincidunt turpis condimentum. Vestibulum in quam diam, nec tincidunt dolor. \n\n " +
			"Nullam id sapien quis massa suscipit ultricies. Donec malesuada eros sit amet quam cursus pretium. Nunc lobortis velit ut sem convallis tempor. Suspendisse sed dapibus massa. Morbi enim sapien, pretium quis elementum eget, auctor eget dui. Maecenas euismod varius malesuada. Sed laoreet tempor viverra. Duis mauris diam, rhoncus non rutrum et, elementum in magna. \n\n " +
			"Donec in elit et orci tempor mollis. Cras dapibus sodales ligula sit amet pharetra. Nullam luctus tortor eget eros bibendum vel consectetur nunc dignissim. Suspendisse vel lacus ut felis imperdiet malesuada. Etiam tincidunt pharetra sem ut suscipit. Aliquam ac turpis est. In consectetur diam eget nibh euismod facilisis venenatis augue molestie. Aenean massa massa, posuere sit amet dictum eget, scelerisque adipiscing ligula.";


	public void setup() {
		size(displayWidth, displayHeight, P3D);
		x = displayWidth/2;
		y = displayHeight/2;
		textSize = displayHeight/30;

		pg[0]= createGraphics(width,height,P3D);
		pg[1]= createGraphics(width,height,P3D);
		pg[2]= createGraphics(width,height,P3D);
		//textSize = getHeight()/30;
		//t = getHeight()/10;
		//pFont = createFont("Arial", textSize);//loadFont("Aharoni-Bold-48.vlw");



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
		stroke(255, 0, 0);
		strokeWeight(3);
		fill(255, 0, 0);
		ellipse(x, y, 15, 15);
		text(pressure, x, y);
		float t = getHeight()/10;




		if(showFrameRate){
			//textFont(pFont, textSize);
			//textAlign(PConstants.LEFT, PConstants.BOTTOM);
			fill(0);
			text("FPS:" + Integer.toString((int)frameRate), t, t/2);
		}

		if(pressureState != -1){
			int j = pressureState;
			//for(int j=0; j<3; j++){
			pg[j].beginDraw();

			pg[j].background(0,0,0,0);
			String s="";

			if(j == 0){
				s=" "; 
			}else if(j == 1){
				s="\\.";
			}else if(j == 2){
				s=" \n\n ";
			}
			String text[] = textSample.split(s);
			pg[j].fill(255,0,0);
			float xpos=10;
			float ypos=100;
			for(int i = 0; i < text.length; i++){
				if(text[i].contains("\n\n")){
					xpos=10;
					ypos+= 2*(textSize + textSize/6); //14;
				} else if(xpos+textWidth(text[i]) > width+10){
					xpos=10;
					ypos+=textSize + textSize/6; //14; 
				}
				if(x >= xpos && x < xpos + textWidth(text[i]) && 
						y >= ypos && y < ypos + textSize){
					if(j == 0){
						pg[j].fill(255, 0, 0, 255/3);
					} else if (j == 1){
						pg[j].fill(0, 255, 0, 255/3);
					} else if (j == 2){
						pg[j].fill(0, 0, 255, 255/3);
					}

					float tempWidth = textWidth(text[i]);

					if(j == 0){
						//Word Selection
						if(text[i].endsWith(".") || text[i].endsWith(",")){
							tempWidth = textWidth(text[i].substring(0, text[i].length()-1));
						}
					} else if(j == 1){
						//Sentence Selection
						String text2[] = text[i].split(" ");
						int k = 0;
						float width = textWidth(text2[k]);
						if(x >= xpos + width){
							while(x >= xpos + width){ 
								xpos += textWidth(text2[k]+ " ");
								k++;
								width = textWidth(text2[k]+ " ");
							}
							if(k > 0){
								while(k > 0){
									k--;
									String temp = text2[k] + " ";
									if(k == 0){
										temp = text2[k];
									}
									tempWidth -= textWidth(temp);

								}
							}
						} else {
							tempWidth += textWidth(".");
						}

					}

					pg[j].rect(xpos, ypos, tempWidth, textSize);



				}
				xpos+=textWidth(text[i]+" ");

			}
			pg[j].endDraw();
			pg[j].flush();
			image(pg[j],0,0,width,height);
			//}
		}
		stroke(0);
		fill(0);
		textSize(textSize);
		text(textSample,10,100, getWidth(), getHeight());


	}

	public static void main(String[] args) {
		PApplet.main(new String[]{"--present", "TextSelectionSketch"});
	}

}

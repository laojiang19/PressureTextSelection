
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import processing.core.PApplet;


@SuppressWarnings("serial")
public class TextSelectionSketch extends PApplet {

	boolean showFrameRate = true;
	Socket socket;
	BufferedReader input;

	int[] x = {0, 0, 0, 0, 0};
	int[] y = {0, 0, 0, 0, 0};
	int[] prevX = {0, 0, 0, 0, 0};
	int[] prevY = {0, 0, 0, 0, 0};


	SocketReader reader;
	float textSize;

	int[] pressure = {0, 0, 0, 0, 0};
	int[] pressureState = {-1, -1, -1, -1, -1};

	String textSample = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam id adipiscing tortor. Praesent aliquet elementum sodales. Proin leo ante, congue non ultricies vel, pellentesque a enim. Sed nec hendrerit magna. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aenean vel erat quis sem bibendum placerat. \n\n " +
			"Mauris vel lacus vitae nunc luctus accumsan at at tellus. Pellentesque mauris dolor, lobortis nec adipiscing sed, vestibulum ut ipsum. Suspendisse pellentesque laoreet ultrices. Aenean placerat lacus non enim pellentesque quis tincidunt turpis condimentum. Vestibulum in quam diam, nec tincidunt dolor. \n\n " +
			"Nullam id sapien quis massa suscipit ultricies. Donec malesuada eros sit amet quam cursus pretium. Nunc lobortis velit ut sem convallis tempor. Suspendisse sed dapibus massa. Morbi enim sapien, pretium quis elementum eget, auctor eget dui. Maecenas euismod varius malesuada. Sed laoreet tempor viverra. Duis mauris diam, rhoncus non rutrum et, elementum in magna. \n\n " +
			"Donec in elit et orci tempor mollis. Cras dapibus sodales ligula sit amet pharetra. Nullam luctus tortor eget eros bibendum vel consectetur nunc dignissim. Suspendisse vel lacus ut felis imperdiet malesuada. Etiam tincidunt pharetra sem ut suscipit. Aliquam ac turpis est. In consectetur diam eget nibh euismod facilisis venenatis augue molestie. Aenean massa massa, posuere sit amet dictum eget, scelerisque adipiscing ligula. \n\n";


	public void setup() {
		size(displayWidth, displayHeight, P3D);
		x[0] = displayWidth/2;
		y[0] = displayHeight/2;
		textSize = displayHeight/30;




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
		ellipse(x[0], y[0], 15, 15);
		text(pressure[0], x[0], y[0]);

		stroke(0, 0, 255);
		strokeWeight(3);
		fill(0, 0, 255);
		ellipse(x[1], y[1], 15, 15);
		text(pressure[1], x[1], y[1]);

		stroke(0, 255, 255);
		strokeWeight(3);
		fill(0, 255, 255);
		ellipse(x[2], y[2], 15, 15);
		text(pressure[2], x[2], y[2]);

		stroke(0, 255, 0);
		strokeWeight(3);
		fill(0, 255, 0);
		ellipse(x[3], y[3], 15, 15);
		text(pressure[3], x[3], y[3]);
		
		stroke(255, 255, 0);
		strokeWeight(3);
		fill(255, 255, 0);
		ellipse(x[4], y[4], 15, 15);
		text(pressure[4], x[4], y[4]);



		float t = getHeight()/10;




		if(showFrameRate){
			//textFont(pFont, textSize);
			//textAlign(PConstants.LEFT, PConstants.BOTTOM);
			fill(0);
			text("FPS:" + Integer.toString((int)frameRate), t, t/2);
		}

		for(int a = 0; a < 4; a++){
			if(pressureState[a] != -1){
				
				int j = pressureState[a];
				String s=" ";


				String text[] = textSample.split(s);
				float xpos = 10;
				float ypos = 100;

				for(int i = 0; i < text.length; i++){

					if(j == 0){
						//Word Selection

						if(text[i].contains("\n\n")){
							xpos = 10;
							ypos += 2*(textSize + textSize/6); //14;
						} 

						if(xpos + textWidth(text[i]) > width + 10){
							xpos = 10;
							ypos += textSize + textSize/6; //14; 
						}

						if(x[a] >= xpos && x[a] < xpos + textWidth(text[i]) && 
								y[a] >= ypos && y[a] < ypos + textSize){
							
							if(a == 0){
								fill(255, 0, 0, 255/8);
								stroke(255, 0, 0, 255/8);
							} else if (a == 1){
								fill(0, 0, 255, 255/8);
								stroke(0, 0, 255, 255/8);
							} else if (a == 2){
								fill(0, 255, 255, 255/8);
								stroke(0, 255, 255, 255/8);
							} else if (a == 3){
								fill(0, 255, 0, 255/8);
								stroke(0, 255, 0, 255/8);
							} else if (a == 4){
								fill(255, 255, 0, 255/8);
								stroke(255, 255, 0, 255/8);
							}
							
							
							float tempWidth = textWidth(text[i]);

							if(text[i].endsWith(".") || text[i].endsWith(",")){
								tempWidth = textWidth(text[i].substring(0, text[i].length()-1));
							}
							rect(xpos, ypos, tempWidth, textSize);
						}
						xpos += textWidth(text[i]+" ");
					} else if(j == 1){
						//Sentence Selection
						if(text[i].contains("\n\n")){
							xpos = 10;
							ypos += 2*(textSize + textSize/6); //14;
						} 

						if(xpos + textWidth(text[i]) > width + 10){
							xpos = 10;
							ypos += textSize + textSize/6; //14; 
						}

						if(x[a] >= xpos && x[a] < xpos + textWidth(text[i]) && 
								y[a] >= ypos && y[a] < ypos + textSize){

							if(a == 0){
								fill(255, 0, 0, 255/5);
								stroke(255, 0, 0, 255/5);
							} else if (a == 1){
								fill(0, 0, 255, 255/5);
								stroke(0, 0, 255, 255/5);
							} else if (a == 2){
								fill(0, 255, 255, 255/5);
								stroke(0, 255, 255, 255/5);
							} else if (a == 3){
								fill(0, 255, 0, 255/5);
								stroke(0, 255, 0, 255/5);
							} else if (a == 4){
								fill(255, 255, 0, 255/5);
								stroke(255, 255, 0, 255/5);
							}

							float tempWidth = textWidth(text[i]);
							float tempWidth2 = 0;
							int k = i;

							while(!text[k].contains(".")){
								k++;
								tempWidth += textWidth(text[k] + " ");

								if(xpos + tempWidth > width + 10){
									tempWidth2 += textWidth(text[k] + " ");
								}

							}
							if(tempWidth2 != 0){
								//Word wrap end of sentence
								rect(10, ypos + textSize + textSize/6, tempWidth2 - textWidth(" "), textSize);
							}
							//Beginning of sentence
							rect(xpos, ypos, tempWidth - tempWidth2, textSize);
						}
						xpos += textWidth(text[i]+" ");
					} else if(j == 2){
						// Paragraph
						if(text[i].contains("\n\n")){
							xpos = 10;
							ypos += 2*(textSize + textSize/6); //14;
						} 

						if(xpos + textWidth(text[i]) > width + 10){
							xpos = 10;
							ypos += textSize + textSize/6; //14; 
						}

						if(x[a] >= xpos && x[a] < xpos + textWidth(text[i]) && 
								y[a] >= ypos && y[a] < ypos + textSize){

							if(a == 0){
								fill(255, 0, 0, 255/3);
								stroke(255, 0, 0, 255/3);
							} else if (a == 1){
								fill(0, 0, 255, 255/3);
								stroke(0, 0, 255, 255/3);
							} else if (a == 2){
								fill(0, 255, 255, 255/3);
								stroke(0, 255, 255, 255/3);
							} else if (a == 3){
								fill(0, 255, 0, 255/3);
								stroke(0, 255, 0, 255/3);
							} else if (a == 4){
								fill(255, 255, 0, 255/3);
								stroke(255, 255, 0, 255/3);
							}
							
							float totalWidth = textWidth(text[i]);
							float curLineWidth = 0;
							float totalWidthOF = 0;
							int k = i;
							int l=0;
							while(!text[k].contains("\n\n")){
								k++;
								totalWidth += textWidth(text[k] + " ");

								if(xpos + totalWidth > width + 10){
									if(curLineWidth + textWidth(text[k] + " ") > width + 10){
										rect(10, ypos + (l+1)*(textSize + textSize/6), curLineWidth - textWidth(" "), textSize);
										curLineWidth = 0;
										l++;
									}
									curLineWidth += textWidth(text[k] + " ");
									totalWidthOF += textWidth(text[k] + " ");
								} else if(k == i-1){
									totalWidth -= textWidth(" ");
								}

							}
							if(curLineWidth != 0){
								//Word wrap end of sentence
								rect(10, ypos + (l+1)*(textSize + textSize/6), curLineWidth - 2*textWidth(" "), textSize);
							}
							//Beginning of sentence
							if(totalWidthOF == 0){
								rect(xpos, ypos, totalWidth - totalWidthOF - textWidth(" "), textSize);
							} else {
								rect(xpos, ypos, totalWidth - totalWidthOF, textSize);
							}
						}
						xpos += textWidth(text[i]+" ");
					}



				}
			}

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

PGraphics pg[] =new PGraphics[3];
void setup(){
  size(600,600,P3D);
  pg[0]= createGraphics(width,height,P3D);
  pg[1]= createGraphics(width,height,P3D);
  pg[2]= createGraphics(width,height,P3D);
}
void draw(){
String t = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean eget turpis id ipsum placerat fermentum. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Quisque vel dui tellus. Sed volutpat euismod blandit. Duis ac arcu ante. Maecenas condimentum sodales ipsum dignissim porttitor. Sed pharetra tortor eget mi suscipit ac adipiscing lorem suscipit. Cras sit amet eleifend enim. Praesent ultricies tempor lorem eget malesuada."; 
 
  for(int j=0; j<3; j++){
    pg[j].beginDraw();
    pg[j].background(255);
    String s="";
    if(j==0){
       s=" "; 
    }else if(j==1){
       s="\\.";
    }else if(j==2){
       s="  ";
    }
    String text[]=t.split(s);
    pg[j].fill(255,0,0);
    float xpos=10;
    float ypos=0;
    for(int i=0; i<text.length; i++){
      if(xpos+textWidth(text[i])>width+10){
         xpos=10;
        ypos+=14; 
      }
      if(mouseX >= xpos && mouseX < xpos + textWidth(text[i]) && 
      mouseY >= ypos && mouseY < ypos + pg[j].textSize){
        pg[j].fill(j,(((float)i/text.length)*255),(((float)i/text.length)*255));
        pg[j].rect(xpos,ypos,textWidth(text[i]),10);
        
        
      }
      xpos+=textWidth(text[i]+" ");
      
    }
    pg[j].endDraw();
    pg[j].flush();
    image(pg[j],0,j*100,width,height);
  }
  stroke(0);
  fill(0);
  text(t,10,0,width,height);
}

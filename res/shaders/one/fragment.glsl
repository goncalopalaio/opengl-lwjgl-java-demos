    
#ifdef GL_ES
	precision mediump float;
#endif
void main()
{
vec4 newcolor;
newcolor[0]=1.0;
newcolor[1]=floor(mod(gl_FragCoord.x,2.0));
newcolor[2]=0.0;
newcolor[3]=floor(mod(newcolor[1],12.0));

gl_FragColor=newcolor;

}
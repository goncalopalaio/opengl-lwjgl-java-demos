
attribute vec4 lightPosition;
uniform mat4 mvp;
void main()
{
	gl_Position = mvp*lightPosition;
gl_PointSize=14.0;
}

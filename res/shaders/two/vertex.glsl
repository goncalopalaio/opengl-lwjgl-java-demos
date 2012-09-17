
attribute vec4 in_Position;
attribute vec3 in_Color;
varying vec3 v_Color;

void main()
{
	v_Color=in_Color;
	gl_Position = in_Position;

}


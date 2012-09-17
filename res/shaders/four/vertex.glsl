
attribute vec4 in_Position;
attribute vec3 in_Color;
uniform mat4 transformation;
varying vec3 v_Color;


void main()
{
	v_Color=in_Color;
	gl_Position =transformation * in_Position;

}


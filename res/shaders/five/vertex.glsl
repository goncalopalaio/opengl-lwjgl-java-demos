
attribute vec4 in_Position;
attribute vec3 in_Color;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
varying vec3 v_Color;

varying float test;


void main()
{
	v_Color=in_Color;
	test=in_Position.x;
	gl_Position = projection * view * model *in_Position;

}


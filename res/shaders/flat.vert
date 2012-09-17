
attribute vec4 in_Position;
attribute vec3 in_Color;
attribute vec2 in_TexCoord;
attribute vec3 in_Normal;

uniform mat4 model;
uniform mat4 view;
uniform float time;
uniform mat4 projection;
varying vec3 v_Color;
varying vec2 v_TexCoord;
varying vec3 v_Normal;


void main()
{
	v_Color=in_Color;	
	v_TexCoord=in_TexCoord;
	v_Normal=in_Normal;
	vec4 pos=in_Position;
	pos.z=sin(5.0*pos.x+time*0.01)*0.25;
	gl_Position = projection * view * model *pos;

}


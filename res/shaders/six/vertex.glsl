
attribute vec4 in_Position;
attribute vec3 in_Color;
attribute vec2 in_TexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
varying vec3 v_Color;
varying vec2 v_TexCoord;

void main()
{
	v_Color=in_Color;
	v_TexCoord=in_TexCoord;
	
	gl_Position = projection * view * model *in_Position;

}


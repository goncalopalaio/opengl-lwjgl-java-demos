
attribute vec4 in_Position;
attribute vec3 in_Color;
attribute vec2 in_TexCoord;
attribute vec3 in_Normal;
attribute vec3 in_LightPosition;


uniform mat4 model;
uniform mat4 view;
uniform float time;


uniform mat4 projection;
varying vec3 v_Color;
varying vec2 v_TexCoord;
varying vec3 v_Normal;


uniform vec3 lightDir;
void main()
{
	v_Color=in_Color;	
	v_TexCoord=in_TexCoord;
	v_Normal=in_Normal;
	



	vec4 pos=in_Position;
	gl_Position = projection * view * model *pos;
}

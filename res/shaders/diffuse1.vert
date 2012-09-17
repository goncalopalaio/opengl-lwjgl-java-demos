
attribute vec4 in_Position;
attribute vec3 in_Color;
attribute vec2 in_TexCoord;
attribute vec3 in_Normal;
attribute vec3 in_LightPosition;


uniform mat4 model;
uniform mat4 view;
uniform float time;


uniform mat4 projection;
uniform mat3 normalmatrix;
uniform mat4 modelview;
varying vec4 v_Color;
varying vec2 v_TexCoord;
varying vec3 v_Normal;

varying float intensity;

uniform vec3 lightDir;

void main()
{
	vec3 modelviewvertex=vec3(modelview * in_Position);
	vec3 modelviewnormal=vec3(modelview * vec4(in_Normal,0.0));
	
	vec3 lightvector=normalize(lightDir-modelviewvertex);
	
	intensity=max(dot(modelviewnormal,lightvector),0.1);
	
	
	v_Color=vec4(1.0,0.0,0.0,1.0)*intensity;
	
	
	gl_Position = gl_Position = projection * view * model *in_Position;
	}


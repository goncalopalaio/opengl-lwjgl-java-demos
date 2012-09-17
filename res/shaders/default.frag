    
#ifdef GL_ES
	precision mediump float;
#endif


varying vec3 v_Color;
varying vec2 v_TexCoord;
varying vec3 v_Normal;
uniform sampler2D texture;
varying float intensity;


void main()
{

gl_FragColor = texture2D(texture,v_TexCoord)*vec4(v_Color,1.0);


}
#ifdef GL_ES_VERSION_2_0
    #ifdef GL_FRAGMENT_PRECISION_HIGH
    precision highp float;           
    #else                          
    precision mediump float;        
    #endif                         

#else
    // Ignore GLES 2 precision specifiers:
    #define lowp
    #define mediump
    #define highp
#endif
//_________________________________________________________


varying vec3 v_Color;
varying vec2 v_TexCoord;
varying vec3 v_Normal;
uniform sampler2D texture;

void main(void) {


	gl_FragColor = texture2D(texture,v_TexCoord)*vec4(v_Normal,1.0);
	

}

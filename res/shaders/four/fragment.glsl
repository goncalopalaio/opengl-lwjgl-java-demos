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

void main(void) {

	//gl_FragColor = vec4(1.0,1.0,1.0, 1.0);
gl_FragColor = vec4(v_Color.xyz, floor(mod(gl_FragCoord.y,10.0))*floor(mod(gl_FragCoord.x,10.0)));


}

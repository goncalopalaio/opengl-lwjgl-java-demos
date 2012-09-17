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





void main()
{

gl_FragColor = vec4(1.0,0.0,1.0,1.0);


}
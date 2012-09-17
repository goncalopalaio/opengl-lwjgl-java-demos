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
varying float intensity;


void main(void) {

    vec4 color;

    if(intensity>0.95){
        color=vec4(1.0,0.5,0.5,1.0);
    }else if(intensity>0.5){
        color=vec4(0.6,0.3,0.3,1.0);
    }else if(intensity>0.25){
        color=vec4(0.4,0.2,0.2,1.0);
    }else{
        color=vec4(0.2,0.1,0.1,1.0);
    }

	//gl_FragColor = texture2D(texture,v_TexCoord)*color;
gl_FragColor =color;
    	

}

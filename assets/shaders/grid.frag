#define HIGHP
#define QUANT 0.3

uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoords;

//shades of cryofluid
#define NSCALE 100.0 / 2.0

uniform vec2 u_campos;
uniform vec2 u_resolution;

uniform float u_time;

void main(){
    vec4 color = texture2D(u_texture, v_texCoords.xy);

    gl_FragColor = vec4(color.rgb, (1.0 - floor(coloro.r / QUANT) * QUANT) * (step(coloro.r, 0.99))) * v_color;
}

//(1.0 - floor(color.r / QUANT) * QUANT) * (step(color.r, 0.99))) * v_color
#define HIGHP
#define QUANT 0.3

uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoords;

//shades of cryofluid
#define S1 vec3(53.0, 83.0, 93.0) / 100.0
#define S2 vec3(68.0, 90.0, 97.0) / 100.0
#define NSCALE 100.0 / 2.0

uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

void main(){
    vec4 color = texture2D(u_texture, v_texCoords.xy);
    vec4 coloro = vec4(color);

    vec2 c = v_texCoords.xy;
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

    float btime = u_time / 5000.0;
    float wave = abs(sin(coords.x * 1.1 + coords.y) + 0.1 * sin(2.5 * coords.x) + 0.15 * sin(3.0 * coords.y)) / 30.0;
    float noise = wave + (texture2D(u_noise, (coords) / NSCALE + vec2(btime) * vec2(-0.2, 0.8)).r + texture2D(u_noise, (coords) / NSCALE + vec2(btime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;

    if(noise > 0.54 && noise < 0.57){
        color.rgb = S2;
    }else if (noise > 0.49 && noise < 0.62){
        color.rgb = S1;
    }

    gl_FragColor = vec4(color.rgb, (1.0 - floor(coloro.r / QUANT) * QUANT) * (step(coloro.r, 0.99))) * v_color;
}

//(1.0 - floor(color.r / QUANT) * QUANT) * (step(color.r, 0.99))) * v_color
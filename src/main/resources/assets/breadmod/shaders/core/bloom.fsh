#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

/* https://github.com/fith/Minecraft-Shaders/blob/master/mods/3ddotminecraft/contents/files/shaders/final.fsh */

float contrast = 1.04;

// Bloom
#define BLURCLAMP 0.01
#define BIAS 0.01
#define KERNEL_SIZE 20.0

const float BRIGHT_PASS_THRESHOLD = 0.1;
const float BRIGHT_PASS_OFFSET = 0.5;

vec4 bright(vec2 coo) {
    vec4 color = texture2D(Sampler0, coo);
    color = max(color - BRIGHT_PASS_THRESHOLD, 0.0);
    return color / (color + BRIGHT_PASS_OFFSET);
}

// Cross Processing
vec4 gradient(vec4 coo) {
    vec4 stripes = coo;
    stripes.r = stripes.r * 1.3 + 0.01;
    stripes.g = stripes.g * 1.2;
    stripes.b = stripes.b * 0.7 + 0.15;
    stripes.a = vertexColor.a;
    return stripes;
}

vec4 getBloomColor(vec4 baseColor) {
    vec4 bloomColor = vec4(0.0, 0.0, 0.0, 0.0);
    vec2 blur = vec2(clamp( BIAS, -BLURCLAMP, BLURCLAMP ));

    for ( float x = -KERNEL_SIZE + 1.0; x < KERNEL_SIZE; x += 1.0 )
    {
        for ( float y = -KERNEL_SIZE + 1.0; y < KERNEL_SIZE; y += 1.0 )
        {
            bloomColor += bright( vec2( blur.x * x, blur.y * y ) );
        }
    }
    bloomColor /= ((KERNEL_SIZE+KERNEL_SIZE)-1.0)*((KERNEL_SIZE+KERNEL_SIZE)-1.0);

    vec4 fin = bloomColor + gradient(baseColor);
    bloomColor = (fin - 0.5) * contrast + 0.5;

    return bloomColor;
}

float random (vec2 st) {
    return fract(sin(dot(st.rg, vec2(12.9898,78.233))) * 4375.97658);
}

void main() {
    vec4 textureColor = vec4(1.0, 0.0, 0.0, 1.0);
    textureColor.g += random(texCoord0 + normal.xy + vertexDistance) / 1.25;
    fragColor = textureColor;
}
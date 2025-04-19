#version 150

#moj_import <minecraft:fog.glsl>

uniform float GameTime;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;

out vec4 fragColor;

#define u_color vec3(0.3137254901960784,0,1)
#define u_background vec4(0, 0, 0, 0)
#define u_detail 0.4
#define u_speed 100.0

/*
* @author Hazsi (kinda)
* @author Meownium (assistance in implementation and GL conversion)
*/

mat2 m(float a) {
    float c=cos(a), s=sin(a);
    return mat2(c,-s,s,c);
}

#ifndef FNC_RGB2LUMA
#define FNC_RGB2LUMA
float rgb2luma(in vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}
float rgb2luma(in vec4 color) {
    return rgb2luma(color.rgb);
}
#endif

#ifndef FNC_LUMA
#define FNC_LUMA
float luma(float v) { return v; }
float luma(in vec3 v) { return rgb2luma(v); }
float luma(in vec4 v) { return rgb2luma(v.rgb); }
#endif

float map(vec3 p) {
    float t = GameTime * u_speed;
    p.xz *= m(t * 0.4);p.xy*= m(t * 0.1);
    vec3 q = p * 2.0 + t;
    return length(p+vec3(sin((t*u_speed) * 0.1))) * log(length(p) + 0.9) + cos(q.x + sin(q.z + cos(q.y))) * 0.5 - 1.0;

}

void main() {
    vec2 a = texCoord0.xy - vec2(0.5, 0.5);
    vec3 cl = vec3(0.0);
    float d = 2.5;
    for (float i = 0.; i <= (1. + 20. * u_detail); i++) {
        vec3 p = vec3(0, 0, 4.0) + normalize(vec3(a, -1.0)) * d;
        float rz = map(p);
        float f =  clamp((rz - map(p + 0.1)) * 0.5, -0.1, 1.0);
        vec3 l = vec3(0.1, 0.3, 0.4) + vec3(5.0, 2.5, 3.0) * f;
        cl = cl * l + smoothstep(2.5, 0.0, rz) * 0.6 * l;
        d += min(rz, 1.0);
    }
    vec4 color = vec4(min(u_color, cl),1.0);
    //color = min(u_background, u_color);
    color.r = max(u_background.r,color.r);
    color.g = max(u_background.g,color.g);
    color.b = max(u_background.b,color.b);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}


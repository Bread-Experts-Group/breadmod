#version 150

#moj_import <fog.glsl>
#moj_import <breadmod:hsv2rgb.glsl>

uniform sampler2D Sampler0;

uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;

out vec4 fragColor;

// "Sun surface" by Duke [2015-10-13] @ https://www.shadertoy.com/view/XlSSzK#
//   Modified by Bread Experts Group for Minecraft usage
// Based on Shanes' Fiery Spikeball https://www.shadertoy.com/view/4lBXzy (I think that his implementation is more understandable than the original :) )
// Relief come from Siggraph workshop by Beautypi/2015 https://www.shadertoy.com/view/MtsSRf
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0

#define PI 3.14159265358
#define gameTime GameTime * 1000.0
#define rendResolution vec2(1024, 2048)
#define R(p, a) p = cos(a) * p + sin(a) * vec2(p.y, -p.x)

float rand(vec2 co) { // implementation found at: lumina.sourceforge.net/Tutorials/Noise.html
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453123);
}

// IQ's noise
float pn(in vec3 p) {
    vec3 ip = floor(p);
    p = fract(p);
    p *= p * (3.0-2.0 * p);
    vec2 uv = (ip.xy+vec2(37.0, 17.0)*ip.z) + p.xy;
    uv = textureLod(Sampler0, (uv + 0.5) / 256.0, 0.0).yx;
    return mix(uv.x, uv.y, p.z);
}

// FBM
float fpn(vec3 p) {
    return pn(p * 0.06125) * 0.57 + pn(p * 0.125) * 0.28 + pn(p * 0.25) * 0.15;
}

float cosNoise(in vec2 p) {
    return 0.5 * (sin(p.x) + sin(p.y));
}

const mat2 m2 = mat2(1.6, -1.2, 1.2, 1.6);

float sdTorus(vec3 p, vec2 t) {
    return length(vec2(length(p.xz)-t.x*1.2, p.y))-t.y;
}

float smin(float a, float b, float k){
    float h = clamp(0.5 + 0.5*(b-a)/k, 0.0, 1.0);
    return mix(b, a, h) - k*h*(1.0-h);
}

float SunSurface(in vec3 pos) {
    float h = 0.0;
    vec2 q = pos.xz*0.5;
    float s = 0.5;
    float d2 = 0.0;

    for (int i=0; i<6; i++) {
        h += s*cosNoise(q);
        q = m2*q*0.85;
        q += vec2(2.41, 8.13);
        s *= 0.48 + 0.2*h;
    }
    h *= 2.0;

    float d1 = pos.y - h;

    // rings
    vec3 r1 = mod(2.3+pos+1.0, 10.0)-5.0;
    r1.y = pos.y - 0.1 - 0.7 * h + 0.5 * sin(3.0 * gameTime + pos.x + 3.0 * pos.z);
    float c = cos(pos.x);
    float s1 = sin(pos.x);
    r1.xz = c * r1.xz + s1 * vec2(r1.z, -r1.x);
    d2 = sdTorus(r1.xzy, vec2(clamp(abs(pos.x/pos.z), 0.7, 2.5), 0.20));
    return smin(d1, d2, 1.0);
}

float map(vec3 p) {
    p.z += 1.;
    R(p.yz, -1.5);// ;
    R(p.xz, 4.0*PI*0.1);
    return SunSurface(p) +  fpn(p * 50.0 + gameTime * 25.0) * 0.45;
}

// See "Combustible Voronoi"
// https://www.shadertoy.com/view/4tlSzl
vec3 firePalette(float i) {
    float T = 1500. + 1400.*i;// Temperature range (in Kelvin).
    vec3 L = vec3(7.4, 5.6, 4.4);// Red, green, blue wavelengths (in hundreds of nanometers).
    L = pow(L, vec3(5.0)) * (exp(1.43876719683e5/(T * L)) - 1.0);
    return 1.0 - exp(-5e8 / L);// Exposure level. Set to "50." For "70," change the "5" to a "7," etc.
}

void main() {
    // p: position on the ray
    // rd: direction of the ray
    vec2 coordLarge = texCoord0 * 512;
    vec3 rd = normalize(vec3((coordLarge.xy - 0.5 * rendResolution.xy) / rendResolution.y, 1.0));
    vec3 ro = vec3(0., 0., -22.);

    // ld, td: local, total density
    // w: weighting factor
    float ld=0., td=0., w=0.;

    // t: length of the ray
    // d: distance function
    float d=1., t=1.;

    // Distance threshold.
    const float h = .1;

    // total color
    vec3 tc = vec3(0.);

    vec2 pos = (coordLarge.xy / rendResolution.xy);
    vec2 seed = pos + fract(gameTime);
    //t=(1.+0.2*rand(seed));

    // rm loop
    for (int i=0; i<56; i++) {

        // Loop break conditions. Seems to work, but let me
        // know if I've overlooked something.
        if (td>(1.-1./80.) || d<0.001*t || t>40.)break;

        // evaluate distance function
        d = map(ro+t*rd);

        // fix some holes deep inside
        //d=max(d,-.3);

        // check whether we are close enough (step)
        // compute local density and weighting factor
        //const float h = .1;
        ld = (h - d) * step(d, h);
        w = (1. - td) * ld;

        // accumulate color and density
        tc += w*w + 1./50.;// Different weight distribution.
        td += w + 1./200.;

        // dithering implementation come from Eiffies' https://www.shadertoy.com/view/MsBGRh
        // add in noise to reduce banding and create fuzz
        d=abs(d)*(.8+0.28*rand(seed*vec2(i)));
        // enforce minimum stepsize
        d = max(d, 0.04);

        // step forward
        t += d * 0.5;

    }

    // Fire palette.
    tc = firePalette(tc.x);
    fragColor = linear_fog(vec4(tc, 1.0), vertexDistance, FogStart, FogEnd, FogColor);
}
#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// todo figure out how to replicate the behavior of vertexDistance
//  fog.glsl -> fog_distance(Positon, 1) -> vertexDistance
//  todo side quest.. actually figure out shaders
void main() {
    vec3 rgb = hsv2rgb(vec3(GameTime * 20, 1.0, 1.0));
    fragColor = vec4(rgb, 1.0);
}

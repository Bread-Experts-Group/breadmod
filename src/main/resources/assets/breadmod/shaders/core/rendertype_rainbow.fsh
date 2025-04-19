#version 150

#moj_import <fog.glsl>

uniform float GameTime;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    float time = GameTime * 100.0;
    vec2 a = texCoord0.xy - vec2(1.0, 1.0);
    vec3 color = hsv2rgb(vec3((texCoord0.x - time) + a.y, (texCoord0.y + time) + a.x, -time));
    vec4 normalized = vec4(normalize(color), 1.0);
    fragColor = linear_fog(normalized, vertexDistance, FogStart, FogEnd, FogColor);
}
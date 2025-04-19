#version 150

#moj_import <fog.glsl>
#moj_import <breadmod:hsv2rgb.glsl>

uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec2 RainbowDirection;
uniform int RainbowSpeed;

in float vertexDistance;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    fragColor = linear_fog(
    hsv2rgb(
    vec3(
    mod(
    (texCoord0.x * RainbowDirection.x) +
    (texCoord0.y * RainbowDirection.y) +
    GameTime * RainbowSpeed,
    1
    ),
    1,
    1
    ),
    1
    ),
    vertexDistance, FogStart, FogEnd, FogColor
    );
}
#version 150

#moj_import <fog.glsl>
#moj_import <breadmod:hsv2rgb.glsl>

uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in float rainbowSpeed;
in vec2 rainbowDirection;

out vec4 fragColor;

void main() {
    float texPosition = (texCoord0.x * rainbowDirection.x) + (texCoord0.y * rainbowDirection.y);
    float time = (GameTime * rainbowSpeed);
    float huePosition = mod(texPosition - time, 1);
//    if (texCoord0.x < 0.9 && texCoord0.x > 0.1 && texCoord0.y > 0.05 && texCoord0.y < 0.94) discard;
    fragColor = linear_fog(hsv2rgb(vec3(huePosition, 1, 1), 1), vertexDistance, FogStart, FogEnd, FogColor);
}
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

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(Sampler0, texCoord0);
    if (textureColor.a == 0) discard;
    fragColor = linear_fog((textureColor * vertexColor) * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}

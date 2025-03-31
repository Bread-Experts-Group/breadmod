#version 150

#moj_import <fog.glsl>
uniform float GameTime;
uniform vec2 ScreenSize;

in float vertexDistance;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// todo figure out how to replicate the behavior of vertexDistance
//  fog.glsl -> fog_distance(Positon, 1) -> vertexDistance
//  todo side quest.. actually figure out shaders
void main() {
    float time = GameTime * 40.0;
    vec3 rgb = hsv2rgb(vec3(vertexDistance + time, 1.0, 1.0));
    fragColor = vec4(rgb, 1.0);
}

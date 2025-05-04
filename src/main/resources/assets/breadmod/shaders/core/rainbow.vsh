#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec2 UV0;
in float Speed;
in vec2 Direction;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

out float vertexDistance;
out vec2 texCoord0;
out float rainbowSpeed;
out vec2 rainbowDirection;

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    vertexDistance = fog_distance(Position, FogShape);
    texCoord0 = UV0;
    rainbowSpeed = Speed;
    rainbowDirection = Direction;
}

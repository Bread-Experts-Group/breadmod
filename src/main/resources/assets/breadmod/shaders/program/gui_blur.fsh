#version 430

uniform sampler2D DiffuseSampler;

layout(std430, binding = 2) buffer DataBuffer {
    ivec2 kernelSize;
    ivec2 kernelHalf;
    float gaussian[];
};

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    for (int X = 0; X < DataBuffer.kernelSize.x; ++X) {
        for (int Y = 0; Y < DataBuffer.kernelSize.y; ++Y) {
            int gaussianLinear = X + (Y * DataBuffer.kernelSize.x);
            fragColor +=
            texture (DiffuseSampler, texCoord + (oneTexel * (vec2(X, Y) - DataBuffer.kernelHalf)))
            * DataBuffer.gaussian[gaussianLinear];
        }
    }
}

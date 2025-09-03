#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D AlphaOverSampler;

in vec2 texCoord;

out vec4 fragColor;

vec4 blend(vec4 src, vec4 dst) {
    return src + dst;
}

void main(){
    fragColor = blend(texture(AlphaOverSampler, texCoord), texture(DiffuseSampler, texCoord));
}

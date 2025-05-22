#version 150

uniform sampler2D InSampler;
uniform float Strength;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec2 offset = vec2(oneTexel.x * (abs(texCoord.x - 0.5) + 0.05) * Strength * 60.0, 0.0);

    float r = texture(InSampler, texCoord + offset).r;
    float g = texture(InSampler, texCoord).g;
    float b = texture(InSampler, texCoord - offset).b;

    fragColor = vec4(r, g, b, 1.0);
}
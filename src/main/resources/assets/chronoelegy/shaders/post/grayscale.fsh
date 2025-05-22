#version 150

uniform sampler2D InSampler;
uniform float Strength;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord);
    float gray = (color.r + color.g + color.b) / 3.0 * Strength;

    fragColor = vec4(gray + color.r * (1 - Strength), gray + color.g * (1 - Strength), gray + color.b * (1 - Strength), 1.0);
}
#version 150

uniform sampler2D InSampler;
uniform sampler2D BrightnessSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec3 color = texture(InSampler, texCoord).rgb;
    float colorIntensity = length(color) * 0.8;

    fragColor = vec4(color + texture(BrightnessSampler, texCoord).rgb * pow(0.1, colorIntensity), 1.0);
}
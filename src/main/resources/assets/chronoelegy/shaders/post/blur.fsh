#version 150

uniform sampler2D InSampler;

uniform vec2 BlurDir;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

float normalizer = 8.0 * 2.506628274;

void main() {
    vec2 texelStep = oneTexel * BlurDir;
    vec3 filteredColor = texture(InSampler, texCoord).rgb;

    for(int i = 1; i < 24; ++i) {
        float weight = exp(((i * i) * -1.0f) / 128);
        vec2 offset = texelStep * i;

        filteredColor += (texture(InSampler, texCoord + offset).rgb + texture(InSampler, texCoord - offset).rgb) * weight;
    }

    fragColor = vec4(filteredColor / normalizer, 1.0);
}
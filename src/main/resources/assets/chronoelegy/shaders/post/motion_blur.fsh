#version 150

uniform sampler2D InSampler;

uniform vec2 MotionDir;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

float normalizer = 8.0 * 2.506628274;

void main() {
    vec2 texelStep = oneTexel * MotionDir;
    vec3 filteredColor = texture(InSampler, texCoord).rgb;

    for(int i = -24; i < 24; ++i) {
        filteredColor += texture(InSampler, texCoord + texelStep * i).rgb * exp(((i * i) * -1.0f) / 128);
    }

    fragColor = vec4(filteredColor / normalizer, 1.0);
}
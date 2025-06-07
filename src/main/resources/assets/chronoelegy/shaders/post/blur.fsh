#version 150

uniform sampler2D InSampler;

uniform vec2 BlurDir;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec2 texelStep = oneTexel * BlurDir;
    vec3 color = texture(InSampler, texCoord).rgb;

    for(int i = 1; i < 24; ++i) {
        float weight = exp(((i * i) * -1.0f) / 128);
        vec2 offset = texelStep * i;

        color += (texture(InSampler, texCoord + offset).rgb + texture(InSampler, texCoord - offset).rgb) * weight;
    }

    fragColor = vec4(color / 20.053026192, 1.0);
}
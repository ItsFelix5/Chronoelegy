#version 150

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;

uniform vec2 BlurDir;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    float blurAmount = 1-texture(DepthSampler, texCoord).r;

    vec3 color = texture(InSampler, texCoord).rgb;
    if(blurAmount > 0.1) {
        blurAmount = min(blurAmount, 1) * 7;

        vec2 texelStep = BlurDir * oneTexel * blurAmount;
        for (float i = 1; i <= 4.0; i++) {
            color += texture(InSampler, texCoord + texelStep * i).rgb + texture(InSampler, texCoord - texelStep * i).rgb;
        }
        color /= 9;
    }

    fragColor = vec4(color, 1.0);
}
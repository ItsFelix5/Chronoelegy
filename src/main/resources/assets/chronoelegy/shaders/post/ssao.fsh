#version 150

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;

in vec2 texCoord;

out vec4 fragColor;

#define TAPS 6
#define DISTANCE 24

const float angle = radians(360.0 / float(TAPS));
const float angleSin = sin(angle);
const float angleCos = cos(angle);
const mat2 rotationMatrix = mat2(angleCos, angleSin, -angleSin, angleCos);

void main() {
    float depth = texture2D(DepthSampler, texCoord).r;
    vec3 color = texture2D(InSampler, texCoord).rgb;

    if (depth < 1.0) {
        vec2 tapOffset = vec2(0.0, 1.0 / 512.0);
        float dist = 1.0 - pow(depth, 64.0);

        float occlusion = 0.0;
        for (int ii = 0; ii < TAPS; ++ii) {
            for (int jj = 0; jj < DISTANCE; ++jj) {
                float mul = float(jj + 1) * dist;
                float tapValue = texture2D(DepthSampler, texCoord + (tapOffset * mul)).r;
                float rangeCheck = clamp(smoothstep(0.0, 1.0, mul / abs(depth - tapValue)), 0.0, 1.0);
                occlusion += tapValue >= depth ? rangeCheck : 0.0;
            }
            tapOffset = rotationMatrix * tapOffset;
        }
        color = color * 0.7 + smoothstep(0.0, 0.5, occlusion / float(TAPS * DISTANCE)) * color * 0.3;
    }
    fragColor = vec4(color, 1.0);
}

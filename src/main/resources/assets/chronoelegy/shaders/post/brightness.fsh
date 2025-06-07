#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec3 color = texture(InSampler, texCoord).xyz;
    float brightness = dot(color, vec3(0.2126, 0.7152, 0.0722)) - 0.3;

    if(brightness >= 0.05) fragColor = vec4(color * clamp(pow(brightness / 0.35, 2.0), 0.0, 1.0), 1.0);
    else fragColor = vec4(0.0, 0.0, 0.0, 1.0);
}
#version 400 core

in vec2 textureCoords;
in vec2 fragPosition;
in vec3 fragColor;

out vec4 out_Color;

uniform sampler2D liquidTexture;

void main(){
    vec3 color = texture(liquidTexture, textureCoords).rgb;
    out_Color = vec4(color, color.x);
}

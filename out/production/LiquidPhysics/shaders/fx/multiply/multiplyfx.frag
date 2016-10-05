#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform vec4 multiply;

void main() {
    out_Color = multiply * texture(colorTexture, textureCoords).rgba;
}
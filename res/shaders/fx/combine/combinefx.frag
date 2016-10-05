#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

const int maxTextures = 16;
uniform sampler2D textures[16];

void main() {
    out_Color = vec4(33, 54, 62, 255)/255.0;
    for(int i=0; i<maxTextures; i+=2){
        out_Color = mix(out_Color, texture(textures[i], textureCoords).rgba, texture(textures[i+1], textureCoords).x);
    }
}
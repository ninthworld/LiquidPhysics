#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colorTexture;

void main() {
    vec3 color = texture(colorTexture, textureCoords).rgb;

    if(color.r > 0.05){
        out_Color = vec4(1);
    }else{
        out_Color = vec4(0);
    }

    //out_Color = vec4(color, 1);
}
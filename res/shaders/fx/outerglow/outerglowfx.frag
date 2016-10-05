#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D maskTexture;
uniform vec4 glowColor;
uniform int glowSize;
uniform bool isMask;
uniform vec2 screenSize;

void main() {
float avg = 0.0;
    int r = glowSize;
    for(int i=-r; i<=r; i++){
        for(int j=-r; j<=r; j++){
            avg += texture(maskTexture, textureCoords + vec2(i, j)/screenSize).r;
        }
    }
    avg /= pow(r*2 + 1, 2);

    if(texture(maskTexture, textureCoords).x > 0.5){
        out_Color = texture(colorTexture, textureCoords).rgba;
    }else if(isMask){
        out_Color = vec4(glowColor.rgb * avg, 1);
    }else{
        out_Color = glowColor;
    }
}
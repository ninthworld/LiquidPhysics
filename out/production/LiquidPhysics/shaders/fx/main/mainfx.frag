#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

const int numTextures = 16;
uniform sampler2D textures[numTextures];
uniform int numColor;
uniform int numMask;
uniform vec2 screenSize;

vec4 applyBorderColor(vec4 color, sampler2D maskTexture, vec4 borderColor, int borderSize, float borderThreshold){
    vec4 borderDiffuse = vec4(0);
    float avg = 0.0;
    int r = borderSize;
    for(int i=-r; i<=r; i++){
        for(int j=-r; j<=r; j++){
            avg += texture(maskTexture, textureCoords + vec2(i, j)/(screenSize)).r;
        }
    }
    avg /= pow(r*2 + 1, 2);
    float c = texture(maskTexture, textureCoords).r;
    if(c - avg > borderThreshold){
        borderDiffuse = borderColor;
    }

    return mix(color, borderDiffuse.rgba, borderDiffuse.a);
}

void main() {

    vec4 boxColor = texture(textures[0], textureCoords).rgba;
    vec4 boxMask = texture(textures[1], textureCoords).rgba;

    vec4 liquidColor = texture(textures[2], textureCoords).rgba;
    vec4 liquidMask = texture(textures[3], textureCoords).rgba;

//    liquidColor *= vec4(0.1, 0.2, 0.8, 1);
//    liquidColor = applyBorderColor(liquidColor, textures[3], vec4(0.3, 0.4, 0.8, 1), 1, 0.1);
    liquidColor *= vec4(0.95, 0.4, 0.1, 1);
    liquidColor = applyBorderColor(liquidColor, textures[3], vec4(0.95, 0.6, 0.5, 1), 1, 0.1);

    vec4 add = vec4(0);

    add += mix(add, boxColor, boxMask.x);
    add += mix(add, liquidColor, liquidMask.x);

    out_Color = add;

//    vec3 add = vec3(0);
//
//    for(int i=0; i<numColor; i++){
//        add += mix(add, texture(textures[i*2], textureCoords).rgb, texture(textures[i*2+1], textureCoords).x);
//    }
//
//    out_Color = vec4(add, 1);
}
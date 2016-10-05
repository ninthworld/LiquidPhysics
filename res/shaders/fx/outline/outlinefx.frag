#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D maskTexture;
uniform vec4 outlineColor;
uniform int outlineSize;
uniform bool isSpecular;
uniform vec2 screenSize;

void main() {
    vec4 outlineDiffuse = vec4(0);
    float avg = 0.0;
    vec2 avgVec = vec2(0);
    int r = outlineSize;
    for(int i=-r; i<=r; i++){
        for(int j=-r; j<=r; j++){
            float val = texture(maskTexture, textureCoords + vec2(i, j)/(screenSize)).r;
            avg += val;
            if(val == 0){
                avgVec += vec2(i, j);
            }
        }
    }
    avg /= pow(r*2 + 1, 2);
    avgVec = normalize(avgVec);
    float c = texture(maskTexture, textureCoords).r;
    if(c - avg > 0.1){
        if(isSpecular){
            if(dot(avgVec, vec2(1, 1)) > 0.95){
                outlineDiffuse = outlineColor;
            }
        }else{
            outlineDiffuse = outlineColor;
        }
    }

    out_Color = mix(texture(colorTexture, textureCoords).rgba, outlineDiffuse.rgba, outlineDiffuse.a);
}
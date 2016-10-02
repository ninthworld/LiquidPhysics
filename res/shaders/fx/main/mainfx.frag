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

    vec4 waterColor = texture(textures[2], textureCoords).rgba;
    vec4 waterMask = texture(textures[3], textureCoords).rgba;

    vec4 lavaColor = texture(textures[4], textureCoords).rgba;
    vec4 lavaMask = texture(textures[5], textureCoords).rgba;

    vec4 steamColor = texture(textures[6], textureCoords).rgba;
    vec4 steamMask = texture(textures[7], textureCoords).rgba;

    vec4 obsidianColor = texture(textures[8], textureCoords).rgba;
    vec4 obsidianMask = texture(textures[9], textureCoords).rgba;

    waterColor *= vec4(0.1, 0.2, 0.8, 1);
    waterColor = applyBorderColor(waterColor, textures[3], vec4(1, 1, 1, 0.3), 4, 0.1);
    waterMask = applyBorderColor(waterMask, textures[3], vec4(1, 1, 1, 1), 4, 0.1);

    lavaColor *= vec4(0.95, 0.4, 0.1, 1);
    lavaColor = applyBorderColor(lavaColor, textures[5], vec4(1, 1, 1, 0.3), 4, 0.1);
    lavaMask = applyBorderColor(lavaMask, textures[5], vec4(1, 1, 1, 1), 4, 0.1);

    steamColor *= vec4(0.8, 0.8, 0.8, 1);
    steamColor = applyBorderColor(steamColor, textures[7], vec4(1, 1, 1, 0.6), 4, 0.1);
    steamMask = applyBorderColor(steamMask, textures[7], vec4(1, 1, 1, 1), 4, 0.1);

    obsidianColor *= vec4(0.1, 0.1, 0.1, 1);
    obsidianColor = applyBorderColor(obsidianColor, textures[9], vec4(0.1, 0.1, 0.1, 0.3), 4, 0.1);
    obsidianMask = applyBorderColor(obsidianMask, textures[9], vec4(1, 1, 1, 1), 4, 0.1);

    vec4 add = vec4(0.2, 0.2, 0.2, 1);

    add = mix(add, boxColor, boxMask.x);
    add = mix(add, waterColor, waterMask.x * 0.8);
    add = mix(add, lavaColor, lavaMask.x * 0.8);
    add = mix(add, steamColor, steamMask.x * 0.2);
    add = mix(add, obsidianColor, obsidianMask.x);

    out_Color = add;
}
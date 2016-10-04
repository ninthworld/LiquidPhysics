#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

const int numTextures = 16;
uniform sampler2D textures[numTextures];
uniform int numColor;
uniform int numMask;
uniform vec2 screenSize;

vec4 applyBorderColor(vec4 color, sampler2D maskTexture, vec4 borderColor, int borderSize, float borderThreshold, bool specular){
    vec4 borderDiffuse = vec4(0);
    float avg = 0.0;
    vec2 avgVec = vec2(0);
    int r = borderSize;
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
    if(c - avg > borderThreshold){
        if(specular){
            if(dot(avgVec, vec2(1, 1)) > 0.95){
                borderDiffuse = borderColor;
            }
        }else{
            borderDiffuse = borderColor;
        }
    }

    return mix(color, borderDiffuse.rgba, borderDiffuse.a);
}

vec4 applyOuterGlowColor(vec4 color, float mask, sampler2D maskTexture, vec4 glowColor, int glowSize){
    float avg = 0.0;
    int r = glowSize;
    for(int i=-r; i<=r; i++){
        for(int j=-r; j<=r; j++){
            avg += texture(maskTexture, textureCoords + vec2(i, j)/screenSize).r;
        }
    }
    avg /= pow(r*2 + 1, 2);

    if(mask > 0.5){
        return color;
    }else{
        return glowColor;
    }
}
vec4 applyOuterGlowMask(vec4 color, float mask, sampler2D maskTexture, vec4 glowColor, int glowSize){
    float avg = 0.0;
    int r = glowSize;
    for(int i=-r; i<=r; i++){
        for(int j=-r; j<=r; j++){
            avg += texture(maskTexture, textureCoords + vec2(i, j)/screenSize).r;
        }
    }
    avg /= pow(r*2 + 1, 2);

    if(mask > 0.5){
        return color;
    }else{
        return vec4(glowColor.rgb*avg, 1);
    }
}

void main() {

    // Box colors
    // 107, 169, 188
    // 88, 147, 166
    // 136, 197, 215

    // Ice colors
    // 157, 214, 232
    // 149, 217, 241
    // 160, 231, 252
    // 219, 242, 248

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

    vec4 shipColor = texture(textures[10], textureCoords).rgba;
    vec4 shipMask = texture(textures[11], textureCoords).rgba;

    boxColor = vec4(107, 169, 188, 255)/255.0;
    float radius = 10;
    float avg = 0;
    for(int i=0; i<10; i++){
        avg += texture(textures[1], textureCoords + vec2(cos(6.28*i/10.0)*radius, sin(6.28*i/10.0)*radius)/screenSize).r;
    }
    avg /= 10;
    if(avg > 0.7){
        boxColor = vec4(88, 147, 166, 255)/255.0;
    }

    waterColor *= vec4(94/255.0, 152/255.0, 181/255.0, 1); //vec4(0.1, 0.2, 0.8, 1);
    waterColor = applyBorderColor(waterColor, textures[3], vec4(118/255.0, 203/255.0, 188/255.0, 1), 2, 0.1, true); //vec4(1, 1, 1, 0.3), 2, 0.1, true);
    waterMask = applyBorderColor(waterMask, textures[3], vec4(1, 1, 1, 1), 2, 0.1, true);

    lavaColor *= vec4(241, 164, 86, 255)/255.0; //vec4(0.95, 0.4, 0.1, 1);
    lavaColor = applyBorderColor(lavaColor, textures[5], vec4(246, 214, 137, 255)/255.0, 6, 0.1, false);
    lavaMask = applyBorderColor(lavaMask, textures[5], vec4(1, 1, 1, 1), 2, 0.1, false);

    lavaColor = applyOuterGlowColor(lavaColor, lavaMask.r, textures[5], vec4(246, 214, 137, 255)/255.0, 8); //vec4(0.95, 0.4, 0.1, 1), 8);
    lavaMask = applyOuterGlowMask(lavaMask, lavaMask.r, textures[5], vec4(1, 1, 1, 1), 8);

    steamColor *= vec4(0.8, 0.8, 0.8, 1);
    steamColor = applyBorderColor(steamColor, textures[7], vec4(1, 1, 1, 0.9), 2, 0.1, true);
    steamMask = applyBorderColor(steamMask, textures[7], vec4(1, 1, 1, 1), 2, 0.1, true);

    obsidianColor *= vec4(0.1, 0.1, 0.1, 1);
    obsidianColor = applyBorderColor(obsidianColor, textures[9], vec4(1, 1, 1, 0.05), 1, 0.1, true);
    obsidianMask = applyBorderColor(obsidianMask, textures[9], vec4(1, 1, 1, 1), 1, 0.1, true);

    // Background colors
    // 41, 69, 80
    // 41, 67, 77
    // 40, 60, 70
    // 33, 54, 62

    vec4 add = vec4(0);
    if(textureCoords.y > sin(textureCoords.x * 8.0)*0.1 + 0.8){
        add = vec4(41, 69, 80, 255)/255.0;
    }else if(textureCoords.y > sin(textureCoords.x * 8.0)*0.1 + 0.6){
        add = vec4(41, 67, 77, 255)/255.0;
    }else if(textureCoords.y > sin(textureCoords.x * 8.0)*0.1 + 0.4){
        add = vec4(40, 60, 70, 255)/255.0;
    }else{
         add = vec4(33, 54, 62, 255)/255.0;
    }

    add = mix(add, boxColor, boxMask.x);
    add = mix(add, waterColor, waterMask.x * 0.8);
    add = mix(add, lavaColor, lavaMask.x * 0.9);
    add = mix(add, steamColor, steamMask.x * 0.2);
    add = mix(add, obsidianColor, obsidianMask.x);
    add = mix(add, shipColor, shipMask.x);

    out_Color = add;
}
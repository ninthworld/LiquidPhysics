#version 400 core

in vec2 fragPosition;
in vec3 fragColor;

out vec4 out_Color;

uniform int isMask = 0;

void main() {
    if(isMask == 0){
	    out_Color = vec4(fragColor, 1.0);
	}else{
	    out_Color = vec4(1.0);
	}
}

#version 400 core

in vec2 fragPosition;
in vec3 fragColor;

out vec4 out_Color;

void main() {
	out_Color = vec4(fragColor, 1.0);
}

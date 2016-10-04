#version 400 core

in vec2 position;
in vec3 color;

out vec2 textureCoords;
out vec2 fragPosition;
out vec3 fragColor;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
    gl_Position = vec4(position, 0.0, 1.0) * transformationMatrix * viewMatrix * projectionMatrix;

	textureCoords = position/32.0 + 0.5;
    fragPosition = position;
    fragColor = color;
}
#version 400 core

in vec2 position;
in vec3 color;

out vec2 fragPosition;
out vec3 fragColor;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

float w = 1280.0;
float h = 720.0;
mat4 testProj = mat4( 2/w,   0,  0,  -1,
                        0, 2/h,  0,   1,
                        0,   0,  1,   0,
                        0,   0,  0,   1);

mat4 testTran = mat4(   1,   0,  0,   2*(w)/w,
                        0,   1,  0,  -2*(h)/h,
                        0,   0,  1,   0,
                        0,   0,  0,   1);

void main(void){
    gl_Position = vec4(position, 1.0, 1.0) * projectionMatrix * viewMatrix * transformationMatrix;

//matrix.m00 = 2f / width;
//matrix.m11 = 2f / height;
//matrix.m22 = 1f;
//matrix.m33 = 1f;
//matrix.m03 = -1f;
//matrix.m13 = 1f;
//
//matrix.m00 = //2f / width;
//matrix.m11 = //2f / height;

    fragPosition = position;
    fragColor = color;
}
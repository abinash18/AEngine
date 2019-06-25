#version 330

out vec4 fragColor;
//in vec4 color;
in vec2 texCoord0;

uniform vec3 color;
uniform sampler2D sampler;

void main() {

  vec4 textureColor = texture(sampler, texCoord0.xy);

     if(textureColor == vec4(0,0,0,0))
         fragColor = vec4(color, 1);
     else
         fragColor = textureColor * vec4(color, 1);
}

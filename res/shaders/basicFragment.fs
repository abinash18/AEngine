#version 330

//out vec4 fragColor;
//in vec4 color;
in vec2 texCoord0;

uniform vec3 color;
uniform sampler2D sampler;

void main() {

  vec4 texColor = texture2D(sampler, texCoord0.xy);

  // if (texColor == vec4(0, 0, 0, 0)) {
  //   gl_FragColor = vec4(color, 1);
  // } else {
  //   gl_FragColor = texture2D(sampler, texCoord0.xy) * vec4(color, 1);
  // }
gl_FragColor = texture2D(sampler, texCoord0.xy) * vec4(color, 1);
}

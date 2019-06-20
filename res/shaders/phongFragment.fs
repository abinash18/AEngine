#version 330

out vec4 fragColor;
//in vec4 color;
in vec2 texCoord0;
in vec3 normal0;

uniform vec3 baseColor;
uniform vec3 ambientLight;
uniform sampler2D sampler;

uniform DirectionalLight directionalLight

struct BaseLight {
  vec3 color;
  float intensity;

};

struct DirectionalLight {
  BaseLight base;
  vec3 direction;
};

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal) {
  float diffuseFactor = dot(-direction, normal);
  vec4 diffuseColor = vec4(0, 0, 0, 0);

  if (diffuseFactor > 0) {
    diffuseColor = vec4(base.color, 1.0) * base.intensity * diffuseFactor;
  }

  return diffuseColor;

}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 normal) {
  return (calcLight(directionalLight.base, directionalLight.direction, normal));
}

void main() {

  vec4 totalLight = vec4(ambientLight, 1);
  vec4 color = vec4(baseColor, 1);
  vec4 texColor = texture(sampler, texCoord0.xy);
  if (texColor != vec4(0, 0, 0, 0)) {
    color *= texColor;
  }

  fragColor = color * totalLight;

  // if (texColor == vec4(0, 0, 0, 0)) {
  //   gl_FragColor = vec4(color, 1);
  // } else {
  //   gl_FragColor = texture2D(sampler, texCoord0.xy) * vec4(color, 1);
  // }
  //gl_FragColor = texture2D(sampler, texCoord0.xy) * vec4(color, 1);
}

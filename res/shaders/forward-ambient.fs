#version 120

varying vec3 texCoord0;

uniform vec3 ambientIntensity;
uniform sampler2D sampler;
void main() {
    gl_FragColor = texture2D(sampler, texCoord.xy) * vec4(ambientIntensity, 1.0);
  }
}

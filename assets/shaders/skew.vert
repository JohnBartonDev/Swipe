attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_proBob;
uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoords;

void main()
{
    v_color = a_color;
    v_color.a = v_color.a * (255.0/254.0);
    v_texCoords = a_texCoord0;
    
    float xSkew = 0.5;
    float ySkew = 0.5;
    
   // mat3 trans = mat3(
   //   1.0       , tan(xSkew), 0.0,
  //    tan(ySkew), 1.0,        0.0,
  //    0.0       , 0.0,        1.0
  // 	);

   //v_texCoords = (trans * (vec3(v_texCoords.xy, 0.0))).xy;
    
    gl_Position =  u_projTrans * a_position;
}
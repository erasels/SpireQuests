#version 120

varying vec2 v_texCoord;
varying vec4 v_color;

uniform sampler2D u_texture;
uniform vec2 u_roll;
uniform vec4 u_targetRect;

void main() {
    vec4 tex = texture2D(u_texture, v_texCoord);

    vec2 uv = (gl_FragCoord.xy - u_targetRect.xy) / u_targetRect.zw;

    vec2 adjusted_uv = uv - vec2(0.5, 0.5);
    adjusted_uv.x = adjusted_uv.x * (u_targetRect.z / u_targetRect.w);

    float low = min(tex.r, min(tex.g, tex.b));
    float high = max(tex.r, max(tex.g, tex.b));
    float delta = min(high, max(0.5, 1.0 - low));

    float r_val = u_roll.x;
    float g_val = u_roll.y;

    float fac = max(min(2.0 * sin((length(90.0 * adjusted_uv) + r_val * 2.0) + 3.0 * (1.0 + 0.8 * cos(length(113.1121 * adjusted_uv) - r_val * 3.121))) - 1.0 - max(5.0 - length(90.0 * adjusted_uv), 0.0), 1.0), 0.0);

    vec2 rotater = vec2(cos(r_val * 0.1221), sin(r_val * 0.3512));
    float angle = dot(rotater, adjusted_uv) / (length(rotater) * length(adjusted_uv));

    float fac2 = max(min(5.0 * cos(g_val * 0.3 + angle * 3.14 * (2.2 + 0.9 * sin(r_val * 1.65 + 0.2 * g_val))) - 4.0 - max(2.0 - length(20.0 * adjusted_uv), 0.0), 1.0), 0.0);
    float fac3 = 0.3 * max(min(2.0 * sin(r_val * 5.0 + uv.x * 3.0 + 3.0 * (1.0 + 0.5 * cos(r_val * 7.0))) - 1.0, 1.0), -1.0);
    float fac4 = 0.3 * max(min(2.0 * sin(r_val * 6.66 + uv.y * 3.8 + 3.0 * (1.0 + 0.5 * cos(r_val * 3.414))) - 1.0, 1.0), -1.0);

    float maxfac = max(max(fac, max(fac2, max(fac3, max(fac4, 0.0)))) + 2.2 * (fac + fac2 + fac3 + fac4), 0.0);

    tex.r = tex.r - (delta * 0.3) + (delta * maxfac * 0.2);
    tex.g = tex.g - (delta * 0.3) + (delta * maxfac * 0.2);
    tex.b = tex.b + delta * maxfac * 0.3;

    tex.rgb *= tex.a;

    gl_FragColor = tex * v_color;
}

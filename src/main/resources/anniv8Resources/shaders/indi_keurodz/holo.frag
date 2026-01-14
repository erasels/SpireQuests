#version 120

varying vec2 v_texCoord;
varying vec4 v_color;

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_holo;
uniform vec4 u_targetRect;

float hue(float s, float t, float h) {
    float hs = mod(h, 1.0) * 6.0;
    if (hs < 1.0) return (t - s) * hs + s;
    if (hs < 3.0) return t;
    if (hs < 4.0) return (t - s) * (4.0 - hs) + s;
    return s;
}
vec4 RGB(vec4 c) {
    if (c.y < 0.0001) return vec4(vec3(c.z), c.a);
    float t = (c.z < 0.5) ? c.y * c.z + c.z : -c.y * c.z + (c.y + c.z);
    float s = 2.0 * c.z - t;
    return vec4(hue(s, t, c.x + 1.0 / 3.0), hue(s, t, c.x), hue(s, t, c.x - 1.0 / 3.0), c.w);
}
vec4 HSL(vec4 c) {
    float low = min(c.r, min(c.g, c.b));
    float high = max(c.r, max(c.g, c.b));
    float delta = high - low;
    float sum = high + low;
    vec4 hsl = vec4(0.0, 0.0, 0.5 * sum, c.a);
    if (delta == 0.0) return hsl;
    hsl.y = (hsl.z < 0.5) ? delta / sum : delta / (2.0 - sum);
    if (high == c.r) hsl.x = (c.g - c.b) / delta; else if (high == c.g) hsl.x = (c.b - c.r) / delta + 2.0; else hsl.x = (c.r - c.g) / delta + 4.0;
    hsl.x = mod(hsl.x / 6.0, 1.0);
    return hsl;
}

void main() {
    vec4 tex = texture2D(u_texture, v_texCoord);

    vec2 uv = (gl_FragCoord.xy - u_targetRect.xy) / u_targetRect.zw;

    vec4 mixedCol = 0.5 * tex + 0.5 * vec4(0.0, 0.0, 1.0, tex.a);
    vec4 hsl = HSL(mixedCol);

    float t = u_holo.y * 7.221 + u_time;

    vec2 uv_scaled_centered = (uv - 0.5) * 250.0;

    vec2 field_part1 = uv_scaled_centered + 50.0 * vec2(sin(-t / 143.6340), cos(-t / 99.4324));
    vec2 field_part2 = uv_scaled_centered + 50.0 * vec2(cos(t / 53.1532), cos(t / 61.4532));
    vec2 field_part3 = uv_scaled_centered + 50.0 * vec2(sin(-t / 87.53218), sin(-t / 49.0000));

    float field = (1.0 + (
            cos(length(field_part1) / 19.483) +
                sin(length(field_part2) / 33.155) * cos(field_part2.y / 15.73) +
                cos(length(field_part3) / 27.193) * sin(field_part3.x / 21.92)
            )) / 2.0;

    float res = (0.5 + 0.5 * cos((u_holo.x) * 2.612 + (field - 0.5) * 3.14));
    float low = min(tex.r, min(tex.g, tex.b));
    float high = max(tex.r, max(tex.g, tex.b));
    float delta = 0.2 + 0.3 * (high - low) + 0.1 * high;

    float gridsize = 0.79;
    float fac = 0.5 * max(max(max(0.0, 7.0 * abs(cos(uv.x * gridsize * 20.0)) - 6.0),
                    max(0.0, 7.0 * cos(uv.y * gridsize * 45.0 + uv.x * gridsize * 20.0) - 6.0)),
                max(0.0, 7.0 * cos(uv.y * gridsize * 45.0 - uv.x * gridsize * 20.0) - 6.0));

    hsl.x = hsl.x + res + fac;
    hsl.y = hsl.y * 1.05;
    hsl.z = hsl.z * 0.6 + 0.4;

    vec4 result = mix(tex, RGB(hsl) * vec4(0.9, 0.8, 1.2, tex.a), delta * 0.75);

    if (result.a < 0.7) result.a = result.a / 3.0;
    result.rgb *= result.a;

    gl_FragColor = result * v_color;
}

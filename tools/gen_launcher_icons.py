#!/usr/bin/env python3
"""Generate Android launcher icon raster resources from prankstar_icon.png.

Produces mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher.png and
ic_launcher_round.png at the standard launcher icon dimensions.
"""
import os
from PIL import Image, ImageDraw, ImageFilter

SRC = "app/src/main/res/drawable/prankstar_icon.png"
RES = "app/src/main/res"

# Standard Android launcher icon sizes
SIZES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

def fit_square(img: Image.Image, size: int) -> Image.Image:
    """Fit the image into a square canvas of `size` preserving aspect ratio,
    centered, with transparent padding."""
    img = img.convert("RGBA")
    w, h = img.size
    scale = size / max(w, h)
    nw, nh = max(1, int(round(w * scale))), max(1, int(round(h * scale)))
    resized = img.resize((nw, nh), Image.LANCZOS)
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    canvas.paste(resized, ((size - nw) // 2, (size - nh) // 2), resized)
    return canvas

def make_round(square: Image.Image) -> Image.Image:
    size = square.size[0]
    mask = Image.new("L", (size, size), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, size - 1, size - 1), fill=255)
    out = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    out.paste(square, (0, 0), mask)
    return out

def main():
    src = Image.open(SRC)
    for density, size in SIZES.items():
        outdir = os.path.join(RES, f"mipmap-{density}")
        os.makedirs(outdir, exist_ok=True)
        sq = fit_square(src, size)
        sq.save(os.path.join(outdir, "ic_launcher.png"), "PNG", optimize=True)
        rd = make_round(sq)
        rd.save(os.path.join(outdir, "ic_launcher_round.png"), "PNG", optimize=True)
        print(f"  wrote {density}: {size}x{size}")

if __name__ == "__main__":
    main()

"""Generates the addon's original 16x16 item textures + 18x18 effect icon.
Palette matches Elysium's bronze body + teal magnetic accents. Run: python3 tools/make_textures.py"""
from PIL import Image
import os
OUT = os.path.join(os.path.dirname(__file__), "..", "src/main/resources/assets/invictus/textures")
PAL = {
 'D':(0x1c,0x2a,0x2a),'T':(0x3e,0xbc,0x98),'L':(0x68,0xd6,0xc0),'W':(0xd6,0xfa,0xfa),'d':(0x29,0x93,0x70),
 'B':(0xab,0x76,0x3e),'b':(0x8f,0x5f,0x34),'k':(0x42,0x2a,0x19),'c':(0xc1,0x8c,0x4e),
 'P':(0x7a,0x4f,0xd1),'p':(0xa9,0x8a,0xf0),'q':(0x4b,0x2f,0x8c),'g':(0x55,0x59,0x5a),'G':(0x8a,0x90,0x92),
}
def grid(rows, size=16):
    im = Image.new("RGBA", (size, size), (0,0,0,0))
    for y,row in enumerate(rows):
        for x,ch in enumerate(row):
            if ch in PAL: im.putpixel((x,y), PAL[ch]+(255,))
    return im
def put(im, pts, ch):
    for x,y in pts:
        if 0<=x<im.width and 0<=y<im.height: im.putpixel((x,y), PAL[ch]+(255,))

# --- rapier: teal blade up-right, bronze guard/handle
r = Image.new("RGBA",(16,16),(0,0,0,0))
for t in range(0,9):
    x,y = 15-t, t
    put(r,[(x,y)],'W' if t<2 else 'L'); put(r,[(x-1,y)],'T'); put(r,[(x,y+1)],'d')
for i in range(-3,4):                      # guard, perpendicular to blade
    put(r,[(7+i,8+i)],'B'); put(r,[(7+i,9+i)],'k')
put(r,[(4,6),(10,12)],'c')
for t in range(0,4):                       # handle
    put(r,[(6-t,9+t)],'b'); put(r,[(5-t,9+t)],'k')
put(r,[(1,13),(2,13),(1,14),(2,14)],'B'); put(r,[(1,15),(2,15)],'k')
r.save(f"{OUT}/item/invictus_rapier.png")

# --- AMPE: bronze forearm cannon, purple core, teal muzzle
ampe = grid([
"................",
"................",
"......kkkkkk....",
"....kkBBBBBBkk..",
"...kBBccccBBbbk.",
"..kBBcPPPPcBbbDL",
"..kBbPpPPqPbbDTW",
"..kBbPPPPqPbbDTL",
"..kBBcqPPqcBbbDd",
"...kbBBccBBbbk..",
"....kkbBBbbkk...",
"....kbbkkbbk....",
"....kbkk.kbk....",
"....kkk..kkk....",
"................",
"................"])
ampe.save(f"{OUT}/item/ampe.png")

# --- battery: dark cell, teal band, bronze cap
bat = grid([
"................",
".......kk.......",
"......kBBk......",
"......kBBk......",
".....DDDDDD.....",
".....DgGGgD.....",
".....DgTTgD.....",
".....DgLLgD.....",
".....DgTTgD.....",
".....DgddgD.....",
".....DgGGgD.....",
".....DgGGgD.....",
".....DgGGgD.....",
".....DDDDDD.....",
"................",
"................"])
bat.save(f"{OUT}/item/battery.png")

# --- enchanted elysium flask: glass bottle, teal liquid
flask = grid([
"................",
"......kkkk......",
".......kk.......",
".......DD.......",
"......DGGD......",
".....DG..GD.....",
"....DG.LL.GD....",
"...DGLTTTTLGD...",
"...DGTTWTTTGD...",
"...DGTTTTTdGD...",
"...DGTTTTddGD...",
"...DGdTTTddGD...",
"....DGdddddD....",
".....DDDDDD.....",
"................",
"................"])
flask.save(f"{OUT}/item/enchanted_elysium_flask.png")

# --- nanobot swarm: dark jar with swirling teal specks
swarm = grid([
"................",
"......bbbb......",
".....kBBBBk.....",
".....DDDDDD.....",
"....DgGggGgD....",
"...DgqLqqLqgD...",
"...DgqqTqqqgD...",
"...DgTqqqLqgD...",
"...DgqqLqqTgD...",
"...DgLqqTqqgD...",
"...DgqTqqqLgD...",
"...DgqqqLqqgD...",
"....DgqqTqgD....",
".....DDDDDD.....",
"................",
"................"])
swarm.save(f"{OUT}/item/nanobot_swarm.png")

# --- invictus engine: bronze casing around a glowing teal/purple core
eng = grid([
"................",
"....kkkkkkkk....",
"...kBBccccBBk...",
"..kBcbbbbbbcBk..",
"..kBbDDDDDDbBk..",
"..kBbDLWWLDbBk..",
"..kBbDWTTWDbBk..",
"..kBbDWTqWDbBk..",
"..kBbDLTPLDbBk..",
"..kBbDDDDDDbBk..",
"..kBcbbbbbbcBk..",
"...kBBccccBBk...",
"....kkkkkkkk....",
"................",
"................",
"................"])
eng.save(f"{OUT}/item/invictus_engine.png")

# --- status-effect icon (18x18): teal ring + purple core
eff = Image.new("RGBA",(18,18),(0,0,0,0))
import math
for y in range(18):
    for x in range(18):
        d = math.hypot(x-8.5,y-8.5)
        if 6.2<=d<=8: put(eff,[(x,y)],'L' if y<9 else 'T')
        elif 3<=d<6.2: put(eff,[(x,y)],'q' if d>4.5 else 'P')
        elif d<3: put(eff,[(x,y)],'W' if d<1.6 else 'p')
eff.save(f"{OUT}/mob_effect/invictus_form.png")

# preview sheet (8x scale) so the art can be eyeballed
names=["invictus_rapier","ampe","battery","enchanted_elysium_flask","nanobot_swarm","invictus_engine"]
sheet=Image.new("RGBA",(16*8*len(names)+8*(len(names)+1)+18*8,16*8+16),(40,44,46,255))
for i,n in enumerate(names):
    im=Image.open(f"{OUT}/item/{n}.png").resize((128,128),Image.NEAREST); sheet.paste(im,(8+i*136,8),im)
im=Image.open(f"{OUT}/mob_effect/invictus_form.png").resize((144,144),Image.NEAREST); sheet.paste(im,(8+len(names)*136,0),im)
sheet.save("/tmp/preview.png")
print("ok")

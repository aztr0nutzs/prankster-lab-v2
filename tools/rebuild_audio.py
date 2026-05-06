"""Wipe app/src/main/assets/sounds and rebuild from tools/_minimal."""
import os, shutil, json, hashlib, re, sys, subprocess

MIN = 'tools/_minimal'
DST = 'app/src/main/assets/sounds'
CATALOG = 'app/src/main/assets/sound_catalog.json'

# Map zip top-level dir -> Android category dir + display category
DIR_MAP = {
    'big_animals':         ('animal',          'ANIMAL'),
    'birds_small.animals': ('animal',          'ANIMAL'),
    'cats_dogs':           ('animal',          'ANIMAL'),
    'chickens':            ('animal',          'ANIMAL'),
    'cartoons':            ('cartoon',         'CARTOON'),
    'horror':              ('creepy',          'CREEPY'),
    'clowns':              ('funny',           'FUNNY'),
    'laughing':            ('funny',           'FUNNY'),
    'minions':             ('funny',           'FUNNY'),
    'tech_scifi':          ('ambience',        'AMBIENCE'),
    'rpg':                 ('misc',            'MISC'),
    'tones_effects':       ('misc',            'MISC'),
    'voices':              ('voice',           'VOICE'),
    'voices_fighter':      ('voices_fighter',  'VOICE'),
}

PACK_BY_CAT = {
    'ANIMAL':   'animal_pack',
    'CARTOON':  'cartoon_pack',
    'CREEPY':   'creepy_pack',
    'FUNNY':    'funny_pack',
    'AMBIENCE': 'ambience_pack',
    'MISC':     'misc_pack',
    'VOICE':    'voice_pack',
}

def slug_id(rel):
    return re.sub(r'[^a-z0-9]+', '_', rel.lower()).strip('_')

def pretty_name(filename):
    s = os.path.splitext(filename)[0]
    s = re.sub(r'^[a-z0-9_]+-', '', s)               # drop leading author tag
    s = re.sub(r'-\d{4,}(\s*\(\d+\))?$', '', s)      # drop trailing freesound id
    s = s.replace('_', ' ').replace('-', ' ')
    s = re.sub(r'\s+', ' ', s).strip()
    s = re.sub(r'\bx27\b', "'", s)                   # decode 'x27' -> apostrophe
    s = re.sub(r'\bsfx\b', 'SFX', s, flags=re.I)
    if not s:
        s = os.path.splitext(filename)[0]
    return s.title().replace("'S", "'s")

def main():
    if not os.path.isdir(MIN):
        print('FATAL: missing', MIN); sys.exit(1)

    # 1. Wipe destination
    if os.path.isdir(DST):
        shutil.rmtree(DST)
    os.makedirs(DST)

    # 2. Copy files into category dirs, preserving subdirs for voices/Male & voices/Female
    copied = []
    for top, (cat_dir, _) in DIR_MAP.items():
        src = os.path.join(MIN, top)
        if not os.path.isdir(src):
            print('  skip (no src):', top); continue
        for r, _, fs in os.walk(src):
            for f in fs:
                if f == 'desktop.ini': continue
                spath = os.path.join(r, f)
                rel_in_top = os.path.relpath(spath, src).replace(os.sep, '/')
                if top == 'voices':
                    out_rel = os.path.join(cat_dir, rel_in_top).replace(os.sep, '/')
                else:
                    out_rel = os.path.join(cat_dir, f).replace(os.sep, '/')
                out_path = os.path.join(DST, out_rel)
                os.makedirs(os.path.dirname(out_path), exist_ok=True)
                if os.path.exists(out_path):
                    # collision (same basename from different source dir): suffix
                    stem, ext = os.path.splitext(out_rel)
                    n = 2
                    while os.path.exists(os.path.join(DST, f'{stem}__{n}{ext}')):
                        n += 1
                    out_rel = f'{stem}__{n}{ext}'
                    out_path = os.path.join(DST, out_rel)
                shutil.copy2(spath, out_path)
                copied.append((top, out_rel))
    print(f'copied {len(copied)} files into sounds/')

    # 3. Build catalog (one entry per file, dedup by content hash)
    seen_hash = {}
    entries = []
    for top, rel in copied:
        full = os.path.join(DST, rel)
        with open(full, 'rb') as fh:
            data = fh.read()
        h = hashlib.sha1(data).hexdigest()
        if h in seen_hash:
            # remove duplicate file
            os.remove(full)
            continue
        seen_hash[h] = rel

        cat_dir, cat = DIR_MAP[top]
        asset_path = f'sounds/{rel}'
        fname = os.path.basename(rel)
        eid = slug_id(asset_path)
        loopable = top in ('tech_scifi',)  # ambience-y stuff is loopable
        is_safe = cat not in ('CREEPY',)
        intensity = 3 if cat == 'CREEPY' else 2
        entries.append({
            'id': eid,
            'name': pretty_name(fname),
            'category': cat,
            'packId': PACK_BY_CAT[cat],
            'assetPath': asset_path,
            'durationMs': 0,
            'tags': [cat.lower(), top.replace('.', '_')],
            'loopable': loopable,
            'intensityLevel': intensity,
            'isSafeForRandomMode': is_safe,
            'description': f'{cat.title()} prank sound.',
            'recommendedUse': 'Prank your friends',
            'prankStyle': 'creepy' if cat == 'CREEPY' else 'funny',
            'previewLabel': 'Tap to play',
        })

    # 4. Enforce unique IDs
    by_id = {}
    for e in entries:
        base_id = e['id']
        n = 2
        while e['id'] in by_id:
            e['id'] = f'{base_id}_{n}'
            n += 1
        by_id[e['id']] = e

    entries.sort(key=lambda x: (x['category'], x['name']))
    with open(CATALOG, 'w', encoding='utf-8') as fh:
        json.dump(entries, fh, indent=2)
    print(f'catalog: {len(entries)} entries written to {CATALOG}')

if __name__ == '__main__':
    main()

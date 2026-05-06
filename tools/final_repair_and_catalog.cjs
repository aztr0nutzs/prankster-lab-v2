const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const mm = require('music-metadata');

const assetsDir = path.join(__dirname, '../app/src/main/assets/sounds');
const catalogPath = path.join(__dirname, '../app/src/main/assets/sound_catalog.json');

async function repair() {
  console.log('--- FINAL REPAIR & CATALOG REBUILD ---');
  
  if (!fs.existsSync(assetsDir)) {
    console.error('Assets dir missing');
    return;
  }

  function walk(dir) {
    let results = [];
    const list = fs.readdirSync(dir);
    list.forEach(file => {
      file = path.join(dir, file);
      const stat = fs.statSync(file);
      if (stat && stat.isDirectory()) {
        results = results.concat(walk(file));
      } else {
        results.push(file);
      }
    });
    return results;
  }

  const allFiles = walk(assetsDir);
  const fileHashes = new Map();
  const validEntries = [];
  const filesToDelete = [];

  const categoryMapping = {
    'ambience': 'AMBIENCE',
    'animal': 'ANIMAL',
    'cartoon': 'CARTOON',
    'creepy': 'CREEPY',
    'funny': 'FUNNY',
    'misc': 'MISC',
    'voice': 'VOICE',
    'voices': 'VOICE',
    'voices_fighter': 'FIGHTER'
  };

  const prefixesToRemove = [
    'freesound', 'community', 'universfield', 'pixabay', 'flutie8211', 'magiaz', 'mangaletp',
    'voicebosch', 'alanajordan', 'arunangshubanerjee', 'digitalstore07', 'alex', 'jauk',
    'adlibs', 'sfx', 'frequencyoasis', 'shrek', 'shut', 'up', 'ghost', 'dragon', 'studio',
    'audiopapkin', 'cartoon-music-soundtrack', 'scottishperson', 'superv007', 'virtual_vibes',
    'x_bass6668', 'sound_garage', 'soundreality', 'iedurodrigues', 'kakaist', 'lazychillzone',
    'feedthestraycats'
  ];

  function cleanTitle(fileName, parentDirName) {
    let name = fileName.replace(/\.[^/.]+$/, "");
    if (/^\d+$/.test(name) || name.length < 3) {
      return (parentDirName.charAt(0).toUpperCase() + parentDirName.substring(1)) + " " + name.toUpperCase();
    }
    let parts = name.split(/[-_ ]/);
    let cleanedParts = parts.filter(part => {
      let lp = part.toLowerCase();
      if (/^\d{3,}$/.test(part)) return false;
      if (prefixesToRemove.includes(lp)) return false;
      if (['sound', 'effect', 'voiced', 'spoken', 'comedic', 'reaction', 'fx'].includes(lp)) return false;
      return true;
    });
    if (cleanedParts.length === 0) return name.replace(/[-_]/g, " ").trim();
    return cleanedParts.map(p => p.charAt(0).toUpperCase() + p.substring(1).toLowerCase()).join(' ');
  }

  for (const file of allFiles) {
    const relPath = path.relative(path.join(__dirname, '../app/src/main/assets'), file).replace(/\\/g, '/');
    const ext = path.extname(file).toLowerCase();
    
    if (!['.mp3', '.ogg', '.wav'].includes(ext)) {
      console.log(`Skipping non-audio: ${relPath}`);
      filesToDelete.push(file);
      continue;
    }

    // Check decodability and duration
    let metadata;
    try {
      metadata = await mm.parseFile(file);
      if (!metadata.format || !metadata.format.duration || metadata.format.duration <= 0) {
        throw new Error('No duration');
      }
    } catch (e) {
      console.log(`Deleting corrupted/invalid: ${relPath} (${e.message})`);
      filesToDelete.push(file);
      continue;
    }

    // Check duplicates
    const hash = crypto.createHash('md5').update(fs.readFileSync(file)).digest('hex');
    if (fileHashes.has(hash)) {
      console.log(`Deleting duplicate: ${relPath} (same as ${fileHashes.get(hash)})`);
      filesToDelete.push(file);
      continue;
    }
    fileHashes.set(hash, relPath);

    // If valid, add to catalog
    const fileName = path.basename(file);
    const parentDir = path.dirname(file);
    const parentDirName = path.basename(parentDir).toLowerCase();
    const grandParentDirName = path.basename(path.dirname(parentDir)).toLowerCase();
    
    let category = categoryMapping[parentDirName] || parentDirName.toUpperCase();
    if (grandParentDirName === 'voices') category = 'VOICE';

    const title = cleanTitle(fileName, parentDirName);
    const isSafe = category !== 'AMBIENCE' && !/^\d+$/.test(fileName.replace(/\.[^/.]+$/, ""));

    validEntries.push({
      id: relPath.replace(/[^a-zA-Z0-9]/g, "_").toLowerCase(),
      name: title,
      category: category,
      packId: category.toLowerCase() + "_pack",
      assetPath: relPath,
      durationMs: Math.round(metadata.format.duration * 1000),
      tags: [category.toLowerCase()],
      loopable: category === 'AMBIENCE',
      intensityLevel: 2,
      isSafeForRandomMode: isSafe,
      description: `A ${category.toLowerCase()} prank sound.`,
      recommendedUse: "Prank your friends",
      prankStyle: category.toLowerCase() === 'creepy' ? 'horror' : 'funny',
      previewLabel: "Tap to play"
    });
  }

  // Delete bad files
  filesToDelete.forEach(f => {
    try { fs.unlinkSync(f); } catch (e) {}
  });

  // Write catalog
  fs.writeFileSync(catalogPath, JSON.stringify(validEntries, null, 2));
  console.log(`Repair complete. Catalog has ${validEntries.length} valid entries.`);
}

repair().catch(err => {
  console.error(err);
  process.exit(1);
});

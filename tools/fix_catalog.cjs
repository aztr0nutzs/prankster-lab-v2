const fs = require('fs');
const path = require('path');

const soundsDir = path.join(__dirname, '../app/src/main/assets/sounds');
const catalogPath = path.join(__dirname, '../app/src/main/assets/sound_catalog.json');

function walkSync(dir, filelist = []) {
  if (!fs.existsSync(dir)) return filelist;
  fs.readdirSync(dir).forEach(file => {
    const p = path.join(dir, file);
    if (fs.statSync(p).isDirectory()) {
      filelist = walkSync(p, filelist);
    } else {
      filelist.push(p);
    }
  });
  return filelist;
}

const allFiles = walkSync(soundsDir);
const catalog = [];
const seenFiles = new Set();

const categoryMapping = {
  'ambience': 'AMBIENCE',
  'animal': 'ANIMAL',
  'cartoon': 'CARTOON',
  'creepy': 'CREEPY',
  'funny': 'FUNNY',
  'misc': 'MISC',
  'voice': 'VOICE',
  'voices': 'VOICE',
  'voices_fighter': 'FIGHTER',
  'female': 'VOICE',
  'male': 'VOICE'
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
  let name = fileName.replace(/\.[^/.]+$/, ""); // remove extension
  
  // Special case for numbers or short generic names
  if (/^\d+$/.test(name) || name.length < 3 || ['go', 'set', 'tie', 'time', 'loser', 'winner'].includes(name.toLowerCase())) {
     return (parentDirName.charAt(0).toUpperCase() + parentDirName.substring(1).toLowerCase()) + " " + name.toUpperCase();
  }

  // Split by common delimiters
  let parts = name.split(/[-_ ]/);
  
  // Filter parts
  let cleanedParts = parts.filter(part => {
    let lp = part.toLowerCase();
    if (/^\d{3,}$/.test(part)) return false; // Remove long numeric IDs
    if (prefixesToRemove.includes(lp)) return false;
    if (['sound', 'effect', 'voiced', 'spoken', 'comedic', 'reaction', 'fx'].includes(lp)) return false;
    if (part.length > 5 && /^[a-z0-9]+$/i.test(part) && /\d/.test(part) && /[a-z]/i.test(part)) return false;
    if (part.length < 2 && !/^\d$/.test(part)) return false; 
    return true;
  });

  if (cleanedParts.length === 0) {
     return name.replace(/[-_]/g, " ").trim();
  }

  return cleanedParts.map(p => p.charAt(0).toUpperCase() + p.substring(1).toLowerCase()).join(' ');
}

allFiles.forEach(f => {
  const fileName = path.basename(f);
  const parentDir = path.dirname(f);
  const parentDirName = path.basename(parentDir).toLowerCase();
  const grandParentDirName = path.basename(path.dirname(parentDir)).toLowerCase();
  
  // Skip duplicates in 'misc' if the file exists elsewhere
  if (parentDirName === 'misc') {
      const existsElsewhere = allFiles.some(other => !other.includes('/misc/') && path.basename(other) === fileName);
      if (existsElsewhere) return;
  }

  const relativePath = path.relative(path.join(__dirname, '../app/src/main/assets'), f).replace(/\\/g, '/');
  
  // Consolidate categories
  let category = categoryMapping[parentDirName] || parentDirName.toUpperCase();
  if (grandParentDirName === 'voices') category = 'VOICE';

  const title = cleanTitle(fileName, parentDirName);
  
  // Safety filter for random mode: exclude ambience and numbers
  const isSafe = category !== 'AMBIENCE' && !/^\d+$/.test(fileName.replace(/\.[^/.]+$/, ""));

  catalog.push({
    id: relativePath.replace(/[^a-zA-Z0-9]/g, "_").toLowerCase(),
    name: title,
    category: category,
    packId: category.toLowerCase() + "_pack",
    assetPath: relativePath,
    durationMs: 3000, 
    tags: [category.toLowerCase()],
    loopable: category === 'AMBIENCE',
    intensityLevel: 2,
    isSafeForRandomMode: isSafe,
    description: `A ${category.toLowerCase()} prank sound.`,
    recommendedUse: "Prank your friends",
    prankStyle: category.toLowerCase() === 'creepy' ? 'horror' : 'funny',
    previewLabel: "Tap to play"
  });
});

fs.writeFileSync(catalogPath, JSON.stringify(catalog, null, 2));
console.log(`Rebuilt catalog with ${catalog.length} unique entries.`);
